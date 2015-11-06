/*     */ package org.jsoup.safety;
/*     */ 
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Attribute;
/*     */ import org.jsoup.nodes.Attributes;
/*     */ import org.jsoup.nodes.DataNode;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.Node;
/*     */ import org.jsoup.nodes.TextNode;
/*     */ import org.jsoup.parser.Tag;
/*     */ import org.jsoup.select.NodeTraversor;
/*     */ import org.jsoup.select.NodeVisitor;
/*     */ 
/*     */ public class Cleaner
/*     */ {
/*     */   private Whitelist whitelist;
/*     */ 
/*     */   public Cleaner(Whitelist whitelist)
/*     */   {
/*  31 */     Validate.notNull(whitelist);
/*  32 */     this.whitelist = whitelist;
/*     */   }
/*     */ 
/*     */   public Document clean(Document dirtyDocument)
/*     */   {
/*  42 */     Validate.notNull(dirtyDocument);
/*     */ 
/*  44 */     Document clean = Document.createShell(dirtyDocument.baseUri());
/*  45 */     if (dirtyDocument.body() != null) {
/*  46 */       copySafeNodes(dirtyDocument.body(), clean.body());
/*     */     }
/*  48 */     return clean;
/*     */   }
/*     */ 
/*     */   public boolean isValid(Document dirtyDocument)
/*     */   {
/*  62 */     Validate.notNull(dirtyDocument);
/*     */ 
/*  64 */     Document clean = Document.createShell(dirtyDocument.baseUri());
/*  65 */     int numDiscarded = copySafeNodes(dirtyDocument.body(), clean.body());
/*  66 */     return numDiscarded == 0;
/*     */   }
/*     */ 
/*     */   private int copySafeNodes(Element source, Element dest)
/*     */   {
/* 117 */     CleaningVisitor cleaningVisitor = new CleaningVisitor(source, dest, null);
/* 118 */     NodeTraversor traversor = new NodeTraversor(cleaningVisitor);
/* 119 */     traversor.traverse(source);
/* 120 */     return cleaningVisitor.numDiscarded;
/*     */   }
/*     */ 
/*     */   private ElementMeta createSafeElement(Element sourceEl) {
/* 124 */     String sourceTag = sourceEl.tagName();
/* 125 */     Attributes destAttrs = new Attributes();
/* 126 */     Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
/* 127 */     int numDiscarded = 0;
/*     */ 
/* 129 */     Attributes sourceAttrs = sourceEl.attributes();
/* 130 */     for (Attribute sourceAttr : sourceAttrs) {
/* 131 */       if (this.whitelist.isSafeAttribute(sourceTag, sourceEl, sourceAttr))
/* 132 */         destAttrs.put(sourceAttr);
/*     */       else
/* 134 */         numDiscarded++;
/*     */     }
/* 136 */     Attributes enforcedAttrs = this.whitelist.getEnforcedAttributes(sourceTag);
/* 137 */     destAttrs.addAll(enforcedAttrs);
/*     */ 
/* 139 */     return new ElementMeta(dest, numDiscarded);
/*     */   }
/*     */   private static class ElementMeta {
/*     */     Element el;
/*     */     int numAttribsDiscarded;
/*     */ 
/*     */     ElementMeta(Element el, int numAttribsDiscarded) {
/* 147 */       this.el = el;
/* 148 */       this.numAttribsDiscarded = numAttribsDiscarded;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class CleaningVisitor
/*     */     implements NodeVisitor
/*     */   {
/*  73 */     private int numDiscarded = 0;
/*     */     private final Element root;
/*     */     private Element destination;
/*     */ 
/*     */     private CleaningVisitor(Element root, Element destination)
/*     */     {
/*  78 */       this.root = root;
/*  79 */       this.destination = destination;
/*     */     }
/*     */ 
/*     */     public void head(Node source, int depth) {
/*  83 */       if ((source instanceof Element)) {
/*  84 */         Element sourceEl = (Element)source;
/*     */ 
/*  86 */         if (Cleaner.this.whitelist.isSafeTag(sourceEl.tagName())) {
/*  87 */           Cleaner.ElementMeta meta = Cleaner.this.createSafeElement(sourceEl);
/*  88 */           Element destChild = meta.el;
/*  89 */           this.destination.appendChild(destChild);
/*     */ 
/*  91 */           this.numDiscarded += meta.numAttribsDiscarded;
/*  92 */           this.destination = destChild;
/*  93 */         } else if (source != this.root) {
/*  94 */           this.numDiscarded += 1;
/*     */         }
/*  96 */       } else if ((source instanceof TextNode)) {
/*  97 */         TextNode sourceText = (TextNode)source;
/*  98 */         TextNode destText = new TextNode(sourceText.getWholeText(), source.baseUri());
/*  99 */         this.destination.appendChild(destText);
/* 100 */       } else if (((source instanceof DataNode)) && (Cleaner.this.whitelist.isSafeTag(source.parent().nodeName()))) {
/* 101 */         DataNode sourceData = (DataNode)source;
/* 102 */         DataNode destData = new DataNode(sourceData.getWholeData(), source.baseUri());
/* 103 */         this.destination.appendChild(destData);
/*     */       } else {
/* 105 */         this.numDiscarded += 1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public void tail(Node source, int depth) {
/* 110 */       if (((source instanceof Element)) && (Cleaner.this.whitelist.isSafeTag(source.nodeName())))
/* 111 */         this.destination = this.destination.parent();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.safety.Cleaner
 * JD-Core Version:    0.6.2
 */