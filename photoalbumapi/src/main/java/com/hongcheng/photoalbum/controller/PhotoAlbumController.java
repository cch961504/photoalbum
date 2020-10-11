package com.hongcheng.photoalbum.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hongcheng.photoalbum.entity.Image;
import com.hongcheng.photoalbum.exeption.FileNotAllowedException;
import com.hongcheng.photoalbum.service.ImageService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PhotoAlbumController {

	@Autowired
	ImageService imageService;
	
	@GetMapping("/images")
	public List<Image> getImages(){
		return imageService.getImages();
	}
	
	@PostMapping("/list")
	public Map<String, Object> listImages(
			@RequestParam("description") String description,
			@RequestParam("page") Long pageNum){
		return imageService.listImages(description, pageNum);
		
	}
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(
			@RequestParam("description") String description,
			@RequestParam("file") MultipartFile file) {
		try{
			imageService.uploadImage(description, file);
			return new ResponseEntity<String>("SUCCESS", null, HttpStatus.CREATED);
		}
		catch(FileNotAllowedException e) {
			return new ResponseEntity<String>(e.getMessage(), null, HttpStatus.BAD_REQUEST);
		}
		catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
