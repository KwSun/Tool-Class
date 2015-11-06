/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jsoup.helper.DescendableLinkedList;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Comment;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Document.OutputSettings;
/*     */ import org.jsoup.nodes.Document.OutputSettings.Syntax;
/*     */ import org.jsoup.nodes.DocumentType;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.Node;
/*     */ import org.jsoup.nodes.TextNode;
/*     */ import org.jsoup.nodes.XmlDeclaration;
/*     */ 
/*     */ public class XmlTreeBuilder extends TreeBuilder
/*     */ {
/*     */   protected void initialiseParse(String input, String baseUri, ParseErrorList errors)
/*     */   {
/*  19 */     super.initialiseParse(input, baseUri, errors);
/*  20 */     this.stack.add(this.doc);
/*  21 */     this.doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
/*     */   }
/*     */ 
/*     */   protected boolean process(Token token)
/*     */   {
/*  27 */     switch (1.$SwitchMap$org$jsoup$parser$Token$TokenType[token.type.ordinal()]) {
/*     */     case 1:
/*  29 */       insert(token.asStartTag());
/*  30 */       break;
/*     */     case 2:
/*  32 */       popStackToClose(token.asEndTag());
/*  33 */       break;
/*     */     case 3:
/*  35 */       insert(token.asComment());
/*  36 */       break;
/*     */     case 4:
/*  38 */       insert(token.asCharacter());
/*  39 */       break;
/*     */     case 5:
/*  41 */       insert(token.asDoctype());
/*  42 */       break;
/*     */     case 6:
/*  44 */       break;
/*     */     default:
/*  46 */       Validate.fail("Unexpected token type: " + token.type);
/*     */     }
/*  48 */     return true;
/*     */   }
/*     */ 
/*     */   private void insertNode(Node node) {
/*  52 */     currentElement().appendChild(node);
/*     */   }
/*     */ 
/*     */   Element insert(Token.StartTag startTag) {
/*  56 */     Tag tag = Tag.valueOf(startTag.name());
/*     */ 
/*  58 */     Element el = new Element(tag, this.baseUri, startTag.attributes);
/*  59 */     insertNode(el);
/*  60 */     if (startTag.isSelfClosing()) {
/*  61 */       this.tokeniser.acknowledgeSelfClosingFlag();
/*  62 */       if (!tag.isKnownTag())
/*  63 */         tag.setSelfClosing();
/*     */     } else {
/*  65 */       this.stack.add(el);
/*     */     }
/*  67 */     return el;
/*     */   }
/*     */ 
/*     */   void insert(Token.Comment commentToken) {
/*  71 */     Comment comment = new Comment(commentToken.getData(), this.baseUri);
/*  72 */     Node insert = comment;
/*  73 */     if (commentToken.bogus) {
/*  74 */       String data = comment.getData();
/*  75 */       if ((data.length() > 1) && ((data.startsWith("!")) || (data.startsWith("?")))) {
/*  76 */         String declaration = data.substring(1);
/*  77 */         insert = new XmlDeclaration(declaration, comment.baseUri(), data.startsWith("!"));
/*     */       }
/*     */     }
/*  80 */     insertNode(insert);
/*     */   }
/*     */ 
/*     */   void insert(Token.Character characterToken) {
/*  84 */     Node node = new TextNode(characterToken.getData(), this.baseUri);
/*  85 */     insertNode(node);
/*     */   }
/*     */ 
/*     */   void insert(Token.Doctype d) {
/*  89 */     DocumentType doctypeNode = new DocumentType(d.getName(), d.getPublicIdentifier(), d.getSystemIdentifier(), this.baseUri);
/*  90 */     insertNode(doctypeNode);
/*     */   }
/*     */ 
/*     */   private void popStackToClose(Token.EndTag endTag)
/*     */   {
/* 100 */     String elName = endTag.name();
/* 101 */     Element firstFound = null;
/*     */ 
/* 103 */     Iterator it = this.stack.descendingIterator();
/* 104 */     while (it.hasNext()) {
/* 105 */       Element next = (Element)it.next();
/* 106 */       if (next.nodeName().equals(elName)) {
/* 107 */         firstFound = next;
/* 108 */         break;
/*     */       }
/*     */     }
/* 111 */     if (firstFound == null) {
/* 112 */       return;
/*     */     }
/* 114 */     it = this.stack.descendingIterator();
/* 115 */     while (it.hasNext()) {
/* 116 */       Element next = (Element)it.next();
/* 117 */       if (next == firstFound) {
/* 118 */         it.remove();
/* 119 */         break;
/*     */       }
/* 121 */       it.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   List<Node> parseFragment(String inputFragment, String baseUri, ParseErrorList errors)
/*     */   {
/* 127 */     initialiseParse(inputFragment, baseUri, errors);
/* 128 */     runParser();
/* 129 */     return this.doc.childNodes();
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.XmlTreeBuilder
 * JD-Core Version:    0.6.2
 */