package se.hernebring.blog.admin.backend.image;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageRepository {
  void save(String header, MultipartFile file) throws IOException;
  byte[] download(String filePath) throws IOException;
}
