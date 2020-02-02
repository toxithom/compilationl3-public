package compiler.sa;

import compiler.sc.analysis.DepthFirstAdapter;
import compiler.sc.node.*;

public class Sc2sa extends DepthFirstAdapter {
  private SaNode returnValue;

  public SaNode getRoot () {
    return returnValue;
  }

  @Override
  public void defaultIn (Node node) {
    this.returnValue = null;
  }

  public void caseAProg (AProg node) {
    node.getOptvars().apply(this);
    SaLDec optVars = (SaLDec) returnValue;
    node.getFunctions().apply(this);
    SaLDec functions = (SaLDec) returnValue;

    returnValue = new SaProg(optVars, functions);
  }

  public void caseANotEmptyOptvars (ANotEmptyOptvars node) {
    node.getDvar().apply(this);
    SaDec head = (SaDec) returnValue;
    node.getDvars().apply(this);
    SaLDec tail = (SaLDec) returnValue;

    returnValue = new SaLDec(head, tail);
  }

  public void caseASimpleDvar (ASimpleDvar node) {
    returnValue = new SaDecVar(node.getId().getText());
  }

  public void caseAArrayDvar (AArrayDvar node) {
    returnValue = new SaDecTab(node.getId().getText(), Integer.parseInt(node.getNumber().toString().trim()));
  }

  public void caseAAppendDvars (AAppendDvars node) {
    node.getDvar().apply(this);
    SaDec head = (SaDec) returnValue;
    node.getDvars().apply(this);
    SaLDec tail = (SaLDec) returnValue;

    returnValue = new SaLDec(head, tail);
  }

  public void caseANotEmptyFunctions (ANotEmptyFunctions node) {
    node.getDfunc().apply(this);
    SaDec head = (SaDec) returnValue;
    node.getFunctions().apply(this);
    SaLDec tail = (SaLDec) returnValue;

    returnValue = new SaLDec(head, tail);
  }

  public void caseADfunc (ADfunc node) {
    node.getParameters().apply(this);
    SaLDec params = (SaLDec) returnValue;
    node.getOptvars().apply(this);
    SaLDec vars = (SaLDec) returnValue;
    node.getBlock().apply(this);
    SaInst body = (SaInst) returnValue;

    returnValue = new SaDecFonc(node.getId().getText(), params, vars, body);
  }

  public void caseAListParameters (AListParameters node) {
    node.getDvar().apply(this);
    SaDec head = (SaDec) returnValue;
    node.getDvars().apply(this);
    SaLDec tail = (SaLDec) returnValue;

    returnValue = new SaLDec(head, tail);
  }

  public void caseABlock (ABlock block) {
    block.getStatements().apply(this);
    returnValue = new SaInstBloc((SaLInst) returnValue);
  }

  public void caseAAppendStatements (AAppendStatements node) {
    node.getStatement().apply(this);
    SaInst head = (SaInst) returnValue;
    node.getStatements().apply(this);
    SaLInst tail = (SaLInst) returnValue;

    returnValue = new SaLInst(head, tail);
  }

  public void caseAAssignStatement (AAssignStatement node) {
    node.getAssign().apply(this);
  }

  public void caseAIfStatement (AIfStatement node) {
    node.getIf().apply(this);
  }

  public void caseALoopStatement (ALoopStatement node) {
    node.getWhile().apply(this);
  }

  public void caseACallStatement (ACallStatement node) {
    node.getCall().apply(this);
  }

  public void caseAWriteStatement (AWriteStatement node) {
    node.getWrite().apply(this);
    returnValue = new SaInstEcriture((SaExp) returnValue);
  }

  public void caseAReturnStatement (AReturnStatement node) {
    node.getReturn().apply(this);
  }

  public void caseABlockStatement (ABlockStatement node) {
    node.getBlock().apply(this);
  }

  public void caseAEmptyStatement (AEmptyStatement node) {
    // @TODO : ?
  }

  public void caseAAssign (AAssign node) {
    node.getVar().apply(this);
    SaVar id = (SaVar) returnValue;
    node.getE().apply(this);
    SaExp value = (SaExp) returnValue;

    returnValue = new SaInstAffect(id, value);
  }

  public void caseAIf (AIf node) {
    node.getE().apply(this);
    SaExp exp = (SaExp) returnValue;
    node.getBlock().apply(this);
    SaInst thenBlock = (SaInst) returnValue;
    node.getElse().apply(this);
    SaInst elseBlock = (SaInst) returnValue;

    returnValue = new SaInstSi(exp, thenBlock, elseBlock);
  }

