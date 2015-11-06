/*     */ package org.jsoup.nodes;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.parser.Parser;
/*     */ import org.jsoup.select.NodeTraversor;
/*     */ import org.jsoup.select.NodeVisitor;
/*     */ 
/*     */ public abstract class Node
/*     */   implements Cloneable
/*     */ {
/*     */   Node parentNode;
/*     */   List<Node> childNodes;
/*     */   Attributes attributes;
/*     */   String baseUri;
/*     */   int siblingIndex;
/*     */ 
/*     */   protected Node(String baseUri, Attributes attributes)
/*     */   {
/*  33 */     Validate.notNull(baseUri);
/*  34 */     Validate.notNull(attributes);
/*     */ 
/*  36 */     this.childNodes = new ArrayList(4);
/*  37 */     this.baseUri = baseUri.trim();
/*  38 */     this.attributes = attributes;
/*     */   }
/*     */ 
/*     */   protected Node(String baseUri) {
/*  42 */     this(baseUri, new Attributes());
/*     */   }
/*     */ 
/*     */   protected Node()
/*     */   {
/*  49 */     this.childNodes = Collections.emptyList();
/*  50 */     this.attributes = null;
/*     */   }
/*     */ 
/*     */   public abstract String nodeName();
/*     */ 
/*     */   public String attr(String attributeKey)
/*     */   {
/*  72 */     Validate.notNull(attributeKey);
/*     */ 
/*  74 */     if (this.attributes.hasKey(attributeKey))
/*  75 */       return this.attributes.get(attributeKey);
/*  76 */     if (attributeKey.toLowerCase().startsWith("abs:"))
/*  77 */       return absUrl(attributeKey.substring("abs:".length()));
/*  78 */     return "";
/*     */   }
/*     */ 
/*     */   public Attributes attributes()
/*     */   {
/*  86 */     return this.attributes;
/*     */   }
/*     */ 
/*     */   public Node attr(String attributeKey, String attributeValue)
/*     */   {
/*  96 */     this.attributes.put(attributeKey, attributeValue);
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean hasAttr(String attributeKey)
/*     */   {
/* 106 */     Validate.notNull(attributeKey);
/*     */ 
/* 108 */     if (attributeKey.startsWith("abs:")) {
/* 109 */       String key = attributeKey.substring("abs:".length());
/* 110 */       if ((this.attributes.hasKey(key)) && (!absUrl(key).equals("")))
/* 111 */         return true;
/*     */     }
/* 113 */     return this.attributes.hasKey(attributeKey);
/*     */   }
/*     */ 
/*     */   public Node removeAttr(String attributeKey)
/*     */   {
/* 122 */     Validate.notNull(attributeKey);
/* 123 */     this.attributes.remove(attributeKey);
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */   public String baseUri()
/*     */   {
/* 132 */     return this.baseUri;
/*     */   }
/*     */ 
/*     */   public void setBaseUri(final String baseUri)
/*     */   {
/* 140 */     Validate.notNull(baseUri);
/*     */ 
/* 142 */     traverse(new NodeVisitor() {
/*     */       public void head(Node node, int depth) {
/* 144 */         node.baseUri = baseUri;
/*     */       }
/*     */ 
/*     */       public void tail(Node node, int depth)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public String absUrl(String attributeKey)
/*     */   {
/* 173 */     Validate.notEmpty(attributeKey);
/*     */ 
/* 175 */     String relUrl = attr(attributeKey);
/* 176 */     if (!hasAttr(attributeKey))
/* 177 */       return "";
/*     */     try
/*     */     {
/*     */       URL base;
/*     */       try {
/* 182 */         base = new URL(this.baseUri);
/*     */       }
/*     */       catch (MalformedURLException e) {
/* 185 */         URL abs = new URL(relUrl);
/* 186 */         return abs.toExternalForm();
/*     */       }
/*     */ 
/* 189 */       if (relUrl.startsWith("?"))
/* 190 */         relUrl = base.getPath() + relUrl;
/* 191 */       URL abs = new URL(base, relUrl);
/* 192 */       return abs.toExternalForm(); } catch (MalformedURLException e) {
/*     */     }
/* 194 */     return "";
/*     */   }
/*     */ 
/*     */   public Node childNode(int index)
/*     */   {
/* 205 */     return (Node)this.childNodes.get(index);
/*     */   }
/*     */ 
/*     */   public List<Node> childNodes()
/*     */   {
/* 214 */     return Collections.unmodifiableList(this.childNodes);
/*     */   }
/*     */ 
/*     */   public List<Node> childNodesCopy()
/*     */   {
/* 223 */     List children = new ArrayList(this.childNodes.size());
/* 224 */     for (Node node : this.childNodes) {
/* 225 */       children.add(node.clone());
/*     */     }
/* 227 */     return children;
/*     */   }
/*     */ 
/*     */   public final int childNodeSize()
/*     */   {
/* 235 */     return this.childNodes.size();
/*     */   }
/*     */ 
/*     */   protected Node[] childNodesAsArray() {
/* 239 */     return (Node[])this.childNodes.toArray(new Node[childNodeSize()]);
/*     */   }
/*     */ 
/*     */   public Node parent()
/*     */   {
/* 247 */     return this.parentNode;
/*     */   }
/*     */ 
/*     */   public final Node parentNode()
/*     */   {
/* 255 */     return this.parentNode;
/*     */   }
/*     */ 
/*     */   public Document ownerDocument()
/*     */   {
/* 263 */     if ((this instanceof Document))
/* 264 */       return (Document)this;
/* 265 */     if (this.parentNode == null) {
/* 266 */       return null;
/*     */     }
/* 268 */     return this.parentNode.ownerDocument();
/*     */   }
/*     */ 
/*     */   public void remove()
/*     */   {
/* 275 */     Validate.notNull(this.parentNode);
/* 276 */     this.parentNode.removeChild(this);
/*     */   }
/*     */ 
/*     */   public Node before(String html)
/*     */   {
/* 286 */     addSiblingHtml(siblingIndex(), html);
/* 287 */     return this;
/*     */   }
/*     */ 
/*     */   public Node before(Node node)
/*     */   {
/* 297 */     Validate.notNull(node);
/* 298 */     Validate.notNull(this.parentNode);
/*     */ 
/* 300 */     this.parentNode.addChildren(siblingIndex(), new Node[] { node });
/* 301 */     return this;
/*     */   }
/*     */ 
/*     */   public Node after(String html)
/*     */   {
/* 311 */     addSiblingHtml(siblingIndex() + 1, html);
/* 312 */     return this;
/*     */   }
/*     */ 
/*     */   public Node after(Node node)
/*     */   {
/* 322 */     Validate.notNull(node);
/* 323 */     Validate.notNull(this.parentNode);
/*     */ 
/* 325 */     this.parentNode.addChildren(siblingIndex() + 1, new Node[] { node });
/* 326 */     return this;
/*     */   }
/*     */ 
/*     */   private void addSiblingHtml(int index, String html) {
/* 330 */     Validate.notNull(html);
/* 331 */     Validate.notNull(this.parentNode);
/*     */ 
/* 333 */     Element context = (parent() instanceof Element) ? (Element)parent() : null;
/* 334 */     List nodes = Parser.parseFragment(html, context, baseUri());
/* 335 */     this.parentNode.addChildren(index, (Node[])nodes.toArray(new Node[nodes.size()]));
/*     */   }
/*     */ 
/*     */   public Node wrap(String html)
/*     */   {
/* 344 */     Validate.notEmpty(html);
/*     */ 
/* 346 */     Element context = (parent() instanceof Element) ? (Element)parent() : null;
/* 347 */     List wrapChildren = Parser.parseFragment(html, context, baseUri());
/* 348 */     Node wrapNode = (Node)wrapChildren.get(0);
/* 349 */     if ((wrapNode == null) || (!(wrapNode instanceof Element))) {
/* 350 */       return null;
/*     */     }
/* 352 */     Element wrap = (Element)wrapNode;
/* 353 */     Element deepest = getDeepChild(wrap);
/* 354 */     this.parentNode.replaceChild(this, wrap);
/* 355 */     deepest.addChildren(new Node[] { this });
/*     */ 
/* 358 */     if (wrapChildren.size() > 0) {
/* 359 */       for (int i = 0; i < wrapChildren.size(); i++) {
/* 360 */         Node remainder = (Node)wrapChildren.get(i);
/* 361 */         remainder.parentNode.removeChild(remainder);
/* 362 */         wrap.appendChild(remainder);
/*     */       }
/*     */     }
/* 365 */     return this;
/*     */   }
/*     */ 
/*     */   public Node unwrap()
/*     */   {
/* 382 */     Validate.notNull(this.parentNode);
/*     */ 
/* 384 */     int index = this.siblingIndex;
/* 385 */     Node firstChild = this.childNodes.size() > 0 ? (Node)this.childNodes.get(0) : null;
/* 386 */     this.parentNode.addChildren(index, childNodesAsArray());
/* 387 */     remove();
/*     */ 
/* 389 */     return firstChild;
/*     */   }
/*     */ 
/*     */   private Element getDeepChild(Element el) {
/* 393 */     List children = el.children();
/* 394 */     if (children.size() > 0) {
/* 395 */       return getDeepChild((Element)children.get(0));
/*     */     }
/* 397 */     return el;
/*     */   }
/*     */ 
/*     */   public void replaceWith(Node in)
/*     */   {
/* 405 */     Validate.notNull(in);
/* 406 */     Validate.notNull(this.parentNode);
/* 407 */     this.parentNode.replaceChild(this, in);
/*     */   }
/*     */ 
/*     */   protected void setParentNode(Node parentNode) {
/* 411 */     if (this.parentNode != null)
/* 412 */       this.parentNode.removeChild(this);
/* 413 */     this.parentNode = parentNode;
/*     */   }
/*     */ 
/*     */   protected void replaceChild(Node out, Node in) {
/* 417 */     Validate.isTrue(out.parentNode == this);
/* 418 */     Validate.notNull(in);
/* 419 */     if (in.parentNode != null) {
/* 420 */       in.parentNode.removeChild(in);
/*     */     }
/* 422 */     Integer index = Integer.valueOf(out.siblingIndex());
/* 423 */     this.childNodes.set(index.intValue(), in);
/* 424 */     in.parentNode = this;
/* 425 */     in.setSiblingIndex(index.intValue());
/* 426 */     out.parentNode = null;
/*     */   }
/*     */ 
/*     */   protected void removeChild(Node out) {
/* 430 */     Validate.isTrue(out.parentNode == this);
/* 431 */     int index = out.siblingIndex();
/* 432 */     this.childNodes.remove(index);
/* 433 */     reindexChildren();
/* 434 */     out.parentNode = null;
/*     */   }
/*     */ 
/*     */   protected void addChildren(Node[] children)
/*     */   {
/* 439 */     for (Node child : children) {
/* 440 */       reparentChild(child);
/* 441 */       this.childNodes.add(child);
/* 442 */       child.setSiblingIndex(this.childNodes.size() - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addChildren(int index, Node[] children) {
/* 447 */     Validate.noNullElements(children);
/* 448 */     for (int i = children.length - 1; i >= 0; i--) {
/* 449 */       Node in = children[i];
/* 450 */       reparentChild(in);
/* 451 */       this.childNodes.add(index, in);
/*     */     }
/* 453 */     reindexChildren();
/*     */   }
/*     */ 
/*     */   private void reparentChild(Node child) {
/* 457 */     if (child.parentNode != null)
/* 458 */       child.parentNode.removeChild(child);
/* 459 */     child.setParentNode(this);
/*     */   }
/*     */ 
/*     */   private void reindexChildren() {
/* 463 */     for (int i = 0; i < this.childNodes.size(); i++)
/* 464 */       ((Node)this.childNodes.get(i)).setSiblingIndex(i);
/*     */   }
/*     */ 
/*     */   public List<Node> siblingNodes()
/*     */   {
/* 474 */     if (this.parentNode == null) {
/* 475 */       return Collections.emptyList();
/*     */     }
/* 477 */     List nodes = this.parentNode.childNodes;
/* 478 */     List siblings = new ArrayList(nodes.size() - 1);
/* 479 */     for (Node node : nodes)
/* 480 */       if (node != this)
/* 481 */         siblings.add(node);
/* 482 */     return siblings;
/*     */   }
/*     */ 
/*     */   public Node nextSibling()
/*     */   {
/* 490 */     if (this.parentNode == null) {
/* 491 */       return null;
/*     */     }
/* 493 */     List siblings = this.parentNode.childNodes;
/* 494 */     Integer index = Integer.valueOf(siblingIndex());
/* 495 */     Validate.notNull(index);
/* 496 */     if (siblings.size() > index.intValue() + 1) {
/* 497 */       return (Node)siblings.get(index.intValue() + 1);
/*     */     }
/* 499 */     return null;
/*     */   }
/*     */ 
/*     */   public Node previousSibling()
/*     */   {
/* 507 */     if (this.parentNode == null) {
/* 508 */       return null;
/*     */     }
/* 510 */     List siblings = this.parentNode.childNodes;
/* 511 */     Integer index = Integer.valueOf(siblingIndex());
/* 512 */     Validate.notNull(index);
/* 513 */     if (index.intValue() > 0) {
/* 514 */       return (Node)siblings.get(index.intValue() - 1);
/*     */     }
/* 516 */     return null;
/*     */   }
/*     */ 
/*     */   public int siblingIndex()
/*     */   {
/* 526 */     return this.siblingIndex;
/*     */   }
/*     */ 
/*     */   protected void setSiblingIndex(int siblingIndex) {
/* 530 */     this.siblingIndex = siblingIndex;
/*     */   }
/*     */ 
/*     */   public Node traverse(NodeVisitor nodeVisitor)
/*     */   {
/* 539 */     Validate.notNull(nodeVisitor);
/* 540 */     NodeTraversor traversor = new NodeTraversor(nodeVisitor);
/* 541 */     traversor.traverse(this);
/* 542 */     return this;
/*     */   }
/*     */ 
/*     */   public String outerHtml()
/*     */   {
/* 550 */     StringBuilder accum = new StringBuilder(128);
/* 551 */     outerHtml(accum);
/* 552 */     return accum.toString();
/*     */   }
/*     */ 
/*     */   protected void outerHtml(StringBuilder accum) {
/* 556 */     new NodeTraversor(new OuterHtmlVisitor(accum, getOutputSettings())).traverse(this);
/*     */   }
/*     */ 
/*     */   Document.OutputSettings getOutputSettings()
/*     */   {
/* 561 */     return ownerDocument() != null ? ownerDocument().outputSettings() : new Document("").outputSettings();
/*     */   }
/*     */ 
/*     */   abstract void outerHtmlHead(StringBuilder paramStringBuilder, int paramInt, Document.OutputSettings paramOutputSettings);
/*     */ 
/*     */   abstract void outerHtmlTail(StringBuilder paramStringBuilder, int paramInt, Document.OutputSettings paramOutputSettings);
/*     */ 
/*     */   public String toString()
/*     */   {
/* 573 */     return outerHtml();
/*     */   }
/*     */ 
/*     */   protected void indent(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 577 */     accum.append("\n").append(StringUtil.padding(depth * out.indentAmount()));
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 582 */     if (this == o) return true;
/*     */ 
/* 584 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 589 */     int result = this.parentNode != null ? this.parentNode.hashCode() : 0;
/*     */ 
/* 591 */     result = 31 * result + (this.attributes != null ? this.attributes.hashCode() : 0);
/* 592 */     return result;
/*     */   }
/*     */ 
/*     */   public Node clone()
/*     */   {
/* 605 */     Node thisClone = doClone(null);
/*     */ 
/* 608 */     LinkedList nodesToProcess = new LinkedList();
/* 609 */     nodesToProcess.add(thisClone);
/*     */ 
/* 611 */     while (!nodesToProcess.isEmpty()) {
/* 612 */       Node currParent = (Node)nodesToProcess.remove();
/*     */ 
/* 614 */       for (int i = 0; i < currParent.childNodes.size(); i++) {
/* 615 */         Node childClone = ((Node)currParent.childNodes.get(i)).doClone(currParent);
/* 616 */         currParent.childNodes.set(i, childClone);
/* 617 */         nodesToProcess.add(childClone);
/*     */       }
/*     */     }
/*     */ 
/* 621 */     return thisClone;
/*     */   }
/*     */ 
/*     */   protected Node doClone(Node parent)
/*     */   {
/*     */     Node clone;
/*     */     try
/*     */     {
/* 632 */       clone = (Node)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 634 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/* 637 */     clone.parentNode = parent;
/* 638 */     clone.siblingIndex = (parent == null ? 0 : this.siblingIndex);
/* 639 */     clone.attributes = (this.attributes != null ? this.attributes.clone() : null);
/* 640 */     clone.baseUri = this.baseUri;
/* 641 */     clone.childNodes = new ArrayList(this.childNodes.size());
/*     */ 
/* 643 */     for (Node child : this.childNodes) {
/* 644 */       clone.childNodes.add(child);
/*     */     }
/* 646 */     return clone;
/*     */   }
/*     */   private static class OuterHtmlVisitor implements NodeVisitor {
/*     */     private StringBuilder accum;
/*     */     private Document.OutputSettings out;
/*     */ 
/*     */     OuterHtmlVisitor(StringBuilder accum, Document.OutputSettings out) {
/* 654 */       this.accum = accum;
/* 655 */       this.out = out;
/*     */     }
/*     */ 
/*     */     public void head(Node node, int depth) {
/* 659 */       node.outerHtmlHead(this.accum, depth, this.out);
/*     */     }
/*     */ 
/*     */     public void tail(Node node, int depth) {
/* 663 */       if (!node.nodeName().equals("#text"))
/* 664 */         node.outerHtmlTail(this.accum, depth, this.out);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Node
 * JD-Core Version:    0.6.2
 */