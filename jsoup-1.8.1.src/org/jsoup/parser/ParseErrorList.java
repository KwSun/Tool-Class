/*    */ package org.jsoup.parser;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ class ParseErrorList extends ArrayList<ParseError>
/*    */ {
/*    */   private static final int INITIAL_CAPACITY = 16;
/*    */   private final int maxSize;
/*    */ 
/*    */   ParseErrorList(int initialCapacity, int maxSize)
/*    */   {
/* 15 */     super(initialCapacity);
/* 16 */     this.maxSize = maxSize;
/*    */   }
/*    */ 
/*    */   boolean canAddError() {
/* 20 */     return size() < this.maxSize;
/*    */   }
/*    */ 
/*    */   int getMaxSize() {
/* 24 */     return this.maxSize;
/*    */   }
/*    */ 
/*    */   static ParseErrorList noTracking() {
/* 28 */     return new ParseErrorList(0, 0);
/*    */   }
/*    */ 
/*    */   static ParseErrorList tracking(int maxSize) {
/* 32 */     return new ParseErrorList(16, maxSize);
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.ParseErrorList
 * JD-Core Version:    0.6.2
 */