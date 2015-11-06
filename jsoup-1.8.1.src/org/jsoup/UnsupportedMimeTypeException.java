/*    */ package org.jsoup;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class UnsupportedMimeTypeException extends IOException
/*    */ {
/*    */   private String mimeType;
/*    */   private String url;
/*    */ 
/*    */   public UnsupportedMimeTypeException(String message, String mimeType, String url)
/*    */   {
/* 13 */     super(message);
/* 14 */     this.mimeType = mimeType;
/* 15 */     this.url = url;
/*    */   }
/*    */ 
/*    */   public String getMimeType() {
/* 19 */     return this.mimeType;
/*    */   }
/*    */ 
/*    */   public String getUrl() {
/* 23 */     return this.url;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 28 */     return super.toString() + ". Mimetype=" + this.mimeType + ", URL=" + this.url;
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.UnsupportedMimeTypeException
 * JD-Core Version:    0.6.2
 */