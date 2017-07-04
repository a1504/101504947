/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.roleandjoin.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.roleandjoin.gcs.FileAccessGCS;
import com.roleandjoin.gcs.FileGCS;
import com.roleandjoin.gcs.entidad.File;
import com.roleandjoin.gcs.entidad.FileAccess;

/**
 * 
 * @author alejandro
 */
@SuppressWarnings("deprecation")
// @Stateless
@Path("/filetest")
public class ArchivoServicesTest{

	public static final int maximun_size = 1015807;

	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String loadFile(InputStream is,
			@Context HttpServletRequest httpRequest) {
		// BlobstoreService blobstoreService =
		// BlobstoreServiceFactory.getBlobstoreService();
		// BlobKey blobKey = blobstoreService.createGsBlobKey(
		// "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
		// blobstoreService.serve(blobKey, resp);
		Logger.getLogger(ArchivoServicesTest.class.getName()).warning(
				"@@@@@loadFile " + httpRequest.getParameter("blob-key"));
		String resp = "";

		try {

			ArchivoVo archivo = UtilidadesDeArchivo.getArchivoFromStream(is);
			if (archivo.getSizeRead() <= UtilidadesDeArchivo.SIZE_MAX_FILE) {
				byte[] bytes = archivo.getByteArchivo();
				
				ArchivoVo archivoVo = null;
				
				if(archivo.getContentType().contains("image")){
					archivoVo = transformImage(bytes,
							archivo.getContentType());
				}else{
					archivoVo = new ArchivoVo();
					archivoVo.setByteArchivo(bytes);
					archivoVo.setImgWidth(-1);
				}
//				resp += bytes.length;
				byte[] thumbnail = null;
				byte[] originalFile = null;
				if (archivoVo.getImgWidth() > 0
						&& archivoVo.getImgWidth() <= 365) {
					thumbnail = archivoVo.getByteArchivo();
					originalFile = bytes;
				} else {
					originalFile = archivoVo.getByteArchivo();
				}
				AppEngineFile appEngineFile = null;
				String thumbnailBlobKey = null;
				if (thumbnail != null) {
					appEngineFile = writeFile(thumbnail,
							archivo.getContentType(), archivo.getFileName());
					thumbnailBlobKey = appEngineFile != null ? getBlobKeyFormAppFile(appEngineFile)
							: "";
				}

				appEngineFile = writeFile(originalFile,
						archivo.getContentType(), archivo.getFileName());
				resp = createFileEntity(appEngineFile, originalFile.length,
						archivo.getContentType(), archivo.getFileName(),
						thumbnailBlobKey);

				// resp = "" + escribirArchivo(bytes,
				// archivo.getContentType(),archivo.getFileName());
			} else {
				resp = "solo archivos de 15 megas";
				// return Response.status(Response.Status.CONFLICT).entity(new
				// String("Solo archivos de 15 Megas")).build();
			}
		} catch (Exception ex) {
			resp += " " + ex.toString();
			Logger.getLogger(ArchivoServicesTest.class.getName()).log(Level.INFO,
					null, ex);
		} finally {
			// return Response.status(Response.Status.OK).build();
			Logger.getLogger(ArchivoServicesTest.class.getName()).warning(
					"@@@@@loadFile " + resp);
			return resp;
		}
	}

	private String getBlobKeyFormAppFile(AppEngineFile appEngineFile) {
		FileService fileService = FileServiceFactory.getFileService();
		BlobKey blobKey = fileService.getBlobKey(appEngineFile);
		return blobKey.getKeyString();
	}

	private ArchivoVo transformImage(byte[] oldImageData, String typeFile) {
		ArchivoVo archivoVo = null;
		try {
			if (typeFile.contains("image")) {
				archivoVo = new ArchivoVo();
				ImagesService imagesService = ImagesServiceFactory
						.getImagesService();
				Image oldImage = ImagesServiceFactory.makeImage(oldImageData);
				// if ((oldImage.getWidth() * 0.2) > 570) {//430
				if ((oldImage.getWidth() * 0.2) > 365) {// 430
					int newWidth = (int) (oldImage.getWidth() * 0.15);
					int newHeight = (int) (oldImage.getHeight() * 0.15);

					Transform resize = ImagesServiceFactory.makeResize(
							newWidth, newHeight, false);

					// Transform resize =
					// ImagesServiceFactory.makeResize(100,100);

					Image newImage = imagesService.applyTransform(resize,
							oldImage);
					byte[] newImageData = newImage.getImageData();
					archivoVo.setByteArchivo(newImageData);
					archivoVo.setImgWidth(newWidth);
				} else {
					archivoVo.setByteArchivo(oldImageData);
					archivoVo.setImgWidth(400);
				}
				return archivoVo;
			}
		} catch (Exception e) {
			Logger.getLogger(ArchivoServicesTest.class.getName()).warning(
					"@@@@@error en imagen " + e.toString());
		}
		return archivoVo;

	}

