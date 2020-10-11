package com.hongcheng.photoalbum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import com.hongcheng.photoalbum.dao.ImageRepository;
import com.hongcheng.photoalbum.entity.Image;
import com.hongcheng.photoalbum.exeption.FileNotAllowedException;
import com.hongcheng.photoalbum.service.ImageService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {
	/*
	 * @LocalServerPort private int port;
	 * 
	 */
	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private ImageService mockImageService;

	private MockMvc mockMvc;

	private MockMultipartFile dummyFile = new MockMultipartFile("file", "Test".getBytes());

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		// mockImageService = mock(ImageService.class);
	}

	@Test
	public void getImageList() throws Exception {
		List<Image> imageList = new ArrayList<Image>();
		imageList.add(new Image());
		imageList.add(new Image());
		when(mockImageService.getImages()).thenReturn(imageList);
		mockMvc.perform(get("/images")).andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void imageUploadResponseSuccess() throws Exception {
		when(mockImageService.uploadImage(anyString(),notNull())).thenReturn(new Image());
		mockMvc.perform(multipart("/upload").file(dummyFile)).andExpect(status().isCreated());
	}

	@Test
	public void imageUploadResponse400() throws Exception {
		when(mockImageService.uploadImage(anyString(),any())).thenThrow(new FileNotAllowedException("Test"));
		mockMvc.perform(multipart("/upload").file(dummyFile)).andExpect(status().isBadRequest());
	}

	@Test
	public void imageUploadResponse500() throws Exception {
		when(mockImageService.uploadImage(anyString(),any())).thenThrow(new Exception("Test"));
		mockMvc.perform(multipart("/upload").file(dummyFile)).andExpect(status().isInternalServerError());
	}
}

/*
 * //Mockito.when(mockImageService.getImages()).thenReturn(null); for(String m :
 * new String[] { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE }) {
 * MockMultipartFile file = new MockMultipartFile( "file", "test.png", m,
 * "Test".getBytes() ); MockMvc mockMvc =
 * MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
 * mockMvc.perform(multipart("/upload").file(file))
 * .andExpect(status().isCreated()); }
 */
