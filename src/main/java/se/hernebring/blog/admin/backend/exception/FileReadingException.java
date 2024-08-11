package se.hernebring.blog.admin.backend.exception;

public class FileReadingException extends RuntimeException {
  public FileReadingException(String message, Exception e) {
    super(message, e);
  }
}
