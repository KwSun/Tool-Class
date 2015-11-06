/*    */ package org.jsoup.parser;
/*    */ 
/*    */ public class ParseError
/*    */ {
/*    */   private int pos;
/*    */   private String errorMsg;
/*    */ 
/*    */   ParseError(int pos, String errorMsg)
/*    */   {
/* 11 */     this.pos = pos;
/* 12 */     this.errorMsg = errorMsg;
/*    */   }
/*    */ 
/*    */   ParseError(int pos, String errorFormat, Object[] args) {
/* 16 */     this.errorMsg = String.format(errorFormat, args);
/* 17 */     this.pos = pos;
/*    */   }
/*    */ 
/*    */   public String getErrorMessage()
/*    */   {
/* 25 */     return this.errorMsg;
/*    */   }
/*    */ 
/*    */   public int getPosition()
/*    */   {
/* 33 */     return this.pos;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 38 */     return this.pos + ": " + this.errorMsg;
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.ParseError
 * JD-Core Version:    0.6.2
 */