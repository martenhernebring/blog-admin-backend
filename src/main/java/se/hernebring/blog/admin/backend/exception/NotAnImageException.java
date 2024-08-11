package se.hernebring.blog.admin.backend.exception;

public class NotAnImageException extends RuntimeException {
  public NotAnImageException(String message) {
    super(message);
  }
}
