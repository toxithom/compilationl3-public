package compiler.util.graph;

import compiler.util.intset.IntSet;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.IntStream;

public class ColorGraph {
  public final int K, R;
  private final Deque<Integer> stack = new ArrayDeque<>();
  public IntSet removed, overflowing;
  public final int[] colors;
  public final Node[] int2Node;
  static final int NOCOLOR = -1;

  public ColorGraph (Graph graph, int K, int[] phi) {
    this.int2Node = graph.nodeArray();
    this.K = K;
    this.removed = new IntSet(graph.nodeCount());
    this.overflowing = new IntSet(graph.nodeCount());
    this.colors = new int[graph.nodeCount()];

    for (int v = 0; v < phi.length; ++v)
      colors[v] = phi[v] >= 0 && phi[v] < K ? phi[v] : NOCOLOR;

    this.R = graph.nodeCount() - (int) IntStream.of(colors).filter(c -> c != NOCOLOR).count();
  }

  public IntSet neighborsColors (int v) {
    var colorSet = new IntSet(K);

    if (int2Node[v].succ() != null)
      for (var successor : int2Node[v].succ())
        if (colors[successor.key] != NOCOLOR) colorSet.add(colors[successor.key]);

    return colorSet;
  }

  public int pickColor (IntSet colorSet) {
    for (int color = 0; color < colorSet.getSize(); ++color)
      if (!colorSet.isMember(color)) return color;

    return NOCOLOR;
  }

  public int neighborsCount (int v) {
    int count = int2Node[v].outDegree();

    if (int2Node[v].succ() != null)
      for (var successor :int2Node[v].succ())
        if (removed.isMember(successor.key)) count--;

    return count;
  }

  public void simplify () {
    boolean updated = true;

    while (stack.size() != R && updated) {
      updated = false;
      for (var node : int2Node) {
        if (removed.isMember(node.key)) continue;
        if (neighborsCount(node.key) < K && colors[node.key] == NOCOLOR) {
          removed.add(node.key);
          stack.push(node.key);
          updated = true;
        }
      }
    }
  }

  public void overflow () {
    while (stack.size() != R) {
      int v = pickVertex();
      stack.push(v);
      removed.add(v);
      overflowing.add(v); // @TODO :: ?
      simplify();
    }
  }

  private int pickVertex () {
    for (int v = 0; v < removed.getSize(); ++v)
      if (!removed.isMember(v) && colors[v] == NOCOLOR) return v;

    return -1;
  }

  public void select () {
    while (!stack.isEmpty()) {
      int v = stack.pop();
      colors[v] = pickColor(neighborsColors(v));
    }
  }

  public ColorGraph color () {
    simplify();
    overflow();
    select();

    return this;
  }

  void print () {
    System.out.println("vertex\tcolor");
    for (int i = 0; i < int2Node.length; i++) {
      System.out.println(i + "\t" + colors[i]);
    }
  }
}
