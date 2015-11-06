/*     */ package org.jsoup.nodes;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Map.Entry;
/*     */ import org.jsoup.helper.Validate;
/*     */ 
/*     */ public class Attribute
/*     */   implements Map.Entry<String, String>, Cloneable
/*     */ {
/*  13 */   private static final String[] booleanAttributes = { "allowfullscreen", "async", "autofocus", "checked", "compact", "declare", "default", "defer", "disabled", "formnovalidate", "hidden", "inert", "ismap", "itemscope", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "readonly", "required", "reversed", "seamless", "selected", "sortable", "truespeed", "typemustmatch" };
/*     */   private String key;
/*     */   private String value;
/*     */ 
/*     */   public Attribute(String key, String value)
/*     */   {
/*  30 */     Validate.notEmpty(key);
/*  31 */     Validate.notNull(value);
/*  32 */     this.key = key.trim().toLowerCase();
/*  33 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public String getKey()
/*     */   {
/*  41 */     return this.key;
/*     */   }
/*     */ 
/*     */   public void setKey(String key)
/*     */   {
/*  49 */     Validate.notEmpty(key);
/*  50 */     this.key = key.trim().toLowerCase();
/*     */   }
/*     */ 
/*     */   public String getValue()
/*     */   {
/*  58 */     return this.value;
/*     */   }
/*     */ 
/*     */   public String setValue(String value)
/*     */   {
/*  66 */     Validate.notNull(value);
/*  67 */     String old = this.value;
/*  68 */     this.value = value;
/*  69 */     return old;
/*     */   }
/*     */ 
/*     */   public String html()
/*     */   {
/*  77 */     StringBuilder accum = new StringBuilder();
/*  78 */     html(accum, new Document("").outputSettings());
/*  79 */     return accum.toString();
/*     */   }
/*     */ 
/*     */   protected void html(StringBuilder accum, Document.OutputSettings out) {
/*  83 */     accum.append(this.key);
/*  84 */     if (!shouldCollapseAttribute(out)) {
/*  85 */       accum.append("=\"");
/*  86 */       Entities.escape(accum, this.value, out, true, false, false);
/*  87 */       accum.append('"');
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  96 */     return html();
/*     */   }
/*     */ 
/*     */   public static Attribute createFromEncoded(String unencodedKey, String encodedValue)
/*     */   {
/* 106 */     String value = Entities.unescape(encodedValue, true);
/* 107 */     return new Attribute(unencodedKey, value);
/*     */   }
/*     */ 
/*     */   protected boolean isDataAttribute() {
/* 111 */     return (this.key.startsWith("data-")) && (this.key.length() > "data-".length());
/*     */   }
/*     */ 
/*     */   protected final boolean shouldCollapseAttribute(Document.OutputSettings out)
/*     */   {
/* 118 */     return (("".equals(this.value)) || (this.value.equalsIgnoreCase(this.key))) && (out.syntax() == Document.OutputSettings.Syntax.html) && (Arrays.binarySearch(booleanAttributes, this.key) >= 0);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 125 */     if (this == o) return true;
/* 126 */     if (!(o instanceof Attribute)) return false;
/*     */ 
/* 128 */     Attribute attribute = (Attribute)o;
/*     */ 
/* 130 */     if (this.key != null ? !this.key.equals(attribute.key) : attribute.key != null) return false;
/* 131 */     if (this.value != null ? !this.value.equals(attribute.value) : attribute.value != null) return false;
/*     */ 
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 138 */     int result = this.key != null ? this.key.hashCode() : 0;
/* 139 */     result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
/* 140 */     return result;
/*     */   }
/*     */ 
/*     */   public Attribute clone()
/*     */   {
/*     */     try {
/* 146 */       return (Attribute)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 148 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Attribute
 * JD-Core Version:    0.6.2
 */