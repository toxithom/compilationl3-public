package compiler.errors;

public class DeclarationException extends RuntimeException {
  public DeclarationException (String identifier) {
    super(identifier + " is already defined");
  }
}
