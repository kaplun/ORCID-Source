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

package org.orcid.jaxb.model.notification.addactivities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import org.orcid.jaxb.model.common.PutCode;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.notification.NotificationType;

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
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}putCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}notificationType"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}authorizationUrl"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}activities"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}createdDate" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}sentDate" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}source" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "putCode", "notificationType", "authorizationUrl", "activities", "createdDate", "sentDate", "source" })
@XmlRootElement(name = "notification")
public class NotificationAddActivities implements Serializable {

    private final static long serialVersionUID = 1L;
    protected PutCode putCode;
    @XmlElement(required = true)
    protected NotificationType notificationType;
    @XmlElement(required = true)
    protected AuthorizationUrl authorizationUrl;
    @XmlElement(required = true)
    protected Activities activities;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar createdDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sentDate;
    protected Source source;

    /**
     * Gets the value of the putCode property.
     * 
     * @return possible object is {@link PutCode }
     * 
     */
    public PutCode getPutCode() {
        return putCode;
    }

    /**
     * Sets the value of the putCode property.
     * 
     * @param value
     *            allowed object is {@link PutCode }
     * 
     */
    public void setPutCode(PutCode value) {
        this.putCode = value;
    }

    /**
     * Gets the value of the notificationType property.
     * 
     * @return possible object is {@link NotificationType }
     * 
     */
    public NotificationType getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the value of the notificationType property.
     * 
     * @param value
     *            allowed object is {@link NotificationType }
     * 
     */
    public void setNotificationType(NotificationType value) {
        this.notificationType = value;
    }

    /**
     * Gets the value of the authorizationUrl property.
     * 
     * @return possible object is {@link AuthorizationUrl }
     * 
     */
    public AuthorizationUrl getAuthorizationUrl() {
        return authorizationUrl;
    }

    /**
     * Sets the value of the authorizationUrl property.
     * 
     * @param value
     *            allowed object is {@link AuthorizationUrl }
     * 
     */
    public void setAuthorizationUrl(AuthorizationUrl value) {
        this.authorizationUrl = value;
    }

    /**
     * Gets the value of the activities property.
     * 
     * @return possible object is {@link Activities }
     * 
     */
    public Activities getActivities() {
        return activities;
    }

    /**
     * Sets the value of the activities property.
     * 
     * @param value
     *            allowed object is {@link Activities }
     * 
     */
    public void setActivities(Activities value) {
        this.activities = value;
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     * 
     */
    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     * 
     */
    public void setCreatedDate(XMLGregorianCalendar value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the sentDate property.
     * 
     * @return possible object is {@link XMLGregorianCalendar }
     * 
     */
    public XMLGregorianCalendar getSentDate() {
        return sentDate;
    }

    /**
     * Sets the value of the sentDate property.
     * 
     * @param value
     *            allowed object is {@link XMLGregorianCalendar }
     * 
     */
    public void setSentDate(XMLGregorianCalendar value) {
        this.sentDate = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link Source }
     * 
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *            allowed object is {@link Source }
     * 
     */
    public void setSource(Source value) {
        this.source = value;
    }

}
