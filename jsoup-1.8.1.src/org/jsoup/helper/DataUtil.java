/*     */ package org.jsoup.helper;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.IllegalCharsetNameException;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Document.OutputSettings;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.parser.Parser;
/*     */ import org.jsoup.select.Elements;
/*     */ 
/*     */ public class DataUtil
/*     */ {
/*  20 */   private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
/*     */   static final String defaultCharset = "UTF-8";
/*     */   private static final int bufferSize = 131072;
/*     */ 
/*     */   public static Document load(File in, String charsetName, String baseUri)
/*     */     throws IOException
/*     */   {
/*  35 */     ByteBuffer byteData = readFileToByteBuffer(in);
/*  36 */     return parseByteData(byteData, charsetName, baseUri, Parser.htmlParser());
/*     */   }
/*     */ 
/*     */   public static Document load(InputStream in, String charsetName, String baseUri)
/*     */     throws IOException
/*     */   {
/*  48 */     ByteBuffer byteData = readToByteBuffer(in);
/*  49 */     return parseByteData(byteData, charsetName, baseUri, Parser.htmlParser());
/*     */   }
/*     */ 
/*     */   public static Document load(InputStream in, String charsetName, String baseUri, Parser parser)
/*     */     throws IOException
/*     */   {
/*  62 */     ByteBuffer byteData = readToByteBuffer(in);
/*  63 */     return parseByteData(byteData, charsetName, baseUri, parser);
/*     */   }
/*     */ 
/*     */   static Document parseByteData(ByteBuffer byteData, String charsetName, String baseUri, Parser parser)
/*     */   {
/*  71 */     Document doc = null;
/*     */     String docData;
/*  72 */     if (charsetName == null)
/*     */     {
/*  74 */       String docData = Charset.forName("UTF-8").decode(byteData).toString();
/*  75 */       doc = parser.parseInput(docData, baseUri);
/*  76 */       Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
/*  77 */       if (meta != null)
/*     */       {
/*     */         String foundCharset;
/*  79 */         if (meta.hasAttr("http-equiv")) {
/*  80 */           String foundCharset = getCharsetFromContentType(meta.attr("content"));
/*  81 */           if ((foundCharset == null) && (meta.hasAttr("charset")))
/*     */             try {
/*  83 */               if (Charset.isSupported(meta.attr("charset")))
/*  84 */                 foundCharset = meta.attr("charset");
/*     */             }
/*     */             catch (IllegalCharsetNameException e) {
/*  87 */               foundCharset = null;
/*     */             }
/*     */         }
/*     */         else {
/*  91 */           foundCharset = meta.attr("charset");
/*     */         }
/*     */ 
/*  94 */         if ((foundCharset != null) && (foundCharset.length() != 0) && (!foundCharset.equals("UTF-8"))) {
/*  95 */           foundCharset = foundCharset.trim().replaceAll("[\"']", "");
/*  96 */           charsetName = foundCharset;
/*  97 */           byteData.rewind();
/*  98 */           docData = Charset.forName(foundCharset).decode(byteData).toString();
/*  99 */           doc = null;
/*     */         }
/*     */       }
/*     */     } else {
/* 103 */       Validate.notEmpty(charsetName, "Must set charset arg to character set of file to parse. Set to null to attempt to detect from HTML");
/* 104 */       docData = Charset.forName(charsetName).decode(byteData).toString();
/*     */     }
/*     */ 
/* 107 */     if ((docData.length() > 0) && (docData.charAt(0) == 65279)) {
/* 108 */       byteData.rewind();
/* 109 */       docData = Charset.forName("UTF-8").decode(byteData).toString();
/* 110 */       docData = docData.substring(1);
/* 111 */       charsetName = "UTF-8";
/* 112 */       doc = null;
/*     */     }
/* 114 */     if (doc == null) {
/* 115 */       doc = parser.parseInput(docData, baseUri);
/* 116 */       doc.outputSettings().charset(charsetName);
/*     */     }
/* 118 */     return doc;
/*     */   }
/*     */ 
/*     */   static ByteBuffer readToByteBuffer(InputStream inStream, int maxSize)
/*     */     throws IOException
/*     */   {
/* 129 */     Validate.isTrue(maxSize >= 0, "maxSize must be 0 (unlimited) or larger");
/* 130 */     boolean capped = maxSize > 0;
/* 131 */     byte[] buffer = new byte[131072];
/* 132 */     ByteArrayOutputStream outStream = new ByteArrayOutputStream(131072);
/*     */ 
/* 134 */     int remaining = maxSize;
/*     */     while (true)
/*     */     {
/* 137 */       int read = inStream.read(buffer);
/* 138 */       if (read == -1) break;
/* 139 */       if (capped) {
/* 140 */         if (read > remaining) {
/* 141 */           outStream.write(buffer, 0, remaining);
/* 142 */           break;
/*     */         }
/* 144 */         remaining -= read;
/*     */       }
/* 146 */       outStream.write(buffer, 0, read);
/*     */     }
/* 148 */     ByteBuffer byteData = ByteBuffer.wrap(outStream.toByteArray());
/* 149 */     return byteData;
/*     */   }
/*     */ 
/*     */   static ByteBuffer readToByteBuffer(InputStream inStream) throws IOException {
/* 153 */     return readToByteBuffer(inStream, 0);
/*     */   }
/*     */ 
/*     */   static ByteBuffer readFileToByteBuffer(File file) throws IOException {
/* 157 */     RandomAccessFile randomAccessFile = null;
/*     */     try {
/* 159 */       randomAccessFile = new RandomAccessFile(file, "r");
/* 160 */       byte[] bytes = new byte[(int)randomAccessFile.length()];
/* 161 */       randomAccessFile.readFully(bytes);
/* 162 */       return ByteBuffer.wrap(bytes);
/*     */     } finally {
/* 164 */       if (randomAccessFile != null)
/* 165 */         randomAccessFile.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   static String getCharsetFromContentType(String contentType)
/*     */   {
/* 176 */     if (contentType == null) return null;
/* 177 */     Matcher m = charsetPattern.matcher(contentType);
/* 178 */     if (m.find()) {
/* 179 */       String charset = m.group(1).trim();
/* 180 */       charset = charset.replace("charset=", "");
/* 181 */       if (charset.length() == 0) return null; try
/*     */       {
/* 183 */         if (Charset.isSupported(charset)) return charset;
/* 184 */         charset = charset.toUpperCase(Locale.ENGLISH);
/* 185 */         if (Charset.isSupported(charset)) return charset; 
/*     */       }
/*     */       catch (IllegalCharsetNameException e)
/*     */       {
/* 188 */         return null;
/*     */       }
/*     */     }
/* 191 */     return null;
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.helper.DataUtil
 * JD-Core Version:    0.6.2
 */