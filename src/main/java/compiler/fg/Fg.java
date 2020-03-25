package compiler.fg;

import compiler.nasm.*;
import compiler.util.graph.*;

import java.util.*;
import java.io.*;

public class Fg implements NasmVisitor<Void> {
  public Nasm nasm;
  public Graph graph;
  Map<NasmInst, Node> inst2Node = new HashMap<>();
  Map<Node, NasmInst> node2Inst = new HashMap<>();
  Map<String, NasmInst> label2Inst = new HashMap<>();

  public Fg (Nasm nasm) {
    this.nasm = nasm;
    this.graph = new Graph();

    nasm.listeInst.forEach(this::init);
    nasm.listeInst.forEach(inst -> inst.accept(this));
  }

  private void init (NasmInst inst) {
    if (inst.label != null) label2Inst.put(inst.label.toString(), inst);
    Node node = graph.newNode();
    inst2Node.put(inst, node);
    node2Inst.put(node, inst);
  }

  private void addEdgeToNextInst (NasmInst inst) {
    graph.addEdge(inst2Node.get(inst), inst2Node.get(nasm.listeInst.get(nasm.listeInst.indexOf(inst) + 1)));
  }

  private void addEdgeToLabeledInst (NasmInst inst, NasmLabel label) {
    graph.addEdge(inst2Node.get(inst), inst2Node.get(label2Inst.get(label.val)));
  }

  public Void visit (NasmAdd inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmCall inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJe inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJle inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJne inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJge inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJl inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJg inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmJmp inst) {
    addEdgeToLabeledInst(inst, (NasmLabel) inst.address);
    return null;
  }

  public Void visit (NasmMul inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmDiv inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmOr inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmCmp inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmInst inst) {
    return null;
  }

  public Void visit (NasmNot inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmPop inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmRet inst) {
    return null;
  }

  public Void visit (NasmXor inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmAnd inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmMov inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmPush inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmSub inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmEmpty inst) {
    addEdgeToNextInst(inst);
    return null;
  }

  public Void visit (NasmAddress operand) {
    return null;
  }

  public Void visit (NasmConstant operand) {
    return null;
  }

  public Void visit (NasmLabel operand) {
    return null;
  }

  public Void visit (NasmRegister operand) {
    return null;
  }

  public void affiche (String baseFileName) {
    String fileName;
    PrintStream out = System.out;

    if (baseFileName != null) {
      try {
        fileName = baseFileName + ".fg";
        out = new PrintStream(fileName);
      } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }

    for (NasmInst nasmInst : nasm.listeInst) {
      Node n = this.inst2Node.get(nasmInst);
      out.print(n + " : ( ");
      for (NodeList q = n.succ(); q != null; q = q.tail) {
        out.print(q.head.toString());
        out.print(" ");
      }
      out.println(")\t" + nasmInst);
    }
  }
}
