package se.hernebring.blog.admin.backend.image;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.exception.AlreadyAddedException;
import se.hernebring.blog.admin.backend.exception.EmptyFileException;
import se.hernebring.blog.admin.backend.exception.NotAnImageException;
import se.hernebring.blog.admin.backend.image.file.FileDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = "se.hernebring.blog.admin.backend.image")
@WebMvcTest(ImageController.class)
public class ImageControllerTest {

  @MockBean
  private ImageService mockedService;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper objectMapper;

  private String baseUrl;
  private String articleUrl;
  private MockMultipartFile mockedMultiFile = null;
  private FileDTO dto;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    baseUrl = "/v1/images";
    articleUrl = baseUrl + "/INRIKES/2022/ekonomi/1617";
    File f = null;
    Path p = Path.of("src/main/resources/static/images/photo-1630666618294-4f22d42c75c1.avif");
    try {
      f = ResourceUtils.getFile(p.toUri());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    try {
      this.mockedMultiFile = new MockMultipartFile(
          "file",
          f.getName(),
          MediaType.MULTIPART_FORM_DATA_VALUE,
          Files.readAllBytes(p)
      );
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    String filePath = articleUrl + "/" + mockedMultiFile.getOriginalFilename();
    dto = new FileDTO(OffsetDateTime.now(), filePath);
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void getAllUnsorted() throws Exception {
    var dtos = List.of(dto);
    when(mockedService.getAllUnsorted()).thenReturn(dtos);
    String actualResponseJson =  mockMvc.perform(get(baseUrl))
        .andExpect(status().is(HttpStatus.OK.value()))
        .andReturn().getResponse().getContentAsString();
    String expectedResultJson = objectMapper.writeValueAsString(dtos);
    assertEquals(expectedResultJson, actualResponseJson);
  }

  @Test
  void postImage() throws Exception {
    when(mockedService.
        save(any(String.class), any(MultipartFile.class))
    ).thenReturn(dto);
    MvcResult mvcResult = mockMvc
        .perform(multipart(articleUrl).file(mockedMultiFile))
        .andExpect(status().isOk())
        .andReturn();

    String actualResponseJson = mvcResult.getResponse().getContentAsString();
    String expectedResultJson = objectMapper.writeValueAsString(dto);
    assertEquals(expectedResultJson, actualResponseJson);
  }

  @Test
  void postImageShouldReturnConflictWhenAlreadyAdded() throws Exception {
    when(mockedService.
        save(any(String.class), any(MultipartFile.class))
    ).thenThrow(new AlreadyAddedException("Test"));
    mockMvc
        .perform(multipart(articleUrl).file(mockedMultiFile))
        .andExpect(status().isConflict())
        .andReturn();
  }

  @Test
  void postImageShouldReturnEmptyFileWhenFileWasEmpty() throws Exception {
    when(mockedService.
        save(any(String.class), any(MultipartFile.class))
    ).thenThrow(new EmptyFileException("Test"));
    String aRJ = mockMvc
        .perform(multipart(articleUrl).file(mockedMultiFile))
        .andExpect(status().isBadRequest())
        .andReturn()
        .getResponse()
        .getContentAsString();
    String err = "\"error\":\"";
    String msg = "\",\"message\":\"";
    assertEquals("EmptyFile",
        aRJ.substring(aRJ.indexOf(err) + err.length(), aRJ.indexOf(msg)));
  }

  @Test
  void postImageShouldReturnNotAnImageWhenNotAnImage() throws Exception {
    when(mockedService.
        save(any(String.class), any(MultipartFile.class))
    ).thenThrow(new NotAnImageException("Test"));
    String aRJ = mockMvc
        .perform(multipart(articleUrl).file(mockedMultiFile))
        .andExpect(status().isBadRequest())
        .andReturn()
        .getResponse()
        .getContentAsString();
    String err = "\"error\":\"";
    String msg = "\",\"message\":\"";
    assertEquals("NotAnImage",
        aRJ.substring(aRJ.indexOf(err) + err.length(), aRJ.indexOf(msg)));
  }

  @Test
  void downloadImage() throws Exception {
    mockMvc.perform(get(articleUrl + "/swaggerimage.png"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
  }

  @Test
  void downloadImageUsingDifferentUrl() throws Exception {
    articleUrl = baseUrl + "/INRIKES/2022/politik/1617";
    mockMvc.perform(get(articleUrl + "/swaggerimage.png"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
  }

  @Test
  void downloadImageUsingDifferentYear() throws Exception {
    articleUrl = baseUrl + "/INRIKES/2023/politik/1617";
    mockMvc.perform(get(articleUrl + "/swaggerimage.png"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
  }

  @Test
  void downloadImageUsingDifferentCategory() throws Exception {
    articleUrl = baseUrl + "/UTRIKES/2022/politik/1617";
    mockMvc.perform(get(articleUrl + "/swaggerimage.png"))
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
  }

  @Test
  void deleteFilePathTest() throws Exception {
    mockMvc.perform(delete(baseUrl + "?path=inrikes/2022/ekonomi/1617/swaggerimage.png"))
        .andExpect(status().isOk())
        .andReturn();
  }
}
