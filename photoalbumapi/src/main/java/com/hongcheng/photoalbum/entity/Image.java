package com.hongcheng.photoalbum.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Data
public class Image {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String filename;
	
	private String description;
	
	private String type;
	
	private long size;
	
	private boolean uploaded;

    @CreationTimestamp
	private Date dateCreated;

    @UpdateTimestamp
	private Date lastUpdated;

    @Transient
    private String url;
}
