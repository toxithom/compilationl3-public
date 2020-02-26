package compiler.sa;

public class SaExpBool implements SaExp {
  public final boolean value;
  public Type type = Type.BOOL;

  public SaExpBool (boolean value) {
    this.value = value;
  }

  @Override
  public Type getType () {
    return type;
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
