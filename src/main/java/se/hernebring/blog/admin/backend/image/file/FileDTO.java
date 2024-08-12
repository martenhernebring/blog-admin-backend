package se.hernebring.blog.admin.backend.image.file;

import se.hernebring.blog.admin.backend.image.file.model.File;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class FileDTO implements Serializable {

  private final OffsetDateTime time;
  private final String filePath;

  public FileDTO(OffsetDateTime time,
                 String filePath) {
    this.time = time;
    this.filePath = filePath;
  }

  public FileDTO(File file) {
    this.time = file.getTime();
    this.filePath = file.getFilePath();
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public String getFilePath() {
    return filePath;
  }

}
