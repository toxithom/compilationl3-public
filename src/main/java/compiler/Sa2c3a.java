package compiler;

import compiler.c3a.*;
import compiler.sa.*;

public class Sa2c3a extends SaDepthFirstVisitor<C3aOperand> {
  private final C3a c3a = new C3a();

  public Sa2c3a (SaNode saRoot) {
    saRoot.accept(this);
  }

  public C3a getC3a () {
    return c3a;
  }

  @Override
  public C3aOperand visit (SaDecFonc node) {
    c3a.ajouteInst(new C3aInstFBegin(node.tsItem, ""));

    if (node.getParametres() != null) node.getParametres().accept(this);
    if (node.getVariable() != null) node.getVariable().accept(this);
    node.getCorps().accept(this);

    c3a.ajouteInst(new C3aInstFEnd(""));

    return null;
  }

  @Override
  public C3aOperand visit (SaInstEcriture node) {
    c3a.ajouteInst(new C3aInstWrite(node.getArg().accept(this), ""));

    return null;
  }

  @Override
  public C3aOperand visit (SaInstAffect node) {
    c3a.ajouteInst(new C3aInstAffect(node.getRhs().accept(this), node.getLhs().accept(this), ""));

    return null;
  }

  @Override
  public C3aOperand visit (SaVarSimple node) {
    return new C3aVar(node.tsItem, null);
  }

  @Override
  public C3aOperand visit (SaVarIndicee node) {
    return new C3aVar(node.tsItem, node.getIndice().accept(this));
  }

  @Override
  public C3aOperand visit (SaAppel node) {
    return handleCall(node, null);
  }

  private C3aOperand handleCall (SaAppel node, C3aOperand result) {
    var c3aFunction = new C3aFunction(node.tsItem);
    if (node.getArguments() != null) node.getArguments().accept(this);
    c3a.ajouteInst(new C3aInstCall(c3aFunction, result, ""));

    return c3aFunction;
  }

  @Override
  public C3aOperand visit (SaLExp node) {
    c3a.ajouteInst(new C3aInstParam(node.getTete().accept(this), ""));

    return node.getQueue() != null
      ? node.getQueue().accept(this)
      : null;
  }

  @Override
  public C3aOperand visit (SaExpInt node) {
    return new C3aConstant(node.getVal());
  }

  @Override
  public C3aOperand visit (SaExpVar node) {
    return node.getVar().accept(this);
   }

  @Override
  public C3aOperand visit (SaExpAppel node) {
    var tx = c3a.newTemp();
    handleCall(node.getVal(), tx);

    return tx;
  }

  @Override
  public C3aOperand visit (SaInstRetour node) {
    c3a.ajouteInst(new C3aInstReturn(node.getVal().accept(this), ""));

    return null;
  }

  @Override
  public C3aOperand visit (SaInstSi node) {
    var labelFalse = c3a.newAutoLabel();
    var labelNext = c3a.newAutoLabel();

    c3a.ajouteInst(new C3aInstJumpIfEqual(
      node.getTest().accept(this), c3a.False, node.getSinon() != null ? labelFalse : labelNext, ""));
    node.getAlors().accept(this);

    if (node.getSinon() != null) {
      c3a.ajouteInst(new C3aInstJump(labelNext, ""));
      c3a.addLabelToNextInst(labelFalse);
      node.getSinon().accept(this);
    }

    c3a.addLabelToNextInst(labelNext);

    return null;
  }

  @Override
  public C3aOperand visit (SaInstTantQue node) {
    var labelTest = c3a.newAutoLabel();
    var labelExitLoop = c3a.newAutoLabel();

    c3a.addLabelToNextInst(labelTest);
    c3a.ajouteInst(new C3aInstJumpIfEqual(node.getTest().accept(this), c3a.False, labelExitLoop, ""));
    node.getFaire().accept(this);
    c3a.ajouteInst(new C3aInstJump(labelTest, ""));
    c3a.addLabelToNextInst(labelExitLoop);

    return null;
  }

