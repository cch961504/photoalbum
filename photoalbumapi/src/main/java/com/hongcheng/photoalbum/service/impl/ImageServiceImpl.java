package com.hongcheng.photoalbum.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hongcheng.photoalbum.dao.ImageRepository;
import com.hongcheng.photoalbum.entity.Image;
import com.hongcheng.photoalbum.exeption.FileNotAllowedException;
import com.hongcheng.photoalbum.service.ImageService;
import com.hongcheng.photoalbum.util.AWSService;
import com.hongcheng.photoalbum.util.FileUtils;

@Service
public class ImageServiceImpl extends AWSService implements ImageService {
	@Autowired
	ImageRepository imageRepo;
	
	public void setImageRepo(ImageRepository imageRepo) {
		this.imageRepo = imageRepo;
	}

	@Value( "${file.upload-dir}" )
	private String uploadDir;

	public List<Image> getImages() {
		return imageRepo.findAll();
	}

	public Image uploadImage(String description, MultipartFile file) throws Exception {

		if (file == null)
			throw new FileNotFoundException();
		String filename = "";
		String extension = "";
		try{
			String[] origFileName = file.getOriginalFilename().split("\\.");
			filename = origFileName[0];
			extension = origFileName[1];
			extension = extension.toLowerCase();
		}catch(Exception e) {}
		if ("png".equals(extension)) {
			if(!file.getContentType().equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE)) {
				throw new FileNotAllowedException("Wrong Media Type");
			}
		}
		else if ("jpg".equals(extension) || "jpeg".equals(extension)) {
			if(!file.getContentType().equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE)) {
				throw new FileNotAllowedException("Wrong Media Type");
			}
			extension = "jpg";
		}
		else {
			throw new FileNotAllowedException("Wrong Media Type");
		}
		Long size = file.getSize();
		if (size > 500*1024)
			throw new FileNotAllowedException("Exceed maximum image size");
		
		StringBuilder S3FileName = new StringBuilder();
		S3FileName.append(new Date().getTime());
		Image image = new Image();
		image.setDescription(description);
		image.setSize(size);
		image.setType(extension);
		imageRepo.save(image);
		try {
			image.setFilename(File.createTempFile(filename+"_","").getName()+"."+extension);
			storeFile(file, image.getFilename());
		}catch(IOException e) {
			imageRepo.delete(image);
			return null;
		}
		image.setUploaded(true);
		imageRepo.save(image);
		return image;
	}
	
	@Async
	public void storeFile(MultipartFile file, String fileName) throws IOException {
		File f = new File(new File(uploadDir), fileName);
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(file.getBytes());
		fos.close();
		// Put on S3
        getClient().putObject(new PutObjectRequest(getBucketName(), fileName, f)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        f.delete();
	}
	
	public String getURL(String fileName) {
		java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(this.getBucketName(), fileName)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = getClient().generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
	}

	@Override
	public Map<String, Object> listImages(String description, Long pageNum) {
		Page<Image> page = imageRepo.findAllByUploadedTrueAndDescriptionContainingIgnoreCase(description, 
				PageRequest.of(pageNum.intValue(), 5, Sort.by("dateCreated").descending()));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", pageNum);
		map.put("total", page.getTotalPages());
		List<Image> list = page.toList();
		list.forEach((image)->{
			image.setUrl(getURL(image.getFilename()));
		});
		map.put("content", list);
		return map;
	}
}
