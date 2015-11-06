/*     */ package org.jsoup.nodes;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.Properties;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.parser.Parser;
/*     */ 
/*     */ public class Entities
/*     */ {
/*     */   private static final Map<String, Character> full;
/*     */   private static final Map<Character, String> xhtmlByVal;
/*     */   private static final Map<String, Character> base;
/*     */   private static final Map<Character, String> baseByVal;
/*     */   private static final Map<Character, String> fullByVal;
/* 173 */   private static final Object[][] xhtmlArray = { { "quot", Integer.valueOf(34) }, { "amp", Integer.valueOf(38) }, { "lt", Integer.valueOf(60) }, { "gt", Integer.valueOf(62) } };
/*     */ 
/*     */   public static boolean isNamedEntity(String name)
/*     */   {
/*  52 */     return full.containsKey(name);
/*     */   }
/*     */ 
/*     */   public static boolean isBaseNamedEntity(String name)
/*     */   {
/*  62 */     return base.containsKey(name);
/*     */   }
/*     */ 
/*     */   public static Character getCharacterByName(String name)
/*     */   {
/*  71 */     return (Character)full.get(name);
/*     */   }
/*     */ 
/*     */   static String escape(String string, Document.OutputSettings out) {
/*  75 */     StringBuilder accum = new StringBuilder(string.length() * 2);
/*  76 */     escape(accum, string, out, false, false, false);
/*  77 */     return accum.toString();
/*     */   }
/*     */ 
/*     */   static void escape(StringBuilder accum, String string, Document.OutputSettings out, boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite)
/*     */   {
/*  84 */     boolean lastWasWhite = false;
/*  85 */     boolean reachedNonWhite = false;
/*  86 */     EscapeMode escapeMode = out.escapeMode();
/*  87 */     CharsetEncoder encoder = out.encoder();
/*  88 */     Map map = escapeMode.getMap();
/*  89 */     int length = string.length();
/*     */     int codePoint;
/*  92 */     for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
/*  93 */       codePoint = string.codePointAt(offset);
/*     */ 
/*  95 */       if (normaliseWhite) {
/*  96 */         if (StringUtil.isWhitespace(codePoint)) {
/*  97 */           if (((!stripLeadingWhite) || (reachedNonWhite)) && (!lastWasWhite))
/*     */           {
/*  99 */             accum.append(' ');
/* 100 */             lastWasWhite = true;
/*     */           }
/*     */         } else {
/* 103 */           lastWasWhite = false;
/* 104 */           reachedNonWhite = true;
/*     */         }
/*     */ 
/*     */       }
/* 108 */       else if (codePoint < 65536) {
/* 109 */         char c = (char)codePoint;
/*     */ 
/* 111 */         switch (c) {
/*     */         case '&':
/* 113 */           accum.append("&amp;");
/* 114 */           break;
/*     */         case ' ':
/* 116 */           if (escapeMode != EscapeMode.xhtml)
/* 117 */             accum.append("&nbsp;");
/*     */           else
/* 119 */             accum.append(c);
/* 120 */           break;
/*     */         case '<':
/* 122 */           if (!inAttribute)
/* 123 */             accum.append("&lt;");
/*     */           else
/* 125 */             accum.append(c);
/* 126 */           break;
/*     */         case '>':
/* 128 */           if (!inAttribute)
/* 129 */             accum.append("&gt;");
/*     */           else
/* 131 */             accum.append(c);
/* 132 */           break;
/*     */         case '"':
/* 134 */           if (inAttribute)
/* 135 */             accum.append("&quot;");
/*     */           else
/* 137 */             accum.append(c);
/* 138 */           break;
/*     */         default:
/* 140 */           if (encoder.canEncode(c))
/* 141 */             accum.append(c);
/* 142 */           else if (map.containsKey(Character.valueOf(c)))
/* 143 */             accum.append('&').append((String)map.get(Character.valueOf(c))).append(';');
/*     */           else
/* 145 */             accum.append("&#x").append(Integer.toHexString(codePoint)).append(';'); break;
/*     */         }
/*     */       } else {
/* 148 */         String c = new String(Character.toChars(codePoint));
/* 149 */         if (encoder.canEncode(c))
/* 150 */           accum.append(c);
/*     */         else
/* 152 */           accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static String unescape(String string) {
/* 158 */     return unescape(string, false);
/*     */   }
/*     */ 
/*     */   static String unescape(String string, boolean strict)
/*     */   {
/* 168 */     return Parser.unescapeEntities(string, strict);
/*     */   }
/*     */ 
/*     */   private static Map<String, Character> loadEntities(String filename)
/*     */   {
/* 194 */     Properties properties = new Properties();
/* 195 */     Map entities = new HashMap();
/*     */     try {
/* 197 */       InputStream in = Entities.class.getResourceAsStream(filename);
/* 198 */       properties.load(in);
/* 199 */       in.close();
/*     */     } catch (IOException e) {
/* 201 */       throw new MissingResourceException("Error loading entities resource: " + e.getMessage(), "Entities", filename);
/*     */     }
/*     */ 
/* 204 */     for (Map.Entry entry : properties.entrySet()) {
/* 205 */       Character val = Character.valueOf((char)Integer.parseInt((String)entry.getValue(), 16));
/* 206 */       String name = (String)entry.getKey();
/* 207 */       entities.put(name, val);
/*     */     }
/* 209 */     return entities;
/*     */   }
/*     */ 
/*     */   private static Map<Character, String> toCharacterKey(Map<String, Character> inMap) {
/* 213 */     Map outMap = new HashMap();
/* 214 */     for (Map.Entry entry : inMap.entrySet()) {
/* 215 */       Character character = (Character)entry.getValue();
/* 216 */       String name = (String)entry.getKey();
/*     */ 
/* 218 */       if (outMap.containsKey(character))
/*     */       {
/* 220 */         if (name.toLowerCase().equals(name))
/* 221 */           outMap.put(character, name);
/*     */       }
/* 223 */       else outMap.put(character, name);
/*     */     }
/*     */ 
/* 226 */     return outMap;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 181 */     xhtmlByVal = new HashMap();
/* 182 */     base = loadEntities("entities-base.properties");
/* 183 */     baseByVal = toCharacterKey(base);
/* 184 */     full = loadEntities("entities-full.properties");
/* 185 */     fullByVal = toCharacterKey(full);
/*     */ 
/* 187 */     for (Object[] entity : xhtmlArray) {
/* 188 */       Character c = Character.valueOf((char)((Integer)entity[1]).intValue());
/* 189 */       xhtmlByVal.put(c, (String)entity[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum EscapeMode
/*     */   {
/*  21 */     xhtml(Entities.xhtmlByVal), 
/*     */ 
/*  23 */     base(Entities.baseByVal), 
/*     */ 
/*  25 */     extended(Entities.fullByVal);
/*     */ 
/*     */     private Map<Character, String> map;
/*     */ 
/*     */     private EscapeMode(Map<Character, String> map) {
/*  30 */       this.map = map;
/*     */     }
/*     */ 
/*     */     public Map<Character, String> getMap() {
/*  34 */       return this.map;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Entities
 * JD-Core Version:    0.6.2
 */