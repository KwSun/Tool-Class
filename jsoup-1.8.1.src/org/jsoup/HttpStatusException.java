/*    */ package org.jsoup;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class HttpStatusException extends IOException
/*    */ {
/*    */   private int statusCode;
/*    */   private String url;
/*    */ 
/*    */   public HttpStatusException(String message, int statusCode, String url)
/*    */   {
/* 13 */     super(message);
/* 14 */     this.statusCode = statusCode;
/* 15 */     this.url = url;
/*    */   }
/*    */ 
/*    */   public int getStatusCode() {
/* 19 */     return this.statusCode;
/*    */   }
/*    */ 
/*    */   public String getUrl() {
/* 23 */     return this.url;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 28 */     return super.toString() + ". Status=" + this.statusCode + ", URL=" + this.url;
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.HttpStatusException
 * JD-Core Version:    0.6.2
 */