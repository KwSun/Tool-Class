/*     */ package org.jsoup.select;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.FormElement;
/*     */ 
/*     */ public class Elements
/*     */   implements List<Element>, Cloneable
/*     */ {
/*     */   private List<Element> contents;
/*     */ 
/*     */   public Elements()
/*     */   {
/*  20 */     this.contents = new ArrayList();
/*     */   }
/*     */ 
/*     */   public Elements(int initialCapacity) {
/*  24 */     this.contents = new ArrayList(initialCapacity);
/*     */   }
/*     */ 
/*     */   public Elements(Collection<Element> elements) {
/*  28 */     this.contents = new ArrayList(elements);
/*     */   }
/*     */ 
/*     */   public Elements(List<Element> elements) {
/*  32 */     this.contents = elements;
/*     */   }
/*     */ 
/*     */   public Elements(Element[] elements) {
/*  36 */     this(Arrays.asList(elements));
/*     */   }
/*     */ 
/*     */   public Elements clone()
/*     */   {
/*     */     Elements clone;
/*     */     try
/*     */     {
/*  47 */       clone = (Elements)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/*  49 */       throw new RuntimeException(e);
/*     */     }
/*  51 */     List elements = new ArrayList();
/*  52 */     clone.contents = elements;
/*     */ 
/*  54 */     for (Element e : this.contents) {
/*  55 */       elements.add(e.clone());
/*     */     }
/*     */ 
/*  58 */     return clone;
/*     */   }
/*     */ 
/*     */   public String attr(String attributeKey)
/*     */   {
/*  70 */     for (Element element : this.contents) {
/*  71 */       if (element.hasAttr(attributeKey))
/*  72 */         return element.attr(attributeKey);
/*     */     }
/*  74 */     return "";
/*     */   }
/*     */ 
/*     */   public boolean hasAttr(String attributeKey)
/*     */   {
/*  83 */     for (Element element : this.contents) {
/*  84 */       if (element.hasAttr(attributeKey))
/*  85 */         return true;
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   public Elements attr(String attributeKey, String attributeValue)
/*     */   {
/*  97 */     for (Element element : this.contents) {
/*  98 */       element.attr(attributeKey, attributeValue);
/*     */     }
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements removeAttr(String attributeKey)
/*     */   {
/* 109 */     for (Element element : this.contents) {
/* 110 */       element.removeAttr(attributeKey);
/*     */     }
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements addClass(String className)
/*     */   {
/* 121 */     for (Element element : this.contents) {
/* 122 */       element.addClass(className);
/*     */     }
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements removeClass(String className)
/*     */   {
/* 133 */     for (Element element : this.contents) {
/* 134 */       element.removeClass(className);
/*     */     }
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements toggleClass(String className)
/*     */   {
/* 145 */     for (Element element : this.contents) {
/* 146 */       element.toggleClass(className);
/*     */     }
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean hasClass(String className)
/*     */   {
/* 157 */     for (Element element : this.contents) {
/* 158 */       if (element.hasClass(className))
/* 159 */         return true;
/*     */     }
/* 161 */     return false;
/*     */   }
/*     */ 
/*     */   public String val()
/*     */   {
/* 170 */     if (size() > 0) {
/* 171 */       return first().val();
/*     */     }
/* 173 */     return "";
/*     */   }
/*     */ 
/*     */   public Elements val(String value)
/*     */   {
/* 182 */     for (Element element : this.contents)
/* 183 */       element.val(value);
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */   public String text()
/*     */   {
/* 196 */     StringBuilder sb = new StringBuilder();
/* 197 */     for (Element element : this.contents) {
/* 198 */       if (sb.length() != 0)
/* 199 */         sb.append(" ");
/* 200 */       sb.append(element.text());
/*     */     }
/* 202 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public boolean hasText() {
/* 206 */     for (Element element : this.contents) {
/* 207 */       if (element.hasText())
/* 208 */         return true;
/*     */     }
/* 210 */     return false;
/*     */   }
/*     */ 
/*     */   public String html()
/*     */   {
/* 220 */     StringBuilder sb = new StringBuilder();
/* 221 */     for (Element element : this.contents) {
/* 222 */       if (sb.length() != 0)
/* 223 */         sb.append("\n");
/* 224 */       sb.append(element.html());
/*     */     }
/* 226 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String outerHtml()
/*     */   {
/* 236 */     StringBuilder sb = new StringBuilder();
/* 237 */     for (Element element : this.contents) {
/* 238 */       if (sb.length() != 0)
/* 239 */         sb.append("\n");
/* 240 */       sb.append(element.outerHtml());
/*     */     }
/* 242 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 252 */     return outerHtml();
/*     */   }
/*     */ 
/*     */   public Elements tagName(String tagName)
/*     */   {
/* 263 */     for (Element element : this.contents) {
/* 264 */       element.tagName(tagName);
/*     */     }
/* 266 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements html(String html)
/*     */   {
/* 276 */     for (Element element : this.contents) {
/* 277 */       element.html(html);
/*     */     }
/* 279 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements prepend(String html)
/*     */   {
/* 289 */     for (Element element : this.contents) {
/* 290 */       element.prepend(html);
/*     */     }
/* 292 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements append(String html)
/*     */   {
/* 302 */     for (Element element : this.contents) {
/* 303 */       element.append(html);
/*     */     }
/* 305 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements before(String html)
/*     */   {
/* 315 */     for (Element element : this.contents) {
/* 316 */       element.before(html);
/*     */     }
/* 318 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements after(String html)
/*     */   {
/* 328 */     for (Element element : this.contents) {
/* 329 */       element.after(html);
/*     */     }
/* 331 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements wrap(String html)
/*     */   {
/* 344 */     Validate.notEmpty(html);
/* 345 */     for (Element element : this.contents) {
/* 346 */       element.wrap(html);
/*     */     }
/* 348 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements unwrap()
/*     */   {
/* 365 */     for (Element element : this.contents) {
/* 366 */       element.unwrap();
/*     */     }
/* 368 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements empty()
/*     */   {
/* 383 */     for (Element element : this.contents) {
/* 384 */       element.empty();
/*     */     }
/* 386 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements remove()
/*     */   {
/* 402 */     for (Element element : this.contents) {
/* 403 */       element.remove();
/*     */     }
/* 405 */     return this;
/*     */   }
/*     */ 
/*     */   public Elements select(String query)
/*     */   {
/* 416 */     return Selector.select(query, this);
/*     */   }
/*     */ 
/*     */   public Elements not(String query)
/*     */   {
/* 430 */     Elements out = Selector.select(query, this);
/* 431 */     return Selector.filterOut(this, out);
/*     */   }
/*     */ 
/*     */   public Elements eq(int index)
/*     */   {
/* 442 */     return this.contents.size() > index ? new Elements(new Element[] { get(index) }) : new Elements();
/*     */   }
/*     */ 
/*     */   public boolean is(String query)
/*     */   {
/* 451 */     Elements children = select(query);
/* 452 */     return !children.isEmpty();
/*     */   }
/*     */ 
/*     */   public Elements parents()
/*     */   {
/* 460 */     HashSet combo = new LinkedHashSet();
/* 461 */     for (Element e : this.contents) {
/* 462 */       combo.addAll(e.parents());
/*     */     }
/* 464 */     return new Elements(combo);
/*     */   }
/*     */ 
/*     */   public Element first()
/*     */   {
/* 473 */     return this.contents.isEmpty() ? null : (Element)this.contents.get(0);
/*     */   }
/*     */ 
/*     */   public Element last()
/*     */   {
/* 481 */     return this.contents.isEmpty() ? null : (Element)this.contents.get(this.contents.size() - 1);
/*     */   }
/*     */ 
/*     */   public Elements traverse(NodeVisitor nodeVisitor)
/*     */   {
/* 490 */     Validate.notNull(nodeVisitor);
/* 491 */     NodeTraversor traversor = new NodeTraversor(nodeVisitor);
/* 492 */     for (Element el : this.contents) {
/* 493 */       traversor.traverse(el);
/*     */     }
/* 495 */     return this;
/*     */   }
/*     */ 
/*     */   public List<FormElement> forms()
/*     */   {
/* 504 */     ArrayList forms = new ArrayList();
/* 505 */     for (Element el : this.contents)
/* 506 */       if ((el instanceof FormElement))
/* 507 */         forms.add((FormElement)el);
/* 508 */     return forms;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 512 */     return this.contents.size();
/*     */   }
/* 514 */   public boolean isEmpty() { return this.contents.isEmpty(); } 
/*     */   public boolean contains(Object o) {
/* 516 */     return this.contents.contains(o);
/*     */   }
/* 518 */   public Iterator<Element> iterator() { return this.contents.iterator(); } 
/*     */   public Object[] toArray() {
/* 520 */     return this.contents.toArray();
/*     */   }
/* 522 */   public <T> T[] toArray(T[] a) { return this.contents.toArray(a); } 
/*     */   public boolean add(Element element) {
/* 524 */     return this.contents.add(element);
/*     */   }
/* 526 */   public boolean remove(Object o) { return this.contents.remove(o); } 
/*     */   public boolean containsAll(Collection<?> c) {
/* 528 */     return this.contents.containsAll(c);
/*     */   }
/* 530 */   public boolean addAll(Collection<? extends Element> c) { return this.contents.addAll(c); } 
/*     */   public boolean addAll(int index, Collection<? extends Element> c) {
/* 532 */     return this.contents.addAll(index, c);
/*     */   }
/* 534 */   public boolean removeAll(Collection<?> c) { return this.contents.removeAll(c); } 
/*     */   public boolean retainAll(Collection<?> c) {
/* 536 */     return this.contents.retainAll(c);
/*     */   }
/* 538 */   public void clear() { this.contents.clear(); } 
/*     */   public boolean equals(Object o) {
/* 540 */     return this.contents.equals(o);
/*     */   }
/* 542 */   public int hashCode() { return this.contents.hashCode(); } 
/*     */   public Element get(int index) {
/* 544 */     return (Element)this.contents.get(index);
/*     */   }
/* 546 */   public Element set(int index, Element element) { return (Element)this.contents.set(index, element); } 
/*     */   public void add(int index, Element element) {
/* 548 */     this.contents.add(index, element);
/*     */   }
/* 550 */   public Element remove(int index) { return (Element)this.contents.remove(index); } 
/*     */   public int indexOf(Object o) {
/* 552 */     return this.contents.indexOf(o);
/*     */   }
/* 554 */   public int lastIndexOf(Object o) { return this.contents.lastIndexOf(o); } 
/*     */   public ListIterator<Element> listIterator() {
/* 556 */     return this.contents.listIterator();
/*     */   }
/* 558 */   public ListIterator<Element> listIterator(int index) { return this.contents.listIterator(index); } 
/*     */   public List<Element> subList(int fromIndex, int toIndex) {
/* 560 */     return this.contents.subList(fromIndex, toIndex);
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.Elements
 * JD-Core Version:    0.6.2
 */