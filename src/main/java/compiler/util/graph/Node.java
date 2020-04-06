package compiler.util.graph;

import java.util.Objects;

public class Node {
  Graph mygraph;
  int key;
  NodeList succs;
  NodeList preds;

  private Node () {}

  public Node (Graph g) {
    mygraph = g;
    key = g.nodecount++;
    NodeList p = new NodeList(this, null);
    if (g.mylast == null)
      g.mynodes = g.mylast = p;
    else g.mylast = g.mylast.tail = p;
  }

  public NodeList succ () {
    return succs;
  }

  public NodeList pred () {
    return preds;
  }

  public int label () {
    return key;
  }

  NodeList cat (NodeList a, NodeList b) {
    if (a == null) return b;
    else return new NodeList(a.head, cat(a.tail, b));
  }

  public NodeList adj () {
    return cat(succ(), pred());
  }

  int len (NodeList l) {
    int i = 0;
    for (NodeList p = l; p != null; p = p.tail) i++;
    return i;
  }

  public int inDegree () {
    return len(pred());
  }

  public int outDegree () {
    return len(succ());
  }

  public int degree () {
    return inDegree() + outDegree();
  }

  public boolean goesTo (Node n) {
    return Graph.inList(n, succ());
  }

  public boolean comesFrom (Node n) {
    return Graph.inList(n, pred());
  }

  public boolean adj (Node n) {
    return goesTo(n) || comesFrom(n);
  }

  public String toString () {
    return String.valueOf(key);
  }

  @Override
  public boolean equals (Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Node node = (Node) o;
    return key == node.key;
  }

  @Override
  public int hashCode () {
    return Objects.hash(key);
  }
}
