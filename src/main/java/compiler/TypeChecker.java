package compiler;

import compiler.sa.*;
import compiler.ts.Ts;

public class TypeChecker extends SaDepthFirstVisitor<Type> {
  private final Ts symbolTable;
  private Type typeExpected;

  public TypeChecker (SaNode root, Ts symbolTable) {
    this.symbolTable = symbolTable;
    root.accept(this);
  }

  @Override
  public Type visit (SaDecFonc node) {
    typeExpected = symbolTable.getFct(node.getNom()).getType();
    if (node.getParametres() != null) node.getParametres().accept(this);
    if (node.getVariable() != null) node.getVariable().accept(this);
    node.getCorps().accept(this);

    return null;
  }

  // STATEMENTS

  @Override
  public Type visit (SaInstBloc node) {
    return node.getVal().accept(this);
  }

  @Override
  public Type visit (SaLInst node) {
    node.getTete().accept(this);
    if (node.getQueue() != null) node.getQueue().accept(this);

    return Type.VOID;
  }

  @Override
  public Type visit (SaInstAffect node) {
    Type t1 = node.getLhs().accept(this);
    Type t2 = node.getRhs().accept(this);
    if (t1 != t2) throw new TypeException(t1);

    return Type.VOID;
  }

  @Override
  public Type visit (SaInstSi node) {
    // @TODO :: allow Type.INT
    if (node.getTest().accept(this) != Type.BOOL) throw new TypeException(Type.BOOL);
    node.getAlors().accept(this);
    if (node.getSinon() != null) node.getSinon().accept(this);

    return Type.VOID;
  }

  @Override
  public Type visit (SaInstTantQue node) {
    // @TODO :: allow Type.INT
    if (node.getTest().accept(this) != Type.BOOL) throw new TypeException(Type.BOOL);
    return node.getFaire().accept(this);
  }

  @Override
  public Type visit (SaInstRetour node) {
    if (node.getVal().accept(this) != typeExpected) throw new TypeException(typeExpected);

    return Type.VOID;
  }

  @Override
  public Type visit (SaInstEcriture node) {
    node.getArg().accept(this);

    return Type.VOID;
  }

  // EXPRESSIONS

  @Override
  public Type visit (SaExpAdd node) {
    return checkArithmeticExpression(node.getOp1(), node.getOp2());
  }

  @Override
  public Type visit (SaExpSub node) {
    return checkArithmeticExpression(node.getOp1(), node.getOp2());
  }

  @Override
  public Type visit (SaExpMult node) {
    return checkArithmeticExpression(node.getOp1(), node.getOp2());
  }

  @Override
  public Type visit (SaExpDiv node) {
    return checkArithmeticExpression(node.getOp1(), node.getOp2());
  }

  private Type checkArithmeticExpression (SaExp op1, SaExp op2) {
    if (op1.accept(this) != Type.INT || op2.accept(this) != Type.INT)
      throw new TypeException(Type.INT);

    return Type.INT;
  }

  @Override
  public Type visit (SaExpEqual node) {
    if (node.getOp1().accept(this) != node.getOp2().accept(this))
      throw new TypeException("expected to be the same type");

    return Type.BOOL;
  }

  @Override
  public Type visit (SaExpInf node) {
    if (node.getOp1().accept(this) != Type.INT || node.getOp2().accept(this) != Type.INT)
      throw new TypeException(Type.INT);

    return Type.BOOL;
  }

  @Override
  public Type visit (SaExpNot node) {
    node.getOp1().accept(this);

    return Type.BOOL;
  }

  @Override
  public Type visit (SaExpBool node) {
    return Type.BOOL;
  }

  @Override
  public Type visit (SaExpInt node) {
    return Type.INT;
  }

  @Override
  public Type visit (SaExpAppel node) {
    return node.getVal().accept(this);
  }

  @Override
  public Type visit (SaExpVar node) {
    return node.getVar().accept(this);
  }

  @Override
  public Type visit (SaAppel node) {
    if (node.getArguments() != null) node.getArguments().accept(this);

    return symbolTable.getFct(node.getNom()).getType();
  }

  @Override
  public Type visit (SaVarSimple node) {
    return node.tsItem.getType();
  }

  @Override
  public Type visit (SaVarIndicee node) {
    return node.tsItem.getType();
  }

  public static class TypeException extends RuntimeException {
    public TypeException (Type expected) {
      super("Type Error : " + expected + " expected.");
    }

    public TypeException (String message) {
      super(message);
    }
  }
}
