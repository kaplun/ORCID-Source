/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.OrgManager;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class LoadFundRefData {
	
	class RDFOrganization {
		String doi, name, country, state, type, subtype;
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadFundRefData.class);
	private static final String FUNDREF_SOURCE_TYPE = "FUNDREF";
	private static String geonamesApiUrl="http://api.geonames.org/getJSON";
	//Params
	@Option(name = "-f", usage = "Path to RDF file containing FundRef info to load into DB")
    private File fileToLoad;
	//Resources
    private GenericDao<OrgDisambiguatedExternalIdentifierEntity, Long> genericDao; 	
	private OrgDisambiguatedDao orgDisambiguatedDao;
    private OrgManager orgManager;    
    private String apiUser;
    // Cache
    private HashMap<String, String> cache = new HashMap<String, String>();    
    // xPath queries
    private String conceptsExpression = "/RDF/ConceptScheme/hasTopConcept";
    private String itemExpression = "/RDF/Concept[@about='%s']";
    private String orgNameExpression = itemExpression + "/prefLabel/Label/literalForm";
    private String orgCountryExpression = itemExpression + "/country";
    private String orgStateExpression = itemExpression + "/state";
    private String orgTypeExpression = itemExpression + "/fundingBodyType";
    private String orgSubTypeExpression = itemExpression + "/fundingBodySubType";
    //xPath init
    private XPath xPath =  XPathFactory.newInstance().newXPath();
    //Statistics
    private long addedOrgs = 0;
    private long addedDisambiguatedOrgs = 0;
    private long addedExternalIdentifiers = 0;    
    
	public static void main(String[] args) {
		LoadFundRefData loadFundRefData = new LoadFundRefData();
        CmdLineParser parser = new CmdLineParser(loadFundRefData);
        try {
            parser.parseArgument(args);
            loadFundRefData.validateArgs(parser);
            loadFundRefData.init();
            loadFundRefData.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }     
                
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (fileToLoad == null) {
            throw new CmdLineException(parser, "-f parameter must be specificed");
        }
    }
    
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        orgDisambiguatedDao = (OrgDisambiguatedDao) context.getBean("orgDisambiguatedDao");
        genericDao = (GenericDao)context.getBean("orgDisambiguatedExternalIdentifierEntityDao");
        orgManager = (OrgManager) context.getBean("orgManager");        
        apiUser = (String)context.getBean("geonamesUser");
    }
    
    private void execute() {
    	try {
    		long start = System.currentTimeMillis();
			FileInputStream file = new FileInputStream(fileToLoad);			
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();			
			DocumentBuilder builder =  builderFactory.newDocumentBuilder();			
			Document xmlDocument = builder.parse(file);								
			// Parent node
			NodeList nodeList = (NodeList) xPath.compile(conceptsExpression).evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {				
				RDFOrganization rdfOrganization = getOrganization(xmlDocument, nodeList.item(i).getAttributes());
												
				//Now look an exact match into the disambiguated orgs
				OrgDisambiguatedEntity existingDisambiguatedOrg = getMatchingDisambiguatedOrg(rdfOrganization.name, rdfOrganization.country, rdfOrganization.state);
				//If exists add an external identifier
				if(existingDisambiguatedOrg != null) { 
					if(!existsExternalIdentifier(existingDisambiguatedOrg, rdfOrganization.doi)){				
						createExternalIdentifier(existingDisambiguatedOrg, rdfOrganization.doi);
					}
				} else {
					//Find an exact match in the list of orgs
					OrgEntity existingOrg = getMatchingOrg(rdfOrganization.name, rdfOrganization.country, rdfOrganization.state);
					String orgType = rdfOrganization.type + (StringUtils.isEmpty(rdfOrganization.subtype) ? "" : "/" + rdfOrganization.subtype);
					Iso3166Country country = StringUtils.isNotBlank(rdfOrganization.country) ? Iso3166Country.fromValue(rdfOrganization.country) : null;
					if(existingOrg != null) {
						//If the disambiguated org exists, just create an external identifier for it
						if(existingOrg.getOrgDisambiguated() != null) {
							createExternalIdentifier(existingOrg.getOrgDisambiguated(), rdfOrganization.doi);
							addedExternalIdentifiers++;
						} else {
							//Else create the disambiguated org and assign it to the existing org							
							OrgDisambiguatedEntity disambiguatedOrg = createDisambiguatedOrg(orgType, rdfOrganization.name, country, rdfOrganization.state, rdfOrganization.doi);
							addedDisambiguatedOrgs++;
							createOrUpdateOrg(existingOrg.getName(), existingOrg.getCity(), existingOrg.getCountry(), existingOrg.getRegion(), disambiguatedOrg.getId());
						}
					} else {
						//Create disambiguated organization
						OrgDisambiguatedEntity disambiguatedOrg = createDisambiguatedOrg(orgType, rdfOrganization.name, country, rdfOrganization.state, rdfOrganization.doi);
						addedDisambiguatedOrgs++;
						//Create organization
						createOrUpdateOrg(rdfOrganization.name, null, country, rdfOrganization.state, disambiguatedOrg.getId());
						addedOrgs++;
					}
				}				
			}
			System.out.println(System.currentTimeMillis() - start);
			System.out.println("Cache size: " + cache.size());
    	} catch(Exception e) {
    		System.out.println(e.toString());
    	}               
    }       
    
    private RDFOrganization getOrganization(Document xmlDocument, NamedNodeMap attrs) {    	
    	RDFOrganization organization = new RDFOrganization();
    	try {
			Node node = attrs.getNamedItem("rdf:resource");
			String itemDoi = node.getNodeValue();
			
			System.out.println("---------------------------------------------------------------------------------------------------------------");
			System.out.println(itemDoi);
			System.out.println("---------------------------------------------------------------------------------------------------------------");
			
			//Get organization name								
			String orgName = (String)xPath.compile(orgNameExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
			//Get country code
			Node countryNode = (Node)xPath.compile(orgCountryExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
			NamedNodeMap countryAttrs = countryNode.getAttributes();
			String countryGeonameUrl = countryAttrs.getNamedItem("rdf:resource").getNodeValue();
			String countryCode = fetchFromGeoNames(countryGeonameUrl, "countryCode");
			
			//Get state name
			Node stateNode = (Node)xPath.compile(orgStateExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.NODE);
			String stateName = null;
			if(stateNode != null) {
				NamedNodeMap stateAttrs = stateNode.getAttributes();
				String stateCode = stateAttrs.getNamedItem("rdf:resource").getNodeValue();
				stateName = fetchFromGeoNames(stateCode, "name");
			}
			
			//Get type
			String orgType = (String)xPath.compile(orgTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
			//Get subType
			String orgSubType = (String)xPath.compile(orgSubTypeExpression.replace("%s", itemDoi)).evaluate(xmlDocument, XPathConstants.STRING);
									
			//Fill the organization object			
			organization.type = orgType;
			organization.doi = itemDoi;
			organization.name = orgName;
			organization.country = countryCode;			
			organization.state = stateName;
			organization.subtype = orgSubType;						
    	} catch(Exception e) {
    		System.out.println(e.toString());
    	}
		
		return organization;
    }
    
    /**
     * TODO
     * */
    private String fetchFromGeoNames(String geoNameUri, String propertyToFetch){
    	String result = null;
    	String geoNameId = geoNameUri.replaceAll( "[^\\d]", "" );  
    	if(StringUtils.isNotBlank(geoNameId)) {
	    	String cacheKey = propertyToFetch + '_' + geoNameId;
	    	if(cache.containsKey(cacheKey)) {
	    		result = cache.get(cacheKey);
	    	} else {
	    		String jsonResponse = fetchJsonFromGeoNames(geoNameId);
	    		result = fetchValueFromJson(jsonResponse, propertyToFetch);    		
	    		System.out.println("Adding " + cacheKey + " -> " + result + " to cache");
	    		cache.put(cacheKey, result);
	    	}
    	}
    	
    	return result;
    }    
    
    /**
     * TODO
     * */
    private String fetchJsonFromGeoNames(String geoNameId) {
    	Client c = Client.create();    	
    	WebResource r = c.resource(geonamesApiUrl);
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("geonameId", geoNameId);
        params.add("username", apiUser);
        
        return r.queryParams(params).get(String.class);    	        
    }
    
    /**
     * It only fetches properties in the first level
     * */
    private String fetchValueFromJson(String jsonString, String propetyName){
    	String result = null;    	
    	try {
        	ObjectMapper m = new ObjectMapper();
        	JsonNode rootNode = m.readTree(jsonString);
        	JsonNode nameNode = rootNode.path(propetyName);
        	if(nameNode != null)
        		result = nameNode.getTextValue();
        } catch (Exception e) {
        	
        }
    	return result;
    }    
    
    /**
     * TODO
     * */
    private OrgDisambiguatedEntity getMatchingDisambiguatedOrg(String orgName, String country, String region) {
    	List<OrgDisambiguatedEntity> orgs = getExistingDisambiguatedOrgs(orgName);
    	if(orgs == null || orgs.size() == 0)
    		return null;
    	
    	for(OrgDisambiguatedEntity disambiguatedOrg : orgs) {
    		if(attributesMatches(disambiguatedOrg, country, region)) {    			
    			return disambiguatedOrg;
    		}    			
    	}
    	return null;
    }
    
    /**
     * TODO
     * */
    private OrgEntity getMatchingOrg(String orgName, String country, String region) {
    	List<OrgEntity> orgs = getExistingOrgs(orgName);
    	if(orgs == null || orgs.size() == 0)
    		return null;
    	for(OrgEntity org : orgs){
    		if(attributesMatches(org, country, region)) {    			
    			return org;
    		}
    	}
    	return null;
    }
    
    /**
     * TODO
     * */
    private List<OrgDisambiguatedEntity> getExistingDisambiguatedOrgs(String orgName) {
    	List<OrgDisambiguatedEntity> orgs = orgDisambiguatedDao.findByName(orgName);
    	if(orgs == null || orgs.size() == 0)
    		return null;
    	return orgs;
    }
    
    /**
     * TODO
     * */
    private List<OrgEntity> getExistingOrgs(String orgName) {
    	List<OrgEntity> orgs = orgManager.getOrgsByName(orgName);
    	if(orgs == null || orgs.size() == 0)
    		return null;
    	return orgs;
    }
    
    /**
     * TODO
     * */
    private boolean attributesMatches(OrgDisambiguatedEntity org, String country, String region) {
    	if(org.getCountry() == null) {
    		if(country != null)
    			return false;
    	} else {
    		if(country == null)
    			return false;
    		if(!org.getCountry().equals(Iso3166Country.fromValue(country)))
    			return false;
    	}
    	
    	if(org.getRegion() == null) {
    		if(region != null)
    			return false;
    	} else {
    		if(!org.getRegion().equals(region))
    			return false;
    	}
    	return true;
    }
    
    /**
     * TODO
     * */
    private boolean attributesMatches(OrgEntity org, String country, String region) {
    	if(org.getCountry() == null) {
    		if(country != null)
    			return false;
    	} else {
    		if(country == null)
    			return false;
    		if(!org.getCountry().equals(Iso3166Country.fromValue(country)))
    			return false;
    	}
    	
    	if(org.getRegion() == null) {
    		if(region != null)
    			return false;
    	} else {
    		if(!org.getRegion().equals(region))
    			return false;
    	}
    	return true;
    }
    
    /**
     * TODO
     * */
    private boolean existsExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String id) {
    	Set<OrgDisambiguatedExternalIdentifierEntity> extIds = disambiguatedOrg.getExternalIdentifiers();
    	if(extIds == null || extIds.size() == 0)
    		return false;
    	for(OrgDisambiguatedExternalIdentifierEntity extId : extIds){
    		if(extId.getIdentifierType().equals(FUNDREF_SOURCE_TYPE)) {
    			if(extId.getIdentifier() != null && extId.getIdentifier().equals(id))
    				return true;
    		}
    	}
    	return false;
    }
    
    /**
     * TODO
     * */
    private boolean createExternalIdentifier(OrgDisambiguatedEntity disambiguatedOrg, String identifier) {
    	LOGGER.info("Creating external identifier for {}", disambiguatedOrg.getId());
    	Date creationDate = new Date();
		OrgDisambiguatedExternalIdentifierEntity externalIdentifier = new OrgDisambiguatedExternalIdentifierEntity();
		externalIdentifier.setIdentifier(identifier);
		externalIdentifier.setIdentifierType(FUNDREF_SOURCE_TYPE);					
		externalIdentifier.setOrgDisambiguated(disambiguatedOrg);
		externalIdentifier.setDateCreated(creationDate);
		externalIdentifier.setLastModified(creationDate);
		genericDao.persist(externalIdentifier);
		return true;
    }
    
    /**
     * TODO
     * */
    private OrgDisambiguatedEntity createDisambiguatedOrg(String orgType, String name, Iso3166Country country, String region, String doi) {    
    	LOGGER.info("Creating disambiguated org {}", name);
    	OrgDisambiguatedEntity orgDisambiguatedEntity = new OrgDisambiguatedEntity();
    	orgDisambiguatedEntity.setName(name);
        orgDisambiguatedEntity.setRegion(region);
        orgDisambiguatedEntity.setCountry(country);
        orgDisambiguatedEntity.setOrgType(orgType);        
        orgDisambiguatedEntity.setSourceId(doi);
        orgDisambiguatedEntity.setSourceUrl(doi);
        orgDisambiguatedEntity.setSourceType(FUNDREF_SOURCE_TYPE);
        orgDisambiguatedDao.persist(orgDisambiguatedEntity);
    	return orgDisambiguatedEntity;
    }
    
    /**
     * TODO
     * */
    private void createOrUpdateOrg(String name, String city, Iso3166Country country, String state, Long orgDisambiguatedId) {
        LOGGER.info("Adding or updating organization {} to disambiguated org {}", name, orgDisambiguatedId);
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setName(name);
        orgEntity.setRegion(state);
        orgEntity.setCity(city);
        orgEntity.setCountry(country);
        orgManager.createUpdate(orgEntity, orgDisambiguatedId);
    }
    
}
