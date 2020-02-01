package compiler.sa;

public interface SaNode{
  <T> T accept(SaVisitor <T> visitor);
}

