/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ 
/*     */ public class TokenQueue
/*     */ {
/*     */   private String queue;
/*  13 */   private int pos = 0;
/*     */   private static final char ESC = '\\';
/*     */ 
/*     */   public TokenQueue(String data)
/*     */   {
/*  22 */     Validate.notNull(data);
/*  23 */     this.queue = data;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  31 */     return remainingLength() == 0;
/*     */   }
/*     */ 
/*     */   private int remainingLength() {
/*  35 */     return this.queue.length() - this.pos;
/*     */   }
/*     */ 
/*     */   public char peek()
/*     */   {
/*  43 */     return isEmpty() ? '\000' : this.queue.charAt(this.pos);
/*     */   }
/*     */ 
/*     */   public void addFirst(Character c)
/*     */   {
/*  51 */     addFirst(c.toString());
/*     */   }
/*     */ 
/*     */   public void addFirst(String seq)
/*     */   {
/*  60 */     this.queue = (seq + this.queue.substring(this.pos));
/*  61 */     this.pos = 0;
/*     */   }
/*     */ 
/*     */   public boolean matches(String seq)
/*     */   {
/*  70 */     return this.queue.regionMatches(true, this.pos, seq, 0, seq.length());
/*     */   }
/*     */ 
/*     */   public boolean matchesCS(String seq)
/*     */   {
/*  79 */     return this.queue.startsWith(seq, this.pos);
/*     */   }
/*     */ 
/*     */   public boolean matchesAny(String[] seq)
/*     */   {
/*  89 */     for (String s : seq) {
/*  90 */       if (matches(s))
/*  91 */         return true;
/*     */     }
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean matchesAny(char[] seq) {
/*  97 */     if (isEmpty()) {
/*  98 */       return false;
/*     */     }
/* 100 */     for (char c : seq) {
/* 101 */       if (this.queue.charAt(this.pos) == c)
/* 102 */         return true;
/*     */     }
/* 104 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean matchesStartTag()
/*     */   {
/* 109 */     return (remainingLength() >= 2) && (this.queue.charAt(this.pos) == '<') && (Character.isLetter(this.queue.charAt(this.pos + 1)));
/*     */   }
/*     */ 
/*     */   public boolean matchChomp(String seq)
/*     */   {
/* 119 */     if (matches(seq)) {
/* 120 */       this.pos += seq.length();
/* 121 */       return true;
/*     */     }
/* 123 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean matchesWhitespace()
/*     */   {
/* 132 */     return (!isEmpty()) && (StringUtil.isWhitespace(this.queue.charAt(this.pos)));
/*     */   }
/*     */ 
/*     */   public boolean matchesWord()
/*     */   {
/* 140 */     return (!isEmpty()) && (Character.isLetterOrDigit(this.queue.charAt(this.pos)));
/*     */   }
/*     */ 
/*     */   public void advance()
/*     */   {
/* 147 */     if (!isEmpty()) this.pos += 1;
/*     */   }
/*     */ 
/*     */   public char consume()
/*     */   {
/* 155 */     return this.queue.charAt(this.pos++);
/*     */   }
/*     */ 
/*     */   public void consume(String seq)
/*     */   {
/* 166 */     if (!matches(seq))
/* 167 */       throw new IllegalStateException("Queue did not match expected sequence");
/* 168 */     int len = seq.length();
/* 169 */     if (len > remainingLength()) {
/* 170 */       throw new IllegalStateException("Queue not long enough to consume sequence");
/*     */     }
/* 172 */     this.pos += len;
/*     */   }
/*     */ 
/*     */   public String consumeTo(String seq)
/*     */   {
/* 181 */     int offset = this.queue.indexOf(seq, this.pos);
/* 182 */     if (offset != -1) {
/* 183 */       String consumed = this.queue.substring(this.pos, offset);
/* 184 */       this.pos += consumed.length();
/* 185 */       return consumed;
/*     */     }
/* 187 */     return remainder();
/*     */   }
/*     */ 
/*     */   public String consumeToIgnoreCase(String seq)
/*     */   {
/* 192 */     int start = this.pos;
/* 193 */     String first = seq.substring(0, 1);
/* 194 */     boolean canScan = first.toLowerCase().equals(first.toUpperCase());
/* 195 */     while ((!isEmpty()) && 
/* 196 */       (!matches(seq)))
/*     */     {
/* 199 */       if (canScan) {
/* 200 */         int skip = this.queue.indexOf(first, this.pos) - this.pos;
/* 201 */         if (skip == 0)
/* 202 */           this.pos += 1;
/* 203 */         else if (skip < 0)
/* 204 */           this.pos = this.queue.length();
/*     */         else
/* 206 */           this.pos += skip;
/*     */       }
/*     */       else {
/* 209 */         this.pos += 1;
/*     */       }
/*     */     }
/* 212 */     String data = this.queue.substring(start, this.pos);
/* 213 */     return data;
/*     */   }
/*     */ 
/*     */   public String consumeToAny(String[] seq)
/*     */   {
/* 224 */     int start = this.pos;
/* 225 */     while ((!isEmpty()) && (!matchesAny(seq))) {
/* 226 */       this.pos += 1;
/*     */     }
/*     */ 
/* 229 */     String data = this.queue.substring(start, this.pos);
/* 230 */     return data;
/*     */   }
/*     */ 
/*     */   public String chompTo(String seq)
/*     */   {
/* 242 */     String data = consumeTo(seq);
/* 243 */     matchChomp(seq);
/* 244 */     return data;
/*     */   }
/*     */ 
/*     */   public String chompToIgnoreCase(String seq) {
/* 248 */     String data = consumeToIgnoreCase(seq);
/* 249 */     matchChomp(seq);
/* 250 */     return data;
/*     */   }
/*     */ 
/*     */   public String chompBalanced(char open, char close)
/*     */   {
/* 263 */     int start = -1;
/* 264 */     int end = -1;
/* 265 */     int depth = 0;
/* 266 */     char last = '\000';
/*     */     do
/*     */     {
/* 269 */       if (isEmpty()) break;
/* 270 */       Character c = Character.valueOf(consume());
/* 271 */       if ((last == 0) || (last != '\\')) {
/* 272 */         if (c.equals(Character.valueOf(open))) {
/* 273 */           depth++;
/* 274 */           if (start == -1)
/* 275 */             start = this.pos;
/*     */         }
/* 277 */         else if (c.equals(Character.valueOf(close))) {
/* 278 */           depth--;
/*     */         }
/*     */       }
/* 281 */       if ((depth > 0) && (last != 0))
/* 282 */         end = this.pos;
/* 283 */       last = c.charValue();
/* 284 */     }while (depth > 0);
/* 285 */     return end >= 0 ? this.queue.substring(start, end) : "";
/*     */   }
/*     */ 
/*     */   public static String unescape(String in)
/*     */   {
/* 294 */     StringBuilder out = new StringBuilder();
/* 295 */     char last = '\000';
/* 296 */     for (char c : in.toCharArray()) {
/* 297 */       if (c == '\\') {
/* 298 */         if ((last != 0) && (last == '\\'))
/* 299 */           out.append(c);
/*     */       }
/*     */       else
/* 302 */         out.append(c);
/* 303 */       last = c;
/*     */     }
/* 305 */     return out.toString();
/*     */   }
/*     */ 
/*     */   public boolean consumeWhitespace()
/*     */   {
/* 312 */     boolean seen = false;
/* 313 */     while (matchesWhitespace()) {
/* 314 */       this.pos += 1;
/* 315 */       seen = true;
/*     */     }
/* 317 */     return seen;
/*     */   }
/*     */ 
/*     */   public String consumeWord()
/*     */   {
/* 325 */     int start = this.pos;
/* 326 */     while (matchesWord())
/* 327 */       this.pos += 1;
/* 328 */     return this.queue.substring(start, this.pos);
/*     */   }
/*     */ 
/*     */   public String consumeTagName()
/*     */   {
/* 337 */     int start = this.pos;
/* 338 */     while (!isEmpty()) { if (!matchesWord()) if (!matchesAny(new char[] { ':', '_', '-' }))
/*     */           break; this.pos += 1;
/*     */     }
/* 341 */     return this.queue.substring(start, this.pos);
/*     */   }
/*     */ 
/*     */   public String consumeElementSelector()
/*     */   {
/* 350 */     int start = this.pos;
/* 351 */     while (!isEmpty()) { if (!matchesWord()) if (!matchesAny(new char[] { '|', '_', '-' }))
/*     */           break; this.pos += 1;
/*     */     }
/* 354 */     return this.queue.substring(start, this.pos);
/*     */   }
/*     */ 
/*     */   public String consumeCssIdentifier()
/*     */   {
/* 363 */     int start = this.pos;
/* 364 */     while (!isEmpty()) { if (!matchesWord()) if (!matchesAny(new char[] { '-', '_' }))
/*     */           break; this.pos += 1;
/*     */     }
/* 367 */     return this.queue.substring(start, this.pos);
/*     */   }
/*     */ 
/*     */   public String consumeAttributeKey()
/*     */   {
/* 375 */     int start = this.pos;
/* 376 */     while (!isEmpty()) { if (!matchesWord()) if (!matchesAny(new char[] { '-', '_', ':' }))
/*     */           break; this.pos += 1;
/*     */     }
/* 379 */     return this.queue.substring(start, this.pos);
/*     */   }
/*     */ 
/*     */   public String remainder()
/*     */   {
/* 387 */     String remainder = this.queue.substring(this.pos, this.queue.length());
/* 388 */     this.pos = this.queue.length();
/* 389 */     return remainder;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 393 */     return this.queue.substring(this.pos);
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.TokenQueue
 * JD-Core Version:    0.6.2
 */