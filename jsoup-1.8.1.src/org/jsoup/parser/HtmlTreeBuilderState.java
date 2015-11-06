/*      */ package org.jsoup.parser;
/*      */ 
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import org.jsoup.helper.DescendableLinkedList;
/*      */ import org.jsoup.helper.StringUtil;
/*      */ import org.jsoup.nodes.Attribute;
/*      */ import org.jsoup.nodes.Attributes;
/*      */ import org.jsoup.nodes.Document;
/*      */ import org.jsoup.nodes.Document.QuirksMode;
/*      */ import org.jsoup.nodes.DocumentType;
/*      */ import org.jsoup.nodes.Element;
/*      */ import org.jsoup.nodes.Node;
/*      */ 
/*      */  enum HtmlTreeBuilderState
/*      */ {
/*   14 */   Initial, 
/*      */ 
/*   37 */   BeforeHtml, 
/*      */ 
/*   66 */   BeforeHead, 
/*      */ 
/*   94 */   InHead, 
/*      */ 
/*  166 */   InHeadNoscript, 
/*      */ 
/*  195 */   AfterHead, 
/*      */ 
/*  246 */   InBody, 
/*      */ 
/*  780 */   Text, 
/*      */ 
/*  799 */   InTable, 
/*      */ 
/*  899 */   InTableText, 
/*      */ 
/*  935 */   InCaption, 
/*      */ 
/*  970 */   InColumnGroup, 
/*      */ 
/* 1025 */   InTableBody, 
/*      */ 
/* 1085 */   InRow, 
/*      */ 
/* 1146 */   InCell, 
/*      */ 
/* 1203 */   InSelect, 
/*      */ 
/* 1291 */   InSelectInTable, 
/*      */ 
/* 1309 */   AfterBody, 
/*      */ 
/* 1337 */   InFrameset, 
/*      */ 
/* 1383 */   AfterFrameset, 
/*      */ 
/* 1407 */   AfterAfterBody, 
/*      */ 
/* 1423 */   AfterAfterFrameset, 
/*      */ 
/* 1440 */   ForeignContent;
/*      */ 
/* 1447 */   private static String nullString = String.valueOf('\000');
/*      */ 
/*      */   abstract boolean process(Token paramToken, HtmlTreeBuilder paramHtmlTreeBuilder);
/*      */ 
/*      */   private static boolean isWhitespace(Token t) {
/* 1452 */     if (t.isCharacter()) {
/* 1453 */       String data = t.asCharacter().getData();
/*      */ 
/* 1455 */       for (int i = 0; i < data.length(); i++) {
/* 1456 */         char c = data.charAt(i);
/* 1457 */         if (!StringUtil.isWhitespace(c))
/* 1458 */           return false;
/*      */       }
/* 1460 */       return true;
/*      */     }
/* 1462 */     return false;
/*      */   }
/*      */ 
/*      */   private static void handleRcData(Token.StartTag startTag, HtmlTreeBuilder tb) {
/* 1466 */     tb.insert(startTag);
/* 1467 */     tb.tokeniser.transition(TokeniserState.Rcdata);
/* 1468 */     tb.markInsertionMode();
/* 1469 */     tb.transition(Text);
/*      */   }
/*      */ 
/*      */   private static void handleRawtext(Token.StartTag startTag, HtmlTreeBuilder tb) {
/* 1473 */     tb.insert(startTag);
/* 1474 */     tb.tokeniser.transition(TokeniserState.Rawtext);
/* 1475 */     tb.markInsertionMode();
/* 1476 */     tb.transition(Text);
/*      */   }
/*      */ 
/*      */   private static final class Constants
/*      */   {
/* 1482 */     private static final String[] InBodyStartToHead = { "base", "basefont", "bgsound", "command", "link", "meta", "noframes", "script", "style", "title" };
/* 1483 */     private static final String[] InBodyStartPClosers = { "address", "article", "aside", "blockquote", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "menu", "nav", "ol", "p", "section", "summary", "ul" };
/*      */ 
/* 1486 */     private static final String[] Headings = { "h1", "h2", "h3", "h4", "h5", "h6" };
/* 1487 */     private static final String[] InBodyStartPreListing = { "pre", "listing" };
/* 1488 */     private static final String[] InBodyStartLiBreakers = { "address", "div", "p" };
/* 1489 */     private static final String[] DdDt = { "dd", "dt" };
/* 1490 */     private static final String[] Formatters = { "b", "big", "code", "em", "font", "i", "s", "small", "strike", "strong", "tt", "u" };
/* 1491 */     private static final String[] InBodyStartApplets = { "applet", "marquee", "object" };
/* 1492 */     private static final String[] InBodyStartEmptyFormatters = { "area", "br", "embed", "img", "keygen", "wbr" };
/* 1493 */     private static final String[] InBodyStartMedia = { "param", "source", "track" };
/* 1494 */     private static final String[] InBodyStartInputAttribs = { "name", "action", "prompt" };
/* 1495 */     private static final String[] InBodyStartOptions = { "optgroup", "option" };
/* 1496 */     private static final String[] InBodyStartRuby = { "rp", "rt" };
/* 1497 */     private static final String[] InBodyStartDrop = { "caption", "col", "colgroup", "frame", "head", "tbody", "td", "tfoot", "th", "thead", "tr" };
/* 1498 */     private static final String[] InBodyEndClosers = { "address", "article", "aside", "blockquote", "button", "center", "details", "dir", "div", "dl", "fieldset", "figcaption", "figure", "footer", "header", "hgroup", "listing", "menu", "nav", "ol", "pre", "section", "summary", "ul" };
/*      */ 
/* 1501 */     private static final String[] InBodyEndAdoptionFormatters = { "a", "b", "big", "code", "em", "font", "i", "nobr", "s", "small", "strike", "strong", "tt", "u" };
/* 1502 */     private static final String[] InBodyEndTableFosters = { "table", "tbody", "tfoot", "thead", "tr" };
/*      */   }
/*      */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.HtmlTreeBuilderState
 * JD-Core Version:    0.6.2
 */