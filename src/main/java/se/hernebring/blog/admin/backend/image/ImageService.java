package se.hernebring.blog.admin.backend.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.exception.AlreadyAddedException;
import se.hernebring.blog.admin.backend.exception.EmptyFileException;
import se.hernebring.blog.admin.backend.exception.FileReadingException;
import se.hernebring.blog.admin.backend.exception.NotAnImageException;
import se.hernebring.blog.admin.backend.image.file.FileDTO;
import se.hernebring.blog.admin.backend.image.file.FileRepository;
import se.hernebring.blog.admin.backend.image.file.model.File;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.hc.core5.http.ContentType.*;

@Service
public class ImageService {

  private final FileRepository fileRepository;
  private final ImageRepository imageRepository;

  @Autowired
  public ImageService(FileRepository metaRepo, ImageRepository imageRepo) {
    this.fileRepository = metaRepo;
    this.imageRepository = imageRepo;
  }

  public List<FileDTO> getAllUnsorted() {
    return fileRepository
        .findAll()
        .stream()
        .map(FileDTO::new)
        .collect(Collectors.toList());
  }

  public FileDTO save(String header, MultipartFile file) {
    validate(file);
    String filePath = header + "/" + file.getOriginalFilename();
    if(fileRepository.existsByFilePath(filePath))
      throw new AlreadyAddedException("Image with path " + filePath + " has already been added");
    File m = persist(header, file);
    return new FileDTO(m.getTime(), m.getFilePath(), m.getMeta());
  }

  private void validate(MultipartFile file) {
    if(file.isEmpty()) {
      throw new EmptyFileException("Cannot upload empty file [ " + file.getSize() + "]!");
    }
    if(!(isImage(file.getContentType()))) {
      throw new NotAnImageException("File must be an image!");
    }
  }

  private File persist(String header, MultipartFile file) {
    File meta;
    try{
      meta = imageRepository.save(header, file);
    } catch (IOException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
    return fileRepository.save(meta);
  }

  private boolean isImage(String contentType) {
    return Stream.of(IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF,
            IMAGE_PNG, IMAGE_SVG, IMAGE_TIFF, IMAGE_WEBP)
        .anyMatch(i -> (i.toString().equalsIgnoreCase(contentType)));
  }

  public byte[] get(String header, String fileName) {
    String filePath = header + "/" + fileName;
    try {
      return imageRepository.download(filePath);
    } catch (IOException e) {
      throw new FileReadingException("Problem with reading file", e);
    }
  }

  public void deleteByFilePath(String filePath) {
    List<File> filesWithPath = fileRepository.findByFilePath(filePath);
    fileRepository.deleteAll(filesWithPath);
  }

}
