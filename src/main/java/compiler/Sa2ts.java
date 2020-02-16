package compiler;

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
  private Ts scope = globalTable;
  private boolean isParam = false;

  public Sa2ts (SaNode root) {
    root.accept(this);
    Objects.requireNonNull(globalTable.getFct("main"), "main function is not defined");
    if (globalTable.getFct("main").nbArgs > 0) throw new IllegalArgumentException("function main takes no arguments");
  }

  public Ts getTableGlobale () {
    return globalTable;
  }

  private boolean isLocalScope () {
    return scope != globalTable;
  }

  public Void visit (SaDecVar node) {
    TsItemVar itemVar = scope.getVar(node.getNom());
    if (itemVar != null)
      if (isLocalScope())
        throw new RuntimeException(node.getNom() + " is already defined in scope");
      else
        return null; // @TODO :: warn user

    node.tsItem = isParam
      ? scope.addParam(node.getNom())
      : scope.addVar(node.getNom(), 1);

    return null;
  }

  public Void visit (SaDecTab node) {
    if (isLocalScope()) throw new RuntimeException("error : array declaration cannot be local");
    if (globalTable.getVar(node.getNom()) != null) return null; // @TODO :: warn user

    node.tsItem = scope.addVar(node.getNom(), node.getTaille());

    return null;
  }

  public Void visit (SaDecFonc node) {
    if (globalTable.getFct(node.getNom()) != null) throw new RuntimeException(node.getNom() + " is already defined");

    node.tsItem = globalTable.addFct(
      node.getNom(),
      node.getParametres() != null ? node.getParametres().length() : 0,
      new Ts(),
      node);

    scope = node.tsItem.getTable();
    isParam = true;
    if (node.getParametres() != null) node.getParametres().accept(this);
    isParam = false;
    if (node.getVariable() != null) node.getVariable().accept(this);
    node.getCorps().accept(this);
    scope = globalTable;

    return null;
  }

  public Void visit (SaVarSimple node) {
    node.tsItem = scope.variables.getOrDefault(node.getNom(), globalTable.getVar(node.getNom()));
    Objects.requireNonNull(node.tsItem, "reference error : undefined identifier '" + node.getNom() + "'");

    // @TODO :: use size of type
    if (node.tsItem.taille > 1)
      throw new RuntimeException("type error : '" + node.getNom() + "' is an array");

    return null;
  }

  public Void visit (SaVarIndicee node) {
    node.tsItem = scope.variables.getOrDefault(node.getNom(), globalTable.getVar(node.getNom()));
    Objects.requireNonNull(node.tsItem, "reference error : undefined identifier '" + node.getNom() + "'");

    // @TODO :: use size of type
    if (node.tsItem.taille == 1)
      throw new RuntimeException("type error : '" + node.getNom() + "' is not an array");

    return null;
  }

  public Void visit (SaAppel node) {
    node.tsItem = globalTable.getFct(node.getNom());
    Objects.requireNonNull(node.tsItem, "reference error : undefined identifier '" + node.getNom() + "'");

    int argsLength = node.getArguments() == null ? 0 : node.getArguments().length();

    if (argsLength > 0) node.getArguments().accept(this);

    if (argsLength != node.tsItem.nbArgs)
      throw new IllegalArgumentException("illegal arguments error : function " +
        node.getNom() + " takes " + (argsLength > 0 ? argsLength : "no") + " arguments");

    return null;
  }
}
