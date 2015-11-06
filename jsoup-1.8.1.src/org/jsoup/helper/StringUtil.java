/*     */ package org.jsoup.helper;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public final class StringUtil
/*     */ {
/*  11 */   private static final String[] padding = { "", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          " };
/*     */ 
/*     */   public static String join(Collection strings, String sep)
/*     */   {
/*  20 */     return join(strings.iterator(), sep);
/*     */   }
/*     */ 
/*     */   public static String join(Iterator strings, String sep)
/*     */   {
/*  30 */     if (!strings.hasNext()) {
/*  31 */       return "";
/*     */     }
/*  33 */     String start = strings.next().toString();
/*  34 */     if (!strings.hasNext()) {
/*  35 */       return start;
/*     */     }
/*  37 */     StringBuilder sb = new StringBuilder(64).append(start);
/*  38 */     while (strings.hasNext()) {
/*  39 */       sb.append(sep);
/*  40 */       sb.append(strings.next());
/*     */     }
/*  42 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String padding(int width)
/*     */   {
/*  51 */     if (width < 0) {
/*  52 */       throw new IllegalArgumentException("width must be > 0");
/*     */     }
/*  54 */     if (width < padding.length) {
/*  55 */       return padding[width];
/*     */     }
/*  57 */     char[] out = new char[width];
/*  58 */     for (int i = 0; i < width; i++)
/*  59 */       out[i] = ' ';
/*  60 */     return String.valueOf(out);
/*     */   }
/*     */ 
/*     */   public static boolean isBlank(String string)
/*     */   {
/*  69 */     if ((string == null) || (string.length() == 0)) {
/*  70 */       return true;
/*     */     }
/*  72 */     int l = string.length();
/*  73 */     for (int i = 0; i < l; i++) {
/*  74 */       if (!isWhitespace(string.codePointAt(i)))
/*  75 */         return false;
/*     */     }
/*  77 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean isNumeric(String string)
/*     */   {
/*  86 */     if ((string == null) || (string.length() == 0)) {
/*  87 */       return false;
/*     */     }
/*  89 */     int l = string.length();
/*  90 */     for (int i = 0; i < l; i++) {
/*  91 */       if (!Character.isDigit(string.codePointAt(i)))
/*  92 */         return false;
/*     */     }
/*  94 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean isWhitespace(int c)
/*     */   {
/* 103 */     return (c == 32) || (c == 9) || (c == 10) || (c == 12) || (c == 13);
/*     */   }
/*     */ 
/*     */   public static String normaliseWhitespace(String string)
/*     */   {
/* 113 */     StringBuilder sb = new StringBuilder(string.length());
/* 114 */     appendNormalisedWhitespace(sb, string, false);
/* 115 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static void appendNormalisedWhitespace(StringBuilder accum, String string, boolean stripLeading)
/*     */   {
/* 126 */     boolean lastWasWhite = false;
/* 127 */     boolean reachedNonWhite = false;
/*     */ 
/* 129 */     int len = string.length();
/*     */     int c;
/* 131 */     for (int i = 0; i < len; i += Character.charCount(c)) {
/* 132 */       c = string.codePointAt(i);
/* 133 */       if (isWhitespace(c)) {
/* 134 */         if (((!stripLeading) || (reachedNonWhite)) && (!lastWasWhite))
/*     */         {
/* 136 */           accum.append(' ');
/* 137 */           lastWasWhite = true;
/*     */         }
/*     */       } else {
/* 140 */         accum.appendCodePoint(c);
/* 141 */         lastWasWhite = false;
/* 142 */         reachedNonWhite = true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean in(String needle, String[] haystack) {
/* 148 */     for (String hay : haystack) {
/* 149 */       if (hay.equals(needle))
/* 150 */         return true;
/*     */     }
/* 152 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.helper.StringUtil
 * JD-Core Version:    0.6.2
 */