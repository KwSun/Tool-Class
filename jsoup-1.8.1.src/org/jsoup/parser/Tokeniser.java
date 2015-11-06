/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Entities;
/*     */ 
/*     */ class Tokeniser
/*     */ {
/*     */   static final char replacementChar = '�';
/*     */   private CharacterReader reader;
/*     */   private ParseErrorList errors;
/*  18 */   private TokeniserState state = TokeniserState.Data;
/*     */   private Token emitPending;
/*  20 */   private boolean isEmitPending = false;
/*  21 */   private StringBuilder charBuffer = new StringBuilder();
/*     */   StringBuilder dataBuffer;
/*     */   Token.Tag tagPending;
/*     */   Token.Doctype doctypePending;
/*     */   Token.Comment commentPending;
/*     */   private Token.StartTag lastStartTag;
/*  28 */   private boolean selfClosingFlagAcknowledged = true;
/*     */ 
/*     */   Tokeniser(CharacterReader reader, ParseErrorList errors) {
/*  31 */     this.reader = reader;
/*  32 */     this.errors = errors;
/*     */   }
/*     */ 
/*     */   Token read() {
/*  36 */     if (!this.selfClosingFlagAcknowledged) {
/*  37 */       error("Self closing flag not acknowledged");
/*  38 */       this.selfClosingFlagAcknowledged = true;
/*     */     }
/*     */ 
/*  41 */     while (!this.isEmitPending) {
/*  42 */       this.state.read(this, this.reader);
/*     */     }
/*     */ 
/*  45 */     if (this.charBuffer.length() > 0) {
/*  46 */       String str = this.charBuffer.toString();
/*  47 */       this.charBuffer.delete(0, this.charBuffer.length());
/*  48 */       return new Token.Character(str);
/*     */     }
/*  50 */     this.isEmitPending = false;
/*  51 */     return this.emitPending;
/*     */   }
/*     */ 
/*     */   void emit(Token token)
/*     */   {
/*  56 */     Validate.isFalse(this.isEmitPending, "There is an unread token pending!");
/*     */ 
/*  58 */     this.emitPending = token;
/*  59 */     this.isEmitPending = true;
/*     */ 
/*  61 */     if (token.type == Token.TokenType.StartTag) {
/*  62 */       Token.StartTag startTag = (Token.StartTag)token;
/*  63 */       this.lastStartTag = startTag;
/*  64 */       if (startTag.selfClosing)
/*  65 */         this.selfClosingFlagAcknowledged = false;
/*  66 */     } else if (token.type == Token.TokenType.EndTag) {
/*  67 */       Token.EndTag endTag = (Token.EndTag)token;
/*  68 */       if (endTag.attributes != null)
/*  69 */         error("Attributes incorrectly present on end tag");
/*     */     }
/*     */   }
/*     */ 
/*     */   void emit(String str)
/*     */   {
/*  76 */     this.charBuffer.append(str);
/*     */   }
/*     */ 
/*     */   void emit(char[] chars) {
/*  80 */     this.charBuffer.append(chars);
/*     */   }
/*     */ 
/*     */   void emit(char c) {
/*  84 */     this.charBuffer.append(c);
/*     */   }
/*     */ 
/*     */   TokeniserState getState() {
/*  88 */     return this.state;
/*     */   }
/*     */ 
/*     */   void transition(TokeniserState state) {
/*  92 */     this.state = state;
/*     */   }
/*     */ 
/*     */   void advanceTransition(TokeniserState state) {
/*  96 */     this.reader.advance();
/*  97 */     this.state = state;
/*     */   }
/*     */ 
/*     */   void acknowledgeSelfClosingFlag() {
/* 101 */     this.selfClosingFlagAcknowledged = true;
/*     */   }
/*     */ 
/*     */   char[] consumeCharacterReference(Character additionalAllowedCharacter, boolean inAttribute) {
/* 105 */     if (this.reader.isEmpty())
/* 106 */       return null;
/* 107 */     if ((additionalAllowedCharacter != null) && (additionalAllowedCharacter.charValue() == this.reader.current()))
/* 108 */       return null;
/* 109 */     if (this.reader.matchesAny(new char[] { '\t', '\n', '\r', '\f', ' ', '<', '&' })) {
/* 110 */       return null;
/*     */     }
/* 112 */     this.reader.mark();
/* 113 */     if (this.reader.matchConsume("#")) {
/* 114 */       boolean isHexMode = this.reader.matchConsumeIgnoreCase("X");
/* 115 */       String numRef = isHexMode ? this.reader.consumeHexSequence() : this.reader.consumeDigitSequence();
/* 116 */       if (numRef.length() == 0) {
/* 117 */         characterReferenceError("numeric reference with no numerals");
/* 118 */         this.reader.rewindToMark();
/* 119 */         return null;
/*     */       }
/* 121 */       if (!this.reader.matchConsume(";"))
/* 122 */         characterReferenceError("missing semicolon");
/* 123 */       int charval = -1;
/*     */       try {
/* 125 */         int base = isHexMode ? 16 : 10;
/* 126 */         charval = Integer.valueOf(numRef, base).intValue();
/*     */       } catch (NumberFormatException e) {
/*     */       }
/* 129 */       if ((charval == -1) || ((charval >= 55296) && (charval <= 57343)) || (charval > 1114111)) {
/* 130 */         characterReferenceError("character outside of valid range");
/* 131 */         return new char[] { 65533 };
/*     */       }
/*     */ 
/* 135 */       return Character.toChars(charval);
/*     */     }
/*     */ 
/* 139 */     String nameRef = this.reader.consumeLetterThenDigitSequence();
/* 140 */     boolean looksLegit = this.reader.matches(';');
/*     */ 
/* 142 */     boolean found = (Entities.isBaseNamedEntity(nameRef)) || ((Entities.isNamedEntity(nameRef)) && (looksLegit));
/*     */ 
/* 144 */     if (!found) {
/* 145 */       this.reader.rewindToMark();
/* 146 */       if (looksLegit)
/* 147 */         characterReferenceError(String.format("invalid named referenece '%s'", new Object[] { nameRef }));
/* 148 */       return null;
/*     */     }
/* 150 */     if (inAttribute) if ((!this.reader.matchesLetter()) && (!this.reader.matchesDigit())) { if (!this.reader.matchesAny(new char[] { '=', '-', '_' }));
/*     */       } else {
/* 152 */         this.reader.rewindToMark();
/* 153 */         return null;
/*     */       }
/* 155 */     if (!this.reader.matchConsume(";"))
/* 156 */       characterReferenceError("missing semicolon");
/* 157 */     return new char[] { Entities.getCharacterByName(nameRef).charValue() };
/*     */   }
/*     */ 
/*     */   Token.Tag createTagPending(boolean start)
/*     */   {
/* 162 */     this.tagPending = (start ? new Token.StartTag() : new Token.EndTag());
/* 163 */     return this.tagPending;
/*     */   }
/*     */ 
/*     */   void emitTagPending() {
/* 167 */     this.tagPending.finaliseTag();
/* 168 */     emit(this.tagPending);
/*     */   }
/*     */ 
/*     */   void createCommentPending() {
/* 172 */     this.commentPending = new Token.Comment();
/*     */   }
/*     */ 
/*     */   void emitCommentPending() {
/* 176 */     emit(this.commentPending);
/*     */   }
/*     */ 
/*     */   void createDoctypePending() {
/* 180 */     this.doctypePending = new Token.Doctype();
/*     */   }
/*     */ 
/*     */   void emitDoctypePending() {
/* 184 */     emit(this.doctypePending);
/*     */   }
/*     */ 
/*     */   void createTempBuffer() {
/* 188 */     this.dataBuffer = new StringBuilder();
/*     */   }
/*     */ 
/*     */   boolean isAppropriateEndTagToken() {
/* 192 */     if (this.lastStartTag == null)
/* 193 */       return false;
/* 194 */     return this.tagPending.tagName.equals(this.lastStartTag.tagName);
/*     */   }
/*     */ 
/*     */   String appropriateEndTagName() {
/* 198 */     if (this.lastStartTag == null)
/* 199 */       return null;
/* 200 */     return this.lastStartTag.tagName;
/*     */   }
/*     */ 
/*     */   void error(TokeniserState state) {
/* 204 */     if (this.errors.canAddError())
/* 205 */       this.errors.add(new ParseError(this.reader.pos(), "Unexpected character '%s' in input state [%s]", new Object[] { Character.valueOf(this.reader.current()), state }));
/*     */   }
/*     */ 
/*     */   void eofError(TokeniserState state) {
/* 209 */     if (this.errors.canAddError())
/* 210 */       this.errors.add(new ParseError(this.reader.pos(), "Unexpectedly reached end of file (EOF) in input state [%s]", new Object[] { state }));
/*     */   }
/*     */ 
/*     */   private void characterReferenceError(String message) {
/* 214 */     if (this.errors.canAddError())
/* 215 */       this.errors.add(new ParseError(this.reader.pos(), "Invalid character reference: %s", new Object[] { message }));
/*     */   }
/*     */ 
/*     */   private void error(String errorMsg) {
/* 219 */     if (this.errors.canAddError())
/* 220 */       this.errors.add(new ParseError(this.reader.pos(), errorMsg));
/*     */   }
/*     */ 
/*     */   boolean currentNodeInHtmlNS()
/*     */   {
/* 225 */     return true;
/*     */   }
/*     */ 
/*     */   String unescapeEntities(boolean inAttribute)
/*     */   {
/* 236 */     StringBuilder builder = new StringBuilder();
/* 237 */     while (!this.reader.isEmpty()) {
/* 238 */       builder.append(this.reader.consumeTo('&'));
/* 239 */       if (this.reader.matches('&')) {
/* 240 */         this.reader.consume();
/* 241 */         char[] c = consumeCharacterReference(null, inAttribute);
/* 242 */         if ((c == null) || (c.length == 0))
/* 243 */           builder.append('&');
/*     */         else
/* 245 */           builder.append(c);
/*     */       }
/*     */     }
/* 248 */     return builder.toString();
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.Tokeniser
 * JD-Core Version:    0.6.2
 */