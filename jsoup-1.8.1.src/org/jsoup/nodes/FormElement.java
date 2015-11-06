/*    */ package org.jsoup.nodes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jsoup.Connection;
/*    */ import org.jsoup.Connection.KeyVal;
/*    */ import org.jsoup.Connection.Method;
/*    */ import org.jsoup.Jsoup;
/*    */ import org.jsoup.helper.HttpConnection.KeyVal;
/*    */ import org.jsoup.helper.Validate;
/*    */ import org.jsoup.parser.Tag;
/*    */ import org.jsoup.select.Elements;
/*    */ 
/*    */ public class FormElement extends Element
/*    */ {
/* 18 */   private final Elements elements = new Elements();
/*    */ 
/*    */   public FormElement(Tag tag, String baseUri, Attributes attributes)
/*    */   {
/* 28 */     super(tag, baseUri, attributes);
/*    */   }
/*    */ 
/*    */   public Elements elements()
/*    */   {
/* 36 */     return this.elements;
/*    */   }
/*    */ 
/*    */   public FormElement addElement(Element element)
/*    */   {
/* 45 */     this.elements.add(element);
/* 46 */     return this;
/*    */   }
/*    */ 
/*    */   public Connection submit()
/*    */   {
/* 57 */     String action = hasAttr("action") ? absUrl("action") : baseUri();
/* 58 */     Validate.notEmpty(action, "Could not determine a form action URL for submit. Ensure you set a base URI when parsing.");
/* 59 */     Connection.Method method = attr("method").toUpperCase().equals("POST") ? Connection.Method.POST : Connection.Method.GET;
/*    */ 
/* 62 */     Connection con = Jsoup.connect(action).data(formData()).method(method);
/*    */ 
/* 66 */     return con;
/*    */   }
/*    */ 
/*    */   public List<Connection.KeyVal> formData()
/*    */   {
/* 75 */     ArrayList data = new ArrayList();
/*    */ 
/* 78 */     for (Element el : this.elements)
/* 79 */       if (el.tag().isFormSubmittable()) {
/* 80 */         String name = el.attr("name");
/* 81 */         if (name.length() != 0)
/*    */         {
/* 83 */           if ("select".equals(el.tagName())) {
/* 84 */             Elements options = el.select("option[selected]");
/* 85 */             for (Element option : options)
/* 86 */               data.add(HttpConnection.KeyVal.create(name, option.val()));
/*    */           }
/*    */           else {
/* 89 */             data.add(HttpConnection.KeyVal.create(name, el.val()));
/*    */           }
/*    */         }
/*    */       }
/* 92 */     return data;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 97 */     return super.equals(o);
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.FormElement
 * JD-Core Version:    0.6.2
 */