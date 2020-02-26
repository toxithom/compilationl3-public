package compiler.sa;

public class SaTypeBool implements SaType {

  @Override
  public Type getType () {
    return Type.BOOL;
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
