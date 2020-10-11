package com.hongcheng.photoalbum.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.hongcheng.photoalbum.entity.Image;

public interface ImageService {
	public List<Image> getImages();
	
	public Image uploadImage(String description, MultipartFile file) throws Exception;

	public Map<String, Object> listImages(String description, Long page);
}
