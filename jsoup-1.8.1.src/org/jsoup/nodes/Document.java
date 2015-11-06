/*     */ package org.jsoup.nodes;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.parser.Tag;
/*     */ import org.jsoup.select.Elements;
/*     */ 
/*     */ public class Document extends Element
/*     */ {
/*  18 */   private OutputSettings outputSettings = new OutputSettings();
/*  19 */   private QuirksMode quirksMode = QuirksMode.noQuirks;
/*     */   private String location;
/*     */ 
/*     */   public Document(String baseUri)
/*     */   {
/*  29 */     super(Tag.valueOf("#root"), baseUri);
/*  30 */     this.location = baseUri;
/*     */   }
/*     */ 
/*     */   public static Document createShell(String baseUri)
/*     */   {
/*  39 */     Validate.notNull(baseUri);
/*     */ 
/*  41 */     Document doc = new Document(baseUri);
/*  42 */     Element html = doc.appendElement("html");
/*  43 */     html.appendElement("head");
/*  44 */     html.appendElement("body");
/*     */ 
/*  46 */     return doc;
/*     */   }
/*     */ 
/*     */   public String location()
/*     */   {
/*  55 */     return this.location;
/*     */   }
/*     */ 
/*     */   public Element head()
/*     */   {
/*  63 */     return findFirstElementByTagName("head", this);
/*     */   }
/*     */ 
/*     */   public Element body()
/*     */   {
/*  71 */     return findFirstElementByTagName("body", this);
/*     */   }
/*     */ 
/*     */   public String title()
/*     */   {
/*  80 */     Element titleEl = getElementsByTag("title").first();
/*  81 */     return titleEl != null ? StringUtil.normaliseWhitespace(titleEl.text()).trim() : "";
/*     */   }
/*     */ 
/*     */   public void title(String title)
/*     */   {
/*  90 */     Validate.notNull(title);
/*  91 */     Element titleEl = getElementsByTag("title").first();
/*  92 */     if (titleEl == null)
/*  93 */       head().appendElement("title").text(title);
/*     */     else
/*  95 */       titleEl.text(title);
/*     */   }
/*     */ 
/*     */   public Element createElement(String tagName)
/*     */   {
/* 105 */     return new Element(Tag.valueOf(tagName), baseUri());
/*     */   }
/*     */ 
/*     */   public Document normalise()
/*     */   {
/* 114 */     Element htmlEl = findFirstElementByTagName("html", this);
/* 115 */     if (htmlEl == null)
/* 116 */       htmlEl = appendElement("html");
/* 117 */     if (head() == null)
/* 118 */       htmlEl.prependElement("head");
/* 119 */     if (body() == null) {
/* 120 */       htmlEl.appendElement("body");
/*     */     }
/*     */ 
/* 124 */     normaliseTextNodes(head());
/* 125 */     normaliseTextNodes(htmlEl);
/* 126 */     normaliseTextNodes(this);
/*     */ 
/* 128 */     normaliseStructure("head", htmlEl);
/* 129 */     normaliseStructure("body", htmlEl);
/*     */ 
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   private void normaliseTextNodes(Element element)
/*     */   {
/* 136 */     List toMove = new ArrayList();
/* 137 */     for (Node node : element.childNodes) {
/* 138 */       if ((node instanceof TextNode)) {
/* 139 */         TextNode tn = (TextNode)node;
/* 140 */         if (!tn.isBlank()) {
/* 141 */           toMove.add(tn);
/*     */         }
/*     */       }
/*     */     }
/* 145 */     for (int i = toMove.size() - 1; i >= 0; i--) {
/* 146 */       Node node = (Node)toMove.get(i);
/* 147 */       element.removeChild(node);
/* 148 */       body().prependChild(new TextNode(" ", ""));
/* 149 */       body().prependChild(node);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void normaliseStructure(String tag, Element htmlEl)
/*     */   {
/* 155 */     Elements elements = getElementsByTag(tag);
/* 156 */     Element master = elements.first();
/* 157 */     if (elements.size() > 1) {
/* 158 */       List toMove = new ArrayList();
/* 159 */       for (int i = 1; i < elements.size(); i++) {
/* 160 */         Node dupe = elements.get(i);
/* 161 */         for (Node node : dupe.childNodes)
/* 162 */           toMove.add(node);
/* 163 */         dupe.remove();
/*     */       }
/*     */ 
/* 166 */       for (Node dupe : toMove) {
/* 167 */         master.appendChild(dupe);
/*     */       }
/*     */     }
/* 170 */     if (!master.parent().equals(htmlEl))
/* 171 */       htmlEl.appendChild(master);
/*     */   }
/*     */ 
/*     */   private Element findFirstElementByTagName(String tag, Node node)
/*     */   {
/* 177 */     if (node.nodeName().equals(tag)) {
/* 178 */       return (Element)node;
/*     */     }
/* 180 */     for (Node child : node.childNodes) {
/* 181 */       Element found = findFirstElementByTagName(tag, child);
/* 182 */       if (found != null) {
/* 183 */         return found;
/*     */       }
/*     */     }
/* 186 */     return null;
/*     */   }
/*     */ 
/*     */   public String outerHtml()
/*     */   {
/* 191 */     return super.html();
/*     */   }
/*     */ 
/*     */   public Element text(String text)
/*     */   {
/* 201 */     body().text(text);
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */   public String nodeName()
/*     */   {
/* 207 */     return "#document";
/*     */   }
/*     */ 
/*     */   public Document clone()
/*     */   {
/* 212 */     Document clone = (Document)super.clone();
/* 213 */     clone.outputSettings = this.outputSettings.clone();
/* 214 */     return clone;
/*     */   }
/*     */ 
/*     */   public OutputSettings outputSettings()
/*     */   {
/* 393 */     return this.outputSettings;
/*     */   }
/*     */ 
/*     */   public Document outputSettings(OutputSettings outputSettings)
/*     */   {
/* 402 */     Validate.notNull(outputSettings);
/* 403 */     this.outputSettings = outputSettings;
/* 404 */     return this;
/*     */   }
/*     */ 
/*     */   public QuirksMode quirksMode()
/*     */   {
/* 412 */     return this.quirksMode;
/*     */   }
/*     */ 
/*     */   public Document quirksMode(QuirksMode quirksMode) {
/* 416 */     this.quirksMode = quirksMode;
/* 417 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 422 */     return super.equals(o);
/*     */   }
/*     */ 
/*     */   public static enum QuirksMode
/*     */   {
/* 408 */     noQuirks, quirks, limitedQuirks;
/*     */   }
/*     */ 
/*     */   public static class OutputSettings
/*     */     implements Cloneable
/*     */   {
/* 226 */     private Entities.EscapeMode escapeMode = Entities.EscapeMode.base;
/* 227 */     private Charset charset = Charset.forName("UTF-8");
/* 228 */     private CharsetEncoder charsetEncoder = this.charset.newEncoder();
/* 229 */     private boolean prettyPrint = true;
/* 230 */     private boolean outline = false;
/* 231 */     private int indentAmount = 1;
/* 232 */     private Syntax syntax = Syntax.html;
/*     */ 
/*     */     public Entities.EscapeMode escapeMode()
/*     */     {
/* 245 */       return this.escapeMode;
/*     */     }
/*     */ 
/*     */     public OutputSettings escapeMode(Entities.EscapeMode escapeMode)
/*     */     {
/* 255 */       this.escapeMode = escapeMode;
/* 256 */       return this;
/*     */     }
/*     */ 
/*     */     public Charset charset()
/*     */     {
/* 268 */       return this.charset;
/*     */     }
/*     */ 
/*     */     public OutputSettings charset(Charset charset)
/*     */     {
/* 278 */       this.charset = charset;
/* 279 */       this.charsetEncoder = charset.newEncoder();
/* 280 */       return this;
/*     */     }
/*     */ 
/*     */     public OutputSettings charset(String charset)
/*     */     {
/* 289 */       charset(Charset.forName(charset));
/* 290 */       return this;
/*     */     }
/*     */ 
/*     */     CharsetEncoder encoder() {
/* 294 */       return this.charsetEncoder;
/*     */     }
/*     */ 
/*     */     public Syntax syntax()
/*     */     {
/* 302 */       return this.syntax;
/*     */     }
/*     */ 
/*     */     public OutputSettings syntax(Syntax syntax)
/*     */     {
/* 312 */       this.syntax = syntax;
/* 313 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean prettyPrint()
/*     */     {
/* 322 */       return this.prettyPrint;
/*     */     }
/*     */ 
/*     */     public OutputSettings prettyPrint(boolean pretty)
/*     */     {
/* 331 */       this.prettyPrint = pretty;
/* 332 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean outline()
/*     */     {
/* 341 */       return this.outline;
/*     */     }
/*     */ 
/*     */     public OutputSettings outline(boolean outlineMode)
/*     */     {
/* 350 */       this.outline = outlineMode;
/* 351 */       return this;
/*     */     }
/*     */ 
/*     */     public int indentAmount()
/*     */     {
/* 359 */       return this.indentAmount;
/*     */     }
/*     */ 
/*     */     public OutputSettings indentAmount(int indentAmount)
/*     */     {
/* 368 */       Validate.isTrue(indentAmount >= 0);
/* 369 */       this.indentAmount = indentAmount;
/* 370 */       return this;
/*     */     }
/*     */ 
/*     */     public OutputSettings clone()
/*     */     {
/*     */       OutputSettings clone;
/*     */       try {
/* 377 */         clone = (OutputSettings)super.clone();
/*     */       } catch (CloneNotSupportedException e) {
/* 379 */         throw new RuntimeException(e);
/*     */       }
/* 381 */       clone.charset(this.charset.name());
/* 382 */       clone.escapeMode = Entities.EscapeMode.valueOf(this.escapeMode.name());
/*     */ 
/* 384 */       return clone;
/*     */     }
/*     */ 
/*     */     public static enum Syntax
/*     */     {
/* 224 */       html, xml;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Document
 * JD-Core Version:    0.6.2
 */