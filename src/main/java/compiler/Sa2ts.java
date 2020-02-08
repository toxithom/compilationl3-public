package compiler;

import compiler.errors.DeclarationException;
import compiler.errors.SyntaxErrorException;
import compiler.sa.SaAppel;
import compiler.sa.SaDecFonc;
import compiler.sa.SaDecTab;
import compiler.sa.SaDecVar;
import compiler.sa.SaDepthFirstVisitor;
import compiler.sa.SaNode;
import compiler.sa.SaVarIndicee;
import compiler.sa.SaVarSimple;
import compiler.ts.Ts;
import compiler.ts.TsItemVar;

import java.util.Objects;

public class Sa2ts extends SaDepthFirstVisitor<Void> {
  private final Ts globalTable = new Ts();
  private Ts currentTable = globalTable;
  private boolean isParam = false;

  public Sa2ts (SaNode root) {
    root.accept(this);
    Objects.requireNonNull(globalTable.getFct("main"), "main function is not defined");
    if (globalTable.getFct("main").nbArgs > 0) throw new RuntimeException("function main takes no arguments");
  }

  public Ts getTableGlobale () {
    return globalTable;
  }

  public Void visit (SaDecVar node) {
    TsItemVar itemVar = currentTable.getVar(node.getNom());
    if (itemVar != null && currentTable != globalTable)
      throw new RuntimeException("var '" + node.getNom() + "' already defined in scope");
    if (itemVar != null && itemVar.portee == globalTable)
      return null; // @TODO :: warn user

    if (isParam)
      node.tsItem = currentTable.addParam(node.getNom());
    else
      node.tsItem = currentTable.addVar(node.getNom(), 1);

    node.tsItem.portee = currentTable;

    return null;
  }

  public Void visit (SaDecTab node) {
    if (currentTable != globalTable) throw new RuntimeException("array declaration cannot be local");
    if (globalTable.getVar(node.getNom()) != null) return null;

    node.tsItem = currentTable.addVar(node.getNom(), node.getTaille());
    node.tsItem.portee = currentTable;

    return null;
  }

  public Void visit (SaDecFonc node) {
    if (globalTable.getFct(node.getNom()) != null)
      throw new DeclarationException(node.getNom());

    node.tsItem = globalTable.addFct(
      node.getNom(),
      node.getParametres() != null ? node.getParametres().length() : 0,
      new Ts(),
      node);

    currentTable = node.tsItem.getTable();
    isParam = true;
    if (node.getParametres() != null) node.getParametres().accept(this);
    isParam = false;
    if (node.getVariable() != null) node.getVariable().accept(this);
    node.getCorps().accept(this);
    currentTable = globalTable;

    return null;
  }

  public Void visit (SaVarSimple node) {
    node.tsItem = currentTable.variables.getOrDefault(node.getNom(), globalTable.getVar(node.getNom()));
    Objects.requireNonNull(node.tsItem, "undefined var '" + node.getNom() + "'");

    // @TODO :: use size of type
    if (node.tsItem.taille > 1)
      throw new SyntaxErrorException("syntax error : '" + node.getNom() + "' is an array");

    return null;
  }

  public Void visit (SaVarIndicee node) {
    node.tsItem = currentTable.variables.getOrDefault(node.getNom(), globalTable.getVar(node.getNom()));
    Objects.requireNonNull(node.tsItem, "undefined var '" + node.getNom() + "'");

    // @TODO :: use size of type
    if (node.tsItem.taille == 1)
      throw new SyntaxErrorException("syntax error : '" + node.getNom() + "' is not an array");

    return null;
  }

  public Void visit (SaAppel node) {
    node.tsItem = globalTable.getFct(node.getNom());
    Objects.requireNonNull(node.tsItem, "use of undefined function '" + node.getNom() +"'");

    int argsLength = node.getArguments() == null ? 0 : node.getArguments().length();

    if (argsLength > 0) node.getArguments().accept(this);

    if (argsLength != node.tsItem.nbArgs)
      throw new IllegalArgumentException(
        "function '" + node.getNom() + "' " + node.tsItem.nbArgs + " argument requirement\n" +
        "\t -> " + "given " + argsLength);

    return null;
  }
}
