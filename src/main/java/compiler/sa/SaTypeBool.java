package compiler.sa;

public class SaTypeBool implements SaType {

  @Override
  public Type getType () {
    return Type.BOOL;
  }

  @Override
  public String toString () {
    return Type.BOOL.toString();
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
