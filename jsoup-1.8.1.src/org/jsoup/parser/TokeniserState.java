/*      */ package org.jsoup.parser;
/*      */ 
/*      */  enum TokeniserState
/*      */ {
/*    7 */   Data, 
/*      */ 
/*   31 */   CharacterReferenceInData, 
/*      */ 
/*   42 */   Rcdata, 
/*      */ 
/*   67 */   CharacterReferenceInRcdata, 
/*      */ 
/*   77 */   Rawtext, 
/*      */ 
/*   98 */   ScriptData, 
/*      */ 
/*  119 */   PLAINTEXT, 
/*      */ 
/*  137 */   TagOpen, 
/*      */ 
/*  163 */   EndTagOpen, 
/*      */ 
/*  181 */   TagName, 
/*      */ 
/*  213 */   RcdataLessthanSign, 
/*      */ 
/*  232 */   RCDATAEndTagOpen, 
/*      */ 
/*  245 */   RCDATAEndTagName, 
/*      */ 
/*  291 */   RawtextLessthanSign, 
/*      */ 
/*  302 */   RawtextEndTagOpen, 
/*      */ 
/*  313 */   RawtextEndTagName, 
/*      */ 
/*  318 */   ScriptDataLessthanSign, 
/*      */ 
/*  336 */   ScriptDataEndTagOpen, 
/*      */ 
/*  348 */   ScriptDataEndTagName, 
/*      */ 
/*  353 */   ScriptDataEscapeStart, 
/*      */ 
/*  363 */   ScriptDataEscapeStartDash, 
/*      */ 
/*  373 */   ScriptDataEscaped, 
/*      */ 
/*  400 */   ScriptDataEscapedDash, 
/*      */ 
/*  428 */   ScriptDataEscapedDashDash, 
/*      */ 
/*  459 */   ScriptDataEscapedLessthanSign, 
/*      */ 
/*  475 */   ScriptDataEscapedEndTagOpen, 
/*      */ 
/*  488 */   ScriptDataEscapedEndTagName, 
/*      */ 
/*  493 */   ScriptDataDoubleEscapeStart, 
/*      */ 
/*  498 */   ScriptDataDoubleEscaped, 
/*      */ 
/*  525 */   ScriptDataDoubleEscapedDash, 
/*      */ 
/*  552 */   ScriptDataDoubleEscapedDashDash, 
/*      */ 
/*  582 */   ScriptDataDoubleEscapedLessthanSign, 
/*      */ 
/*  593 */   ScriptDataDoubleEscapeEnd, 
/*      */ 
/*  598 */   BeforeAttributeName, 
/*      */ 
/*  642 */   AttributeName, 
/*      */ 
/*  684 */   AfterAttributeName, 
/*      */ 
/*  729 */   BeforeAttributeValue, 
/*      */ 
/*  777 */   AttributeValue_doubleQuoted, 
/*      */ 
/*  807 */   AttributeValue_singleQuoted, 
/*      */ 
/*  837 */   AttributeValue_unquoted, 
/*      */ 
/*  885 */   AfterAttributeValue_quoted, 
/*      */ 
/*  915 */   SelfClosingStartTag, 
/*      */ 
/*  934 */   BogusComment, 
/*      */ 
/*  947 */   MarkupDeclarationOpen, 
/*      */ 
/*  965 */   CommentStart, 
/*      */ 
/*  993 */   CommentStartDash, 
/*      */ 
/* 1021 */   Comment, 
/*      */ 
/* 1043 */   CommentEndDash, 
/*      */ 
/* 1066 */   CommentEnd, 
/*      */ 
/* 1099 */   CommentEndBang, 
/*      */ 
/* 1127 */   Doctype, 
/*      */ 
/* 1154 */   BeforeDoctypeName, 
/*      */ 
/* 1189 */   DoctypeName, 
/*      */ 
/* 1224 */   AfterDoctypeName, 
/*      */ 
/* 1250 */   AfterDoctypePublicKeyword, 
/*      */ 
/* 1290 */   BeforeDoctypePublicIdentifier, 
/*      */ 
/* 1327 */   DoctypePublicIdentifier_doubleQuoted, 
/*      */ 
/* 1355 */   DoctypePublicIdentifier_singleQuoted, 
/*      */ 
/* 1383 */   AfterDoctypePublicIdentifier, 
/*      */ 
/* 1421 */   BetweenDoctypePublicAndSystemIdentifiers, 
/*      */ 
/* 1458 */   AfterDoctypeSystemKeyword, 
/*      */ 
/* 1498 */   BeforeDoctypeSystemIdentifier, 
/*      */ 
/* 1535 */   DoctypeSystemIdentifier_doubleQuoted, 
/*      */ 
/* 1563 */   DoctypeSystemIdentifier_singleQuoted, 
/*      */ 
/* 1591 */   AfterDoctypeSystemIdentifier, 
/*      */ 
/* 1618 */   BogusDoctype, 
/*      */ 
/* 1636 */   CdataSection;
/*      */ 
/*      */   private static final char nullChar = '\000';
/*      */   private static final char replacementChar = '�';
/* 1650 */   private static final String replacementStr = String.valueOf(65533);
/*      */   private static final char eof = '￿';
/*      */ 
/*      */   abstract void read(Tokeniser paramTokeniser, CharacterReader paramCharacterReader);
/*      */ 
/*      */   private static final void handleDataEndTag(Tokeniser t, CharacterReader r, TokeniserState elseTransition)
/*      */   {
/* 1658 */     if (r.matchesLetter()) {
/* 1659 */       String name = r.consumeLetterSequence();
/* 1660 */       t.tagPending.appendTagName(name.toLowerCase());
/* 1661 */       t.dataBuffer.append(name);
/* 1662 */       return;
/*      */     }
/*      */ 
/* 1665 */     boolean needsExitTransition = false;
/* 1666 */     if ((t.isAppropriateEndTagToken()) && (!r.isEmpty())) {
/* 1667 */       char c = r.consume();
/* 1668 */       switch (c) {
/*      */       case '\t':
/*      */       case '\n':
/*      */       case '\f':
/*      */       case '\r':
/*      */       case ' ':
/* 1674 */         t.transition(BeforeAttributeName);
/* 1675 */         break;
/*      */       case '/':
/* 1677 */         t.transition(SelfClosingStartTag);
/* 1678 */         break;
/*      */       case '>':
/* 1680 */         t.emitTagPending();
/* 1681 */         t.transition(Data);
/* 1682 */         break;
/*      */       default:
/* 1684 */         t.dataBuffer.append(c);
/* 1685 */         needsExitTransition = true;
/*      */       }
/*      */     } else {
/* 1688 */       needsExitTransition = true;
/*      */     }
/*      */ 
/* 1691 */     if (needsExitTransition) {
/* 1692 */       t.emit("</" + t.dataBuffer.toString());
/* 1693 */       t.transition(elseTransition);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final void handleDataDoubleEscapeTag(Tokeniser t, CharacterReader r, TokeniserState primary, TokeniserState fallback) {
/* 1698 */     if (r.matchesLetter()) {
/* 1699 */       String name = r.consumeLetterSequence();
/* 1700 */       t.dataBuffer.append(name.toLowerCase());
/* 1701 */       t.emit(name);
/* 1702 */       return;
/*      */     }
/*      */ 
/* 1705 */     char c = r.consume();
/* 1706 */     switch (c) {
/*      */     case '\t':
/*      */     case '\n':
/*      */     case '\f':
/*      */     case '\r':
/*      */     case ' ':
/*      */     case '/':
/*      */     case '>':
/* 1714 */       if (t.dataBuffer.toString().equals("script"))
/* 1715 */         t.transition(primary);
/*      */       else
/* 1717 */         t.transition(fallback);
/* 1718 */       t.emit(c);
/* 1719 */       break;
/*      */     default:
/* 1721 */       r.unconsume();
/* 1722 */       t.transition(fallback);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.TokeniserState
 * JD-Core Version:    0.6.2
 */