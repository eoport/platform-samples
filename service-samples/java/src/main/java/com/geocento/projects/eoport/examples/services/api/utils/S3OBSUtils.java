package com.geocento.projects.eoport.examples.services.api.utils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.SignerFactory;
import com.amazonaws.services.s3.model.GetObjectRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class S3OBSUtils {

    static Logger logger = Logger.getLogger(S3OBSUtils.class);

    static String ak = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.otcAccessKey);
    static String sk = Configuration.getProperty(Configuration.APPLICATION_SETTINGS.otcSecretKey);

    static boolean vhostVersion = Configuration.getBooleanProperty(Configuration.APPLICATION_SETTINGS.deliveryURIVhostFormatted);

    public static OtcObsClient getOTCOBSClient() {
        AWSCredentials credentials = new BasicAWSCredentials(ak, sk);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        clientConfig.setSignerOverride("OtcObsSigner");
        SignerFactory.registerSigner("OtcObsSigner", OtcObsSigner.class);
        return new OtcObsClient(credentials, clientConfig);
    }

    public static String uploadFile(String bucketName, File zipFile) throws Exception {
        OtcObsClient otcObsClient = S3OBSUtils.getOTCOBSClient();
        // Create an instance of ObsClient.
        String objectKey = zipFile.getName();
        otcObsClient.putObject(bucketName, objectKey, zipFile);
        return otcObsClient.getResourceUrl(bucketName, objectKey);
    }

    public static File downloadFromURI(String objectURI, File directory) throws URISyntaxException {
        // Create an instance of ObsClient.
        URI uri = new URI(objectURI);
        String bucketname, objectKey;
        if(vhostVersion) {
            String value = uri.getHost();
            int index = value.indexOf(".");
            bucketname = value.substring(0, index);
            objectKey = uri.getPath().substring(1);
        } else {
            List<String> path = Arrays.asList(uri.getPath().split("/"));
            path.remove(0);
            bucketname = path.get(0);
            path.remove(0);
            objectKey = StringUtils.join(path, "/");
        }
        // create file
        // use the object key directly for path and file name
        String fileName = objectKey.substring(objectKey.lastIndexOf("/") + 1);
        File file = new File(directory, fileName);
        file.getParentFile().mkdirs();
        OtcObsClient otcObsClient = S3OBSUtils.getOTCOBSClient();
        otcObsClient.getObject(new GetObjectRequest(bucketname, objectKey), file);
        return file;
    }

}
