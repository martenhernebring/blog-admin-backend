package se.hernebring.blog.admin.backend.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.hernebring.blog.admin.backend.exception.AlreadyAddedException;
import se.hernebring.blog.admin.backend.exception.EmptyFileException;
import se.hernebring.blog.admin.backend.exception.FileReadingException;
import se.hernebring.blog.admin.backend.exception.NotAnImageException;
import se.hernebring.blog.admin.backend.image.file.FileDTO;
import se.hernebring.blog.admin.backend.image.file.model.File;
import se.hernebring.blog.admin.backend.image.file.FileRepository;
import se.hernebring.blog.admin.backend.image.multipart.BadIOMultiPart;
import se.hernebring.blog.admin.backend.image.multipart.ContentMultiPart;
import se.hernebring.blog.admin.backend.image.multipart.CorrectMultiPart;
import se.hernebring.blog.admin.backend.image.multipart.EmptyMultiPart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.hc.core5.http.ContentType.TEXT_HTML;
import static org.apache.hc.core5.http.ContentType.TEXT_PLAIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  @Mock
  private ImageRepository mockedImageRepository;

  @Mock
  private FileRepository mockedFileRepository;

  @InjectMocks
  private ImageService imageServiceTest;

  private String h;

  @BeforeEach
  void setUp() {
    h = "/INRIKES/2022/1617/ekonomi/swaggerimage.png";
  }

  @Test
  void getAllFilesUnsorted() {
    List<File> files = new ArrayList<>();
    files.add(new File("inrikes/2022/ekonomi/1617/swaggerimage.png"));
    files.add(new File("inrikes/2022/ekonomi/1617/mock.png"));
    when(mockedFileRepository.findAll()).thenReturn(files);
    assertFalse(imageServiceTest.getAllUnsorted().isEmpty());
  }

  @Test
  void shouldThrowEmptyFileException() {
    assertThrows(EmptyFileException.class,
        () -> imageServiceTest.save(h, new EmptyMultiPart()));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithPlainText() {
    assertThrows(NotAnImageException.class,
        () -> imageServiceTest.save(h, new ContentMultiPart(TEXT_PLAIN)));
  }

  @Test
  void shouldThrowNotAnImageExceptionWithHtml() {
    assertThrows(NotAnImageException.class,
        () -> imageServiceTest.save(h, new ContentMultiPart(TEXT_HTML)));
  }

  @Test
  void shouldThrowAlreadyAddedException() {
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(true);
    assertThrows(AlreadyAddedException.class,
        () -> imageServiceTest.save(h, new CorrectMultiPart()));
  }

  @Test
  void shouldThrowFileReadingExceptionWhenSaving() throws IOException {
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    doThrow(new IOException()).when(mockedImageRepository).save(any(String.class), any(MultipartFile.class));
    assertThrows(FileReadingException.class,
        () -> imageServiceTest.save(h, new BadIOMultiPart()));
  }

  @Test
  void shouldReturnFileDto() {
    MultipartFile multiFile = new CorrectMultiPart();
    File model = new File(h + multiFile.getOriginalFilename());
    when(mockedFileRepository.existsByFilePath(any(String.class))).thenReturn(false);
    when(mockedFileRepository.save(any())).thenReturn(model);
    FileDTO dto = imageServiceTest.save(h, multiFile);
    assertEquals(model.getTime(), dto.getTime());
  }

  @Test
  void shouldThrowFileReadingExceptionWhenDownloading() throws IOException {
    when(mockedImageRepository.download(any(String.class))).thenThrow(new IOException());
    assertThrows(FileReadingException.class,
        () -> imageServiceTest.get(h, "swaggerimage.png"));
  }

  @Test
  void shouldDownloadAnImage() throws IOException {
    when(mockedImageRepository.download(any(String.class))).thenReturn(new byte[1]);
    var bytes = imageServiceTest.get(h, "swaggerimage.png");
    assertTrue(bytes.length > 0);
  }

  @Test
  void deletingFilesNotInUseIsPossible() {
    String filePath = "inrikes/2022/ekonomi/1617/swaggerimage.png";
    imageServiceTest.deleteByFilePath(filePath);
    verify(mockedFileRepository, times(1)).deleteAll(any());
  }

}