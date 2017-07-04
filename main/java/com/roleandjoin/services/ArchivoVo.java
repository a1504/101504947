/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.roleandjoin.services;

/**
 *
 * @author alejandro
 */
public class ArchivoVo {
    
    String fileName;
    String contentType;
    int endIndex;
    byte[] byteArchivo;
    int sizeRead;
    int imgWidth = 0;
   
    public ArchivoVo(String fileName, String contentType, int endIndex) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.endIndex = endIndex;
    }
    
    public ArchivoVo() {
		// TODO Auto-generated constructor stub
	}

	public int getSizeRead() {
        return sizeRead;
    }

    public void setSizeRead(int sizeRead) {
        this.sizeRead = sizeRead;
    }
    

    public byte[] getByteArchivo() {
        return byteArchivo;
    }

    public void setByteArchivo(byte[] byteArchivo) {
        this.byteArchivo = byteArchivo;
    }
    

    public int getEndIndex() {
        return endIndex;
    }

    
    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

	public int getImgWidth() {
		return imgWidth;
	}

	public void setImgWidth(int imgWidth) {
		this.imgWidth = imgWidth;
	}
    
    
    
}
