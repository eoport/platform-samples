package com.geocento.projects.eoport.examples.services.api.utils;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    public static void zipFiles(File zipFile, List<File> files) throws IOException {
        int buffer = 2048;
        byte data[] = new byte[buffer];
        FileOutputStream fout = new FileOutputStream(zipFile);
        ZipOutputStream zout = new ZipOutputStream(fout);
        for(File file : files) {
            ZipEntry ze = new ZipEntry(file.getName());
            zout.putNextEntry(ze);
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, buffer);
            int size = -1;
            while((size = bufferedInputStream.read(data, 0, buffer)) != -1  ) {
                zout.write(data, 0, size);
            }
            bufferedInputStream.close();
            zout.closeEntry();
        }
        zout.close();
    }

}
