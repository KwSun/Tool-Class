/*    */ package org.jsoup.parser;
/*    */ 
/*    */ import org.jsoup.helper.DescendableLinkedList;
/*    */ import org.jsoup.helper.Validate;
/*    */ import org.jsoup.nodes.Document;
/*    */ import org.jsoup.nodes.Element;
/*    */ 
/*    */ abstract class TreeBuilder
/*    */ {
/*    */   CharacterReader reader;
/*    */   Tokeniser tokeniser;
/*    */   protected Document doc;
/*    */   protected DescendableLinkedList<Element> stack;
/*    */   protected String baseUri;
/*    */   protected Token currentToken;
/*    */   protected ParseErrorList errors;
/*    */ 
/*    */   protected void initialiseParse(String input, String baseUri, ParseErrorList errors)
/*    */   {
/* 24 */     Validate.notNull(input, "String input must not be null");
/* 25 */     Validate.notNull(baseUri, "BaseURI must not be null");
/*    */ 
/* 27 */     this.doc = new Document(baseUri);
/* 28 */     this.reader = new CharacterReader(input);
/* 29 */     this.errors = errors;
/* 30 */     this.tokeniser = new Tokeniser(this.reader, errors);
/* 31 */     this.stack = new DescendableLinkedList();
/* 32 */     this.baseUri = baseUri;
/*    */   }
/*    */ 
/*    */   Document parse(String input, String baseUri) {
/* 36 */     return parse(input, baseUri, ParseErrorList.noTracking());
/*    */   }
/*    */ 
/*    */   Document parse(String input, String baseUri, ParseErrorList errors) {
/* 40 */     initialiseParse(input, baseUri, errors);
/* 41 */     runParser();
/* 42 */     return this.doc;
/*    */   }
/*    */ 
/*    */   protected void runParser() {
/*    */     while (true) {
/* 47 */       Token token = this.tokeniser.read();
/* 48 */       process(token);
/*    */ 
/* 50 */       if (token.type == Token.TokenType.EOF)
/*    */         break;
/*    */     }
/*    */   }
/*    */ 
/*    */   protected abstract boolean process(Token paramToken);
/*    */ 
/*    */   protected Element currentElement() {
/* 58 */     return (Element)this.stack.getLast();
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.TreeBuilder
 * JD-Core Version:    0.6.2
 */