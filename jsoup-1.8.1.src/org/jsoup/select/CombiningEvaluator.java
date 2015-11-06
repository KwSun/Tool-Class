/*     */ package org.jsoup.select;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.nodes.Element;
/*     */ 
/*     */ abstract class CombiningEvaluator extends Evaluator
/*     */ {
/*     */   final ArrayList<Evaluator> evaluators;
/*  16 */   int num = 0;
/*     */ 
/*     */   CombiningEvaluator()
/*     */   {
/*  20 */     this.evaluators = new ArrayList();
/*     */   }
/*     */ 
/*     */   CombiningEvaluator(Collection<Evaluator> evaluators) {
/*  24 */     this();
/*  25 */     this.evaluators.addAll(evaluators);
/*  26 */     updateNumEvaluators();
/*     */   }
/*     */ 
/*     */   Evaluator rightMostEvaluator() {
/*  30 */     return this.num > 0 ? (Evaluator)this.evaluators.get(this.num - 1) : null;
/*     */   }
/*     */ 
/*     */   void replaceRightMostEvaluator(Evaluator replacement) {
/*  34 */     this.evaluators.set(this.num - 1, replacement);
/*     */   }
/*     */ 
/*     */   void updateNumEvaluators()
/*     */   {
/*  39 */     this.num = this.evaluators.size();
/*     */   }
/*     */ 
/*     */   static final class Or extends CombiningEvaluator
/*     */   {
/*     */     Or(Collection<Evaluator> evaluators)
/*     */     {
/*  74 */       if (this.num > 1)
/*  75 */         this.evaluators.add(new CombiningEvaluator.And(evaluators));
/*     */       else
/*  77 */         this.evaluators.addAll(evaluators);
/*  78 */       updateNumEvaluators();
/*     */     }
/*     */ 
/*     */     Or()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void add(Evaluator e) {
/*  86 */       this.evaluators.add(e);
/*  87 */       updateNumEvaluators();
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element node)
/*     */     {
/*  92 */       for (int i = 0; i < this.num; i++) {
/*  93 */         Evaluator s = (Evaluator)this.evaluators.get(i);
/*  94 */         if (s.matches(root, node))
/*  95 */           return true;
/*     */       }
/*  97 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 102 */       return String.format(":or%s", new Object[] { this.evaluators });
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class And extends CombiningEvaluator
/*     */   {
/*     */     And(Collection<Evaluator> evaluators)
/*     */     {
/*  44 */       super();
/*     */     }
/*     */ 
/*     */     And(Evaluator[] evaluators) {
/*  48 */       this(Arrays.asList(evaluators));
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element node)
/*     */     {
/*  53 */       for (int i = 0; i < this.num; i++) {
/*  54 */         Evaluator s = (Evaluator)this.evaluators.get(i);
/*  55 */         if (!s.matches(root, node))
/*  56 */           return false;
/*     */       }
/*  58 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  63 */       return StringUtil.join(this.evaluators, " ");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.CombiningEvaluator
 * JD-Core Version:    0.6.2
 */