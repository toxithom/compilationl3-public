package compiler.sa;

public class SaTypeVoid implements SaType {

  @Override
  public Type getType () {
    return Type.VOID;
  }

  @Override
  public String toString () {
    return Type.VOID.toString();
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
