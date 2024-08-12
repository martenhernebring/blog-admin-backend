package se.hernebring.blog.admin.backend.image;

import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.image.file.ImageStorage;
import se.hernebring.blog.admin.backend.image.file.model.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Disabled
public class ImageStorageIT {

  @Autowired
  ImageStorage imageStorage;

  @Test
  void saveFile() {
    java.io.File initialFile = null;
    FileInputStream input = null;
    try {
      initialFile = ResourceUtils
          .getFile("classpath:static/images/photo-1630666618294-4f22d42c75c1.avif");
      input = new FileInputStream(initialFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    }
    MultipartFile multipartFile = null;
    try {
      multipartFile = new MockMultipartFile(initialFile.getName(), initialFile.getName(),
          String.valueOf(ContentType.IMAGE_JPEG), input.readAllBytes());
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    String h = "inrikes/2022/ekonomi/1617";
    try {
      imageStorage.save(h, multipartFile);
    } catch (IOException e) {
      fail();
      e.printStackTrace();
    }
  }
}
