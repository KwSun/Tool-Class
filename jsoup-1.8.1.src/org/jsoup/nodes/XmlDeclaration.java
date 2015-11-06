/*    */ package org.jsoup.nodes;
/*    */ 
/*    */ public class XmlDeclaration extends Node
/*    */ {
/*    */   private static final String DECL_KEY = "declaration";
/*    */   private final boolean isProcessingInstruction;
/*    */ 
/*    */   public XmlDeclaration(String data, String baseUri, boolean isProcessingInstruction)
/*    */   {
/* 18 */     super(baseUri);
/* 19 */     this.attributes.put("declaration", data);
/* 20 */     this.isProcessingInstruction = isProcessingInstruction;
/*    */   }
/*    */ 
/*    */   public String nodeName() {
/* 24 */     return "#declaration";
/*    */   }
/*    */ 
/*    */   public String getWholeDeclaration()
/*    */   {
/* 32 */     return this.attributes.get("declaration");
/*    */   }
/*    */ 
/*    */   void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 36 */     accum.append("<").append(this.isProcessingInstruction ? "!" : "?").append(getWholeDeclaration()).append(">");
/*    */   }
/*    */ 
/*    */   void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out)
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 46 */     return outerHtml();
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.XmlDeclaration
 * JD-Core Version:    0.6.2
 */