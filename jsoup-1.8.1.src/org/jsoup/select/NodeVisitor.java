package org.jsoup.select;

import org.jsoup.nodes.Node;

public abstract interface NodeVisitor
{
  public abstract void head(Node paramNode, int paramInt);

  public abstract void tail(Node paramNode, int paramInt);
}

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.NodeVisitor
 * JD-Core Version:    0.6.2
 */