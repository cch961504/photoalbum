package com.hongcheng.photoalbum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.hongcheng.photoalbum.dao.ImageRepository;
import com.hongcheng.photoalbum.entity.Image;
import com.hongcheng.photoalbum.exeption.FileNotAllowedException;
import com.hongcheng.photoalbum.service.impl.ImageServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServiceTest {

	@Spy
	ImageServiceImpl imageService;
	
	@Mock
    ImageRepository imageRepo;
	
	@BeforeEach
	public void setup() throws Exception {
		when(imageRepo.save(any())).thenAnswer(i -> i.getArgument(0));
		doNothing().when(imageRepo).delete(any());
		imageService = spy(new ImageServiceImpl());
		imageService.setImageRepo(imageRepo);
		doNothing().when(imageService).storeFile(any(), any());
		doReturn("mockUrl").when(imageService).getURL(any());
	}
	@Test
	public void serviceGetImageList() throws Exception {
		Page<Image> page = mock(Page.class);
		List<Image> imgList = new ArrayList<Image>();
		imgList.add(new Image());
		imgList.add(new Image());
		when(page.toList()).thenReturn(imgList);
		when(page.getTotalPages()).thenReturn(1);
		when(imageRepo.findAllByUploadedTrueAndDescriptionContainingIgnoreCase(any(), any()))
			.thenReturn(page);
		Map<String, Object> map = imageService.listImages("SS", Long.valueOf(1));
		List<Image> imgListOut = (List<Image>) map.get("content");
		assertEquals(2, imgListOut.size());
		assertEquals("mockUrl", imgListOut.get(0).getUrl());
	}
    
	@Test
	public void serviceUploadImageTypeAllowed() throws Exception {
		{
		    MockMultipartFile file = new MockMultipartFile(
			        "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, 
			        "Test".getBytes()
			      );
		    	assertNotNull(imageService.uploadImage("Test", file));
		}
		{
		    MockMultipartFile file = new MockMultipartFile(
			        "file", "test.png", MediaType.IMAGE_PNG_VALUE, 
			        "Test".getBytes()
			      );
		    	assertNotNull(imageService.uploadImage("Test", file));
		}
	}
	
	@Test
	public void serviceUploadImageNull() throws Exception {
	    assertThrows(Exception.class, () -> imageService.uploadImage("Test", null));
	}
	
	@Test
	public void serviceUploadImageTypeNotAllowed() throws Exception {
		{
		    MockMultipartFile file = new MockMultipartFile(
			        "file", "test.jpg", MediaType.IMAGE_PNG_VALUE, 
			        "Test".getBytes()
			      );
		    assertThrows(FileNotAllowedException.class, () -> imageService.uploadImage("Test", file));
		}
		{
		    MockMultipartFile file = new MockMultipartFile(
			        "file", "test.txt", MediaType.IMAGE_PNG_VALUE, 
			        "Test".getBytes()
			      );
		    assertThrows(FileNotAllowedException.class, () -> imageService.uploadImage("Test", file));
		}
		{
		    MockMultipartFile file = new MockMultipartFile(
			        "file", "test.png", MediaType.TEXT_HTML_VALUE, 
			        "Test".getBytes()
			      );
		    assertThrows(FileNotAllowedException.class, () -> imageService.uploadImage("Test", file));
		}
	}

	@Test
	public void uploadImageSizeExceed500KNotAllowed() throws Exception {
	    MockMultipartFile file = new MockMultipartFile(
	        "file", "test.png", MediaType.IMAGE_PNG_VALUE,
	        new byte[600*1024]
	      );
	    assertThrows(FileNotAllowedException.class, () -> imageService.uploadImage("Test", file));
	}
}
