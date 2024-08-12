package se.hernebring.blog.admin.backend.image.file.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@SecondaryTable(name = "meta",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "meta_id"))
@Table(name = "file")
public class File {

  public File(){}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  private String filePath;
  private OffsetDateTime time;

  public File(String filePath) {
    this.filePath = filePath;
    setTime();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public OffsetDateTime getTime() {
    return time;
  }

  public void setTime() {
    this.time = OffsetDateTime.now();
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

}
