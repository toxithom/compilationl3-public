package compiler.sa;

public class SaTypeArray implements SaType {
  private final SaType saType;
  private final int size;

  public SaTypeArray (SaType saType, int size) {
    this.saType = saType;
    this.size = size;
  }

  @Override
  public Type getType () {
    return saType.getType();
  }

  @Override
  public int getSize () {
    return saType.getType().size * size;
  }

  @Override
  public String toString () {
    return getType() + "[]";
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
