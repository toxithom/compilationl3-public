package compiler.sa;

import compiler.sc.analysis.DepthFirstAdapter;
import compiler.sc.node.*;

public class Sc2sa extends DepthFirstAdapter {
  private SaNode returnValue;

  public SaNode getRoot () {
    return returnValue;
  }

  private SaNode apply (Switchable switchable) {
    switchable.apply(this);
    return returnValue;
  }

  @Override
  public void defaultIn (Node node) {
    this.returnValue = null;
  }

  public void caseAProg (AProg node) {
    returnValue = new SaProg((SaLDec) apply(node.getOptvars()), (SaLDec) apply(node.getFunctions()));
  }

  public void caseANotEmptyOptvars (ANotEmptyOptvars node) {
    returnValue = new SaLDec((SaDec) apply(node.getDvar()), (SaLDec) apply(node.getDvars()));
  }

  public void caseASimpleDvar (ASimpleDvar node) {
    returnValue = new SaDecVar(node.getId().getText(), (SaType) apply(node.getType()));
  }

  public void caseAArrayDvar (AArrayDvar node) {
    returnValue = new SaDecTab(
      node.getId().getText(),
      Integer.parseInt(node.getNumber().toString().trim()),
      new SaTypeArray((SaType) apply(node.getType()), Integer.parseInt(node.getNumber().toString().trim())));
  }

  public void caseABoolType (ABoolType node) {
    returnValue = new SaTypeBool();
  }

  public void caseAIntType (AIntType node) {
    returnValue = new SaTypeInt();
  }

  public void caseAVoidType (AVoidType node) {
    returnValue = new SaTypeVoid();
  }

  public void caseAAppendDvars (AAppendDvars node) {
    returnValue = new SaLDec((SaDec) apply(node.getDvar()), (SaLDec) apply(node.getDvars()));
  }

  public void caseANotEmptyFunctions (ANotEmptyFunctions node) {
    returnValue = new SaLDec((SaDec) apply(node.getDfunc()), (SaLDec) apply(node.getFunctions()));
  }

  public void caseADfunc (ADfunc node) {
    returnValue = new SaDecFonc(
      (SaType) apply(node.getType()),
      node.getId().getText(),
      (SaLDec) apply(node.getParameters()),
      (SaLDec) apply(node.getOptvars()),
      (SaInst) apply(node.getBlock()));
  }

  public void caseAListParameters (AListParameters node) {
    returnValue = new SaLDec((SaDec) apply(node.getDvar()), (SaLDec) apply(node.getDvars()));
  }

  // @FIXME :: empty block case is broken
  public void caseABlock (ABlock block) {
    returnValue = new SaInstBloc((SaLInst) apply(block.getStatements()));
  }

  public void caseAAppendStatements (AAppendStatements node) {
    returnValue = new SaLInst((SaInst) apply(node.getStatement()), (SaLInst) apply(node.getStatements()));
  }

  // @FIXME :: broken case
//  public void caseAEmptyStatements (AEmptyStatements node) {
//    returnValue = new SaLInst(new SaInst() {
//      @Override
//      public <T> T accept (SaVisitor<T> visitor) {
//        return null;
//      }
//    }, null);
//  }

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
    returnValue = new SaInstEcriture((SaExp) apply(node.getWrite()));
  }

  public void caseAReturnStatement (AReturnStatement node) {
    node.getReturn().apply(this);
  }

  public void caseABlockStatement (ABlockStatement node) {
    node.getBlock().apply(this);
  }

  // @FIXME :: seems broken too
