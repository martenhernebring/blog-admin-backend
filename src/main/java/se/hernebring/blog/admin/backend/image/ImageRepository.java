package se.hernebring.blog.admin.backend.image;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.image.file.model.File;

import java.io.IOException;

public interface ImageRepository {
  File save(String header, MultipartFile file) throws IOException;
  byte[] download(String filePath) throws IOException;
}
