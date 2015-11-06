/*     */ package org.jsoup.examples;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import org.jsoup.Connection;
/*     */ import org.jsoup.Jsoup;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.Node;
/*     */ import org.jsoup.nodes.TextNode;
/*     */ import org.jsoup.select.NodeTraversor;
/*     */ import org.jsoup.select.NodeVisitor;
/*     */ 
/*     */ public class HtmlToPlainText
/*     */ {
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/*  26 */     Validate.isTrue(args.length == 1, "usage: supply url to fetch");
/*  27 */     String url = args[0];
/*     */ 
/*  30 */     Document doc = Jsoup.connect(url).get();
/*     */ 
/*  32 */     HtmlToPlainText formatter = new HtmlToPlainText();
/*  33 */     String plainText = formatter.getPlainText(doc);
/*  34 */     System.out.println(plainText);
/*     */   }
/*     */ 
/*     */   public String getPlainText(Element element)
/*     */   {
/*  43 */     FormattingVisitor formatter = new FormattingVisitor(null);
/*  44 */     NodeTraversor traversor = new NodeTraversor(formatter);
/*  45 */     traversor.traverse(element);
/*     */ 
/*  47 */     return formatter.toString();
/*     */   }
/*     */ 
/*     */   private class FormattingVisitor
/*     */     implements NodeVisitor
/*     */   {
/*     */     private static final int maxWidth = 80;
/*  53 */     private int width = 0;
/*  54 */     private StringBuilder accum = new StringBuilder();
/*     */ 
/*     */     private FormattingVisitor() {
/*     */     }
/*  58 */     public void head(Node node, int depth) { String name = node.nodeName();
/*  59 */       if ((node instanceof TextNode))
/*  60 */         append(((TextNode)node).text());
/*  61 */       else if (name.equals("li"))
/*  62 */         append("\n * ");
/*     */     }
/*     */ 
/*     */     public void tail(Node node, int depth)
/*     */     {
/*  67 */       String name = node.nodeName();
/*  68 */       if (name.equals("br"))
/*  69 */         append("\n");
/*  70 */       else if (StringUtil.in(name, new String[] { "p", "h1", "h2", "h3", "h4", "h5" }))
/*  71 */         append("\n\n");
/*  72 */       else if (name.equals("a"))
/*  73 */         append(String.format(" <%s>", new Object[] { node.absUrl("href") }));
/*     */     }
/*     */ 
/*     */     private void append(String text)
/*     */     {
/*  78 */       if (text.startsWith("\n"))
/*  79 */         this.width = 0;
/*  80 */       if (text.equals(" ")) if (this.accum.length() != 0) { if (!StringUtil.in(this.accum.substring(this.accum.length() - 1), new String[] { " ", "\n" }));
/*     */         } else {
/*  82 */           return;
/*     */         }
/*  84 */       if (text.length() + this.width > 80) {
/*  85 */         String[] words = text.split("\\s+");
/*  86 */         for (int i = 0; i < words.length; i++) {
/*  87 */           String word = words[i];
/*  88 */           boolean last = i == words.length - 1;
/*  89 */           if (!last)
/*  90 */             word = word + " ";
/*  91 */           if (word.length() + this.width > 80) {
/*  92 */             this.accum.append("\n").append(word);
/*  93 */             this.width = word.length();
/*     */           } else {
/*  95 */             this.accum.append(word);
/*  96 */             this.width += word.length();
/*     */           }
/*     */         }
/*     */       } else {
/* 100 */         this.accum.append(text);
/* 101 */         this.width += text.length();
/*     */       }
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 106 */       return this.accum.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.examples.HtmlToPlainText
 * JD-Core Version:    0.6.2
 */