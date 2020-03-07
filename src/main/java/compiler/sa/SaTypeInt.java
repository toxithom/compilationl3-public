package compiler.sa;

public class SaTypeInt implements SaType {

  @Override
  public Type getType () {
    return Type.INT;
  }

  @Override
  public int getSize () {
    return Type.INT.size;
  }

  @Override
  public String toString () {
    return Type.INT.toString();
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
