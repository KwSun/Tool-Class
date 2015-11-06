/*    */ package org.jsoup.examples;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ import org.jsoup.Connection;
/*    */ import org.jsoup.Jsoup;
/*    */ import org.jsoup.helper.Validate;
/*    */ import org.jsoup.nodes.Document;
/*    */ import org.jsoup.nodes.Element;
/*    */ import org.jsoup.select.Elements;
/*    */ 
/*    */ public class ListLinks
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws IOException
/*    */   {
/* 16 */     Validate.isTrue(args.length == 1, "usage: supply url to fetch");
/* 17 */     String url = args[0];
/* 18 */     print("Fetching %s...", new Object[] { url });
/*    */ 
/* 20 */     Document doc = Jsoup.connect(url).get();
/* 21 */     Elements links = doc.select("a[href]");
/* 22 */     Elements media = doc.select("[src]");
/* 23 */     Elements imports = doc.select("link[href]");
/*    */ 
/* 25 */     print("\nMedia: (%d)", new Object[] { Integer.valueOf(media.size()) });
/* 26 */     for (Element src : media) {
/* 27 */       if (src.tagName().equals("img")) {
/* 28 */         print(" * %s: <%s> %sx%s (%s)", new Object[] { src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"), trim(src.attr("alt"), 20) });
/*    */       }
/*    */       else
/*    */       {
/* 32 */         print(" * %s: <%s>", new Object[] { src.tagName(), src.attr("abs:src") });
/*    */       }
/*    */     }
/* 35 */     print("\nImports: (%d)", new Object[] { Integer.valueOf(imports.size()) });
/* 36 */     for (Element link : imports) {
/* 37 */       print(" * %s <%s> (%s)", new Object[] { link.tagName(), link.attr("abs:href"), link.attr("rel") });
/*    */     }
/*    */ 
/* 40 */     print("\nLinks: (%d)", new Object[] { Integer.valueOf(links.size()) });
/* 41 */     for (Element link : links)
/* 42 */       print(" * a: <%s>  (%s)", new Object[] { link.attr("abs:href"), trim(link.text(), 35) });
/*    */   }
/*    */ 
/*    */   private static void print(String msg, Object[] args)
/*    */   {
/* 47 */     System.out.println(String.format(msg, args));
/*    */   }
/*    */ 
/*    */   private static String trim(String s, int width) {
/* 51 */     if (s.length() > width) {
/* 52 */       return s.substring(0, width - 1) + ".";
/*    */     }
/* 54 */     return s;
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.examples.ListLinks
 * JD-Core Version:    0.6.2
 */