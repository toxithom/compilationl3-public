package compiler.errors;

public class SyntaxErrorException extends RuntimeException {
  public SyntaxErrorException (String message) {
    super(message);
  }
}
