package com.geocento.projects.eoport.examples.services.api;

import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;

/**
 * Created by thomas on 20/12/2017.
 */
public abstract class BaseResource {

    static Logger logger;

    protected WebApplicationException handleException(Exception e) {
        return handleException(e, "Server error");
    }
    protected WebApplicationException handleException(Exception e, String message) {
        logger.error(e.getMessage(), e);
        return e instanceof WebApplicationException ? (WebApplicationException) e :
                new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR, message);
    }
}
