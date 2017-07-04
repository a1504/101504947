/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roleandjoin.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.FormParam;

/**
 *
 * @author alejandro
 */
public class UtilidadesDeArchivo {

    private static final String strFileName = "filename=\"";
    private static final String strContentType = "Content-Type: ";
    public static final int SIZE_MAX_FILE = 15000000;

    private static ArchivoVo getDescriptorDeArchivo(String strBuffer) {
        int index1 = strBuffer.indexOf(strFileName);
        int index2 = strBuffer.indexOf("\"", index1 + strFileName.length());
        String fileName = strBuffer.substring(index1 + strFileName.length(), index2);
        index1 = strBuffer.indexOf(strContentType, index2);
        index2 = strBuffer.indexOf("\n", index1 + strContentType.length());
        String contentType = strBuffer.substring(index1 + strContentType.length(), index2);
        ArchivoVo descriptorDeArchivo = new ArchivoVo(fileName, contentType, index2);
        return descriptorDeArchivo;
    }

    public static ArchivoVo getArchivoFromStream(InputStream stream)
            throws IOException {

        ArchivoVo archivoVo = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int wasRead = 0;
        int off = 0;
        int sizeBuffer = 1000;
        int sizeRead = 0;
        byte[] buffer = new byte[sizeBuffer];
        boolean ini = false;
        boolean ban = false;
        do {
            wasRead = stream.read(buffer);
            if (wasRead > 0) {

                if (!ini) {
                    String strBuffer = new String(buffer);
                    archivoVo = UtilidadesDeArchivo.getDescriptorDeArchivo(strBuffer);
                    off = archivoVo.getEndIndex() + 3;
                }

                sizeRead += sizeBuffer;
                if (sizeRead > SIZE_MAX_FILE) {
                    ban = true;
                    archivoVo.setSizeRead(sizeRead);
                    break;
                }

                ini = true;
                baos.write(buffer, off, (wasRead - off));
                off = 0;

            }
        } while (wasRead > -1);
        if (!ban) {
            archivoVo.setByteArchivo(baos.toByteArray());
            archivoVo.setSizeRead(archivoVo.getByteArchivo().length);
        }
        archivoVo.contentType = archivoVo.getContentType().replaceAll("\r", "");
        //int indexOf = archivoVo.getContentType().indexOf("/");
        return archivoVo;
    }
}