//  public void caseAEmptyStatement (AEmptyStatement node) {
//    returnValue = new SaInst() {
//      @Override
//      public <T> T accept (SaVisitor<T> visitor) {
//        return null;
//      }
//    };
//  }

  public void caseAAssign (AAssign node) {
    returnValue = new SaInstAffect((SaVar) apply(node.getVar()), (SaExp) apply(node.getE()));
  }

  public void caseAIf (AIf node) {
    returnValue = new SaInstSi(
      (SaExp) apply(node.getE()), (SaInst) apply(node.getBlock()), (SaInst) apply(node.getElse()));
  }

  public void caseAElseElse (AElseElse node) {
    node.getBlock().apply(this);
  }

  public void caseAWhile (AWhile node) {
    returnValue = new SaInstTantQue((SaExp) apply(node.getE()), (SaInst) apply(node.getBlock()));
  }

  public void caseAWrite (AWrite node) {
    node.getE().apply(this);
  }

  public void caseAReturn (AReturn node) {
    returnValue = new SaInstRetour((SaExp) apply(node.getE()));
  }

  public void caseAOrE (AOrE node) {
    returnValue = new SaExpOr((SaExp) apply(node.getE()), (SaExp) apply(node.getE1()));
  }

  public void caseAAndE (AAndE node) {
    node.getE1().apply(this);
  }

  public void caseAAndE1 (AAndE1 node) {
    returnValue = new SaExpAnd((SaExp) apply(node.getE1()), (SaExp) apply(node.getE2()));
  }

  public void caseAEqualE1 (AEqualE1 node) {
    node.getE2().apply(this);
  }

  public void caseAEqualE2 (AEqualE2 node) {
    returnValue = new SaExpEqual((SaExp) apply(node.getE2()), (SaExp) apply(node.getE3()));
  }

  public void caseALtE2 (ALtE2 node) {
    returnValue = new SaExpInf((SaExp) apply(node.getE2()), (SaExp) apply(node.getE3()));
  }

  public void caseAAddE2 (AAddE2 node) {
    node.getE3().apply(this);
  }

  public void caseAAddE3 (AAddE3 node) {
    returnValue = new SaExpAdd((SaExp) apply(node.getE3()), (SaExp) apply(node.getE4()));
  }

  public void caseASubE3 (ASubE3 node) {
    returnValue = new SaExpSub((SaExp) apply(node.getE3()), (SaExp) apply(node.getE4()));
  }

  public void caseAFactorE3 (AFactorE3 node) {
    node.getE4().apply(this);
  }

  public void caseAFactorE4 (AFactorE4 node) {
    returnValue = new SaExpMult((SaExp) apply(node.getE4()), (SaExp) apply(node.getE5()));
  }

  public void caseADivE4 (ADivE4 node) {
    returnValue = new SaExpDiv((SaExp) apply(node.getE4()), (SaExp) apply(node.getE5()));
  }

  public void caseANotE4 (ANotE4 node) {
    node.getE5().apply(this);
  }

  public void caseANotE5 (ANotE5 node) {
    returnValue = new SaExpNot((SaExp) apply(node.getE5()));
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

  public void caseABooleanTerm (ABooleanTerm node) {
    returnValue = new SaExpBool(Boolean.parseBoolean(node.getBooleanLiteral().toString().trim()));
  }

  public void caseAVarTerm (AVarTerm node) {
    returnValue = new SaExpVar((SaVar) apply(node.getVar()));
  }

  public void caseACallTerm (ACallTerm node) {
    returnValue = new SaExpAppel((SaAppel) apply(node.getCall()));
  }

  public void caseAReadTerm (AReadTerm node) {
    node.getRead().apply(this);
  }

  public void caseASimpleVar (ASimpleVar node) {
    returnValue = new SaVarSimple(node.getId().getText());
  }

  public void caseAArrayVar (AArrayVar node) {
    returnValue = new SaVarIndicee(node.getId().getText(), (SaExp) apply(node.getE()));
  }

  public void caseACall (ACall node) {
    returnValue = new SaAppel(node.getId().getText(), (SaLExp) apply(node.getArguments()));
  }

  public void caseAListArguments (AListArguments node) {
    returnValue = new SaLExp((SaExp) apply(node.getE()), (SaLExp) apply(node.getEs()));
  }

  public void caseAAppendEs (AAppendEs node) {
    returnValue = new SaLExp((SaExp) apply(node.getE()), (SaLExp) apply(node.getEs()));
  }

  public void caseARead (ARead node) {
    returnValue = new SaExpLire();
  }
}
