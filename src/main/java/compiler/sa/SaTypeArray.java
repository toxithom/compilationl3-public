package compiler.sa;

public class SaTypeArray implements SaType {
  private final SaType saType;

  public SaTypeArray (SaType saType) {
    this.saType = saType;
  }

  @Override
  public Type getType () {
    return saType.getType();
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
