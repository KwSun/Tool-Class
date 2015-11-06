/*     */ package org.jsoup.safety;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Attribute;
/*     */ import org.jsoup.nodes.Attributes;
/*     */ import org.jsoup.nodes.Element;
/*     */ 
/*     */ public class Whitelist
/*     */ {
/*     */   private Set<TagName> tagNames;
/*     */   private Map<TagName, Set<AttributeKey>> attributes;
/*     */   private Map<TagName, Map<AttributeKey, AttributeValue>> enforcedAttributes;
/*     */   private Map<TagName, Map<AttributeKey, Set<Protocol>>> protocols;
/*     */   private boolean preserveRelativeLinks;
/*     */ 
/*     */   public static Whitelist none()
/*     */   {
/*  63 */     return new Whitelist();
/*     */   }
/*     */ 
/*     */   public static Whitelist simpleText()
/*     */   {
/*  73 */     return new Whitelist().addTags(new String[] { "b", "em", "i", "strong", "u" });
/*     */   }
/*     */ 
/*     */   public static Whitelist basic()
/*     */   {
/*  90 */     return new Whitelist().addTags(new String[] { "a", "b", "blockquote", "br", "cite", "code", "dd", "dl", "dt", "em", "i", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong", "sub", "sup", "u", "ul" }).addAttributes("a", new String[] { "href" }).addAttributes("blockquote", new String[] { "cite" }).addAttributes("q", new String[] { "cite" }).addProtocols("a", "href", new String[] { "ftp", "http", "https", "mailto" }).addProtocols("blockquote", "cite", new String[] { "http", "https" }).addProtocols("cite", "cite", new String[] { "http", "https" }).addEnforcedAttribute("a", "rel", "nofollow");
/*     */   }
/*     */ 
/*     */   public static Whitelist basicWithImages()
/*     */   {
/* 116 */     return basic().addTags(new String[] { "img" }).addAttributes("img", new String[] { "align", "alt", "height", "src", "title", "width" }).addProtocols("img", "src", new String[] { "http", "https" });
/*     */   }
/*     */ 
/*     */   public static Whitelist relaxed()
/*     */   {
/* 133 */     return new Whitelist().addTags(new String[] { "a", "b", "blockquote", "br", "caption", "cite", "code", "col", "colgroup", "dd", "div", "dl", "dt", "em", "h1", "h2", "h3", "h4", "h5", "h6", "i", "img", "li", "ol", "p", "pre", "q", "small", "span", "strike", "strong", "sub", "sup", "table", "tbody", "td", "tfoot", "th", "thead", "tr", "u", "ul" }).addAttributes("a", new String[] { "href", "title" }).addAttributes("blockquote", new String[] { "cite" }).addAttributes("col", new String[] { "span", "width" }).addAttributes("colgroup", new String[] { "span", "width" }).addAttributes("img", new String[] { "align", "alt", "height", "src", "title", "width" }).addAttributes("ol", new String[] { "start", "type" }).addAttributes("q", new String[] { "cite" }).addAttributes("table", new String[] { "summary", "width" }).addAttributes("td", new String[] { "abbr", "axis", "colspan", "rowspan", "width" }).addAttributes("th", new String[] { "abbr", "axis", "colspan", "rowspan", "scope", "width" }).addAttributes("ul", new String[] { "type" }).addProtocols("a", "href", new String[] { "ftp", "http", "https", "mailto" }).addProtocols("blockquote", "cite", new String[] { "http", "https" }).addProtocols("cite", "cite", new String[] { "http", "https" }).addProtocols("img", "src", new String[] { "http", "https" }).addProtocols("q", "cite", new String[] { "http", "https" });
/*     */   }
/*     */ 
/*     */   public Whitelist()
/*     */   {
/* 172 */     this.tagNames = new HashSet();
/* 173 */     this.attributes = new HashMap();
/* 174 */     this.enforcedAttributes = new HashMap();
/* 175 */     this.protocols = new HashMap();
/* 176 */     this.preserveRelativeLinks = false;
/*     */   }
/*     */ 
/*     */   public Whitelist addTags(String[] tags)
/*     */   {
/* 186 */     Validate.notNull(tags);
/*     */ 
/* 188 */     for (String tagName : tags) {
/* 189 */       Validate.notEmpty(tagName);
/* 190 */       this.tagNames.add(TagName.valueOf(tagName));
/*     */     }
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */   public Whitelist addAttributes(String tag, String[] keys)
/*     */   {
/* 209 */     Validate.notEmpty(tag);
/* 210 */     Validate.notNull(keys);
/* 211 */     Validate.isTrue(keys.length > 0, "No attributes supplied.");
/*     */ 
/* 213 */     TagName tagName = TagName.valueOf(tag);
/* 214 */     if (!this.tagNames.contains(tagName))
/* 215 */       this.tagNames.add(tagName);
/* 216 */     Set attributeSet = new HashSet();
/* 217 */     for (String key : keys) {
/* 218 */       Validate.notEmpty(key);
/* 219 */       attributeSet.add(AttributeKey.valueOf(key));
/*     */     }
/* 221 */     if (this.attributes.containsKey(tagName)) {
/* 222 */       Set currentSet = (Set)this.attributes.get(tagName);
/* 223 */       currentSet.addAll(attributeSet);
/*     */     } else {
/* 225 */       this.attributes.put(tagName, attributeSet);
/*     */     }
/* 227 */     return this;
/*     */   }
/*     */ 
/*     */   public Whitelist addEnforcedAttribute(String tag, String key, String value)
/*     */   {
/* 243 */     Validate.notEmpty(tag);
/* 244 */     Validate.notEmpty(key);
/* 245 */     Validate.notEmpty(value);
/*     */ 
/* 247 */     TagName tagName = TagName.valueOf(tag);
/* 248 */     if (!this.tagNames.contains(tagName))
/* 249 */       this.tagNames.add(tagName);
/* 250 */     AttributeKey attrKey = AttributeKey.valueOf(key);
/* 251 */     AttributeValue attrVal = AttributeValue.valueOf(value);
/*     */ 
/* 253 */     if (this.enforcedAttributes.containsKey(tagName)) {
/* 254 */       ((Map)this.enforcedAttributes.get(tagName)).put(attrKey, attrVal);
/*     */     } else {
/* 256 */       Map attrMap = new HashMap();
/* 257 */       attrMap.put(attrKey, attrVal);
/* 258 */       this.enforcedAttributes.put(tagName, attrMap);
/*     */     }
/* 260 */     return this;
/*     */   }
/*     */ 
/*     */   public Whitelist preserveRelativeLinks(boolean preserve)
/*     */   {
/* 278 */     this.preserveRelativeLinks = preserve;
/* 279 */     return this;
/*     */   }
/*     */ 
/*     */   public Whitelist addProtocols(String tag, String key, String[] protocols)
/*     */   {
/* 294 */     Validate.notEmpty(tag);
/* 295 */     Validate.notEmpty(key);
/* 296 */     Validate.notNull(protocols);
/*     */ 
/* 298 */     TagName tagName = TagName.valueOf(tag);
/* 299 */     AttributeKey attrKey = AttributeKey.valueOf(key);
/*     */     Map attrMap;
/*     */     Map attrMap;
/* 303 */     if (this.protocols.containsKey(tagName)) {
/* 304 */       attrMap = (Map)this.protocols.get(tagName);
/*     */     } else {
/* 306 */       attrMap = new HashMap();
/* 307 */       this.protocols.put(tagName, attrMap);
/*     */     }
/*     */     Set protSet;
/*     */     Set protSet;
/* 309 */     if (attrMap.containsKey(attrKey)) {
/* 310 */       protSet = (Set)attrMap.get(attrKey);
/*     */     } else {
/* 312 */       protSet = new HashSet();
/* 313 */       attrMap.put(attrKey, protSet);
/*     */     }
/* 315 */     for (String protocol : protocols) {
/* 316 */       Validate.notEmpty(protocol);
/* 317 */       Protocol prot = Protocol.valueOf(protocol);
/* 318 */       protSet.add(prot);
/*     */     }
/* 320 */     return this;
/*     */   }
/*     */ 
/*     */   protected boolean isSafeTag(String tag)
/*     */   {
/* 329 */     return this.tagNames.contains(TagName.valueOf(tag));
/*     */   }
/*     */ 
/*     */   protected boolean isSafeAttribute(String tagName, Element el, Attribute attr)
/*     */   {
/* 340 */     TagName tag = TagName.valueOf(tagName);
/* 341 */     AttributeKey key = AttributeKey.valueOf(attr.getKey());
/*     */ 
/* 343 */     if ((this.attributes.containsKey(tag)) && 
/* 344 */       (((Set)this.attributes.get(tag)).contains(key))) {
/* 345 */       if (this.protocols.containsKey(tag)) {
/* 346 */         Map attrProts = (Map)this.protocols.get(tag);
/*     */ 
/* 348 */         return (!attrProts.containsKey(key)) || (testValidProtocol(el, attr, (Set)attrProts.get(key)));
/*     */       }
/* 350 */       return true;
/*     */     }
/*     */ 
/* 355 */     return (!tagName.equals(":all")) && (isSafeAttribute(":all", el, attr));
/*     */   }
/*     */ 
/*     */   private boolean testValidProtocol(Element el, Attribute attr, Set<Protocol> protocols)
/*     */   {
/* 361 */     String value = el.absUrl(attr.getKey());
/* 362 */     if (value.length() == 0)
/* 363 */       value = attr.getValue();
/* 364 */     if (!this.preserveRelativeLinks) {
/* 365 */       attr.setValue(value);
/*     */     }
/* 367 */     for (Protocol protocol : protocols) {
/* 368 */       String prot = protocol.toString() + ":";
/* 369 */       if (value.toLowerCase().startsWith(prot)) {
/* 370 */         return true;
/*     */       }
/*     */     }
/* 373 */     return false;
/*     */   }
/*     */ 
/*     */   Attributes getEnforcedAttributes(String tagName) {
/* 377 */     Attributes attrs = new Attributes();
/* 378 */     TagName tag = TagName.valueOf(tagName);
/* 379 */     if (this.enforcedAttributes.containsKey(tag)) {
/* 380 */       Map keyVals = (Map)this.enforcedAttributes.get(tag);
/* 381 */       for (Map.Entry entry : keyVals.entrySet()) {
/* 382 */         attrs.put(((AttributeKey)entry.getKey()).toString(), ((AttributeValue)entry.getValue()).toString());
/*     */       }
/*     */     }
/* 385 */     return attrs;
/*     */   }
/*     */ 
/*     */   static abstract class TypedValue
/*     */   {
/*     */     private String value;
/*     */ 
/*     */     TypedValue(String value)
/*     */     {
/* 434 */       Validate.notNull(value);
/* 435 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 440 */       int prime = 31;
/* 441 */       int result = 1;
/* 442 */       result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
/* 443 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 448 */       if (this == obj) return true;
/* 449 */       if (obj == null) return false;
/* 450 */       if (getClass() != obj.getClass()) return false;
/* 451 */       TypedValue other = (TypedValue)obj;
/* 452 */       if (this.value == null) {
/* 453 */         if (other.value != null) return false; 
/*     */       }
/* 454 */       else if (!this.value.equals(other.value)) return false;
/* 455 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 460 */       return this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Protocol extends Whitelist.TypedValue
/*     */   {
/*     */     Protocol(String value)
/*     */     {
/* 422 */       super();
/*     */     }
/*     */ 
/*     */     static Protocol valueOf(String value) {
/* 426 */       return new Protocol(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class AttributeValue extends Whitelist.TypedValue
/*     */   {
/*     */     AttributeValue(String value)
/*     */     {
/* 412 */       super();
/*     */     }
/*     */ 
/*     */     static AttributeValue valueOf(String value) {
/* 416 */       return new AttributeValue(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class AttributeKey extends Whitelist.TypedValue
/*     */   {
/*     */     AttributeKey(String value)
/*     */     {
/* 402 */       super();
/*     */     }
/*     */ 
/*     */     static AttributeKey valueOf(String value) {
/* 406 */       return new AttributeKey(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class TagName extends Whitelist.TypedValue
/*     */   {
/*     */     TagName(String value)
/*     */     {
/* 392 */       super();
/*     */     }
/*     */ 
/*     */     static TagName valueOf(String value) {
/* 396 */       return new TagName(value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.safety.Whitelist
 * JD-Core Version:    0.6.2
 */