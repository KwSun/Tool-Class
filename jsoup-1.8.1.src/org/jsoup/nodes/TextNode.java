/*     */ package org.jsoup.nodes;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.parser.Tag;
/*     */ 
/*     */ public class TextNode extends Node
/*     */ {
/*     */   private static final String TEXT_KEY = "text";
/*     */   String text;
/*     */ 
/*     */   public TextNode(String text, String baseUri)
/*     */   {
/*  27 */     this.baseUri = baseUri;
/*  28 */     this.text = text;
/*     */   }
/*     */ 
/*     */   public String nodeName() {
/*  32 */     return "#text";
/*     */   }
/*     */ 
/*     */   public String text()
/*     */   {
/*  41 */     return normaliseWhitespace(getWholeText());
/*     */   }
/*     */ 
/*     */   public TextNode text(String text)
/*     */   {
/*  50 */     this.text = text;
/*  51 */     if (this.attributes != null)
/*  52 */       this.attributes.put("text", text);
/*  53 */     return this;
/*     */   }
/*     */ 
/*     */   public String getWholeText()
/*     */   {
/*  61 */     return this.attributes == null ? this.text : this.attributes.get("text");
/*     */   }
/*     */ 
/*     */   public boolean isBlank()
/*     */   {
/*  69 */     return StringUtil.isBlank(getWholeText());
/*     */   }
/*     */ 
/*     */   public TextNode splitText(int offset)
/*     */   {
/*  79 */     Validate.isTrue(offset >= 0, "Split offset must be not be negative");
/*  80 */     Validate.isTrue(offset < this.text.length(), "Split offset must not be greater than current text length");
/*     */ 
/*  82 */     String head = getWholeText().substring(0, offset);
/*  83 */     String tail = getWholeText().substring(offset);
/*  84 */     text(head);
/*  85 */     TextNode tailNode = new TextNode(tail, baseUri());
/*  86 */     if (parent() != null) {
/*  87 */       parent().addChildren(siblingIndex() + 1, new Node[] { tailNode });
/*     */     }
/*  89 */     return tailNode;
/*     */   }
/*     */ 
/*     */   void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/*  93 */     if ((out.prettyPrint()) && (((siblingIndex() == 0) && ((this.parentNode instanceof Element)) && (((Element)this.parentNode).tag().formatAsBlock()) && (!isBlank())) || ((out.outline()) && (siblingNodes().size() > 0) && (!isBlank())))) {
/*  94 */       indent(accum, depth, out);
/*     */     }
/*  96 */     boolean normaliseWhite = (out.prettyPrint()) && ((parent() instanceof Element)) && (!Element.preserveWhitespace((Element)parent()));
/*     */ 
/*  98 */     Entities.escape(accum, getWholeText(), out, false, normaliseWhite, false);
/*     */   }
/*     */   void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 104 */     return outerHtml();
/*     */   }
/*     */ 
/*     */   public static TextNode createFromEncoded(String encodedText, String baseUri)
/*     */   {
/* 113 */     String text = Entities.unescape(encodedText);
/* 114 */     return new TextNode(text, baseUri);
/*     */   }
/*     */ 
/*     */   static String normaliseWhitespace(String text) {
/* 118 */     text = StringUtil.normaliseWhitespace(text);
/* 119 */     return text;
/*     */   }
/*     */ 
/*     */   static String stripLeadingWhitespace(String text) {
/* 123 */     return text.replaceFirst("^\\s+", "");
/*     */   }
/*     */ 
/*     */   static boolean lastCharIsWhitespace(StringBuilder sb) {
/* 127 */     return (sb.length() != 0) && (sb.charAt(sb.length() - 1) == ' ');
/*     */   }
/*     */ 
/*     */   private void ensureAttributes()
/*     */   {
/* 132 */     if (this.attributes == null) {
/* 133 */       this.attributes = new Attributes();
/* 134 */       this.attributes.put("text", this.text);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String attr(String attributeKey)
/*     */   {
/* 140 */     ensureAttributes();
/* 141 */     return super.attr(attributeKey);
/*     */   }
/*     */ 
/*     */   public Attributes attributes()
/*     */   {
/* 146 */     ensureAttributes();
/* 147 */     return super.attributes();
/*     */   }
/*     */ 
/*     */   public Node attr(String attributeKey, String attributeValue)
/*     */   {
/* 152 */     ensureAttributes();
/* 153 */     return super.attr(attributeKey, attributeValue);
/*     */   }
/*     */ 
/*     */   public boolean hasAttr(String attributeKey)
/*     */   {
/* 158 */     ensureAttributes();
/* 159 */     return super.hasAttr(attributeKey);
/*     */   }
/*     */ 
/*     */   public Node removeAttr(String attributeKey)
/*     */   {
/* 164 */     ensureAttributes();
/* 165 */     return super.removeAttr(attributeKey);
/*     */   }
/*     */ 
/*     */   public String absUrl(String attributeKey)
/*     */   {
/* 170 */     ensureAttributes();
/* 171 */     return super.absUrl(attributeKey);
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.TextNode
 * JD-Core Version:    0.6.2
 */