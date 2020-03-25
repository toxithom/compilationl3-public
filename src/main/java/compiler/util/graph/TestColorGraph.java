package compiler.util.graph;

public class TestColorGraph {

  @SuppressWarnings("Duplicates")
  public static void main (String[] args) {
    Graph g = new Graph();

    Node n1 = g.newNode();
    Node n2 = g.newNode();
    Node n3 = g.newNode();
    Node n4 = g.newNode();
    Node n5 = g.newNode();
    Node n6 = g.newNode();
    Node n7 = g.newNode();
    Node n8 = g.newNode();
    Node n9 = g.newNode();
    Node n10 = g.newNode();

    g.addEdge(n1, n2);
    g.addEdge(n1, n4);
    g.addEdge(n1, n9);

    g.addEdge(n2, n10);

    g.addEdge(n3, n8);
    g.addEdge(n3, n9);
    g.addEdge(n3, n10);

    g.addEdge(n4, n5);
    g.addEdge(n4, n8);
    g.addEdge(n4, n10);

    g.addEdge(n5, n8);
    g.addEdge(n5, n10);

    g.addEdge(n6, n7);
    g.addEdge(n6, n8);
    g.addEdge(n6, n9);

    g.addEdge(n7, n8);

    g.addEdge(n8, n9);

    g.show(System.out);
    int[] phi = new int[] {-3, -3, -3, 0, -3, 2, 1, -3, -3, -3};
    ColorGraph cg = new ColorGraph(g, 3, phi);
    cg.color();
    cg.print();
  }
}
