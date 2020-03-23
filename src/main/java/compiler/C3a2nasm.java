package compiler;

import compiler.c3a.*;
import compiler.nasm.*;
import compiler.ts.Ts;

public class C3a2nasm implements C3aVisitor<NasmOperand> {
  private Ts scope;
  private final Ts globalTable;
  private final Nasm nasm;
  private final NasmOperand ebp = new NasmRegister(Nasm.REG_EBP, Nasm.REG_EBP);
  private final NasmOperand esp = new NasmRegister(Nasm.REG_ESP, Nasm.REG_ESP);

  public C3a2nasm (C3a c3a, Ts table) {
    globalTable = table;
    nasm = new Nasm(globalTable);
    nasm.setTempCounter(0);
    var eax = nasm.newRegister();
    var ebx = nasm.newRegister();
    eax.colorRegister(Nasm.REG_EAX);
    ebx.colorRegister(Nasm.REG_EBX);

    nasm.ajouteInst(new NasmCall(null, new NasmLabel("main"), ""));
    nasm.ajouteInst(new NasmMov(null, ebx, new NasmConstant(0), "valeur de retour du programme"));
    nasm.ajouteInst(new NasmMov(null, eax, new NasmConstant(1), ""));
    nasm.ajouteInst(new NasmInt(null, ""));

    c3a.listeInst.forEach(inst -> inst.accept(this));
  }

  public Nasm getNasm () {
    return nasm;
  }

  private NasmOperand getLabel (C3aInst inst) {
    return inst.label != null
      ? inst.label.accept(this)
      : null;
  }

