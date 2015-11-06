/*     */ package org.jsoup;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import org.jsoup.helper.DataUtil;
/*     */ import org.jsoup.helper.HttpConnection;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Document.OutputSettings;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.parser.Parser;
/*     */ import org.jsoup.safety.Cleaner;
/*     */ import org.jsoup.safety.Whitelist;
/*     */ 
/*     */ public class Jsoup
/*     */ {
/*     */   public static Document parse(String html, String baseUri)
/*     */   {
/*  31 */     return Parser.parse(html, baseUri);
/*     */   }
/*     */ 
/*     */   public static Document parse(String html, String baseUri, Parser parser)
/*     */   {
/*  45 */     return parser.parseInput(html, baseUri);
/*     */   }
/*     */ 
/*     */   public static Document parse(String html)
/*     */   {
/*  58 */     return Parser.parse(html, "");
/*     */   }
/*     */ 
/*     */   public static Connection connect(String url)
/*     */   {
/*  73 */     return HttpConnection.connect(url);
/*     */   }
/*     */ 
/*     */   public static Document parse(File in, String charsetName, String baseUri)
/*     */     throws IOException
/*     */   {
/*  88 */     return DataUtil.load(in, charsetName, baseUri);
/*     */   }
/*     */ 
/*     */   public static Document parse(File in, String charsetName)
/*     */     throws IOException
/*     */   {
/* 103 */     return DataUtil.load(in, charsetName, in.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public static Document parse(InputStream in, String charsetName, String baseUri)
/*     */     throws IOException
/*     */   {
/* 118 */     return DataUtil.load(in, charsetName, baseUri);
/*     */   }
/*     */ 
/*     */   public static Document parse(InputStream in, String charsetName, String baseUri, Parser parser)
/*     */     throws IOException
/*     */   {
/* 135 */     return DataUtil.load(in, charsetName, baseUri, parser);
/*     */   }
/*     */ 
/*     */   public static Document parseBodyFragment(String bodyHtml, String baseUri)
/*     */   {
/* 148 */     return Parser.parseBodyFragment(bodyHtml, baseUri);
/*     */   }
/*     */ 
/*     */   public static Document parseBodyFragment(String bodyHtml)
/*     */   {
/* 160 */     return Parser.parseBodyFragment(bodyHtml, "");
/*     */   }
/*     */ 
/*     */   public static Document parse(URL url, int timeoutMillis)
/*     */     throws IOException
/*     */   {
/* 181 */     Connection con = HttpConnection.connect(url);
/* 182 */     con.timeout(timeoutMillis);
/* 183 */     return con.get();
/*     */   }
/*     */ 
/*     */   public static String clean(String bodyHtml, String baseUri, Whitelist whitelist)
/*     */   {
/* 198 */     Document dirty = parseBodyFragment(bodyHtml, baseUri);
/* 199 */     Cleaner cleaner = new Cleaner(whitelist);
/* 200 */     Document clean = cleaner.clean(dirty);
/* 201 */     return clean.body().html();
/*     */   }
/*     */ 
/*     */   public static String clean(String bodyHtml, Whitelist whitelist)
/*     */   {
/* 215 */     return clean(bodyHtml, "", whitelist);
/*     */   }
/*     */ 
/*     */   public static String clean(String bodyHtml, String baseUri, Whitelist whitelist, Document.OutputSettings outputSettings)
/*     */   {
/* 231 */     Document dirty = parseBodyFragment(bodyHtml, baseUri);
/* 232 */     Cleaner cleaner = new Cleaner(whitelist);
/* 233 */     Document clean = cleaner.clean(dirty);
/* 234 */     clean.outputSettings(outputSettings);
/* 235 */     return clean.body().html();
/*     */   }
/*     */ 
/*     */   public static boolean isValid(String bodyHtml, Whitelist whitelist)
/*     */   {
/* 247 */     Document dirty = parseBodyFragment(bodyHtml, "");
/* 248 */     Cleaner cleaner = new Cleaner(whitelist);
/* 249 */     return cleaner.isValid(dirty);
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.Jsoup
 * JD-Core Version:    0.6.2
 */