  public void caseAElseElse (AElseElse node) {
    node.getBlock().apply(this);
  }

  public void caseAWhile (AWhile node) {
    node.getE().apply(this);
    SaExp exp = (SaExp) returnValue;
    node.getBlock().apply(this);
    SaInst block = (SaInst) returnValue;

    returnValue = new SaInstTantQue(exp, block);
  }

  public void caseAWrite (AWrite node) {
    node.getE().apply(this);
  }

  public void caseAReturn (AReturn node) {
    node.getE().apply(this);
    returnValue = new SaInstRetour((SaExp) returnValue);
  }

  public void caseAOrE (AOrE node) {
    node.getE().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE1().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpOr(op1, op2);
  }

  public void caseAAndE (AAndE node) {
    node.getE1().apply(this);
  }

  public void caseAAndE1 (AAndE1 node) {
    node.getE1().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE2().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpAnd(op1, op2);
  }

  public void caseAEqualE1 (AEqualE1 node) {
    node.getE2().apply(this);
  }

  public void caseAEqualE2 (AEqualE2 node) {
    node.getE2().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE3().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpEqual(op1, op2);
  }

  public void caseALtE2 (ALtE2 node) {
    node.getE2().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE3().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpInf(op1, op2);
  }

  public void caseAAddE2 (AAddE2 node) {
    node.getE3().apply(this);
  }

  public void caseAAddE3 (AAddE3 node) {
    node.getE3().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE4().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpAdd(op1, op2);
  }

  public void caseASubE3 (ASubE3 node) {
    node.getE3().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE4().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpSub(op1, op2);
  }

  public void caseAFactorE3 (AFactorE3 node) {
    node.getE4().apply(this);
  }

  public void caseAFactorE4 (AFactorE4 node) {
    node.getE4().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE5().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpMult(op1, op2);
  }

  public void caseADivE4 (ADivE4 node) {
    node.getE4().apply(this);
    SaExp op1 = (SaExp) returnValue;
    node.getE5().apply(this);
    SaExp op2 = (SaExp) returnValue;

    returnValue = new SaExpDiv(op1, op2);
  }

  public void caseANotE4 (ANotE4 node) {
    node.getE5().apply(this);
  }

  public void caseANotE5 (ANotE5 node) {
    node.getE5().apply(this);
    returnValue = new SaExpNot((SaExp) returnValue);
  }

  public void caseAParE5 (AParE5 node) {
    node.getE6().apply(this);
  }

  public void caseAParE6 (AParE6 node) {
    node.getE().apply(this);
  }

  public void caseATermE6 (ATermE6 node) {
    node.getTerm().apply(this);
  }

  public void caseANumTerm (ANumTerm node) {
    returnValue = new SaExpInt(Integer.parseInt(node.getNumber().toString().trim()));
  }

  public void caseAVarTerm (AVarTerm node) {
    node.getVar().apply(this);
    returnValue = new SaExpVar((SaVar) returnValue);
  }

  public void caseACallTerm (ACallTerm node) {
    node.getCall().apply(this);
  }

  public void caseAReadTerm (AReadTerm node) {
    node.getRead().apply(this);
  }

  public void caseASimpleVar (ASimpleVar node) {
    returnValue = new SaVarSimple(node.getId().getText());
  }

  public void caseAArrayVar (AArrayVar node) {
    node.getE().apply(this);
    returnValue = new SaVarIndicee(node.getId().getText(), (SaExp) returnValue);
  }

  public void caseACall (ACall node) {
    node.getArguments().apply(this);
    returnValue = new SaAppel(node.getId().getText(), (SaLExp) returnValue);
  }

  public void caseAListArguments (AListArguments node) {
    node.getE().apply(this);
    SaExp head = (SaExp) returnValue;
    node.getEs().apply(this);
    SaLExp tail = (SaLExp) returnValue;

    returnValue = new SaLExp(head, tail);
  }

  public void caseAAppendEs (AAppendEs node) {
    node.getE().apply(this);
    SaExp head = (SaExp) returnValue;
    node.getEs().apply(this);
    SaLExp tail = (SaLExp) returnValue;

    returnValue = new SaLExp(head, tail);
  }

  public void caseARead (ARead node) {
    returnValue = new SaExpLire();
  }
}
