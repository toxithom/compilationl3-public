package compiler.sa;

public enum Type {
  VOID(0) , BOOL(1), INT(4);

  public final int size;

  Type (int size) {
    this.size = size;
  }

  @Override
  public String toString () {
    return super.toString().toLowerCase();
  }
}
