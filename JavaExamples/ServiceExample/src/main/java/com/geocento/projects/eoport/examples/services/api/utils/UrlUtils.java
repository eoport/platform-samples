package com.geocento.projects.eoport.examples.services.api.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * Created by thomas on 18/09/2017.
 */
public class UrlUtils {

    // download file preserving the file name in the URL or in the content-type, if not use the product id
    public static File downloadFileFromHTTP(String urlString, String parentDirectory, String alternativeName) throws Exception {
        return downloadFileFromHTTP(urlString, parentDirectory, alternativeName, null);
    }

    public static File downloadFileFromHTTP(String urlString, String parentDirectory, String productId, String userName, String password) throws Exception {
        String token = userName + ":" + password;
        return downloadFileFromHTTP(urlString, parentDirectory, productId, "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8)));
    }

    public static File downloadFileFromHTTP(String urlString, String parentDirectory, String alternativeName, String token) throws Exception {
        URL url = new URL(urlString);
        // open the connection
        URLConnection connection = url.openConnection();
        if(token != null) {
            connection.setRequestProperty("Authorization", token);
        }
        String filename = getFileNameFromHeaders(connection);
        if (filename == null) {
            filename = alternativeName == null ? (new Date().getTime() + "") : alternativeName;
            boolean hasExtension = !StringUtils.isEmpty(FilenameUtils.getExtension(filename));
            if(!hasExtension) {
                String format = getFormat(connection);
                if (format == null || format.contentEquals("octet-stream")) {
                    format = "zip";
                }
                filename += "." + format;
            }
        }
        // create file in systems temporary directory
        File download = new File(parentDirectory, filename);

        // open the stream and download
        ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(download);
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } finally {
            fos.close();
        }
        return download;
    }

    public static String getFormat(URLConnection connection) {
        String format = connection.getContentType();
        if(format == null) {
            return null;
        }
        return format.substring(format.lastIndexOf("/") + 1);
    }

    public static String getFileNameFromHeaders(URLConnection connection) {
        String fieldValue = connection.getHeaderField("Content-Disposition");
        String option = "filename=";
        if (fieldValue == null || ! fieldValue.contains(option)) {
            return null;
        }
        // check if has quote or not
        int index = 0;
        // parse the file name from the header field
        String filename = fieldValue.substring(fieldValue.indexOf(option) + option.length(), fieldValue.length());
        // check for quotes
        if(filename.contains("\"")) {
            filename = filename.replaceAll("\"", "");
        }
        filename = filename.trim();
        return filename;
    }

    public static void downloadFileFromHTTP(String urlString, File file, String token) throws Exception {
        URL url = new URL(urlString);
        // open the connection
        URLConnection connection = url.openConnection();
        if(token != null) {
            connection.setRequestProperty("Authorization", token);
        }
        // open the stream and download
        ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } finally {
            fos.close();
        }
    }

    public static String buildUrl(String baseUrl, String... path) {
        String url = baseUrl;
        for(String pathElement : path) {
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

    public static String getNameFromPath(URI uri) {
        // try with the path instead...
        String path = uri.getPath();
        if(path.endsWith("/")) {
            return null;
        }
        String lastSegment = path.substring(path.lastIndexOf('/') + 1);
        return lastSegment;
    }
}
