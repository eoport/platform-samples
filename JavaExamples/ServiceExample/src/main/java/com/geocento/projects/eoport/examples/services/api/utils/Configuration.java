package com.geocento.projects.eoport.examples.services.api.utils;

import org.apache.log4j.Logger;

import java.io.FileReader;
import java.util.Properties;

/**
 * A simple class to manage loading the property file containing needed configuration data
 * from the package. Once loaded the configuration is held in memory as a singleton.  Since
 * we already require the simplejpa.properties file to support SimpleJPA, we use that
 * to store additional configuration values.
 */
public class Configuration {

    static public enum APPLICATION_SETTINGS {
        // notifications stuff
        emailReportingPipeId, emailStartupReportingPipeId,
        pathToTmp,
        // otc stuff for S3
        otcAccessKey, otcSecretKey, otcObsEndpoint, bucketName, deliveryURIVhostFormatted,
        productionManagerURL
    };

    static private Properties props=new Properties();

    static private Logger logger = Logger.getLogger(Configuration.class.getName());

    private Configuration() throws Exception {
    	loadConfiguration();
    }
    
    static public void loadConfiguration() throws Exception {
        String home = System.getProperty("user.home");
        System.out.println("HOME: " + home);
        props.load(new FileReader(home + "/configurations/eoportsampleservice.properties"));
    }

    static public String getProperty (String propertyName) {
        return props.getProperty(propertyName);
    }

    static public String getProperty (APPLICATION_SETTINGS propertyName) {
        return getProperty(propertyName.toString());
    }

    static public int getIntProperty (APPLICATION_SETTINGS propertyName) {
        return Integer.parseInt(props.getProperty(propertyName.toString()));
    }

    public static double getDoubleProperty(APPLICATION_SETTINGS propertyName) {
        return Double.parseDouble(props.getProperty(propertyName.toString()));
    }

    // temporary hack
	public static void updateProperty(APPLICATION_SETTINGS propertyName, String propertyValue) {
		props.setProperty(propertyName.toString(), propertyValue);
	}

    public static String getProperty(String rootProperty, String property) {
        return props.getProperty(rootProperty + "." + property);
    }

    public static String getProperty(String rootProperty, APPLICATION_SETTINGS property) {
        return getProperty(rootProperty, property.toString());
    }

    public static boolean getBooleanProperty(APPLICATION_SETTINGS propertyName) {
		String property = props.getProperty(propertyName.toString());
		if(property == null) {
			return false;
		}
		try {
			return Boolean.parseBoolean(property);
		} catch(Exception e) {
			return false;
		}
	}
	
}
