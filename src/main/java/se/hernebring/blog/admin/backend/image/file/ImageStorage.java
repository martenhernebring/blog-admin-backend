package se.hernebring.blog.admin.backend.image.file;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.image.ImageRepository;

import java.io.IOException;

@Repository("imageRepository")
public class ImageStorage implements ImageRepository {

  @Override
  public void save(String header, MultipartFile file) throws IOException {
//    var blobServiceClient = new BlobServiceClientBuilder().connectionString("your_connection_string").buildClient();
//    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("image");
//    containerClient.createIfNotExists();
//    BlobClient blobClient = containerClient.getBlobClient(file.getOriginalFilename());
//    blobClient.upload(file.getInputStream());
    throw new UnsupportedOperationException();
  }

  @Override
  public byte[] download(String filePath) throws IOException {
    throw new UnsupportedOperationException();
  }
}
