/*     */ package org.jsoup.select;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Attribute;
/*     */ import org.jsoup.nodes.Attributes;
/*     */ import org.jsoup.nodes.Comment;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.DocumentType;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.Node;
/*     */ import org.jsoup.nodes.XmlDeclaration;
/*     */ import org.jsoup.parser.Tag;
/*     */ 
/*     */ public abstract class Evaluator
/*     */ {
/*     */   public abstract boolean matches(Element paramElement1, Element paramElement2);
/*     */ 
/*     */   public static final class MatchesOwn extends Evaluator
/*     */   {
/*     */     private Pattern pattern;
/*     */ 
/*     */     public MatchesOwn(Pattern pattern)
/*     */     {
/* 690 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 695 */       Matcher m = this.pattern.matcher(element.ownText());
/* 696 */       return m.find();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 701 */       return String.format(":matchesOwn(%s", new Object[] { this.pattern });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Matches extends Evaluator
/*     */   {
/*     */     private Pattern pattern;
/*     */ 
/*     */     public Matches(Pattern pattern)
/*     */     {
/* 668 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 673 */       Matcher m = this.pattern.matcher(element.text());
/* 674 */       return m.find();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 679 */       return String.format(":matches(%s", new Object[] { this.pattern });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class ContainsOwnText extends Evaluator
/*     */   {
/*     */     private String searchText;
/*     */ 
/*     */     public ContainsOwnText(String searchText)
/*     */     {
/* 647 */       this.searchText = searchText.toLowerCase();
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 652 */       return element.ownText().toLowerCase().contains(this.searchText);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 657 */       return String.format(":containsOwn(%s", new Object[] { this.searchText });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class ContainsText extends Evaluator
/*     */   {
/*     */     private String searchText;
/*     */ 
/*     */     public ContainsText(String searchText)
/*     */     {
/* 626 */       this.searchText = searchText.toLowerCase();
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 631 */       return element.text().toLowerCase().contains(this.searchText);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 636 */       return String.format(":contains(%s", new Object[] { this.searchText });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class IndexEvaluator extends Evaluator
/*     */   {
/*     */     int index;
/*     */ 
/*     */     public IndexEvaluator(int index)
/*     */     {
/* 615 */       this.index = index;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsEmpty extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 593 */       List family = element.childNodes();
/* 594 */       for (int i = 0; i < family.size(); i++) {
/* 595 */         Node n = (Node)family.get(i);
/* 596 */         if ((!(n instanceof Comment)) && (!(n instanceof XmlDeclaration)) && (!(n instanceof DocumentType))) return false;
/*     */       }
/* 598 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 602 */       return ":empty";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsOnlyOfType extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 574 */       Element p = element.parent();
/* 575 */       if ((p == null) || ((p instanceof Document))) return false;
/*     */ 
/* 577 */       int pos = 0;
/* 578 */       Elements family = p.children();
/* 579 */       for (int i = 0; i < family.size(); i++) {
/* 580 */         if (family.get(i).tag().equals(element.tag())) pos++;
/*     */       }
/* 582 */       return pos == 1;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 586 */       return ":only-of-type";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsOnlyChild extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 562 */       Element p = element.parent();
/* 563 */       return (p != null) && (!(p instanceof Document)) && (element.siblingElements().size() == 0);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 567 */       return ":only-child";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsRoot extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 550 */       Element r = (root instanceof Document) ? root.child(0) : root;
/* 551 */       return element == r;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 555 */       return ":root";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsFirstChild extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 532 */       Element p = element.parent();
/* 533 */       return (p != null) && (!(p instanceof Document)) && (element.elementSiblingIndex().intValue() == 0);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 538 */       return ":first-child";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class IsNthLastOfType extends Evaluator.CssNthEvaluator
/*     */   {
/*     */     public IsNthLastOfType(int a, int b)
/*     */     {
/* 507 */       super(b);
/*     */     }
/*     */ 
/*     */     protected int calculatePosition(Element root, Element element)
/*     */     {
/* 512 */       int pos = 0;
/* 513 */       Elements family = element.parent().children();
/* 514 */       for (int i = element.elementSiblingIndex().intValue(); i < family.size(); i++) {
/* 515 */         if (family.get(i).tag().equals(element.tag())) pos++;
/*     */       }
/* 517 */       return pos;
/*     */     }
/*     */ 
/*     */     protected String getPseudoClass()
/*     */     {
/* 522 */       return "nth-last-of-type";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class IsNthOfType extends Evaluator.CssNthEvaluator
/*     */   {
/*     */     public IsNthOfType(int a, int b)
/*     */     {
/* 485 */       super(b);
/*     */     }
/*     */ 
/*     */     protected int calculatePosition(Element root, Element element) {
/* 489 */       int pos = 0;
/* 490 */       Elements family = element.parent().children();
/* 491 */       for (int i = 0; i < family.size(); i++) {
/* 492 */         if (family.get(i).tag().equals(element.tag())) pos++;
/* 493 */         if (family.get(i) == element) break;
/*     */       }
/* 495 */       return pos;
/*     */     }
/*     */ 
/*     */     protected String getPseudoClass()
/*     */     {
/* 500 */       return "nth-of-type";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsNthLastChild extends Evaluator.CssNthEvaluator
/*     */   {
/*     */     public IsNthLastChild(int a, int b)
/*     */     {
/* 465 */       super(b);
/*     */     }
/*     */ 
/*     */     protected int calculatePosition(Element root, Element element)
/*     */     {
/* 470 */       return element.parent().children().size() - element.elementSiblingIndex().intValue();
/*     */     }
/*     */ 
/*     */     protected String getPseudoClass()
/*     */     {
/* 475 */       return "nth-last-child";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsNthChild extends Evaluator.CssNthEvaluator
/*     */   {
/*     */     public IsNthChild(int a, int b)
/*     */     {
/* 445 */       super(b);
/*     */     }
/*     */ 
/*     */     protected int calculatePosition(Element root, Element element) {
/* 449 */       return element.elementSiblingIndex().intValue() + 1;
/*     */     }
/*     */ 
/*     */     protected String getPseudoClass()
/*     */     {
/* 454 */       return "nth-child";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class CssNthEvaluator extends Evaluator
/*     */   {
/*     */     protected final int a;
/*     */     protected final int b;
/*     */ 
/*     */     public CssNthEvaluator(int a, int b)
/*     */     {
/* 405 */       this.a = a;
/* 406 */       this.b = b;
/*     */     }
/*     */     public CssNthEvaluator(int b) {
/* 409 */       this(0, b);
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 414 */       Element p = element.parent();
/* 415 */       if ((p == null) || ((p instanceof Document))) return false;
/*     */ 
/* 417 */       int pos = calculatePosition(root, element);
/* 418 */       if (this.a == 0) return pos == this.b;
/*     */ 
/* 420 */       return ((pos - this.b) * this.a >= 0) && ((pos - this.b) % this.a == 0);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 425 */       if (this.a == 0)
/* 426 */         return String.format(":%s(%d)", new Object[] { getPseudoClass(), Integer.valueOf(this.b) });
/* 427 */       if (this.b == 0)
/* 428 */         return String.format(":%s(%dn)", new Object[] { getPseudoClass(), Integer.valueOf(this.a) });
/* 429 */       return String.format(":%s(%dn%+d)", new Object[] { getPseudoClass(), Integer.valueOf(this.a), Integer.valueOf(this.b) });
/*     */     }
/*     */ 
/*     */     protected abstract String getPseudoClass();
/*     */ 
/*     */     protected abstract int calculatePosition(Element paramElement1, Element paramElement2);
/*     */   }
/*     */ 
/*     */   public static final class IsLastOfType extends Evaluator.IsNthLastOfType
/*     */   {
/*     */     public IsLastOfType()
/*     */     {
/* 392 */       super(1);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 396 */       return ":last-of-type";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsFirstOfType extends Evaluator.IsNthOfType
/*     */   {
/*     */     public IsFirstOfType()
/*     */     {
/* 382 */       super(1);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 386 */       return ":first-of-type";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IsLastChild extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 370 */       Element p = element.parent();
/* 371 */       return (p != null) && (!(p instanceof Document)) && (element.elementSiblingIndex().intValue() == p.children().size() - 1);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 376 */       return ":last-child";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IndexEquals extends Evaluator.IndexEvaluator
/*     */   {
/*     */     public IndexEquals(int index)
/*     */     {
/* 349 */       super();
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 354 */       return element.elementSiblingIndex().intValue() == this.index;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 359 */       return String.format(":eq(%d)", new Object[] { Integer.valueOf(this.index) });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IndexGreaterThan extends Evaluator.IndexEvaluator
/*     */   {
/*     */     public IndexGreaterThan(int index)
/*     */     {
/* 329 */       super();
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 334 */       return element.elementSiblingIndex().intValue() > this.index;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 339 */       return String.format(":gt(%d)", new Object[] { Integer.valueOf(this.index) });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class IndexLessThan extends Evaluator.IndexEvaluator
/*     */   {
/*     */     public IndexLessThan(int index)
/*     */     {
/* 309 */       super();
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 314 */       return element.elementSiblingIndex().intValue() < this.index;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 319 */       return String.format(":lt(%d)", new Object[] { Integer.valueOf(this.index) });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AllElements extends Evaluator
/*     */   {
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 295 */       return true;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 300 */       return "*";
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class AttributeKeyPair extends Evaluator
/*     */   {
/*     */     String key;
/*     */     String value;
/*     */ 
/*     */     public AttributeKeyPair(String key, String value)
/*     */     {
/* 277 */       Validate.notEmpty(key);
/* 278 */       Validate.notEmpty(value);
/*     */ 
/* 280 */       this.key = key.trim().toLowerCase();
/* 281 */       if ((value.startsWith("\"")) && (value.endsWith("\""))) {
/* 282 */         value = value.substring(1, value.length() - 1);
/*     */       }
/* 284 */       this.value = value.trim().toLowerCase();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeWithValueMatching extends Evaluator
/*     */   {
/*     */     String key;
/*     */     Pattern pattern;
/*     */ 
/*     */     public AttributeWithValueMatching(String key, Pattern pattern)
/*     */     {
/* 253 */       this.key = key.trim().toLowerCase();
/* 254 */       this.pattern = pattern;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 259 */       return (element.hasAttr(this.key)) && (this.pattern.matcher(element.attr(this.key)).find());
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 264 */       return String.format("[%s~=%s]", new Object[] { this.key, this.pattern.toString() });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeWithValueContaining extends Evaluator.AttributeKeyPair
/*     */   {
/*     */     public AttributeWithValueContaining(String key, String value)
/*     */     {
/* 230 */       super(value);
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 235 */       return (element.hasAttr(this.key)) && (element.attr(this.key).toLowerCase().contains(this.value));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 240 */       return String.format("[%s*=%s]", new Object[] { this.key, this.value });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeWithValueEnding extends Evaluator.AttributeKeyPair
/*     */   {
/*     */     public AttributeWithValueEnding(String key, String value)
/*     */     {
/* 210 */       super(value);
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 215 */       return (element.hasAttr(this.key)) && (element.attr(this.key).toLowerCase().endsWith(this.value));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 220 */       return String.format("[%s$=%s]", new Object[] { this.key, this.value });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeWithValueStarting extends Evaluator.AttributeKeyPair
/*     */   {
/*     */     public AttributeWithValueStarting(String key, String value)
/*     */     {
/* 190 */       super(value);
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 195 */       return (element.hasAttr(this.key)) && (element.attr(this.key).toLowerCase().startsWith(this.value));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 200 */       return String.format("[%s^=%s]", new Object[] { this.key, this.value });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeWithValueNot extends Evaluator.AttributeKeyPair
/*     */   {
/*     */     public AttributeWithValueNot(String key, String value)
/*     */     {
/* 170 */       super(value);
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 175 */       return !this.value.equalsIgnoreCase(element.attr(this.key));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 180 */       return String.format("[%s!=%s]", new Object[] { this.key, this.value });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeWithValue extends Evaluator.AttributeKeyPair
/*     */   {
/*     */     public AttributeWithValue(String key, String value)
/*     */     {
/* 150 */       super(value);
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 155 */       return (element.hasAttr(this.key)) && (this.value.equalsIgnoreCase(element.attr(this.key)));
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 160 */       return String.format("[%s=%s]", new Object[] { this.key, this.value });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class AttributeStarting extends Evaluator
/*     */   {
/*     */     private String keyPrefix;
/*     */ 
/*     */     public AttributeStarting(String keyPrefix)
/*     */     {
/* 125 */       this.keyPrefix = keyPrefix;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 130 */       List values = element.attributes().asList();
/* 131 */       for (Attribute attribute : values) {
/* 132 */         if (attribute.getKey().startsWith(this.keyPrefix))
/* 133 */           return true;
/*     */       }
/* 135 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 140 */       return String.format("[^%s]", new Object[] { this.keyPrefix });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Attribute extends Evaluator
/*     */   {
/*     */     private String key;
/*     */ 
/*     */     public Attribute(String key)
/*     */     {
/* 103 */       this.key = key;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/* 108 */       return element.hasAttr(this.key);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 113 */       return String.format("[%s]", new Object[] { this.key });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Class extends Evaluator
/*     */   {
/*     */     private String className;
/*     */ 
/*     */     public Class(String className)
/*     */     {
/*  81 */       this.className = className;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/*  86 */       return element.hasClass(this.className);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  91 */       return String.format(".%s", new Object[] { this.className });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Id extends Evaluator
/*     */   {
/*     */     private String id;
/*     */ 
/*     */     public Id(String id)
/*     */     {
/*  59 */       this.id = id;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/*  64 */       return this.id.equals(element.id());
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  69 */       return String.format("#%s", new Object[] { this.id });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Tag extends Evaluator
/*     */   {
/*     */     private String tagName;
/*     */ 
/*     */     public Tag(String tagName)
/*     */     {
/*  38 */       this.tagName = tagName;
/*     */     }
/*     */ 
/*     */     public boolean matches(Element root, Element element)
/*     */     {
/*  43 */       return element.tagName().equals(this.tagName);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  48 */       return String.format("%s", new Object[] { this.tagName });
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.Evaluator
 * JD-Core Version:    0.6.2
 */