/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.jsoup.helper.Validate;
/*     */ 
/*     */ public class Tag
/*     */ {
/*  14 */   private static final Map<String, Tag> tags = new HashMap();
/*     */   private String tagName;
/*  17 */   private boolean isBlock = true;
/*  18 */   private boolean formatAsBlock = true;
/*  19 */   private boolean canContainBlock = true;
/*  20 */   private boolean canContainInline = true;
/*  21 */   private boolean empty = false;
/*  22 */   private boolean selfClosing = false;
/*  23 */   private boolean preserveWhitespace = false;
/*  24 */   private boolean formList = false;
/*  25 */   private boolean formSubmit = false;
/*     */ 
/* 221 */   private static final String[] blockTags = { "html", "head", "body", "frameset", "script", "noscript", "style", "meta", "link", "title", "frame", "noframes", "section", "nav", "aside", "hgroup", "header", "footer", "p", "h1", "h2", "h3", "h4", "h5", "h6", "ul", "ol", "pre", "div", "blockquote", "hr", "address", "figure", "figcaption", "form", "fieldset", "ins", "del", "s", "dl", "dt", "dd", "li", "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th", "td", "video", "audio", "canvas", "details", "menu", "plaintext" };
/*     */ 
/* 228 */   private static final String[] inlineTags = { "object", "base", "font", "tt", "i", "b", "u", "big", "small", "em", "strong", "dfn", "code", "samp", "kbd", "var", "cite", "abbr", "time", "acronym", "mark", "ruby", "rt", "rp", "a", "img", "br", "wbr", "map", "q", "sub", "sup", "bdo", "iframe", "embed", "span", "input", "select", "textarea", "label", "button", "optgroup", "option", "legend", "datalist", "keygen", "output", "progress", "meter", "area", "param", "source", "track", "summary", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track" };
/*     */ 
/* 235 */   private static final String[] emptyTags = { "meta", "link", "base", "frame", "img", "br", "wbr", "embed", "hr", "input", "keygen", "col", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track" };
/*     */ 
/* 239 */   private static final String[] formatAsInlineTags = { "title", "a", "p", "h1", "h2", "h3", "h4", "h5", "h6", "pre", "address", "li", "th", "td", "script", "style", "ins", "del", "s" };
/*     */ 
/* 243 */   private static final String[] preserveWhitespaceTags = { "pre", "plaintext", "title", "textarea" };
/*     */ 
/* 248 */   private static final String[] formListedTags = { "button", "fieldset", "input", "keygen", "object", "output", "select", "textarea" };
/*     */ 
/* 251 */   private static final String[] formSubmitTags = { "input", "keygen", "object", "select", "textarea" };
/*     */ 
/*     */   private Tag(String tagName)
/*     */   {
/*  28 */     this.tagName = tagName.toLowerCase();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  37 */     return this.tagName;
/*     */   }
/*     */ 
/*     */   public static Tag valueOf(String tagName)
/*     */   {
/*  49 */     Validate.notNull(tagName);
/*  50 */     Tag tag = (Tag)tags.get(tagName);
/*     */ 
/*  52 */     if (tag == null) {
/*  53 */       tagName = tagName.trim().toLowerCase();
/*  54 */       Validate.notEmpty(tagName);
/*  55 */       tag = (Tag)tags.get(tagName);
/*     */ 
/*  57 */       if (tag == null)
/*     */       {
/*  59 */         tag = new Tag(tagName);
/*  60 */         tag.isBlock = false;
/*  61 */         tag.canContainBlock = true;
/*     */       }
/*     */     }
/*  64 */     return tag;
/*     */   }
/*     */ 
/*     */   public boolean isBlock()
/*     */   {
/*  73 */     return this.isBlock;
/*     */   }
/*     */ 
/*     */   public boolean formatAsBlock()
/*     */   {
/*  82 */     return this.formatAsBlock;
/*     */   }
/*     */ 
/*     */   public boolean canContainBlock()
/*     */   {
/*  91 */     return this.canContainBlock;
/*     */   }
/*     */ 
/*     */   public boolean isInline()
/*     */   {
/* 100 */     return !this.isBlock;
/*     */   }
/*     */ 
/*     */   public boolean isData()
/*     */   {
/* 109 */     return (!this.canContainInline) && (!isEmpty());
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 118 */     return this.empty;
/*     */   }
/*     */ 
/*     */   public boolean isSelfClosing()
/*     */   {
/* 127 */     return (this.empty) || (this.selfClosing);
/*     */   }
/*     */ 
/*     */   public boolean isKnownTag()
/*     */   {
/* 136 */     return tags.containsKey(this.tagName);
/*     */   }
/*     */ 
/*     */   public static boolean isKnownTag(String tagName)
/*     */   {
/* 146 */     return tags.containsKey(tagName);
/*     */   }
/*     */ 
/*     */   public boolean preserveWhitespace()
/*     */   {
/* 155 */     return this.preserveWhitespace;
/*     */   }
/*     */ 
/*     */   public boolean isFormListed()
/*     */   {
/* 163 */     return this.formList;
/*     */   }
/*     */ 
/*     */   public boolean isFormSubmittable()
/*     */   {
/* 171 */     return this.formSubmit;
/*     */   }
/*     */ 
/*     */   Tag setSelfClosing() {
/* 175 */     this.selfClosing = true;
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 181 */     if (this == o) return true;
/* 182 */     if (!(o instanceof Tag)) return false;
/*     */ 
/* 184 */     Tag tag = (Tag)o;
/*     */ 
/* 186 */     if (this.canContainBlock != tag.canContainBlock) return false;
/* 187 */     if (this.canContainInline != tag.canContainInline) return false;
/* 188 */     if (this.empty != tag.empty) return false;
/* 189 */     if (this.formatAsBlock != tag.formatAsBlock) return false;
/* 190 */     if (this.isBlock != tag.isBlock) return false;
/* 191 */     if (this.preserveWhitespace != tag.preserveWhitespace) return false;
/* 192 */     if (this.selfClosing != tag.selfClosing) return false;
/* 193 */     if (this.formList != tag.formList) return false;
/* 194 */     if (this.formSubmit != tag.formSubmit) return false;
/* 195 */     if (!this.tagName.equals(tag.tagName)) return false;
/*     */ 
/* 197 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 202 */     int result = this.tagName.hashCode();
/* 203 */     result = 31 * result + (this.isBlock ? 1 : 0);
/* 204 */     result = 31 * result + (this.formatAsBlock ? 1 : 0);
/* 205 */     result = 31 * result + (this.canContainBlock ? 1 : 0);
/* 206 */     result = 31 * result + (this.canContainInline ? 1 : 0);
/* 207 */     result = 31 * result + (this.empty ? 1 : 0);
/* 208 */     result = 31 * result + (this.selfClosing ? 1 : 0);
/* 209 */     result = 31 * result + (this.preserveWhitespace ? 1 : 0);
/* 210 */     result = 31 * result + (this.formList ? 1 : 0);
/* 211 */     result = 31 * result + (this.formSubmit ? 1 : 0);
/* 212 */     return result;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 216 */     return this.tagName;
/*     */   }
/*     */ 
/*     */   private static void register(Tag tag)
/*     */   {
/* 304 */     tags.put(tag.tagName, tag);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 257 */     for (String tagName : blockTags) {
/* 258 */       Tag tag = new Tag(tagName);
/* 259 */       register(tag);
/*     */     }
/* 261 */     for (String tagName : inlineTags) {
/* 262 */       Tag tag = new Tag(tagName);
/* 263 */       tag.isBlock = false;
/* 264 */       tag.canContainBlock = false;
/* 265 */       tag.formatAsBlock = false;
/* 266 */       register(tag);
/*     */     }
/*     */ 
/* 270 */     for (String tagName : emptyTags) {
/* 271 */       Tag tag = (Tag)tags.get(tagName);
/* 272 */       Validate.notNull(tag);
/* 273 */       tag.canContainBlock = false;
/* 274 */       tag.canContainInline = false;
/* 275 */       tag.empty = true;
/*     */     }
/*     */ 
/* 278 */     for (String tagName : formatAsInlineTags) {
/* 279 */       Tag tag = (Tag)tags.get(tagName);
/* 280 */       Validate.notNull(tag);
/* 281 */       tag.formatAsBlock = false;
/*     */     }
/*     */ 
/* 284 */     for (String tagName : preserveWhitespaceTags) {
/* 285 */       Tag tag = (Tag)tags.get(tagName);
/* 286 */       Validate.notNull(tag);
/* 287 */       tag.preserveWhitespace = true;
/*     */     }
/*     */ 
/* 290 */     for (String tagName : formListedTags) {
/* 291 */       Tag tag = (Tag)tags.get(tagName);
/* 292 */       Validate.notNull(tag);
/* 293 */       tag.formList = true;
/*     */     }
/*     */ 
/* 296 */     for (String tagName : formSubmitTags) {
/* 297 */       Tag tag = (Tag)tags.get(tagName);
/* 298 */       Validate.notNull(tag);
/* 299 */       tag.formSubmit = true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.Tag
 * JD-Core Version:    0.6.2
 */