	@GET
	@Path("{key}")
	// @Produces("image/png")
	@Produces("application/octet-stream")
	// @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
	public Response getFileOct(@PathParam("key") long key,
			@HeaderParam("Authorization") String auth) {
		return getFile(key, auth,false);
	}

	@GET
	@Path("img/{key}")
	@Produces("image/*")
	public Response getImage(@PathParam("key") long key,
			@HeaderParam("Authorization") String auth) {
		return getFile(key, auth,false);
	}
	
	@GET
	@Path("img/th/{key}")
	@Produces("image/*")
	public Response getImageThumbnail(@PathParam("key") long key,
			@HeaderParam("Authorization") String auth) {
		return getFile(key, auth,true);
	}

	private Response getFile(long key, String auth, boolean getThumbnail) {
		FileAccessGCS accessGCS = new FileAccessGCS();
		FileAccess fileAccess = accessGCS.get(key);
		long keyBlob = 0;

		if (fileAccess == null) {
			keyBlob = key;
		} else if (com.roleandjoin.utils.DateUtils.getSecondsPassed(fileAccess
				.getDateRequest()) > 15) {
			return null;
		} else {
			keyBlob = fileAccess.getFileKey();
			accessGCS.delete(key);
		}
		FileGCS gcs = new FileGCS();
		File blobFile = gcs.getImagen(keyBlob);
		if (blobFile == null)
			return null;
		BlobKey blobKey = null;//new BlobKey(blobFile.getBlobKey());
		if(getThumbnail){
			blobKey = new BlobKey(blobFile.getThumbBlobKey());
		}else{
			blobKey = new BlobKey(blobFile.getBlobKey());
		}

		byte[] imageData = null;
		try {
			com.google.appengine.api.blobstore.BlobstoreService blobStoreService = BlobstoreServiceFactory
					.getBlobstoreService();
			int parts = 0;
			long sizeFile = blobFile.getSizeFile();
			List<byte[]> lstparts;
			if (blobFile.getSizeFile() > maximun_size) {
				int ini = 0;
				int end = 0;
				parts = ((int) (blobFile.getSizeFile() / maximun_size)) + 1;
				sizeFile = maximun_size;
				lstparts = new ArrayList<byte[]>(parts);
				for (int i = 0; i < parts; i++) {
					end += sizeFile;
					lstparts.add(blobStoreService.fetchData(blobKey, ini, end));
					ini += sizeFile;
				}
				imageData = new byte[maximun_size * parts];
				ini = 0;
				end = maximun_size;
				int cont = 0;
				for (int i = 0; i < parts; i++) {
					cont = 0;
					for (int j = ini; j < end; j++) {
						imageData[ini] = lstparts.get(i)[cont];
						ini++;
						cont++;
					}
					end += maximun_size;
				}
			} else
				imageData = blobStoreService.fetchData(blobKey, 0, sizeFile);

		} catch (Exception e) {
			Logger.getLogger(ArchivoServicesTest.class.getName()).warning(
					"@@@@@Error " + e.toString());
		} finally {

			String fileName = blobFile.getName() != null ? blobFile.getName()
					: "";
			String typeFile = blobFile.getTypeFile();
			String typeProduce = "";
			String header = "filename = ";
			if (typeFile.contains("image/")) {
				typeProduce = "image/*";
			} else {
				typeProduce = MediaType.APPLICATION_OCTET_STREAM;
				header = "attachment; " + header;
			}

			return Response.ok(new ByteArrayInputStream(imageData))
					.header("content-disposition", header + fileName).build();
			// return Response.ok(new ByteArrayInputStream(imageData)).build();
		}
		// return Response.ok().build();
	}

	private AppEngineFile writeFile(byte[] arch, String typeFile,
			String fileName) {

		try {

			FileService fileService = FileServiceFactory.getFileService();

			// Create a new Blob file with mime-type "text/plain"
			AppEngineFile file = fileService.createNewBlobFile("text/plain");

			// Open a channel to write to it
			boolean lock = true;
			FileWriteChannel writeChannel = fileService.openWriteChannel(file,
					lock);

			writeChannel.write(ByteBuffer.wrap(arch));

			// Close without finalizing and save the file path for writing later
			writeChannel.closeFinally();
			return file;
			// BlobKey blobKey = fileService.getBlobKey(file);
			// FileGCS fileGCS = new FileGCS();
			// resp = fileGCS.crear(blobKey.getKeyString(), arch.length,
			// typeFile,
			// fileName) + "";

		} catch (Exception e) {
			Logger.getLogger(ArchivoServicesTest.class.getName()).warning(
					"@@@@@Error " + e.toString());
			return null;
		}
	}

	private String createFileEntity(AppEngineFile file, int fileLength,
			String typeFile, String fileName, String thumbnailBlobKey) {
		String resp = "ok";
		FileService fileService = FileServiceFactory.getFileService();
		BlobKey blobKey = fileService.getBlobKey(file);
		FileGCS fileGCS = new FileGCS();
		resp = fileGCS.crear(blobKey.getKeyString(), fileLength, typeFile,
				fileName, thumbnailBlobKey) + "";
		return resp;
	}

}
