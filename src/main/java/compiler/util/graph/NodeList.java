package compiler.util.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NodeList implements Iterable<Node> {
  public Node head;
  public NodeList tail;

  public NodeList (Node h, NodeList t) {
    head = h;
    tail = t;
  }

  private class NodeIterator implements Iterator<Node> {
    private Node _head = head;
    private NodeList _tail = tail;

    @Override
    public boolean hasNext () {
      return _head != null;
    }

    @Override
    public Node next () {
      if (!hasNext()) throw new NoSuchElementException();

      Node next = _head;
      _head = _tail != null ? _tail.head : null;
      _tail = _tail != null ? _tail.tail : null;

      return next;
    }
  }

  @Override
  public Iterator<Node> iterator () {
    return new NodeIterator();
  }
}



