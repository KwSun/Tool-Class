/*     */ package org.jsoup.select;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashSet;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Element;
/*     */ 
/*     */ public class Selector
/*     */ {
/*     */   private final Evaluator evaluator;
/*     */   private final Element root;
/*     */ 
/*     */   private Selector(String query, Element root)
/*     */   {
/*  76 */     Validate.notNull(query);
/*  77 */     query = query.trim();
/*  78 */     Validate.notEmpty(query);
/*  79 */     Validate.notNull(root);
/*     */ 
/*  81 */     this.evaluator = QueryParser.parse(query);
/*     */ 
/*  83 */     this.root = root;
/*     */   }
/*     */ 
/*     */   public static Elements select(String query, Element root)
/*     */   {
/*  94 */     return new Selector(query, root).select();
/*     */   }
/*     */ 
/*     */   public static Elements select(String query, Iterable<Element> roots)
/*     */   {
/* 105 */     Validate.notEmpty(query);
/* 106 */     Validate.notNull(roots);
/* 107 */     LinkedHashSet elements = new LinkedHashSet();
/*     */ 
/* 109 */     for (Element root : roots) {
/* 110 */       elements.addAll(select(query, root));
/*     */     }
/* 112 */     return new Elements(elements);
/*     */   }
/*     */ 
/*     */   private Elements select() {
/* 116 */     return Collector.collect(this.evaluator, this.root);
/*     */   }
/*     */ 
/*     */   static Elements filterOut(Collection<Element> elements, Collection<Element> outs)
/*     */   {
/* 121 */     Elements output = new Elements();
/* 122 */     for (Element el : elements) {
/* 123 */       boolean found = false;
/* 124 */       for (Element out : outs) {
/* 125 */         if (el.equals(out)) {
/* 126 */           found = true;
/* 127 */           break;
/*     */         }
/*     */       }
/* 130 */       if (!found)
/* 131 */         output.add(el);
/*     */     }
/* 133 */     return output;
/*     */   }
/*     */ 
/*     */   public static class SelectorParseException extends IllegalStateException {
/*     */     public SelectorParseException(String msg, Object[] params) {
/* 138 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.Selector
 * JD-Core Version:    0.6.2
 */