  @Override
  public C3aOperand visit (SaExpNot node) {
    var label = c3a.newAutoLabel();
    var tx = c3a.newTemp();

    c3a.ajouteInst(new C3aInstAffect(c3a.True, tx, ""));
    c3a.ajouteInst(new C3aInstJumpIfEqual(node.getOp1().accept(this), c3a.False, label, ""));
    c3a.ajouteInst(new C3aInstAffect(c3a.False, tx, ""));
    c3a.addLabelToNextInst(label);

    return tx;
  }

  @Override
  @SuppressWarnings("Duplicates")
  public C3aOperand visit (SaExpEqual node) {
    var label = c3a.newAutoLabel();
    var tx = c3a.newTemp();

    c3a.ajouteInst(new C3aInstAffect(c3a.True, tx, ""));
    c3a.ajouteInst(new C3aInstJumpIfEqual(node.getOp1().accept(this), node.getOp2().accept(this), label, ""));
    c3a.ajouteInst(new C3aInstAffect(c3a.False, tx, ""));
    c3a.addLabelToNextInst(label);

    return tx;
  }

  @Override
  @SuppressWarnings("Duplicates")
  public C3aOperand visit (SaExpInf node) {
    var label = c3a.newAutoLabel();
    var tx = c3a.newTemp();

    c3a.ajouteInst(new C3aInstAffect(c3a.True, tx, ""));
    c3a.ajouteInst(new C3aInstJumpIfLess(node.getOp1().accept(this), node.getOp2().accept(this), label, ""));
    c3a.ajouteInst(new C3aInstAffect(c3a.False, tx, ""));
    c3a.addLabelToNextInst(label);

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpAnd node) {
    var labelExit = c3a.newAutoLabel();
    var labelFalse = c3a.newAutoLabel();
    var tx = c3a.newTemp();

    c3a.ajouteInst(new C3aInstJumpIfEqual(node.getOp1().accept(this), c3a.False, labelFalse, ""));
    c3a.ajouteInst(new C3aInstJumpIfEqual(node.getOp2().accept(this), c3a.False, labelFalse, ""));
    c3a.ajouteInst(new C3aInstAffect(c3a.True, tx, ""));
    c3a.ajouteInst(new C3aInstJump(labelExit, ""));
    c3a.addLabelToNextInst(labelFalse);
    c3a.ajouteInst(new C3aInstAffect(c3a.False, tx, ""));
    c3a.addLabelToNextInst(labelExit);

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpOr node) {
    var labelExit = c3a.newAutoLabel();
    var labelTrue = c3a.newAutoLabel();
    var tx = c3a.newTemp();

    c3a.ajouteInst(new C3aInstJumpIfNotEqual(node.getOp1().accept(this), c3a.False, labelTrue, ""));
    c3a.ajouteInst(new C3aInstJumpIfNotEqual(node.getOp2().accept(this), c3a.False, labelTrue, ""));
    c3a.ajouteInst(new C3aInstAffect(c3a.False, tx, ""));
    c3a.ajouteInst(new C3aInstJump(labelExit, ""));
    c3a.addLabelToNextInst(labelTrue);
    c3a.ajouteInst(new C3aInstAffect(c3a.True, tx, ""));
    c3a.addLabelToNextInst(labelExit);

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpAdd node) {
    var tx = c3a.newTemp();
    c3a.ajouteInst(new C3aInstAdd(node.getOp1().accept(this), node.getOp2().accept(this), tx, ""));

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpSub node) {
    var tx = c3a.newTemp();
    c3a.ajouteInst(new C3aInstSub(node.getOp1().accept(this), node.getOp2().accept(this), tx, ""));

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpMult node) {
    var tx = c3a.newTemp();
    c3a.ajouteInst(new C3aInstMult(node.getOp1().accept(this), node.getOp2().accept(this), tx, ""));

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpDiv node) {
    var tx = c3a.newTemp();
    c3a.ajouteInst(new C3aInstDiv(node.getOp1().accept(this), node.getOp2().accept(this), tx, ""));

    return tx;
  }

  @Override
  public C3aOperand visit (SaExpLire node) {
    var tx = c3a.newTemp();
    c3a.ajouteInst(new C3aInstRead(tx, ""));

    return tx;
  }
}
