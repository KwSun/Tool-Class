/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Attribute;
/*     */ import org.jsoup.nodes.Attributes;
/*     */ 
/*     */ abstract class Token
/*     */ {
/*     */   TokenType type;
/*     */ 
/*     */   String tokenType()
/*     */   {
/*  17 */     return getClass().getSimpleName();
/*     */   }
/*     */ 
/*     */   boolean isDoctype()
/*     */   {
/* 224 */     return this.type == TokenType.Doctype;
/*     */   }
/*     */ 
/*     */   Doctype asDoctype() {
/* 228 */     return (Doctype)this;
/*     */   }
/*     */ 
/*     */   boolean isStartTag() {
/* 232 */     return this.type == TokenType.StartTag;
/*     */   }
/*     */ 
/*     */   StartTag asStartTag() {
/* 236 */     return (StartTag)this;
/*     */   }
/*     */ 
/*     */   boolean isEndTag() {
/* 240 */     return this.type == TokenType.EndTag;
/*     */   }
/*     */ 
/*     */   EndTag asEndTag() {
/* 244 */     return (EndTag)this;
/*     */   }
/*     */ 
/*     */   boolean isComment() {
/* 248 */     return this.type == TokenType.Comment;
/*     */   }
/*     */ 
/*     */   Comment asComment() {
/* 252 */     return (Comment)this;
/*     */   }
/*     */ 
/*     */   boolean isCharacter() {
/* 256 */     return this.type == TokenType.Character;
/*     */   }
/*     */ 
/*     */   Character asCharacter() {
/* 260 */     return (Character)this;
/*     */   }
/*     */ 
/*     */   boolean isEOF() {
/* 264 */     return this.type == TokenType.EOF;
/*     */   }
/*     */ 
/*     */   static enum TokenType {
/* 268 */     Doctype, 
/* 269 */     StartTag, 
/* 270 */     EndTag, 
/* 271 */     Comment, 
/* 272 */     Character, 
/* 273 */     EOF;
/*     */   }
/*     */ 
/*     */   static class EOF extends Token
/*     */   {
/*     */     EOF()
/*     */     {
/* 218 */       super();
/* 219 */       this.type = Token.TokenType.EOF;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Character extends Token
/*     */   {
/*     */     private final String data;
/*     */ 
/*     */     Character(String data)
/*     */     {
/* 202 */       super();
/* 203 */       this.type = Token.TokenType.Character;
/* 204 */       this.data = data;
/*     */     }
/*     */ 
/*     */     String getData() {
/* 208 */       return this.data;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 213 */       return getData();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Comment extends Token
/*     */   {
/* 182 */     final StringBuilder data = new StringBuilder();
/* 183 */     boolean bogus = false;
/*     */ 
/* 185 */     Comment() { super();
/* 186 */       this.type = Token.TokenType.Comment; }
/*     */ 
/*     */     String getData()
/*     */     {
/* 190 */       return this.data.toString();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 195 */       return "<!--" + getData() + "-->";
/*     */     }
/*     */   }
/*     */ 
/*     */   static class EndTag extends Token.Tag
/*     */   {
/*     */     EndTag()
/*     */     {
/* 167 */       this.type = Token.TokenType.EndTag;
/*     */     }
/*     */ 
/*     */     EndTag(String name) {
/* 171 */       this();
/* 172 */       this.tagName = name;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 177 */       return "</" + name() + ">";
/*     */     }
/*     */   }
/*     */ 
/*     */   static class StartTag extends Token.Tag
/*     */   {
/*     */     StartTag()
/*     */     {
/* 140 */       this.attributes = new Attributes();
/* 141 */       this.type = Token.TokenType.StartTag;
/*     */     }
/*     */ 
/*     */     StartTag(String name) {
/* 145 */       this();
/* 146 */       this.tagName = name;
/*     */     }
/*     */ 
/*     */     StartTag(String name, Attributes attributes) {
/* 150 */       this();
/* 151 */       this.tagName = name;
/* 152 */       this.attributes = attributes;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 157 */       if ((this.attributes != null) && (this.attributes.size() > 0)) {
/* 158 */         return "<" + name() + " " + this.attributes.toString() + ">";
/*     */       }
/* 160 */       return "<" + name() + ">";
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class Tag extends Token
/*     */   {
/*     */     protected String tagName;
/*     */     private String pendingAttributeName;
/*     */     private StringBuilder pendingAttributeValue;
/*  52 */     boolean selfClosing = false;
/*     */     Attributes attributes;
/*     */ 
/*     */     Tag()
/*     */     {
/*  47 */       super();
/*     */     }
/*     */ 
/*     */     void newAttribute()
/*     */     {
/*  56 */       if (this.attributes == null) {
/*  57 */         this.attributes = new Attributes();
/*     */       }
/*  59 */       if (this.pendingAttributeName != null)
/*     */       {
/*     */         Attribute attribute;
/*     */         Attribute attribute;
/*  61 */         if (this.pendingAttributeValue == null)
/*  62 */           attribute = new Attribute(this.pendingAttributeName, "");
/*     */         else
/*  64 */           attribute = new Attribute(this.pendingAttributeName, this.pendingAttributeValue.toString());
/*  65 */         this.attributes.put(attribute);
/*     */       }
/*  67 */       this.pendingAttributeName = null;
/*  68 */       if (this.pendingAttributeValue != null)
/*  69 */         this.pendingAttributeValue.delete(0, this.pendingAttributeValue.length());
/*     */     }
/*     */ 
/*     */     void finaliseTag()
/*     */     {
/*  74 */       if (this.pendingAttributeName != null)
/*     */       {
/*  76 */         newAttribute();
/*     */       }
/*     */     }
/*     */ 
/*     */     String name() {
/*  81 */       Validate.isFalse((this.tagName == null) || (this.tagName.length() == 0));
/*  82 */       return this.tagName;
/*     */     }
/*     */ 
/*     */     Tag name(String name) {
/*  86 */       this.tagName = name;
/*  87 */       return this;
/*     */     }
/*     */ 
/*     */     boolean isSelfClosing() {
/*  91 */       return this.selfClosing;
/*     */     }
/*     */ 
/*     */     Attributes getAttributes()
/*     */     {
/*  96 */       return this.attributes;
/*     */     }
/*     */ 
/*     */     void appendTagName(String append)
/*     */     {
/* 101 */       this.tagName = (this.tagName == null ? append : this.tagName.concat(append));
/*     */     }
/*     */ 
/*     */     void appendTagName(char append) {
/* 105 */       appendTagName(String.valueOf(append));
/*     */     }
/*     */ 
/*     */     void appendAttributeName(String append) {
/* 109 */       this.pendingAttributeName = (this.pendingAttributeName == null ? append : this.pendingAttributeName.concat(append));
/*     */     }
/*     */ 
/*     */     void appendAttributeName(char append) {
/* 113 */       appendAttributeName(String.valueOf(append));
/*     */     }
/*     */ 
/*     */     void appendAttributeValue(String append) {
/* 117 */       ensureAttributeValue();
/* 118 */       this.pendingAttributeValue.append(append);
/*     */     }
/*     */ 
/*     */     void appendAttributeValue(char append) {
/* 122 */       ensureAttributeValue();
/* 123 */       this.pendingAttributeValue.append(append);
/*     */     }
/*     */ 
/*     */     void appendAttributeValue(char[] append) {
/* 127 */       ensureAttributeValue();
/* 128 */       this.pendingAttributeValue.append(append);
/*     */     }
/*     */ 
/*     */     private final void ensureAttributeValue() {
/* 132 */       if (this.pendingAttributeValue == null)
/* 133 */         this.pendingAttributeValue = new StringBuilder();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Doctype extends Token
/*     */   {
/*  21 */     final StringBuilder name = new StringBuilder();
/*  22 */     final StringBuilder publicIdentifier = new StringBuilder();
/*  23 */     final StringBuilder systemIdentifier = new StringBuilder();
/*  24 */     boolean forceQuirks = false;
/*     */ 
/*  26 */     Doctype() { super();
/*  27 */       this.type = Token.TokenType.Doctype; }
/*     */ 
/*     */     String getName()
/*     */     {
/*  31 */       return this.name.toString();
/*     */     }
/*     */ 
/*     */     String getPublicIdentifier() {
/*  35 */       return this.publicIdentifier.toString();
/*     */     }
/*     */ 
/*     */     public String getSystemIdentifier() {
/*  39 */       return this.systemIdentifier.toString();
/*     */     }
/*     */ 
/*     */     public boolean isForceQuirks() {
/*  43 */       return this.forceQuirks;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.Token
 * JD-Core Version:    0.6.2
 */