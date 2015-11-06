/*      */ package org.jsoup.nodes;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.regex.PatternSyntaxException;
/*      */ import org.jsoup.helper.StringUtil;
/*      */ import org.jsoup.helper.Validate;
/*      */ import org.jsoup.parser.Parser;
/*      */ import org.jsoup.parser.Tag;
/*      */ import org.jsoup.select.Collector;
/*      */ import org.jsoup.select.Elements;
/*      */ import org.jsoup.select.Evaluator.AllElements;
/*      */ import org.jsoup.select.Evaluator.Attribute;
/*      */ import org.jsoup.select.Evaluator.AttributeStarting;
/*      */ import org.jsoup.select.Evaluator.AttributeWithValue;
/*      */ import org.jsoup.select.Evaluator.AttributeWithValueContaining;
/*      */ import org.jsoup.select.Evaluator.AttributeWithValueEnding;
/*      */ import org.jsoup.select.Evaluator.AttributeWithValueMatching;
/*      */ import org.jsoup.select.Evaluator.AttributeWithValueNot;
/*      */ import org.jsoup.select.Evaluator.AttributeWithValueStarting;
/*      */ import org.jsoup.select.Evaluator.Class;
/*      */ import org.jsoup.select.Evaluator.ContainsOwnText;
/*      */ import org.jsoup.select.Evaluator.ContainsText;
/*      */ import org.jsoup.select.Evaluator.Id;
/*      */ import org.jsoup.select.Evaluator.IndexEquals;
/*      */ import org.jsoup.select.Evaluator.IndexGreaterThan;
/*      */ import org.jsoup.select.Evaluator.IndexLessThan;
/*      */ import org.jsoup.select.Evaluator.Matches;
/*      */ import org.jsoup.select.Evaluator.MatchesOwn;
/*      */ import org.jsoup.select.Evaluator.Tag;
/*      */ import org.jsoup.select.NodeTraversor;
/*      */ import org.jsoup.select.NodeVisitor;
/*      */ import org.jsoup.select.Selector;
/*      */ 
/*      */ public class Element extends Node
/*      */ {
/*      */   private Tag tag;
/*      */   private Set<String> classNames;
/*      */ 
/*      */   public Element(Tag tag, String baseUri, Attributes attributes)
/*      */   {
/*   35 */     super(baseUri, attributes);
/*      */ 
/*   37 */     Validate.notNull(tag);
/*   38 */     this.tag = tag;
/*      */   }
/*      */ 
/*      */   public Element(Tag tag, String baseUri)
/*      */   {
/*   50 */     this(tag, baseUri, new Attributes());
/*      */   }
/*      */ 
/*      */   public String nodeName()
/*      */   {
/*   55 */     return this.tag.getName();
/*      */   }
/*      */ 
/*      */   public String tagName()
/*      */   {
/*   64 */     return this.tag.getName();
/*      */   }
/*      */ 
/*      */   public Element tagName(String tagName)
/*      */   {
/*   75 */     Validate.notEmpty(tagName, "Tag name must not be empty.");
/*   76 */     this.tag = Tag.valueOf(tagName);
/*   77 */     return this;
/*      */   }
/*      */ 
/*      */   public Tag tag()
/*      */   {
/*   86 */     return this.tag;
/*      */   }
/*      */ 
/*      */   public boolean isBlock()
/*      */   {
/*   96 */     return this.tag.isBlock();
/*      */   }
/*      */ 
/*      */   public String id()
/*      */   {
/*  105 */     String id = attr("id");
/*  106 */     return id == null ? "" : id;
/*      */   }
/*      */ 
/*      */   public Element attr(String attributeKey, String attributeValue)
/*      */   {
/*  116 */     super.attr(attributeKey, attributeValue);
/*  117 */     return this;
/*      */   }
/*      */ 
/*      */   public Map<String, String> dataset()
/*      */   {
/*  134 */     return this.attributes.dataset();
/*      */   }
/*      */ 
/*      */   public final Element parent()
/*      */   {
/*  139 */     return (Element)this.parentNode;
/*      */   }
/*      */ 
/*      */   public Elements parents()
/*      */   {
/*  147 */     Elements parents = new Elements();
/*  148 */     accumulateParents(this, parents);
/*  149 */     return parents;
/*      */   }
/*      */ 
/*      */   private static void accumulateParents(Element el, Elements parents) {
/*  153 */     Element parent = el.parent();
/*  154 */     if ((parent != null) && (!parent.tagName().equals("#root"))) {
/*  155 */       parents.add(parent);
/*  156 */       accumulateParents(parent, parents);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Element child(int index)
/*      */   {
/*  171 */     return children().get(index);
/*      */   }
/*      */ 
/*      */   public Elements children()
/*      */   {
/*  184 */     List elements = new ArrayList(this.childNodes.size());
/*  185 */     for (Node node : this.childNodes) {
/*  186 */       if ((node instanceof Element))
/*  187 */         elements.add((Element)node);
/*      */     }
/*  189 */     return new Elements(elements);
/*      */   }
/*      */ 
/*      */   public List<TextNode> textNodes()
/*      */   {
/*  209 */     List textNodes = new ArrayList();
/*  210 */     for (Node node : this.childNodes) {
/*  211 */       if ((node instanceof TextNode))
/*  212 */         textNodes.add((TextNode)node);
/*      */     }
/*  214 */     return Collections.unmodifiableList(textNodes);
/*      */   }
/*      */ 
/*      */   public List<DataNode> dataNodes()
/*      */   {
/*  226 */     List dataNodes = new ArrayList();
/*  227 */     for (Node node : this.childNodes) {
/*  228 */       if ((node instanceof DataNode))
/*  229 */         dataNodes.add((DataNode)node);
/*      */     }
/*  231 */     return Collections.unmodifiableList(dataNodes);
/*      */   }
/*      */ 
/*      */   public Elements select(String cssQuery)
/*      */   {
/*  252 */     return Selector.select(cssQuery, this);
/*      */   }
/*      */ 
/*      */   public Element appendChild(Node child)
/*      */   {
/*  262 */     Validate.notNull(child);
/*      */ 
/*  264 */     addChildren(new Node[] { child });
/*  265 */     return this;
/*      */   }
/*      */ 
/*      */   public Element prependChild(Node child)
/*      */   {
/*  275 */     Validate.notNull(child);
/*      */ 
/*  277 */     addChildren(0, new Node[] { child });
/*  278 */     return this;
/*      */   }
/*      */ 
/*      */   public Element insertChildren(int index, Collection<? extends Node> children)
/*      */   {
/*  292 */     Validate.notNull(children, "Children collection to be inserted must not be null.");
/*  293 */     int currentSize = childNodeSize();
/*  294 */     if (index < 0) index += currentSize + 1;
/*  295 */     Validate.isTrue((index >= 0) && (index <= currentSize), "Insert position out of bounds.");
/*      */ 
/*  297 */     ArrayList nodes = new ArrayList(children);
/*  298 */     Node[] nodeArray = (Node[])nodes.toArray(new Node[nodes.size()]);
/*  299 */     addChildren(index, nodeArray);
/*  300 */     return this;
/*      */   }
/*      */ 
/*      */   public Element appendElement(String tagName)
/*      */   {
/*  311 */     Element child = new Element(Tag.valueOf(tagName), baseUri());
/*  312 */     appendChild(child);
/*  313 */     return child;
/*      */   }
/*      */ 
/*      */   public Element prependElement(String tagName)
/*      */   {
/*  324 */     Element child = new Element(Tag.valueOf(tagName), baseUri());
/*  325 */     prependChild(child);
/*  326 */     return child;
/*      */   }
/*      */ 
/*      */   public Element appendText(String text)
/*      */   {
/*  336 */     TextNode node = new TextNode(text, baseUri());
/*  337 */     appendChild(node);
/*  338 */     return this;
/*      */   }
/*      */ 
/*      */   public Element prependText(String text)
/*      */   {
/*  348 */     TextNode node = new TextNode(text, baseUri());
/*  349 */     prependChild(node);
/*  350 */     return this;
/*      */   }
/*      */ 
/*      */   public Element append(String html)
/*      */   {
/*  360 */     Validate.notNull(html);
/*      */ 
/*  362 */     List nodes = Parser.parseFragment(html, this, baseUri());
/*  363 */     addChildren((Node[])nodes.toArray(new Node[nodes.size()]));
/*  364 */     return this;
/*      */   }
/*      */ 
/*      */   public Element prepend(String html)
/*      */   {
/*  374 */     Validate.notNull(html);
/*      */ 
/*  376 */     List nodes = Parser.parseFragment(html, this, baseUri());
/*  377 */     addChildren(0, (Node[])nodes.toArray(new Node[nodes.size()]));
/*  378 */     return this;
/*      */   }
/*      */ 
/*      */   public Element before(String html)
/*      */   {
/*  390 */     return (Element)super.before(html);
/*      */   }
/*      */ 
/*      */   public Element before(Node node)
/*      */   {
/*  401 */     return (Element)super.before(node);
/*      */   }
/*      */ 
/*      */   public Element after(String html)
/*      */   {
/*  413 */     return (Element)super.after(html);
/*      */   }
/*      */ 
/*      */   public Element after(Node node)
/*      */   {
/*  424 */     return (Element)super.after(node);
/*      */   }
/*      */ 
/*      */   public Element empty()
/*      */   {
/*  432 */     this.childNodes.clear();
/*  433 */     return this;
/*      */   }
/*      */ 
/*      */   public Element wrap(String html)
/*      */   {
/*  444 */     return (Element)super.wrap(html);
/*      */   }
/*      */ 
/*      */   public String cssSelector()
/*      */   {
/*  456 */     if (id().length() > 0) {
/*  457 */       return "#" + id();
/*      */     }
/*  459 */     StringBuilder selector = new StringBuilder(tagName());
/*  460 */     String classes = StringUtil.join(classNames(), ".");
/*  461 */     if (classes.length() > 0) {
/*  462 */       selector.append('.').append(classes);
/*      */     }
/*  464 */     if ((parent() == null) || ((parent() instanceof Document))) {
/*  465 */       return selector.toString();
/*      */     }
/*  467 */     selector.insert(0, " > ");
/*  468 */     if (parent().select(selector.toString()).size() > 1) {
/*  469 */       selector.append(String.format(":nth-child(%d)", new Object[] { Integer.valueOf(elementSiblingIndex().intValue() + 1) }));
/*      */     }
/*      */ 
/*  472 */     return parent().cssSelector() + selector.toString();
/*      */   }
/*      */ 
/*      */   public Elements siblingElements()
/*      */   {
/*  481 */     if (this.parentNode == null) {
/*  482 */       return new Elements(0);
/*      */     }
/*  484 */     List elements = parent().children();
/*  485 */     Elements siblings = new Elements(elements.size() - 1);
/*  486 */     for (Element el : elements)
/*  487 */       if (el != this)
/*  488 */         siblings.add(el);
/*  489 */     return siblings;
/*      */   }
/*      */ 
/*      */   public Element nextElementSibling()
/*      */   {
/*  501 */     if (this.parentNode == null) return null;
/*  502 */     List siblings = parent().children();
/*  503 */     Integer index = indexInList(this, siblings);
/*  504 */     Validate.notNull(index);
/*  505 */     if (siblings.size() > index.intValue() + 1) {
/*  506 */       return (Element)siblings.get(index.intValue() + 1);
/*      */     }
/*  508 */     return null;
/*      */   }
/*      */ 
/*      */   public Element previousElementSibling()
/*      */   {
/*  517 */     if (this.parentNode == null) return null;
/*  518 */     List siblings = parent().children();
/*  519 */     Integer index = indexInList(this, siblings);
/*  520 */     Validate.notNull(index);
/*  521 */     if (index.intValue() > 0) {
/*  522 */       return (Element)siblings.get(index.intValue() - 1);
/*      */     }
/*  524 */     return null;
/*      */   }
/*      */ 
/*      */   public Element firstElementSibling()
/*      */   {
/*  533 */     List siblings = parent().children();
/*  534 */     return siblings.size() > 1 ? (Element)siblings.get(0) : null;
/*      */   }
/*      */ 
/*      */   public Integer elementSiblingIndex()
/*      */   {
/*  543 */     if (parent() == null) return Integer.valueOf(0);
/*  544 */     return indexInList(this, parent().children());
/*      */   }
/*      */ 
/*      */   public Element lastElementSibling()
/*      */   {
/*  552 */     List siblings = parent().children();
/*  553 */     return siblings.size() > 1 ? (Element)siblings.get(siblings.size() - 1) : null;
/*      */   }
/*      */ 
/*      */   private static <E extends Element> Integer indexInList(Element search, List<E> elements) {
/*  557 */     Validate.notNull(search);
/*  558 */     Validate.notNull(elements);
/*      */ 
/*  560 */     for (int i = 0; i < elements.size(); i++) {
/*  561 */       Element element = (Element)elements.get(i);
/*  562 */       if (element.equals(search))
/*  563 */         return Integer.valueOf(i);
/*      */     }
/*  565 */     return null;
/*      */   }
/*      */ 
/*      */   public Elements getElementsByTag(String tagName)
/*      */   {
/*  576 */     Validate.notEmpty(tagName);
/*  577 */     tagName = tagName.toLowerCase().trim();
/*      */ 
/*  579 */     return Collector.collect(new Evaluator.Tag(tagName), this);
/*      */   }
/*      */ 
/*      */   public Element getElementById(String id)
/*      */   {
/*  592 */     Validate.notEmpty(id);
/*      */ 
/*  594 */     Elements elements = Collector.collect(new Evaluator.Id(id), this);
/*  595 */     if (elements.size() > 0) {
/*  596 */       return elements.get(0);
/*      */     }
/*  598 */     return null;
/*      */   }
/*      */ 
/*      */   public Elements getElementsByClass(String className)
/*      */   {
/*  613 */     Validate.notEmpty(className);
/*      */ 
/*  615 */     return Collector.collect(new Evaluator.Class(className), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttribute(String key)
/*      */   {
/*  625 */     Validate.notEmpty(key);
/*  626 */     key = key.trim().toLowerCase();
/*      */ 
/*  628 */     return Collector.collect(new Evaluator.Attribute(key), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeStarting(String keyPrefix)
/*      */   {
/*  638 */     Validate.notEmpty(keyPrefix);
/*  639 */     keyPrefix = keyPrefix.trim().toLowerCase();
/*      */ 
/*  641 */     return Collector.collect(new Evaluator.AttributeStarting(keyPrefix), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValue(String key, String value)
/*      */   {
/*  652 */     return Collector.collect(new Evaluator.AttributeWithValue(key, value), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValueNot(String key, String value)
/*      */   {
/*  663 */     return Collector.collect(new Evaluator.AttributeWithValueNot(key, value), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValueStarting(String key, String valuePrefix)
/*      */   {
/*  674 */     return Collector.collect(new Evaluator.AttributeWithValueStarting(key, valuePrefix), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValueEnding(String key, String valueSuffix)
/*      */   {
/*  685 */     return Collector.collect(new Evaluator.AttributeWithValueEnding(key, valueSuffix), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValueContaining(String key, String match)
/*      */   {
/*  696 */     return Collector.collect(new Evaluator.AttributeWithValueContaining(key, match), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValueMatching(String key, Pattern pattern)
/*      */   {
/*  706 */     return Collector.collect(new Evaluator.AttributeWithValueMatching(key, pattern), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByAttributeValueMatching(String key, String regex)
/*      */   {
/*      */     Pattern pattern;
/*      */     try
/*      */     {
/*  719 */       pattern = Pattern.compile(regex);
/*      */     } catch (PatternSyntaxException e) {
/*  721 */       throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*      */     }
/*  723 */     return getElementsByAttributeValueMatching(key, pattern);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByIndexLessThan(int index)
/*      */   {
/*  732 */     return Collector.collect(new Evaluator.IndexLessThan(index), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByIndexGreaterThan(int index)
/*      */   {
/*  741 */     return Collector.collect(new Evaluator.IndexGreaterThan(index), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsByIndexEquals(int index)
/*      */   {
/*  750 */     return Collector.collect(new Evaluator.IndexEquals(index), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsContainingText(String searchText)
/*      */   {
/*  761 */     return Collector.collect(new Evaluator.ContainsText(searchText), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsContainingOwnText(String searchText)
/*      */   {
/*  772 */     return Collector.collect(new Evaluator.ContainsOwnText(searchText), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsMatchingText(Pattern pattern)
/*      */   {
/*  782 */     return Collector.collect(new Evaluator.Matches(pattern), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsMatchingText(String regex)
/*      */   {
/*      */     Pattern pattern;
/*      */     try
/*      */     {
/*  794 */       pattern = Pattern.compile(regex);
/*      */     } catch (PatternSyntaxException e) {
/*  796 */       throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*      */     }
/*  798 */     return getElementsMatchingText(pattern);
/*      */   }
/*      */ 
/*      */   public Elements getElementsMatchingOwnText(Pattern pattern)
/*      */   {
/*  808 */     return Collector.collect(new Evaluator.MatchesOwn(pattern), this);
/*      */   }
/*      */ 
/*      */   public Elements getElementsMatchingOwnText(String regex)
/*      */   {
/*      */     Pattern pattern;
/*      */     try
/*      */     {
/*  820 */       pattern = Pattern.compile(regex);
/*      */     } catch (PatternSyntaxException e) {
/*  822 */       throw new IllegalArgumentException("Pattern syntax error: " + regex, e);
/*      */     }
/*  824 */     return getElementsMatchingOwnText(pattern);
/*      */   }
/*      */ 
/*      */   public Elements getAllElements()
/*      */   {
/*  833 */     return Collector.collect(new Evaluator.AllElements(), this);
/*      */   }
/*      */ 
/*      */   public String text()
/*      */   {
/*  846 */     final StringBuilder accum = new StringBuilder();
/*  847 */     new NodeTraversor(new NodeVisitor() {
/*      */       public void head(Node node, int depth) {
/*  849 */         if ((node instanceof TextNode)) {
/*  850 */           TextNode textNode = (TextNode)node;
/*  851 */           Element.appendNormalisedText(accum, textNode);
/*  852 */         } else if ((node instanceof Element)) {
/*  853 */           Element element = (Element)node;
/*  854 */           if ((accum.length() > 0) && ((element.isBlock()) || (element.tag.getName().equals("br"))) && (!TextNode.lastCharIsWhitespace(accum)))
/*      */           {
/*  857 */             accum.append(" ");
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */       public void tail(Node node, int depth)
/*      */       {
/*      */       }
/*      */     }).traverse(this);
/*      */ 
/*  864 */     return accum.toString().trim();
/*      */   }
/*      */ 
/*      */   public String ownText()
/*      */   {
/*  879 */     StringBuilder sb = new StringBuilder();
/*  880 */     ownText(sb);
/*  881 */     return sb.toString().trim();
/*      */   }
/*      */ 
/*      */   private void ownText(StringBuilder accum) {
/*  885 */     for (Node child : this.childNodes)
/*  886 */       if ((child instanceof TextNode)) {
/*  887 */         TextNode textNode = (TextNode)child;
/*  888 */         appendNormalisedText(accum, textNode);
/*  889 */       } else if ((child instanceof Element)) {
/*  890 */         appendWhitespaceIfBr((Element)child, accum);
/*      */       }
/*      */   }
/*      */ 
/*      */   private static void appendNormalisedText(StringBuilder accum, TextNode textNode)
/*      */   {
/*  896 */     String text = textNode.getWholeText();
/*      */ 
/*  898 */     if (preserveWhitespace(textNode.parentNode))
/*  899 */       accum.append(text);
/*      */     else
/*  901 */       StringUtil.appendNormalisedWhitespace(accum, text, TextNode.lastCharIsWhitespace(accum));
/*      */   }
/*      */ 
/*      */   private static void appendWhitespaceIfBr(Element element, StringBuilder accum) {
/*  905 */     if ((element.tag.getName().equals("br")) && (!TextNode.lastCharIsWhitespace(accum)))
/*  906 */       accum.append(" ");
/*      */   }
/*      */ 
/*      */   static boolean preserveWhitespace(Node node)
/*      */   {
/*  911 */     if ((node != null) && ((node instanceof Element))) {
/*  912 */       Element element = (Element)node;
/*  913 */       return (element.tag.preserveWhitespace()) || ((element.parent() != null) && (element.parent().tag.preserveWhitespace()));
/*      */     }
/*      */ 
/*  916 */     return false;
/*      */   }
/*      */ 
/*      */   public Element text(String text)
/*      */   {
/*  925 */     Validate.notNull(text);
/*      */ 
/*  927 */     empty();
/*  928 */     TextNode textNode = new TextNode(text, this.baseUri);
/*  929 */     appendChild(textNode);
/*      */ 
/*  931 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean hasText()
/*      */   {
/*  939 */     for (Node child : this.childNodes) {
/*  940 */       if ((child instanceof TextNode)) {
/*  941 */         TextNode textNode = (TextNode)child;
/*  942 */         if (!textNode.isBlank())
/*  943 */           return true;
/*  944 */       } else if ((child instanceof Element)) {
/*  945 */         Element el = (Element)child;
/*  946 */         if (el.hasText())
/*  947 */           return true;
/*      */       }
/*      */     }
/*  950 */     return false;
/*      */   }
/*      */ 
/*      */   public String data()
/*      */   {
/*  960 */     StringBuilder sb = new StringBuilder();
/*      */ 
/*  962 */     for (Node childNode : this.childNodes) {
/*  963 */       if ((childNode instanceof DataNode)) {
/*  964 */         DataNode data = (DataNode)childNode;
/*  965 */         sb.append(data.getWholeData());
/*  966 */       } else if ((childNode instanceof Element)) {
/*  967 */         Element element = (Element)childNode;
/*  968 */         String elementData = element.data();
/*  969 */         sb.append(elementData);
/*      */       }
/*      */     }
/*  972 */     return sb.toString();
/*      */   }
/*      */ 
/*      */   public String className()
/*      */   {
/*  981 */     return attr("class");
/*      */   }
/*      */ 
/*      */   public Set<String> classNames()
/*      */   {
/*  991 */     if (this.classNames == null) {
/*  992 */       String[] names = className().split("\\s+");
/*  993 */       this.classNames = new LinkedHashSet(Arrays.asList(names));
/*      */     }
/*  995 */     return this.classNames;
/*      */   }
/*      */ 
/*      */   public Element classNames(Set<String> classNames)
/*      */   {
/* 1004 */     Validate.notNull(classNames);
/* 1005 */     this.attributes.put("class", StringUtil.join(classNames, " "));
/* 1006 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean hasClass(String className)
/*      */   {
/* 1015 */     Set classNames = classNames();
/* 1016 */     for (String name : classNames) {
/* 1017 */       if (className.equalsIgnoreCase(name))
/* 1018 */         return true;
/*      */     }
/* 1020 */     return false;
/*      */   }
/*      */ 
/*      */   public Element addClass(String className)
/*      */   {
/* 1029 */     Validate.notNull(className);
/*      */ 
/* 1031 */     Set classes = classNames();
/* 1032 */     classes.add(className);
/* 1033 */     classNames(classes);
/*      */ 
/* 1035 */     return this;
/*      */   }
/*      */ 
/*      */   public Element removeClass(String className)
/*      */   {
/* 1044 */     Validate.notNull(className);
/*      */ 
/* 1046 */     Set classes = classNames();
/* 1047 */     classes.remove(className);
/* 1048 */     classNames(classes);
/*      */ 
/* 1050 */     return this;
/*      */   }
/*      */ 
/*      */   public Element toggleClass(String className)
/*      */   {
/* 1059 */     Validate.notNull(className);
/*      */ 
/* 1061 */     Set classes = classNames();
/* 1062 */     if (classes.contains(className))
/* 1063 */       classes.remove(className);
/*      */     else
/* 1065 */       classes.add(className);
/* 1066 */     classNames(classes);
/*      */ 
/* 1068 */     return this;
/*      */   }
/*      */ 
/*      */   public String val()
/*      */   {
/* 1076 */     if (tagName().equals("textarea")) {
/* 1077 */       return text();
/*      */     }
/* 1079 */     return attr("value");
/*      */   }
/*      */ 
/*      */   public Element val(String value)
/*      */   {
/* 1088 */     if (tagName().equals("textarea"))
/* 1089 */       text(value);
/*      */     else
/* 1091 */       attr("value", value);
/* 1092 */     return this;
/*      */   }
/*      */ 
/*      */   void outerHtmlHead(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 1096 */     if ((accum.length() > 0) && (out.prettyPrint()) && ((this.tag.formatAsBlock()) || ((parent() != null) && (parent().tag().formatAsBlock())) || (out.outline())))
/* 1097 */       indent(accum, depth, out);
/* 1098 */     accum.append("<").append(tagName());
/*      */ 
/* 1101 */     this.attributes.html(accum, out);
/*      */ 
/* 1104 */     if ((this.childNodes.isEmpty()) && (this.tag.isSelfClosing())) {
/* 1105 */       if ((out.syntax() == Document.OutputSettings.Syntax.html) && (this.tag.isEmpty()))
/* 1106 */         accum.append('>');
/*      */       else
/* 1108 */         accum.append(" />");
/*      */     }
/*      */     else
/* 1111 */       accum.append(">");
/*      */   }
/*      */ 
/*      */   void outerHtmlTail(StringBuilder accum, int depth, Document.OutputSettings out) {
/* 1115 */     if ((!this.childNodes.isEmpty()) || (!this.tag.isSelfClosing())) {
/* 1116 */       if ((out.prettyPrint()) && (!this.childNodes.isEmpty()) && ((this.tag.formatAsBlock()) || ((out.outline()) && ((this.childNodes.size() > 1) || ((this.childNodes.size() == 1) && (!(this.childNodes.get(0) instanceof TextNode)))))))
/*      */       {
/* 1119 */         indent(accum, depth, out);
/* 1120 */       }accum.append("</").append(tagName()).append(">");
/*      */     }
/*      */   }
/*      */ 
/*      */   public String html()
/*      */   {
/* 1132 */     StringBuilder accum = new StringBuilder();
/* 1133 */     html(accum);
/* 1134 */     return getOutputSettings().prettyPrint() ? accum.toString().trim() : accum.toString();
/*      */   }
/*      */ 
/*      */   private void html(StringBuilder accum) {
/* 1138 */     for (Node node : this.childNodes)
/* 1139 */       node.outerHtml(accum);
/*      */   }
/*      */ 
/*      */   public Element html(String html)
/*      */   {
/* 1149 */     empty();
/* 1150 */     append(html);
/* 1151 */     return this;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1155 */     return outerHtml();
/*      */   }
/*      */ 
/*      */   public boolean equals(Object o)
/*      */   {
/* 1160 */     return this == o;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1166 */     int result = super.hashCode();
/* 1167 */     result = 31 * result + (this.tag != null ? this.tag.hashCode() : 0);
/* 1168 */     return result;
/*      */   }
/*      */ 
/*      */   public Element clone()
/*      */   {
/* 1173 */     Element clone = (Element)super.clone();
/* 1174 */     clone.classNames = null;
/* 1175 */     return clone;
/*      */   }
/*      */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Element
 * JD-Core Version:    0.6.2
 */