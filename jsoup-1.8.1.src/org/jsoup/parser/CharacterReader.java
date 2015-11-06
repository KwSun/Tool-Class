/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.jsoup.helper.Validate;
/*     */ 
/*     */ class CharacterReader
/*     */ {
/*     */   static final char EOF = '￿';
/*     */   private final char[] input;
/*     */   private final int length;
/*  15 */   private int pos = 0;
/*  16 */   private int mark = 0;
/*     */ 
/*     */   CharacterReader(String input) {
/*  19 */     Validate.notNull(input);
/*  20 */     this.input = input.toCharArray();
/*  21 */     this.length = this.input.length;
/*     */   }
/*     */ 
/*     */   int pos() {
/*  25 */     return this.pos;
/*     */   }
/*     */ 
/*     */   boolean isEmpty() {
/*  29 */     return this.pos >= this.length;
/*     */   }
/*     */ 
/*     */   char current() {
/*  33 */     return this.pos >= this.length ? 65535 : this.input[this.pos];
/*     */   }
/*     */ 
/*     */   char consume() {
/*  37 */     char val = this.pos >= this.length ? 65535 : this.input[this.pos];
/*  38 */     this.pos += 1;
/*  39 */     return val;
/*     */   }
/*     */ 
/*     */   void unconsume() {
/*  43 */     this.pos -= 1;
/*     */   }
/*     */ 
/*     */   void advance() {
/*  47 */     this.pos += 1;
/*     */   }
/*     */ 
/*     */   void mark() {
/*  51 */     this.mark = this.pos;
/*     */   }
/*     */ 
/*     */   void rewindToMark() {
/*  55 */     this.pos = this.mark;
/*     */   }
/*     */ 
/*     */   String consumeAsString() {
/*  59 */     return new String(this.input, this.pos++, 1);
/*     */   }
/*     */ 
/*     */   int nextIndexOf(char c)
/*     */   {
/*  69 */     for (int i = this.pos; i < this.length; i++) {
/*  70 */       if (c == this.input[i])
/*  71 */         return i - this.pos;
/*     */     }
/*  73 */     return -1;
/*     */   }
/*     */ 
/*     */   int nextIndexOf(CharSequence seq)
/*     */   {
/*  84 */     char startChar = seq.charAt(0);
/*  85 */     for (int offset = this.pos; offset < this.length; offset++)
/*     */     {
/*  87 */       if (startChar != this.input[offset])
/*     */         do offset++; while ((offset < this.length) && (startChar != this.input[offset]));
/*  89 */       int i = offset + 1;
/*  90 */       int last = i + seq.length() - 1;
/*  91 */       if ((offset < this.length) && (last <= this.length)) {
/*  92 */         for (int j = 1; (i < last) && (seq.charAt(j) == this.input[i]); j++) i++;
/*  93 */         if (i == last)
/*  94 */           return offset - this.pos;
/*     */       }
/*     */     }
/*  97 */     return -1;
/*     */   }
/*     */ 
/*     */   String consumeTo(char c) {
/* 101 */     int offset = nextIndexOf(c);
/* 102 */     if (offset != -1) {
/* 103 */       String consumed = new String(this.input, this.pos, offset);
/* 104 */       this.pos += offset;
/* 105 */       return consumed;
/*     */     }
/* 107 */     return consumeToEnd();
/*     */   }
/*     */ 
/*     */   String consumeTo(String seq)
/*     */   {
/* 112 */     int offset = nextIndexOf(seq);
/* 113 */     if (offset != -1) {
/* 114 */       String consumed = new String(this.input, this.pos, offset);
/* 115 */       this.pos += offset;
/* 116 */       return consumed;
/*     */     }
/* 118 */     return consumeToEnd();
/*     */   }
/*     */ 
/*     */   String consumeToAny(char[] chars)
/*     */   {
/* 123 */     int start = this.pos;
/*     */ 
/* 125 */     while (this.pos < this.length) {
/* 126 */       for (int i = 0; i < chars.length; i++) {
/* 127 */         if (this.input[this.pos] == chars[i])
/*     */           break label61;
/*     */       }
/* 130 */       this.pos += 1;
/*     */     }
/*     */ 
/* 133 */     label61: return this.pos > start ? new String(this.input, start, this.pos - start) : "";
/*     */   }
/*     */ 
/*     */   String consumeToEnd() {
/* 137 */     String data = new String(this.input, this.pos, this.length - this.pos);
/* 138 */     this.pos = this.length;
/* 139 */     return data;
/*     */   }
/*     */ 
/*     */   String consumeLetterSequence() {
/* 143 */     int start = this.pos;
/* 144 */     while (this.pos < this.length) {
/* 145 */       char c = this.input[this.pos];
/* 146 */       if (((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z'))) break;
/* 147 */       this.pos += 1;
/*     */     }
/*     */ 
/* 152 */     return new String(this.input, start, this.pos - start);
/*     */   }
/*     */ 
/*     */   String consumeLetterThenDigitSequence() {
/* 156 */     int start = this.pos;
/* 157 */     while (this.pos < this.length) {
/* 158 */       char c = this.input[this.pos];
/* 159 */       if (((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z'))) break;
/* 160 */       this.pos += 1;
/*     */     }
/*     */ 
/* 164 */     while (!isEmpty()) {
/* 165 */       char c = this.input[this.pos];
/* 166 */       if ((c < '0') || (c > '9')) break;
/* 167 */       this.pos += 1;
/*     */     }
/*     */ 
/* 172 */     return new String(this.input, start, this.pos - start);
/*     */   }
/*     */ 
/*     */   String consumeHexSequence() {
/* 176 */     int start = this.pos;
/* 177 */     while (this.pos < this.length) {
/* 178 */       char c = this.input[this.pos];
/* 179 */       if (((c < '0') || (c > '9')) && ((c < 'A') || (c > 'F')) && ((c < 'a') || (c > 'f'))) break;
/* 180 */       this.pos += 1;
/*     */     }
/*     */ 
/* 184 */     return new String(this.input, start, this.pos - start);
/*     */   }
/*     */ 
/*     */   String consumeDigitSequence() {
/* 188 */     int start = this.pos;
/* 189 */     while (this.pos < this.length) {
/* 190 */       char c = this.input[this.pos];
/* 191 */       if ((c < '0') || (c > '9')) break;
/* 192 */       this.pos += 1;
/*     */     }
/*     */ 
/* 196 */     return new String(this.input, start, this.pos - start);
/*     */   }
/*     */ 
/*     */   boolean matches(char c) {
/* 200 */     return (!isEmpty()) && (this.input[this.pos] == c);
/*     */   }
/*     */ 
/*     */   boolean matches(String seq)
/*     */   {
/* 205 */     int scanLength = seq.length();
/* 206 */     if (scanLength > this.length - this.pos) {
/* 207 */       return false;
/*     */     }
/* 209 */     for (int offset = 0; offset < scanLength; offset++)
/* 210 */       if (seq.charAt(offset) != this.input[(this.pos + offset)])
/* 211 */         return false;
/* 212 */     return true;
/*     */   }
/*     */ 
/*     */   boolean matchesIgnoreCase(String seq) {
/* 216 */     int scanLength = seq.length();
/* 217 */     if (scanLength > this.length - this.pos) {
/* 218 */       return false;
/*     */     }
/* 220 */     for (int offset = 0; offset < scanLength; offset++) {
/* 221 */       char upScan = Character.toUpperCase(seq.charAt(offset));
/* 222 */       char upTarget = Character.toUpperCase(this.input[(this.pos + offset)]);
/* 223 */       if (upScan != upTarget)
/* 224 */         return false;
/*     */     }
/* 226 */     return true;
/*     */   }
/*     */ 
/*     */   boolean matchesAny(char[] seq) {
/* 230 */     if (isEmpty()) {
/* 231 */       return false;
/*     */     }
/* 233 */     char c = this.input[this.pos];
/* 234 */     for (char seek : seq) {
/* 235 */       if (seek == c)
/* 236 */         return true;
/*     */     }
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   boolean matchesLetter() {
/* 242 */     if (isEmpty())
/* 243 */       return false;
/* 244 */     char c = this.input[this.pos];
/* 245 */     return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
/*     */   }
/*     */ 
/*     */   boolean matchesDigit() {
/* 249 */     if (isEmpty())
/* 250 */       return false;
/* 251 */     char c = this.input[this.pos];
/* 252 */     return (c >= '0') && (c <= '9');
/*     */   }
/*     */ 
/*     */   boolean matchConsume(String seq) {
/* 256 */     if (matches(seq)) {
/* 257 */       this.pos += seq.length();
/* 258 */       return true;
/*     */     }
/* 260 */     return false;
/*     */   }
/*     */ 
/*     */   boolean matchConsumeIgnoreCase(String seq)
/*     */   {
/* 265 */     if (matchesIgnoreCase(seq)) {
/* 266 */       this.pos += seq.length();
/* 267 */       return true;
/*     */     }
/* 269 */     return false;
/*     */   }
/*     */ 
/*     */   boolean containsIgnoreCase(String seq)
/*     */   {
/* 275 */     String loScan = seq.toLowerCase(Locale.ENGLISH);
/* 276 */     String hiScan = seq.toUpperCase(Locale.ENGLISH);
/* 277 */     return (nextIndexOf(loScan) > -1) || (nextIndexOf(hiScan) > -1);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 282 */     return new String(this.input, this.pos, this.length - this.pos);
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.CharacterReader
 * JD-Core Version:    0.6.2
 */