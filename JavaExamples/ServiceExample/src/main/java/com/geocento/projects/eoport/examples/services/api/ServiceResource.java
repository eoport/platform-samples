package com.geocento.projects.eoport.examples.services.api;


import com.geocento.projects.eoport.examples.services.api.dtos.InputProduct;
import com.geocento.projects.eoport.examples.services.api.dtos.Metadata;
import com.geocento.projects.eoport.examples.services.api.dtos.ResponseProduct;
import com.geocento.projects.eoport.examples.services.api.dtos.UsageReport;
import com.geocento.projects.eoport.examples.services.api.utils.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 *
 * dummy example service
 * the service exposes two end points
 * (1) for receiving a processing request
 *      - when data matching a subscription to the service is available
 *      the production manager calls the end point
 * (2) for receiving a scheduling notification
 *      - a few minutes before the service is to be called a notification is
 *      sent on the scheduler end point. This way the service can prepare all
 *      the necessary resources for the processing, eg provision a kubernetes pod or a VM.
 *
 */
@Path("/service")
public class ServiceResource extends BaseResource {

    @Context
    ServletContext servletContext;

    @Context
    HttpServletResponse response;

    @Context
    SecurityContext securityContext;

    // create the logger
    static {
        logger = Logger.getLogger(ServiceResource.class);
    }

    static String bucketname = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.bucketName);

    public ServiceResource() {
    }

    /**
     * Called for processing new available data
     * The service is not expected to return anything except a 2xx status
     * The call should initiate a process to generate the output product
     * Once the product is generated, the production manager needs to be notified
     *
     * @param inputProduct - gives you all the information for the input product
     */
    @POST
    @Path("/process")
    @Consumes("application/json")
    public void processProduct(InputProduct inputProduct) {
        try {
            // collect the process information for the response
            // field values need to be reused
            final String taskID = inputProduct.getTaskID();
            final String pipelineID = inputProduct.getPipelineID();
            final String dumpID = inputProduct.getDumpID();
            final Boolean taskFinished = inputProduct.getTaskFinished();
            // the downstream URI is the URL the service should send the result back
            final String downstreamURI = inputProduct.getDownstreamURI();

            // for the sake of the demo we just create a thread to send a message back after a while
            // with a real service a task would be stored and a process started
            // when the process is finished the production manager is notified
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        // for the example we just collect data from the metadata and generatye a shape file
                        // with a real service you would go and fetch the product
                        // the product is always stored on S3 OBS so it can be downloaded using the S3 protocol
                        // the otc s3 aws client can be used for this
/*
                        File inputDirectory = new File(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.pathToTmp));
                        File file = S3OBSUtils.downloadFromURI(inputProduct.getObjectURI(), inputDirectory);
*/

                        File downloadedFile = new File(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.pathToTmp), "testfile");
                        FileUtils.copyURLToFile(new URL(inputProduct.getObjectURI()), downloadedFile);

                        // send back a file which is a shapefile of the input coordinates
                        String boundary = inputProduct.getMetadata().get("boundaryCoordinates");
                        boundary = boundary.trim();
                        // remove leading and training CR
                        if(boundary.startsWith("\n")) {
                            boundary = boundary.substring("\n".length());
                        }
                        String[] latLngs = boundary.split(" ");
                        SimpleFeatureType TYPE = DataUtilities.createType(
                                "example", "the_geom:Point:srid=4326," + "name:String");
                        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
                        DefaultFeatureCollection collection = new DefaultFeatureCollection();
                        GeometryFactory geometryFactory
                                = JTSFactoryFinder.getGeometryFactory(null);
                        Arrays.asList(latLngs).stream().map(latLng -> {
                            String[] latLngValue = latLng.split(",");
                            Point point = geometryFactory.createPoint(
                                    new Coordinate(Double.valueOf(latLngValue[1]), Double.valueOf(latLngValue[0])));
                            featureBuilder.add(point);
                            featureBuilder.add("Point for " + taskID);
                            return featureBuilder.buildFeature(null);
                        }).forEach(collection::add);
                        ShapefileDataStoreFactory dataStoreFactory
                                = new ShapefileDataStoreFactory();

                        File directory = new File(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.pathToTmp), taskID);
                        if(!directory.exists()) {
                            directory.mkdirs();
                        }
                        File shapeFile = new File(directory,
                                "test_" + taskID + "_" + new Date().getTime() + ".shp");
                        Map<String, Serializable> params = new HashMap<>();
                        params.put("url", shapeFile.toURI().toURL());
                        params.put("create spatial index", Boolean.TRUE);
                        ShapefileDataStore dataStore
                                = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
                        dataStore.createSchema(TYPE);
                        Transaction transaction = new DefaultTransaction("create");

                        String typeName = dataStore.getTypeNames()[0];
                        SimpleFeatureSource featureSource
                                = dataStore.getFeatureSource(typeName);

                        SimpleFeatureStore featureStore
                                = (SimpleFeatureStore) featureSource;

                        featureStore.setTransaction(transaction);
                        try {
                            featureStore.addFeatures(collection);
                            transaction.commit();
                        } catch (Exception problem) {
                            transaction.rollback();
                            throw new Exception("Could not generate shapefile");
                        } finally {
                            transaction.close();
                        }

                        // now zip and save on S3
                        File zipFile = new File(Configuration.getProperty(Configuration.APPLICATION_SETTINGS.pathToTmp), taskID + ".zip");
                        Utils.zipFiles(zipFile, Arrays.asList(directory.listFiles()));

                        // upload the result to S3
                        String objectURI = S3OBSUtils.uploadFile(bucketname, zipFile);

                        // send notification to the production manager
                        ResponseProduct responseProduct = new ResponseProduct();
                        // copy across the useful IDs
                        responseProduct.setTaskID(taskID);
                        responseProduct.setPipelineID(pipelineID);
                        responseProduct.setDumpID(dumpID);
                        responseProduct.setTaskFinished(taskFinished);
                        // add some metadata if needed
                        Metadata metadata = new Metadata();
                        metadata.put("property", "example");
                        responseProduct.setMetadata(metadata);
                        // set the result URI
                        responseProduct.setObjectURI(objectURI);
                        // add usage information
                        UsageReport usageReport = new UsageReport();
                        // example value
                        usageReport.setAmount("10");
                        usageReport.setRecordDate(new Date());
                        // units need to match how you set up your service
                        usageReport.setUnitType("km2");
                        responseProduct.setUsage(usageReport);

                        // now send the message to the production manager
                        ProductionManagerUtil.notifyDelivery(downstreamURI, responseProduct);

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }, 1000);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     *
     * this end point gets called a few minutes before the process is called
     * this allows the service to provision some hardware beforehand to be ready when needed
     *
     * @param inputProduct
     */
    @POST
    @Path("/schedule")
    @Consumes("application/json")
    public void scheduleProcess(InputProduct inputProduct) {
        try {
            // TODO - start spinnning some new VM or launch some pod...
            logger.info("Received scheduler notification");
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @GET
    @Path("/test")
    public String testService() {
        return "OK";
    }
}
