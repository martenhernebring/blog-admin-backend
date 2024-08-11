package se.hernebring.blog.admin.backend.exception;

public class AlreadyAddedException extends RuntimeException {
  public AlreadyAddedException(String message) {
    super(message);
  }
}
