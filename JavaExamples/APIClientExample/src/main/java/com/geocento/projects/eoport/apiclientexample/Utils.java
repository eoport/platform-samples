package com.geocento.projects.eoport.apiclientexample;

import org.apache.commons.lang3.StringUtils;

public class Utils {

    static public String buildUrl(String baseUrl, String... path) {
        for(String pathElement : path) {
            if(StringUtils.isEmpty(pathElement)) {
                continue;
            }
            if(!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
            if(pathElement.startsWith("/")) {
                pathElement = pathElement.substring(1);
            }
            baseUrl += pathElement;
        }
        return baseUrl;
    }

}
