/*     */ package org.jsoup.select;
/*     */ 
/*     */ import org.jsoup.nodes.Element;
/*     */ 
/*     */ abstract class StructuralEvaluator extends Evaluator
/*     */ {
/*     */   Evaluator evaluator;
/*     */ 
/*     */   static class ImmediatePreviousSibling extends StructuralEvaluator
/*     */   {
/*     */     public ImmediatePreviousSibling(Evaluator evaluator)
/*     */     {
/* 117 */       this.evaluator = evaluator;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element) {
/* 121 */       if (root == element) {
/* 122 */         return false;
/*     */       }
/* 124 */       Element prev = element.previousElementSibling();
/* 125 */       return (prev != null) && (this.evaluator.matches(root, prev));
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 129 */       return String.format(":prev%s", new Object[] { this.evaluator });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class PreviousSibling extends StructuralEvaluator
/*     */   {
/*     */     public PreviousSibling(Evaluator evaluator)
/*     */     {
/*  92 */       this.evaluator = evaluator;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element) {
/*  96 */       if (root == element) {
/*  97 */         return false;
/*     */       }
/*  99 */       Element prev = element.previousElementSibling();
/*     */ 
/* 101 */       while (prev != null) {
/* 102 */         if (this.evaluator.matches(root, prev)) {
/* 103 */           return true;
/*     */         }
/* 105 */         prev = prev.previousElementSibling();
/*     */       }
/* 107 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 111 */       return String.format(":prev*%s", new Object[] { this.evaluator });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ImmediateParent extends StructuralEvaluator
/*     */   {
/*     */     public ImmediateParent(Evaluator evaluator)
/*     */     {
/*  74 */       this.evaluator = evaluator;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element) {
/*  78 */       if (root == element) {
/*  79 */         return false;
/*     */       }
/*  81 */       Element parent = element.parent();
/*  82 */       return (parent != null) && (this.evaluator.matches(root, parent));
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  86 */       return String.format(":ImmediateParent%s", new Object[] { this.evaluator });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Parent extends StructuralEvaluator
/*     */   {
/*     */     public Parent(Evaluator evaluator)
/*     */     {
/*  51 */       this.evaluator = evaluator;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element) {
/*  55 */       if (root == element) {
/*  56 */         return false;
/*     */       }
/*  58 */       Element parent = element.parent();
/*  59 */       while (parent != root) {
/*  60 */         if (this.evaluator.matches(root, parent))
/*  61 */           return true;
/*  62 */         parent = parent.parent();
/*     */       }
/*  64 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  68 */       return String.format(":parent%s", new Object[] { this.evaluator });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Not extends StructuralEvaluator
/*     */   {
/*     */     public Not(Evaluator evaluator)
/*     */     {
/*  37 */       this.evaluator = evaluator;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element node) {
/*  41 */       return !this.evaluator.matches(root, node);
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  45 */       return String.format(":not%s", new Object[] { this.evaluator });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Has extends StructuralEvaluator
/*     */   {
/*     */     public Has(Evaluator evaluator)
/*     */     {
/*  19 */       this.evaluator = evaluator;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element) {
/*  23 */       for (Element e : element.getAllElements()) {
/*  24 */         if ((e != element) && (this.evaluator.matches(root, e)))
/*  25 */           return true;
/*     */       }
/*  27 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  31 */       return String.format(":has(%s)", new Object[] { this.evaluator });
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Root extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/*  13 */       return root == element;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.StructuralEvaluator
 * JD-Core Version:    0.6.2
 */