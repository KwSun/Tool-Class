/*    */ package org.jsoup.nodes;
/*    */ 
/*    */ public class Comment extends Node
/*    */ {
/*    */   private static final String COMMENT_KEY = "comment";
/*    */ 
/*    */   public Comment(String data, String baseUri)
/*    */   {
/* 16 */     super(baseUri);
/* 17 */     this.attributes.put("comment", data);
/*    */   }
/*    */ 
/*    */   public String nodeName() {
/* 21 */     return "#comment";
/*    */   }
/*    */ 
/*    */   public String getData()
/*    */   {
/* 29 */     return this.attributes.get("comment");
/*    */   }
/*    */ 
/*    */   void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 33 */     if (out.prettyPrint())
/* 34 */       indent(accum, depth, out);
/* 35 */     accum.append("<!--").append(getData()).append("-->");
/*    */   }
/*    */ 
/*    */   void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out)
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 44 */     return outerHtml();
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Comment
 * JD-Core Version:    0.6.2
 */