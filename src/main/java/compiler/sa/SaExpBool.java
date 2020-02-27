package compiler.sa;

public class SaExpBool implements SaExp {
  public final boolean value;

  public SaExpBool (boolean value) {
    this.value = value;
  }

  @Override
  public <T> T accept (SaVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
