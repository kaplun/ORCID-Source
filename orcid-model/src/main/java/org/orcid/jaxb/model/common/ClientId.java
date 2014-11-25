/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.24 at 04:27:39 PM GMT 
//

package org.orcid.jaxb.model.common;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uri" type="{http://www.orcid.org/ns/orcid}clientUri"/>
 *         &lt;element name="path" type="{http://www.orcid.org/ns/orcid}clientPath"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}host"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "uri", "path", "host" })
@XmlRootElement(name = "clientId")
public class ClientId implements Serializable {

    private final static long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String uri;
    @XmlElement(required = true)
    protected String path;
    @XmlElement(required = true)
    protected String host;

    /**
     * Gets the value of the uri property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the value of the uri property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setUri(String value) {
        this.uri = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the host property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the value of the host property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setHost(String value) {
        this.host = value;
    }

}
