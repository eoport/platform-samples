package com.geocento.projects.eoport.examples.services.api;

import javax.ws.rs.core.Response;

/**
 * Created by thomas on 03/03/2016.
 */
public class WebApplicationException extends javax.ws.rs.WebApplicationException {

    public WebApplicationException(Response.Status status, String message) {
        super(Response.status(status)
                .entity(message)
                .type("text/plain")
                .build());
    }
}
