package com.hongcheng.photoalbum.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hongcheng.photoalbum.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>{	
	Page<Image> findAllByUploadedTrueAndDescriptionContainingIgnoreCase(String description, Pageable pageable);
}
