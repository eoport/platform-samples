package com.geocento.projects.eoport.examples.services.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.projects.eoport.examples.services.api.dtos.ResponseProduct;
import org.apache.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class ProductionManagerUtil {

    static private String baseUrl = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.productionManagerURL);

    static private Logger logger = Logger.getLogger(ProductionManagerUtil.class);

    public static void notifyDelivery(String downstreamURI, ResponseProduct responseProduct) throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target(downstreamURI)
                .request()
                .buildPost(Entity.json(responseProduct))
                .submit().get();
        if(response.getStatus() >= 300) {
            throw new Exception("Error processing request, message is " + response.readEntity(String.class));
        }
    }

}
