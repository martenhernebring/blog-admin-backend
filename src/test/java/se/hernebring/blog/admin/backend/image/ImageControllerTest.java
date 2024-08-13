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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
        .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
        .andReturn().getResponse().getContentAsString();
    String expectedResultJson = objectMapper.writeValueAsString(dtos);
    assertEquals(expectedResultJson, actualResponseJson);
  }
}
