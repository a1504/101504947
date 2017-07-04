package com.roleandjoin.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
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

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.roleandjoin.gcs.FileAccessGCS;
import com.roleandjoin.gcs.FileGCS;
import com.roleandjoin.gcs.entidad.File;
import com.roleandjoin.gcs.entidad.FileAccess;
import com.roleandjoin.utils.TokenIdentifierGenerator;

@Path("file")
public class FileServices {

	private static final String BUCKETNAME = "roleandjoin.appspot.com";
	public static final int maximun_size = 1015807;

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String loadFile(InputStream is,
			@Context HttpServletRequest httpRequest) {
		String resp = "";
		try {
			ArchivoVo archivo = UtilidadesDeArchivo.getArchivoFromStream(is);
			if (archivo.getSizeRead() <= UtilidadesDeArchivo.SIZE_MAX_FILE) {
				String blobKey = generateBlobKey();
				String thumbnailBlobKey = generateBlobKey();
				GcsFilename fileName = null;
				// GcsFilename fileName = new GcsFilename(BUCKETNAME, blobKey);
				byte[] bytes = archivo.getByteArchivo();
				ArchivoVo archivoVo = null;

				if (archivo.getContentType().contains("image")) {
					archivoVo = transformImage(bytes, archivo.getContentType());
				} else {
					archivoVo = new ArchivoVo();
					archivoVo.setByteArchivo(bytes);
					archivoVo.setImgWidth(-1);
				}
				// resp += bytes.length;
				byte[] thumbnail = null;
				byte[] originalFile = null;
				if (archivoVo.getImgWidth() > 0
						&& archivoVo.getImgWidth() <= 365) {
					thumbnail = archivoVo.getByteArchivo();
					originalFile = bytes;
				} else {
					originalFile = archivoVo.getByteArchivo();
				}
				// String thumbnailBlobKey = null;
				if (thumbnail != null) {
					fileName = new GcsFilename(BUCKETNAME, thumbnailBlobKey);
					writeToFile(fileName, thumbnail);
				} else {
					thumbnailBlobKey = null;
				}
				fileName = new GcsFilename(BUCKETNAME, blobKey);
				writeToFile(fileName, originalFile);
				resp = createFileEntity(blobKey, originalFile.length,
						archivo.getContentType(), archivo.getFileName(),
						thumbnailBlobKey);
			} else {
				resp = "solo archivos de 15 megas";
			}
		} catch (Exception ex) {
			resp += " " + ex.toString();
			Logger.getLogger(ArchivoServices.class.getName()).log(Level.INFO,
					null, ex);
		}
		Logger.getLogger(ArchivoServices.class.getName()).warning(
				"@@@@@loadFile " + resp);
		return resp;
	}

	private String generateBlobKey() {
		String idChannel = TokenIdentifierGenerator.nextSessionId();
		return idChannel;
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
			Logger.getLogger(ArchivoServices.class.getName()).warning(
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
		return getFile(key, auth, false);
	}

	@GET
	@Path("img/{key}")
	@Produces("image/*")
	public Response getImage(@PathParam("key") long key,
			@HeaderParam("Authorization") String auth) {
		return getFile(key, auth, false);
	}

	@GET
	@Path("img/th/{key}")
	@Produces("image/*")
	public Response getImageThumbnail(@PathParam("key") long key,
			@HeaderParam("Authorization") String auth) {
		return getFile(key, auth, true);
	}

	@SuppressWarnings("finally")
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
		String blobKey = null;// new BlobKey(blobFile.getBlobKey());
		if (getThumbnail) {
			blobKey = blobFile.getThumbBlobKey();
		} else {
			blobKey = blobFile.getBlobKey();
		}
		byte[] imageData = null;
		try {
			GcsFilename fileName = new GcsFilename(BUCKETNAME, blobKey);
			imageData = readFromFile(fileName);
		} catch (Exception e) {
			Logger.getLogger(ArchivoServices.class.getName()).warning(
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

	private String createFileEntity(String blobKey, int fileLength,
			String typeFile, String fileName, String thumbnailBlobKey) {
		String resp = "ok";
		FileGCS fileGCS = new FileGCS();
		resp = fileGCS.crear(blobKey, fileLength, typeFile, fileName,
				thumbnailBlobKey) + "";
		return resp;
	}

	private final GcsService gcsService = GcsServiceFactory
			.createGcsService(RetryParams.getDefaultInstance());

	private void writeToFile(GcsFilename fileName, byte[] content)
			throws IOException {
		@SuppressWarnings("resource")
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,
				GcsFileOptions.getDefaultInstance());
		outputChannel.write(ByteBuffer.wrap(content));
		outputChannel.close();
	}

	private byte[] readFromFile(GcsFilename fileName) throws IOException {
		int fileSize = (int) gcsService.getMetadata(fileName).getLength();
		ByteBuffer result = ByteBuffer.allocate(fileSize);
		try (GcsInputChannel readChannel = gcsService.openReadChannel(fileName,
				0)) {
			readChannel.read(result);
		}
		return result.array();
	}

	private String readFile(GcsFilename fileName) throws IOException {
		GcsInputChannel readChannel = null;
		BufferedReader reader = null;
		String resp = "";
		try {
			readChannel = gcsService.openReadChannel(fileName, 0);
			reader = new BufferedReader(Channels.newReader(readChannel, "UTF8"));
			String line;

			while ((line = reader.readLine()) != null) {
				resp += line;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return resp;
	}
}
