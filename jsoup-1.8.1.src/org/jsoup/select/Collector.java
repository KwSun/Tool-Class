/*    */ package org.jsoup.select;
/*    */ 
/*    */ import org.jsoup.nodes.Element;
/*    */ import org.jsoup.nodes.Node;
/*    */ 
/*    */ public class Collector
/*    */ {
/*    */   public static Elements collect(Evaluator eval, Element root)
/*    */   {
/* 23 */     Elements elements = new Elements();
/* 24 */     new NodeTraversor(new Accumulator(root, elements, eval)).traverse(root);
/* 25 */     return elements;
/*    */   }
/*    */   private static class Accumulator implements NodeVisitor {
/*    */     private final Element root;
/*    */     private final Elements elements;
/*    */     private final Evaluator eval;
/*    */ 
/* 34 */     Accumulator(Element root, Elements elements, Evaluator eval) { this.root = root;
/* 35 */       this.elements = elements;
/* 36 */       this.eval = eval; }
/*    */ 
/*    */     public void head(Node node, int depth)
/*    */     {
/* 40 */       if ((node instanceof Element)) {
/* 41 */         Element el = (Element)node;
/* 42 */         if (this.eval.matches(this.root, el))
/* 43 */           this.elements.add(el);
/*    */       }
/*    */     }
/*    */ 
/*    */     public void tail(Node node, int depth)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.Collector
 * JD-Core Version:    0.6.2
 */