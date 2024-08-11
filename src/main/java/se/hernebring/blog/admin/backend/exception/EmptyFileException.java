package se.hernebring.blog.admin.backend.exception;

public class EmptyFileException extends RuntimeException{
  public EmptyFileException(String message) {
    super(message);
  }
}
