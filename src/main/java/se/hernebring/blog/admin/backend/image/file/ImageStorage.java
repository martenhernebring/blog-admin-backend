package se.hernebring.blog.admin.backend.image.file;

import org.apache.hc.core5.http.NotImplementedException;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.image.ImageRepository;
import se.hernebring.blog.admin.backend.image.file.model.File;

import java.io.IOException;

@Repository("imageRepository")
public class ImageStorage implements ImageRepository {

  @Override
  public File save(String header, MultipartFile file) throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] download(String filePath) throws IOException {
    throw new UnsupportedOperationException();
  }
}
