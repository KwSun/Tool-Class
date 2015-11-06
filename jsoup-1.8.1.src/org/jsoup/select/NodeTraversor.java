/*    */ package org.jsoup.select;
/*    */ 
/*    */ import org.jsoup.nodes.Node;
/*    */ 
/*    */ public class NodeTraversor
/*    */ {
/*    */   private NodeVisitor visitor;
/*    */ 
/*    */   public NodeTraversor(NodeVisitor visitor)
/*    */   {
/* 18 */     this.visitor = visitor;
/*    */   }
/*    */ 
/*    */   public void traverse(Node root)
/*    */   {
/* 26 */     Node node = root;
/* 27 */     int depth = 0;
/*    */ 
/* 29 */     while (node != null) {
/* 30 */       this.visitor.head(node, depth);
/* 31 */       if (node.childNodeSize() > 0) {
/* 32 */         node = node.childNode(0);
/* 33 */         depth++;
/*    */       } else {
/* 35 */         while ((node.nextSibling() == null) && (depth > 0)) {
/* 36 */           this.visitor.tail(node, depth);
/* 37 */           node = node.parentNode();
/* 38 */           depth--;
/*    */         }
/* 40 */         this.visitor.tail(node, depth);
/* 41 */         if (node == root)
/*    */           break;
/* 43 */         node = node.nextSibling();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.NodeTraversor
 * JD-Core Version:    0.6.2
 */