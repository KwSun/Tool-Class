/*    */ package org.jsoup.nodes;
/*    */ 
/*    */ public class DataNode extends Node
/*    */ {
/*    */   private static final String DATA_KEY = "data";
/*    */ 
/*    */   public DataNode(String data, String baseUri)
/*    */   {
/* 16 */     super(baseUri);
/* 17 */     this.attributes.put("data", data);
/*    */   }
/*    */ 
/*    */   public String nodeName() {
/* 21 */     return "#data";
/*    */   }
/*    */ 
/*    */   public String getWholeData()
/*    */   {
/* 29 */     return this.attributes.get("data");
/*    */   }
/*    */ 
/*    */   public DataNode setWholeData(String data)
/*    */   {
/* 38 */     this.attributes.put("data", data);
/* 39 */     return this;
/*    */   }
/*    */ 
/*    */   void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 43 */     accum.append(getWholeData());
/*    */   }
/*    */   void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 49 */     return outerHtml();
/*    */   }
/*    */ 
/*    */   public static DataNode createFromEncoded(String encodedData, String baseUri)
/*    */   {
/* 59 */     String data = Entities.unescape(encodedData);
/* 60 */     return new DataNode(data, baseUri);
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.DataNode
 * JD-Core Version:    0.6.2
 */