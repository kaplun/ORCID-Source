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
package org.orcid.api.common.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.orcid.api.common.OrcidApiConstants;
import org.orcid.jaxb.model.notification.Notification;

/**
 * 
 * @author Will Simpson
 *
 */
@Provider
@Produces(value = { OrcidApiConstants.VND_ORCID_JSON, OrcidApiConstants.ORCID_JSON, MediaType.APPLICATION_JSON })
public class NotificationJsonMessageBodyWriter implements MessageBodyWriter<Notification> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Notification.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Notification notification, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        OrcidJacksonJaxbJsonProviderPretty jaxbJsonProvider = new OrcidJacksonJaxbJsonProviderPretty();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            jaxbJsonProvider.writeTo(notification, type, genericType, annotations, mediaType, null, bos);
        } catch (IOException e) {
            throw new RuntimeException("Problem getting size of notification body", e);
        }
        return bos.size();
    }

    @Override
    public void writeTo(Notification notification, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OrcidJacksonJaxbJsonProviderPretty jaxbJsonProvider = new OrcidJacksonJaxbJsonProviderPretty();
        jaxbJsonProvider.writeTo(notification, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

}
