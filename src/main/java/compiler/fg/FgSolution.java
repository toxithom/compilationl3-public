package compiler.fg;

import compiler.util.graph.*;
import compiler.nasm.*;
import compiler.util.intset.*;

import java.io.*;
import java.util.*;

public class FgSolution {
  int iterNum = 0;
  public Nasm nasm;
  Fg fg;
  public Map<NasmInst, IntSet> use = new HashMap<>();
  public Map<NasmInst, IntSet> def = new HashMap<>();
  public Map<NasmInst, IntSet> in = new HashMap<>();
  public Map<NasmInst, IntSet> out = new HashMap<>();

  public FgSolution (Nasm nasm, Fg fg) {
    this.nasm = nasm;
    this.fg = fg;

    nasm.listeInst.forEach(this::init);
    computeInAndOut();
  }

  private void init (NasmInst inst) {
    int size = nasm.getTempCounter() - 1;

    in.put(inst, new IntSet(size));
    out.put(inst, new IntSet(size));
    use.put(inst, new IntSet(size));
    def.put(inst, new IntSet(size));

    if (inst.srcUse)
      addOperandToIntSet(inst.source, use.get(inst));
    if (inst.destUse)
      addOperandToIntSet(inst.destination, use.get(inst));
    if (inst.destDef)
      addOperandToIntSet(inst.destination, def.get(inst));
  }

  private void addOperandToIntSet (NasmOperand operand, IntSet intSet) {
    if (operand.isGeneralRegister())
      intSet.add(((NasmRegister) operand).val);

    if (operand instanceof NasmAddress) {
      var address = (NasmAddress) operand;
      if (address.base.isGeneralRegister())
        intSet.add(((NasmRegister) address.base).val);
      if (address.offset != null && address.offset.isGeneralRegister())
        intSet.add(((NasmRegister) address.offset).val);
    }
  }

  private void computeInAndOut () {
    boolean stable;
    Node[] nodes = fg.graph.nodeArray();

    do {
      stable = true;
      iterNum++;

      for (var node : nodes) {
        var inst = fg.node2Inst.get(node);
        var _in = in.get(inst).copy();
        var _out = out.get(inst).copy();

        in.replace(inst, use.get(inst).copy().union(out.get(inst).copy().minus(def.get(inst))));

        if (node.succ() != null)
          for (var successor : node.succ())
            out.get(inst).union(in.get(fg.node2Inst.get(successor)));

        if (stable)
          stable = _in.equal(in.get(inst)) && _out.equal(out.get(inst));
      }
    } while (!stable);
  }

  public void affiche (String baseFileName) {
    String fileName;
    PrintStream out = System.out;

    if (baseFileName != null) {
      try {
        fileName = baseFileName + ".fgs";
        out = new PrintStream(fileName);
      } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }

    out.println("iter num = " + iterNum);
    for (NasmInst nasmInst : this.nasm.listeInst) {
      out.println("use = " + this.use.get(nasmInst) + " def = " + this.def.get(nasmInst) +
        "\tin = " + this.in.get(nasmInst) + "\t \tout = " + this.out.get(nasmInst) + "\t \t" + nasmInst);
    }
  }
}