  @Override
  public NasmOperand visit (C3aInstFBegin inst) {
    scope = inst.val.getTable();
    nasm.ajouteInst(new NasmPush(new NasmLabel(inst.val.identif), ebp, "sauvegarde la valeur de ebp"));
    nasm.ajouteInst(new NasmMov(null, ebp, esp, "nouvelle valeur de ebp"));
    nasm.ajouteInst(new NasmSub(
      null, esp, new NasmConstant(scope.nbVar() * 4), "allocation des variables locales"));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstReturn inst) {
    nasm.ajouteInst(new NasmMov(
      getLabel(inst),
      new NasmAddress(ebp, '+', new NasmConstant(2)),
      inst.op1.accept(this), "ecriture de la valeur de retour"));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstFEnd inst) {
    nasm.ajouteInst(new NasmAdd(
      getLabel(inst), esp, new NasmConstant(scope.nbVar() * 4), "désallocation des variables locales"));
    nasm.ajouteInst(new NasmPop(null, ebp, "restaure la valeur de ebp"));
    nasm.ajouteInst(new NasmRet(null, ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstCall inst) {
    nasm.ajouteInst(new NasmSub(getLabel(inst), esp, new NasmConstant(4), ""));
    nasm.ajouteInst(new NasmCall(null, inst.op1.accept(this), ""));
    nasm.ajouteInst(new NasmPop(null, inst.result.accept(this), ""));
    nasm.ajouteInst(new NasmAdd(null, esp, new NasmConstant(inst.op1.val.nbArgs * 4), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstParam inst) {
    nasm.ajouteInst(new NasmPush(getLabel(inst), inst.op1.accept(this), ""));

    return null;
  }


  @Override
  public NasmOperand visit (C3aInst inst) {
    return null;
  }

  @Override
  public NasmOperand visit (C3aInstAdd inst) {
    var dest = inst.result.accept(this);
    nasm.ajouteInst(new NasmMov(getLabel(inst), dest, inst.op1.accept(this), ""));
    nasm.ajouteInst(new NasmAdd(null, dest, inst.op2.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstSub inst) {
    var dest = inst.result.accept(this);
    nasm.ajouteInst(new NasmMov(getLabel(inst), dest, inst.op1.accept(this), ""));
    nasm.ajouteInst(new NasmSub(null, dest, inst.op2.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstMult inst) {
    var dest = inst.result.accept(this);
    nasm.ajouteInst(new NasmMov(getLabel(inst), dest, inst.op1.accept(this), ""));
    nasm.ajouteInst(new NasmMul(null, dest, inst.op2.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstDiv inst) {
    var dest = inst.result.accept(this);
    var eax = nasm.newRegister();
    eax.colorRegister(Nasm.REG_EAX);
    var op2 = inst.op2.accept(this);
    var reg = op2 instanceof NasmConstant
      ? nasm.newRegister()
      : op2;

    nasm.ajouteInst(new NasmMov(getLabel(inst), eax, inst.op1.accept(this), ""));
    if (op2 instanceof NasmConstant) nasm.ajouteInst(new NasmMov(null, reg, op2, ""));
    nasm.ajouteInst(new NasmDiv(null, reg, ""));
    nasm.ajouteInst(new NasmMov(null, dest, eax, ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstAffect inst) {
    nasm.ajouteInst(new NasmMov(
      getLabel(inst), inst.result.accept(this), inst.op1.accept(this), "Affect"));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstJump inst) {
    nasm.ajouteInst(new NasmJmp(getLabel(inst), inst.result.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstJumpIfEqual inst) {
    nasm.ajouteInst(new NasmCmp(getLabel(inst), inst.op1.accept(this), inst.op2.accept(this), ""));
    nasm.ajouteInst(new NasmJe(null, inst.result.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstJumpIfNotEqual inst) {
    nasm.ajouteInst(new NasmCmp(getLabel(inst), inst.op1.accept(this), inst.op2.accept(this), ""));
    nasm.ajouteInst(new NasmJne(null, inst.result.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstJumpIfLess inst) {
    nasm.ajouteInst(new NasmCmp(null, inst.op1.accept(this), inst.op2.accept(this), ""));
    nasm.ajouteInst(new NasmJl(getLabel(inst), inst.result.accept(this), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstRead inst) {
    var dest = inst.result.accept(this);
    var eax = nasm.newRegister();
    eax.colorRegister(Nasm.REG_EAX);

    nasm.ajouteInst(new NasmMov(getLabel(inst), eax, new NasmConstant(2), ""));
    nasm.ajouteInst(new NasmCall(null, new NasmLabel("readline"), ""));
    nasm.ajouteInst(new NasmCall(null, new NasmLabel("atoi"), ""));
    nasm.ajouteInst(new NasmMov(null, dest, eax, ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aInstWrite inst) {
    var eax = nasm.newRegister();
    eax.colorRegister(Nasm.REG_EAX);

    nasm.ajouteInst(new NasmMov(getLabel(inst), eax, inst.op1.accept(this), ""));
    nasm.ajouteInst(new NasmCall(null, new NasmLabel("iprintLF"), ""));

    return null;
  }

  @Override
  public NasmOperand visit (C3aFunction oper) {
    return new NasmLabel(oper.toString());
  }

  @Override
  public NasmOperand visit (C3aConstant oper) {
    return new NasmConstant(oper.val);
  }

  @Override
  public NasmOperand visit (C3aLabel oper) {
    return new NasmLabel(oper.toString());
  }

  @Override
  public NasmOperand visit (C3aTemp oper) {
    nasm.setTempCounter(nasm.getTempCounter() + 1);
    return new NasmRegister(oper.num);
  }

  @Override
  public NasmOperand visit (C3aVar oper) {
    var base = oper.item.portee == globalTable
      ? new NasmLabel(oper.item.identif)
      : ebp;

    if (oper.item.portee == globalTable && oper.item.getTaille() == 1)
      return new NasmAddress(base);

    var direction = oper.item.isParam || oper.item.portee == globalTable ? '+' : '-';
    NasmConstant offset;

    if (oper.item.getTaille() > 1)
      offset = new NasmConstant(((C3aConstant) oper.index).val);
    else
      offset = new NasmConstant(oper.item.isParam
        ? 2 + (oper.item.portee.nbArg() - oper.item.getAdresse())
        : 1 + oper.item.getAdresse());

    return new NasmAddress(base, direction, offset);
  }
}
