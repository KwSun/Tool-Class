/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.jsoup.helper.DescendableLinkedList;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Attributes;
/*     */ import org.jsoup.nodes.Comment;
/*     */ import org.jsoup.nodes.DataNode;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.FormElement;
/*     */ import org.jsoup.nodes.Node;
/*     */ import org.jsoup.nodes.TextNode;
/*     */ import org.jsoup.select.Elements;
/*     */ 
/*     */ class HtmlTreeBuilder extends TreeBuilder
/*     */ {
/*  19 */   private static final String[] TagsScriptStyle = { "script", "style" };
/*  20 */   public static final String[] TagsSearchInScope = { "applet", "caption", "html", "table", "td", "th", "marquee", "object" };
/*  21 */   private static final String[] TagSearchList = { "ol", "ul" };
/*  22 */   private static final String[] TagSearchButton = { "button" };
/*  23 */   private static final String[] TagSearchTableScope = { "html", "table" };
/*  24 */   private static final String[] TagSearchSelectScope = { "optgroup", "option" };
/*  25 */   private static final String[] TagSearchEndTags = { "dd", "dt", "li", "option", "optgroup", "p", "rp", "rt" };
/*  26 */   private static final String[] TagSearchSpecial = { "address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp" };
/*     */   private HtmlTreeBuilderState state;
/*     */   private HtmlTreeBuilderState originalState;
/*  38 */   private boolean baseUriSetFromDoc = false;
/*     */   private Element headElement;
/*     */   private FormElement formElement;
/*     */   private Element contextElement;
/*  42 */   private DescendableLinkedList<Element> formattingElements = new DescendableLinkedList();
/*  43 */   private List<Token.Character> pendingTableCharacters = new ArrayList();
/*     */ 
/*  45 */   private boolean framesetOk = true;
/*  46 */   private boolean fosterInserts = false;
/*  47 */   private boolean fragmentParsing = false;
/*     */ 
/*     */   Document parse(String input, String baseUri, ParseErrorList errors)
/*     */   {
/*  53 */     this.state = HtmlTreeBuilderState.Initial;
/*  54 */     this.baseUriSetFromDoc = false;
/*  55 */     return super.parse(input, baseUri, errors);
/*     */   }
/*     */ 
/*     */   List<Node> parseFragment(String inputFragment, Element context, String baseUri, ParseErrorList errors)
/*     */   {
/*  60 */     this.state = HtmlTreeBuilderState.Initial;
/*  61 */     initialiseParse(inputFragment, baseUri, errors);
/*  62 */     this.contextElement = context;
/*  63 */     this.fragmentParsing = true;
/*  64 */     Element root = null;
/*     */ 
/*  66 */     if (context != null) {
/*  67 */       if (context.ownerDocument() != null) {
/*  68 */         this.doc.quirksMode(context.ownerDocument().quirksMode());
/*     */       }
/*     */ 
/*  71 */       String contextTag = context.tagName();
/*  72 */       if (StringUtil.in(contextTag, new String[] { "title", "textarea" }))
/*  73 */         this.tokeniser.transition(TokeniserState.Rcdata);
/*  74 */       else if (StringUtil.in(contextTag, new String[] { "iframe", "noembed", "noframes", "style", "xmp" }))
/*  75 */         this.tokeniser.transition(TokeniserState.Rawtext);
/*  76 */       else if (contextTag.equals("script"))
/*  77 */         this.tokeniser.transition(TokeniserState.ScriptData);
/*  78 */       else if (contextTag.equals("noscript"))
/*  79 */         this.tokeniser.transition(TokeniserState.Data);
/*  80 */       else if (contextTag.equals("plaintext"))
/*  81 */         this.tokeniser.transition(TokeniserState.Data);
/*     */       else {
/*  83 */         this.tokeniser.transition(TokeniserState.Data);
/*     */       }
/*  85 */       root = new Element(Tag.valueOf("html"), baseUri);
/*  86 */       this.doc.appendChild(root);
/*  87 */       this.stack.push(root);
/*  88 */       resetInsertionMode();
/*     */ 
/*  92 */       Elements contextChain = context.parents();
/*  93 */       contextChain.add(0, context);
/*  94 */       for (Element parent : contextChain) {
/*  95 */         if ((parent instanceof FormElement)) {
/*  96 */           this.formElement = ((FormElement)parent);
/*  97 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 102 */     runParser();
/* 103 */     if (context != null) {
/* 104 */       return root.childNodes();
/*     */     }
/* 106 */     return this.doc.childNodes();
/*     */   }
/*     */ 
/*     */   protected boolean process(Token token)
/*     */   {
/* 111 */     this.currentToken = token;
/* 112 */     return this.state.process(token, this);
/*     */   }
/*     */ 
/*     */   boolean process(Token token, HtmlTreeBuilderState state) {
/* 116 */     this.currentToken = token;
/* 117 */     return state.process(token, this);
/*     */   }
/*     */ 
/*     */   void transition(HtmlTreeBuilderState state) {
/* 121 */     this.state = state;
/*     */   }
/*     */ 
/*     */   HtmlTreeBuilderState state() {
/* 125 */     return this.state;
/*     */   }
/*     */ 
/*     */   void markInsertionMode() {
/* 129 */     this.originalState = this.state;
/*     */   }
/*     */ 
/*     */   HtmlTreeBuilderState originalState() {
/* 133 */     return this.originalState;
/*     */   }
/*     */ 
/*     */   void framesetOk(boolean framesetOk) {
/* 137 */     this.framesetOk = framesetOk;
/*     */   }
/*     */ 
/*     */   boolean framesetOk() {
/* 141 */     return this.framesetOk;
/*     */   }
/*     */ 
/*     */   Document getDocument() {
/* 145 */     return this.doc;
/*     */   }
/*     */ 
/*     */   String getBaseUri() {
/* 149 */     return this.baseUri;
/*     */   }
/*     */ 
/*     */   void maybeSetBaseUri(Element base) {
/* 153 */     if (this.baseUriSetFromDoc) {
/* 154 */       return;
/*     */     }
/* 156 */     String href = base.absUrl("href");
/* 157 */     if (href.length() != 0) {
/* 158 */       this.baseUri = href;
/* 159 */       this.baseUriSetFromDoc = true;
/* 160 */       this.doc.setBaseUri(href);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isFragmentParsing() {
/* 165 */     return this.fragmentParsing;
/*     */   }
/*     */ 
/*     */   void error(HtmlTreeBuilderState state) {
/* 169 */     if (this.errors.canAddError())
/* 170 */       this.errors.add(new ParseError(this.reader.pos(), "Unexpected token [%s] when in state [%s]", new Object[] { this.currentToken.tokenType(), state }));
/*     */   }
/*     */ 
/*     */   Element insert(Token.StartTag startTag)
/*     */   {
/* 176 */     if (startTag.isSelfClosing()) {
/* 177 */       Element el = insertEmpty(startTag);
/* 178 */       this.stack.add(el);
/* 179 */       this.tokeniser.transition(TokeniserState.Data);
/* 180 */       this.tokeniser.emit(new Token.EndTag(el.tagName()));
/* 181 */       return el;
/*     */     }
/*     */ 
/* 184 */     Element el = new Element(Tag.valueOf(startTag.name()), this.baseUri, startTag.attributes);
/* 185 */     insert(el);
/* 186 */     return el;
/*     */   }
/*     */ 
/*     */   Element insert(String startTagName) {
/* 190 */     Element el = new Element(Tag.valueOf(startTagName), this.baseUri);
/* 191 */     insert(el);
/* 192 */     return el;
/*     */   }
/*     */ 
/*     */   void insert(Element el) {
/* 196 */     insertNode(el);
/* 197 */     this.stack.add(el);
/*     */   }
/*     */ 
/*     */   Element insertEmpty(Token.StartTag startTag) {
/* 201 */     Tag tag = Tag.valueOf(startTag.name());
/* 202 */     Element el = new Element(tag, this.baseUri, startTag.attributes);
/* 203 */     insertNode(el);
/* 204 */     if (startTag.isSelfClosing()) {
/* 205 */       if (tag.isKnownTag()) {
/* 206 */         if (tag.isSelfClosing()) this.tokeniser.acknowledgeSelfClosingFlag(); 
/*     */       }
/*     */       else
/*     */       {
/* 209 */         tag.setSelfClosing();
/* 210 */         this.tokeniser.acknowledgeSelfClosingFlag();
/*     */       }
/*     */     }
/* 213 */     return el;
/*     */   }
/*     */ 
/*     */   FormElement insertForm(Token.StartTag startTag, boolean onStack) {
/* 217 */     Tag tag = Tag.valueOf(startTag.name());
/* 218 */     FormElement el = new FormElement(tag, this.baseUri, startTag.attributes);
/* 219 */     setFormElement(el);
/* 220 */     insertNode(el);
/* 221 */     if (onStack)
/* 222 */       this.stack.add(el);
/* 223 */     return el;
/*     */   }
/*     */ 
/*     */   void insert(Token.Comment commentToken) {
/* 227 */     Comment comment = new Comment(commentToken.getData(), this.baseUri);
/* 228 */     insertNode(comment);
/*     */   }
/*     */ 
/*     */   void insert(Token.Character characterToken)
/*     */   {
/* 234 */     String tagName = currentElement().tagName();
/*     */     Node node;
/*     */     Node node;
/* 235 */     if ((tagName.equals("script")) || (tagName.equals("style")))
/* 236 */       node = new DataNode(characterToken.getData(), this.baseUri);
/*     */     else
/* 238 */       node = new TextNode(characterToken.getData(), this.baseUri);
/* 239 */     currentElement().appendChild(node);
/*     */   }
/*     */ 
/*     */   private void insertNode(Node node)
/*     */   {
/* 244 */     if (this.stack.size() == 0)
/* 245 */       this.doc.appendChild(node);
/* 246 */     else if (isFosterInserts())
/* 247 */       insertInFosterParent(node);
/*     */     else {
/* 249 */       currentElement().appendChild(node);
/*     */     }
/*     */ 
/* 252 */     if (((node instanceof Element)) && (((Element)node).tag().isFormListed()) && 
/* 253 */       (this.formElement != null))
/* 254 */       this.formElement.addElement((Element)node);
/*     */   }
/*     */ 
/*     */   Element pop()
/*     */   {
/* 260 */     if ((((Element)this.stack.peekLast()).nodeName().equals("td")) && (!this.state.name().equals("InCell")))
/* 261 */       Validate.isFalse(true, "pop td not in cell");
/* 262 */     if (((Element)this.stack.peekLast()).nodeName().equals("html"))
/* 263 */       Validate.isFalse(true, "popping html!");
/* 264 */     return (Element)this.stack.pollLast();
/*     */   }
/*     */ 
/*     */   void push(Element element) {
/* 268 */     this.stack.add(element);
/*     */   }
/*     */ 
/*     */   DescendableLinkedList<Element> getStack() {
/* 272 */     return this.stack;
/*     */   }
/*     */ 
/*     */   boolean onStack(Element el) {
/* 276 */     return isElementInQueue(this.stack, el);
/*     */   }
/*     */ 
/*     */   private boolean isElementInQueue(DescendableLinkedList<Element> queue, Element element) {
/* 280 */     Iterator it = queue.descendingIterator();
/* 281 */     while (it.hasNext()) {
/* 282 */       Element next = (Element)it.next();
/* 283 */       if (next == element) {
/* 284 */         return true;
/*     */       }
/*     */     }
/* 287 */     return false;
/*     */   }
/*     */ 
/*     */   Element getFromStack(String elName) {
/* 291 */     Iterator it = this.stack.descendingIterator();
/* 292 */     while (it.hasNext()) {
/* 293 */       Element next = (Element)it.next();
/* 294 */       if (next.nodeName().equals(elName)) {
/* 295 */         return next;
/*     */       }
/*     */     }
/* 298 */     return null;
/*     */   }
/*     */ 
/*     */   boolean removeFromStack(Element el) {
/* 302 */     Iterator it = this.stack.descendingIterator();
/* 303 */     while (it.hasNext()) {
/* 304 */       Element next = (Element)it.next();
/* 305 */       if (next == el) {
/* 306 */         it.remove();
/* 307 */         return true;
/*     */       }
/*     */     }
/* 310 */     return false;
/*     */   }
/*     */ 
/*     */   void popStackToClose(String elName) {
/* 314 */     Iterator it = this.stack.descendingIterator();
/* 315 */     while (it.hasNext()) {
/* 316 */       Element next = (Element)it.next();
/* 317 */       if (next.nodeName().equals(elName)) {
/* 318 */         it.remove();
/* 319 */         break;
/*     */       }
/* 321 */       it.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   void popStackToClose(String[] elNames)
/*     */   {
/* 327 */     Iterator it = this.stack.descendingIterator();
/* 328 */     while (it.hasNext()) {
/* 329 */       Element next = (Element)it.next();
/* 330 */       if (StringUtil.in(next.nodeName(), elNames)) {
/* 331 */         it.remove();
/* 332 */         break;
/*     */       }
/* 334 */       it.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   void popStackToBefore(String elName)
/*     */   {
/* 340 */     Iterator it = this.stack.descendingIterator();
/* 341 */     while (it.hasNext()) {
/* 342 */       Element next = (Element)it.next();
/* 343 */       if (next.nodeName().equals(elName)) {
/*     */         break;
/*     */       }
/* 346 */       it.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   void clearStackToTableContext()
/*     */   {
/* 352 */     clearStackToContext(new String[] { "table" });
/*     */   }
/*     */ 
/*     */   void clearStackToTableBodyContext() {
/* 356 */     clearStackToContext(new String[] { "tbody", "tfoot", "thead" });
/*     */   }
/*     */ 
/*     */   void clearStackToTableRowContext() {
/* 360 */     clearStackToContext(new String[] { "tr" });
/*     */   }
/*     */ 
/*     */   private void clearStackToContext(String[] nodeNames) {
/* 364 */     Iterator it = this.stack.descendingIterator();
/* 365 */     while (it.hasNext()) {
/* 366 */       Element next = (Element)it.next();
/* 367 */       if ((StringUtil.in(next.nodeName(), nodeNames)) || (next.nodeName().equals("html"))) {
/*     */         break;
/*     */       }
/* 370 */       it.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   Element aboveOnStack(Element el) {
/* 375 */     assert (onStack(el));
/* 376 */     Iterator it = this.stack.descendingIterator();
/* 377 */     while (it.hasNext()) {
/* 378 */       Element next = (Element)it.next();
/* 379 */       if (next == el) {
/* 380 */         return (Element)it.next();
/*     */       }
/*     */     }
/* 383 */     return null;
/*     */   }
/*     */ 
/*     */   void insertOnStackAfter(Element after, Element in) {
/* 387 */     int i = this.stack.lastIndexOf(after);
/* 388 */     Validate.isTrue(i != -1);
/* 389 */     this.stack.add(i + 1, in);
/*     */   }
/*     */ 
/*     */   void replaceOnStack(Element out, Element in) {
/* 393 */     replaceInQueue(this.stack, out, in);
/*     */   }
/*     */ 
/*     */   private void replaceInQueue(LinkedList<Element> queue, Element out, Element in) {
/* 397 */     int i = queue.lastIndexOf(out);
/* 398 */     Validate.isTrue(i != -1);
/* 399 */     queue.remove(i);
/* 400 */     queue.add(i, in);
/*     */   }
/*     */ 
/*     */   void resetInsertionMode() {
/* 404 */     boolean last = false;
/* 405 */     Iterator it = this.stack.descendingIterator();
/* 406 */     while (it.hasNext()) {
/* 407 */       Element node = (Element)it.next();
/* 408 */       if (!it.hasNext()) {
/* 409 */         last = true;
/* 410 */         node = this.contextElement;
/*     */       }
/* 412 */       String name = node.nodeName();
/* 413 */       if ("select".equals(name)) {
/* 414 */         transition(HtmlTreeBuilderState.InSelect);
/* 415 */         break;
/* 416 */       }if (("td".equals(name)) || (("td".equals(name)) && (!last))) {
/* 417 */         transition(HtmlTreeBuilderState.InCell);
/* 418 */         break;
/* 419 */       }if ("tr".equals(name)) {
/* 420 */         transition(HtmlTreeBuilderState.InRow);
/* 421 */         break;
/* 422 */       }if (("tbody".equals(name)) || ("thead".equals(name)) || ("tfoot".equals(name))) {
/* 423 */         transition(HtmlTreeBuilderState.InTableBody);
/* 424 */         break;
/* 425 */       }if ("caption".equals(name)) {
/* 426 */         transition(HtmlTreeBuilderState.InCaption);
/* 427 */         break;
/* 428 */       }if ("colgroup".equals(name)) {
/* 429 */         transition(HtmlTreeBuilderState.InColumnGroup);
/* 430 */         break;
/* 431 */       }if ("table".equals(name)) {
/* 432 */         transition(HtmlTreeBuilderState.InTable);
/* 433 */         break;
/* 434 */       }if ("head".equals(name)) {
/* 435 */         transition(HtmlTreeBuilderState.InBody);
/* 436 */         break;
/* 437 */       }if ("body".equals(name)) {
/* 438 */         transition(HtmlTreeBuilderState.InBody);
/* 439 */         break;
/* 440 */       }if ("frameset".equals(name)) {
/* 441 */         transition(HtmlTreeBuilderState.InFrameset);
/* 442 */         break;
/* 443 */       }if ("html".equals(name)) {
/* 444 */         transition(HtmlTreeBuilderState.BeforeHead);
/* 445 */         break;
/* 446 */       }if (last) {
/* 447 */         transition(HtmlTreeBuilderState.InBody);
/* 448 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes)
/*     */   {
/* 455 */     return inSpecificScope(new String[] { targetName }, baseTypes, extraTypes);
/*     */   }
/*     */ 
/*     */   private boolean inSpecificScope(String[] targetNames, String[] baseTypes, String[] extraTypes) {
/* 459 */     Iterator it = this.stack.descendingIterator();
/* 460 */     while (it.hasNext()) {
/* 461 */       Element el = (Element)it.next();
/* 462 */       String elName = el.nodeName();
/* 463 */       if (StringUtil.in(elName, targetNames))
/* 464 */         return true;
/* 465 */       if (StringUtil.in(elName, baseTypes))
/* 466 */         return false;
/* 467 */       if ((extraTypes != null) && (StringUtil.in(elName, extraTypes)))
/* 468 */         return false;
/*     */     }
/* 470 */     Validate.fail("Should not be reachable");
/* 471 */     return false;
/*     */   }
/*     */ 
/*     */   boolean inScope(String[] targetNames) {
/* 475 */     return inSpecificScope(targetNames, TagsSearchInScope, null);
/*     */   }
/*     */ 
/*     */   boolean inScope(String targetName) {
/* 479 */     return inScope(targetName, null);
/*     */   }
/*     */ 
/*     */   boolean inScope(String targetName, String[] extras) {
/* 483 */     return inSpecificScope(targetName, TagsSearchInScope, extras);
/*     */   }
/*     */ 
/*     */   boolean inListItemScope(String targetName)
/*     */   {
/* 489 */     return inScope(targetName, TagSearchList);
/*     */   }
/*     */ 
/*     */   boolean inButtonScope(String targetName) {
/* 493 */     return inScope(targetName, TagSearchButton);
/*     */   }
/*     */ 
/*     */   boolean inTableScope(String targetName) {
/* 497 */     return inSpecificScope(targetName, TagSearchTableScope, null);
/*     */   }
/*     */ 
/*     */   boolean inSelectScope(String targetName) {
/* 501 */     Iterator it = this.stack.descendingIterator();
/* 502 */     while (it.hasNext()) {
/* 503 */       Element el = (Element)it.next();
/* 504 */       String elName = el.nodeName();
/* 505 */       if (elName.equals(targetName))
/* 506 */         return true;
/* 507 */       if (!StringUtil.in(elName, TagSearchSelectScope))
/* 508 */         return false;
/*     */     }
/* 510 */     Validate.fail("Should not be reachable");
/* 511 */     return false;
/*     */   }
/*     */ 
/*     */   void setHeadElement(Element headElement) {
/* 515 */     this.headElement = headElement;
/*     */   }
/*     */ 
/*     */   Element getHeadElement() {
/* 519 */     return this.headElement;
/*     */   }
/*     */ 
/*     */   boolean isFosterInserts() {
/* 523 */     return this.fosterInserts;
/*     */   }
/*     */ 
/*     */   void setFosterInserts(boolean fosterInserts) {
/* 527 */     this.fosterInserts = fosterInserts;
/*     */   }
/*     */ 
/*     */   FormElement getFormElement() {
/* 531 */     return this.formElement;
/*     */   }
/*     */ 
/*     */   void setFormElement(FormElement formElement) {
/* 535 */     this.formElement = formElement;
/*     */   }
/*     */ 
/*     */   void newPendingTableCharacters() {
/* 539 */     this.pendingTableCharacters = new ArrayList();
/*     */   }
/*     */ 
/*     */   List<Token.Character> getPendingTableCharacters() {
/* 543 */     return this.pendingTableCharacters;
/*     */   }
/*     */ 
/*     */   void setPendingTableCharacters(List<Token.Character> pendingTableCharacters) {
/* 547 */     this.pendingTableCharacters = pendingTableCharacters;
/*     */   }
/*     */ 
/*     */   void generateImpliedEndTags(String excludeTag)
/*     */   {
/* 560 */     while ((excludeTag != null) && (!currentElement().nodeName().equals(excludeTag)) && (StringUtil.in(currentElement().nodeName(), TagSearchEndTags)))
/*     */     {
/* 562 */       pop();
/*     */     }
/*     */   }
/*     */ 
/* 566 */   void generateImpliedEndTags() { generateImpliedEndTags(null); }
/*     */ 
/*     */ 
/*     */   boolean isSpecial(Element el)
/*     */   {
/* 572 */     String name = el.nodeName();
/* 573 */     return StringUtil.in(name, TagSearchSpecial);
/*     */   }
/*     */ 
/*     */   void pushActiveFormattingElements(Element in)
/*     */   {
/* 578 */     int numSeen = 0;
/* 579 */     Iterator iter = this.formattingElements.descendingIterator();
/* 580 */     while (iter.hasNext()) {
/* 581 */       Element el = (Element)iter.next();
/* 582 */       if (el == null) {
/*     */         break;
/*     */       }
/* 585 */       if (isSameFormattingElement(in, el)) {
/* 586 */         numSeen++;
/*     */       }
/* 588 */       if (numSeen == 3) {
/* 589 */         iter.remove();
/* 590 */         break;
/*     */       }
/*     */     }
/* 593 */     this.formattingElements.add(in);
/*     */   }
/*     */ 
/*     */   private boolean isSameFormattingElement(Element a, Element b)
/*     */   {
/* 598 */     return (a.nodeName().equals(b.nodeName())) && (a.attributes().equals(b.attributes()));
/*     */   }
/*     */ 
/*     */   void reconstructFormattingElements()
/*     */   {
/* 605 */     int size = this.formattingElements.size();
/* 606 */     if ((size == 0) || (this.formattingElements.getLast() == null) || (onStack((Element)this.formattingElements.getLast()))) {
/* 607 */       return;
/*     */     }
/* 609 */     Element entry = (Element)this.formattingElements.getLast();
/* 610 */     int pos = size - 1;
/* 611 */     boolean skip = false;
/*     */     while (true)
/* 613 */       if (pos == 0) {
/* 614 */         skip = true;
/*     */       }
/*     */       else {
/* 617 */         entry = (Element)this.formattingElements.get(--pos);
/* 618 */         if (entry != null) if (onStack(entry))
/* 619 */             break; 
/*     */       }
/*     */     while (true)
/*     */     {
/* 622 */       if (!skip)
/* 623 */         entry = (Element)this.formattingElements.get(++pos);
/* 624 */       Validate.notNull(entry);
/*     */ 
/* 627 */       skip = false;
/* 628 */       Element newEl = insert(entry.nodeName());
/*     */ 
/* 630 */       newEl.attributes().addAll(entry.attributes());
/*     */ 
/* 633 */       this.formattingElements.add(pos, newEl);
/* 634 */       this.formattingElements.remove(pos + 1);
/*     */ 
/* 637 */       if (pos == size - 1)
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   void clearFormattingElementsToLastMarker() {
/* 643 */     while (!this.formattingElements.isEmpty()) {
/* 644 */       Element el = (Element)this.formattingElements.peekLast();
/* 645 */       this.formattingElements.removeLast();
/* 646 */       if (el == null)
/*     */         break;
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeFromActiveFormattingElements(Element el) {
/* 652 */     Iterator it = this.formattingElements.descendingIterator();
/* 653 */     while (it.hasNext()) {
/* 654 */       Element next = (Element)it.next();
/* 655 */       if (next == el) {
/* 656 */         it.remove();
/* 657 */         break;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean isInActiveFormattingElements(Element el) {
/* 663 */     return isElementInQueue(this.formattingElements, el);
/*     */   }
/*     */ 
/*     */   Element getActiveFormattingElement(String nodeName) {
/* 667 */     Iterator it = this.formattingElements.descendingIterator();
/* 668 */     while (it.hasNext()) {
/* 669 */       Element next = (Element)it.next();
/* 670 */       if (next == null)
/*     */         break;
/* 672 */       if (next.nodeName().equals(nodeName))
/* 673 */         return next;
/*     */     }
/* 675 */     return null;
/*     */   }
/*     */ 
/*     */   void replaceActiveFormattingElement(Element out, Element in) {
/* 679 */     replaceInQueue(this.formattingElements, out, in);
/*     */   }
/*     */ 
/*     */   void insertMarkerToFormattingElements() {
/* 683 */     this.formattingElements.add(null);
/*     */   }
/*     */ 
/*     */   void insertInFosterParent(Node in) {
/* 687 */     Element fosterParent = null;
/* 688 */     Element lastTable = getFromStack("table");
/* 689 */     boolean isLastTableParent = false;
/* 690 */     if (lastTable != null) {
/* 691 */       if (lastTable.parent() != null) {
/* 692 */         fosterParent = lastTable.parent();
/* 693 */         isLastTableParent = true;
/*     */       } else {
/* 695 */         fosterParent = aboveOnStack(lastTable);
/*     */       }
/*     */     } else fosterParent = (Element)this.stack.get(0);
/*     */ 
/* 700 */     if (isLastTableParent) {
/* 701 */       Validate.notNull(lastTable);
/* 702 */       lastTable.before(in);
/*     */     }
/*     */     else {
/* 705 */       fosterParent.appendChild(in);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 710 */     return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + this.state + ", currentElement=" + currentElement() + '}';
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.HtmlTreeBuilder
 * JD-Core Version:    0.6.2
 */