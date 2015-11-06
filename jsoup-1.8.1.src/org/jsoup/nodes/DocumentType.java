/*    */ package org.jsoup.nodes;
/*    */ 
/*    */ import org.jsoup.helper.StringUtil;
/*    */ 
/*    */ public class DocumentType extends Node
/*    */ {
/*    */   public DocumentType(String name, String publicId, String systemId, String baseUri)
/*    */   {
/* 20 */     super(baseUri);
/*    */ 
/* 22 */     attr("name", name);
/* 23 */     attr("publicId", publicId);
/* 24 */     attr("systemId", systemId);
/*    */   }
/*    */ 
/*    */   public String nodeName()
/*    */   {
/* 29 */     return "#doctype";
/*    */   }
/*    */ 
/*    */   void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out)
/*    */   {
/* 34 */     accum.append("<!DOCTYPE");
/* 35 */     if (!StringUtil.isBlank(attr("name")))
/* 36 */       accum.append(" ").append(attr("name"));
/* 37 */     if (!StringUtil.isBlank(attr("publicId")))
/* 38 */       accum.append(" PUBLIC \"").append(attr("publicId")).append('"');
/* 39 */     if (!StringUtil.isBlank(attr("systemId")))
/* 40 */       accum.append(" \"").append(attr("systemId")).append('"');
/* 41 */     accum.append('>');
/*    */   }
/*    */ 
/*    */   void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.DocumentType
 * JD-Core Version:    0.6.2
 */