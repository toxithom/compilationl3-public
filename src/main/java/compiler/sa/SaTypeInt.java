package compiler.sa;

public class SaTypeInt implements SaType {

  @Override
  public Type getType () {
    return Type.INT;
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
