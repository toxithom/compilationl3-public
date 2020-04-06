package compiler.ig;

import compiler.fg.FgSolution;
import compiler.nasm.Nasm;
import compiler.nasm.NasmAddress;
import compiler.nasm.NasmOperand;
import compiler.nasm.NasmRegister;
import compiler.util.graph.ColorGraph;
import compiler.util.graph.Graph;
import compiler.util.graph.Node;
import compiler.util.graph.NodeList;
import compiler.util.intset.IntSet;

import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.IntStream;

public class Ig {
  private final FgSolution fgs;
  private final Nasm nasm;
  public final Graph graph = new Graph();
  private Node[] int2Node;

  public Ig (FgSolution fgs) {
    this.fgs = fgs;
    this.nasm = fgs.nasm;
    build();
  }

  public void build () {
    IntStream.range(0, nasm.getTempCounter()).forEach(dontCare -> graph.newNode());
    int2Node = graph.nodeArray();

    for (var inst : nasm.listeInst) {
      createEdges(fgs.in.get(inst));
      createEdges(fgs.out.get(inst));
    }
  }

  private void createEdges (IntSet intSet) {
    for (int from = 0; from < nasm.getTempCounter() - 1 ; ++from) {
      if (!intSet.isMember(from)) continue;
      for (int to =  from + 1; to < nasm.getTempCounter(); ++to) {
        if (!intSet.isMember(to)) continue;
        graph.addEdge(int2Node[from], int2Node[to]);
        graph.addEdge(int2Node[to], int2Node[from]);
      }
    }
  }

  public int[] getPreColoredTemporaries () {
    int[] colors = new int[nasm.getTempCounter()];

    for (var inst : nasm.listeInst) {
      extractColor(inst.source, colors);
      extractColor(inst.destination, colors);
    }

    return colors;
  }

  private void extractColor (NasmOperand operand, int[] colors) {
    if (operand == null) return;
    if (operand.isGeneralRegister())
      colors[((NasmRegister) operand).val] = ((NasmRegister) operand).color;

    if (operand instanceof NasmAddress) {
      var address = (NasmAddress) operand;
      if (address.base.isGeneralRegister())
        colors[((NasmRegister) address.base).val] = ((NasmRegister) address.base).color;
      if (address.offset != null && address.offset.isGeneralRegister())
        colors[((NasmRegister) address.offset).val] = ((NasmRegister) address.offset).color;
    }
  }

  // @FIXME :: hardcoded K value
  public void allocateRegisters () {
    int[] colors = new ColorGraph(graph, 4, getPreColoredTemporaries()).color().colors;

    for (var inst : nasm.listeInst) {
      allocateRegister(inst.source, colors);
      allocateRegister(inst.destination, colors);
    }
  }

  private void allocateRegister (NasmOperand operand, int[] colors) {
    if (operand == null) return;
    if (operand.isGeneralRegister())
      ((NasmRegister) operand).colorRegister(colors[((NasmRegister) operand).val]);

    if (operand instanceof NasmAddress) {
      var address = (NasmAddress) operand;
      if (address.base.isGeneralRegister())
        ((NasmRegister) address.base).colorRegister(colors[((NasmRegister) address.base).val]);
      if (address.offset != null && address.offset.isGeneralRegister())
        ((NasmRegister) address.offset).colorRegister(colors[((NasmRegister) address.offset).val]);
    }
  }

  public void print (String baseFileName) {
    String fileName;
    PrintStream out = System.out;

    if (baseFileName != null) {
      try {
        fileName = baseFileName + ".ig";
        out = new PrintStream(fileName);
      }

      catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
      }
    }

    for (int i = 0; i < nasm.getTempCounter(); i++) {
      Node n = this.int2Node[i];
      out.print(n + " : ( ");
      for (NodeList q = n.succ(); q != null; q = q.tail) {
        out.print(q.head.toString());
        out.print(" ");
      }
      out.println(")");
    }
  }
}
