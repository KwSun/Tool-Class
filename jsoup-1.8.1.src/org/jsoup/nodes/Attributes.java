/*     */ package org.jsoup.nodes;
/*     */ 
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.jsoup.helper.Validate;
/*     */ 
/*     */ public class Attributes
/*     */   implements Iterable<Attribute>, Cloneable
/*     */ {
/*     */   protected static final String dataPrefix = "data-";
/*     */   private LinkedHashMap<String, Attribute> attributes;
/*     */ 
/*     */   public Attributes()
/*     */   {
/*  20 */     this.attributes = null;
/*     */   }
/*     */ 
/*     */   public String get(String key)
/*     */   {
/*  31 */     Validate.notEmpty(key);
/*     */ 
/*  33 */     if (this.attributes == null) {
/*  34 */       return "";
/*     */     }
/*  36 */     Attribute attr = (Attribute)this.attributes.get(key.toLowerCase());
/*  37 */     return attr != null ? attr.getValue() : "";
/*     */   }
/*     */ 
/*     */   public void put(String key, String value)
/*     */   {
/*  46 */     Attribute attr = new Attribute(key, value);
/*  47 */     put(attr);
/*     */   }
/*     */ 
/*     */   public void put(Attribute attribute)
/*     */   {
/*  55 */     Validate.notNull(attribute);
/*  56 */     if (this.attributes == null)
/*  57 */       this.attributes = new LinkedHashMap(2);
/*  58 */     this.attributes.put(attribute.getKey(), attribute);
/*     */   }
/*     */ 
/*     */   public void remove(String key)
/*     */   {
/*  66 */     Validate.notEmpty(key);
/*  67 */     if (this.attributes == null)
/*  68 */       return;
/*  69 */     this.attributes.remove(key.toLowerCase());
/*     */   }
/*     */ 
/*     */   public boolean hasKey(String key)
/*     */   {
/*  78 */     return (this.attributes != null) && (this.attributes.containsKey(key.toLowerCase()));
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  86 */     if (this.attributes == null)
/*  87 */       return 0;
/*  88 */     return this.attributes.size();
/*     */   }
/*     */ 
/*     */   public void addAll(Attributes incoming)
/*     */   {
/*  96 */     if (incoming.size() == 0)
/*  97 */       return;
/*  98 */     if (this.attributes == null)
/*  99 */       this.attributes = new LinkedHashMap(incoming.size());
/* 100 */     this.attributes.putAll(incoming.attributes);
/*     */   }
/*     */ 
/*     */   public Iterator<Attribute> iterator() {
/* 104 */     return asList().iterator();
/*     */   }
/*     */ 
/*     */   public List<Attribute> asList()
/*     */   {
/* 113 */     if (this.attributes == null) {
/* 114 */       return Collections.emptyList();
/*     */     }
/* 116 */     List list = new ArrayList(this.attributes.size());
/* 117 */     for (Map.Entry entry : this.attributes.entrySet()) {
/* 118 */       list.add(entry.getValue());
/*     */     }
/* 120 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ 
/*     */   public Map<String, String> dataset()
/*     */   {
/* 129 */     return new Dataset(null);
/*     */   }
/*     */ 
/*     */   public String html()
/*     */   {
/* 137 */     StringBuilder accum = new StringBuilder();
/* 138 */     html(accum, new Document("").outputSettings());
/* 139 */     return accum.toString();
/*     */   }
/*     */ 
/*     */   void html(StringBuilder accum, Document.OutputSettings out) {
/* 143 */     if (this.attributes == null) {
/* 144 */       return;
/*     */     }
/* 146 */     for (Map.Entry entry : this.attributes.entrySet()) {
/* 147 */       Attribute attribute = (Attribute)entry.getValue();
/* 148 */       accum.append(" ");
/* 149 */       attribute.html(accum, out);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 154 */     return html();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 159 */     if (this == o) return true;
/* 160 */     if (!(o instanceof Attributes)) return false;
/*     */ 
/* 162 */     Attributes that = (Attributes)o;
/*     */ 
/* 164 */     if (this.attributes != null ? !this.attributes.equals(that.attributes) : that.attributes != null) return false;
/*     */ 
/* 166 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 171 */     return this.attributes != null ? this.attributes.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   public Attributes clone()
/*     */   {
/* 176 */     if (this.attributes == null)
/* 177 */       return new Attributes();
/*     */     Attributes clone;
/*     */     try
/*     */     {
/* 181 */       clone = (Attributes)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/* 183 */       throw new RuntimeException(e);
/*     */     }
/* 185 */     clone.attributes = new LinkedHashMap(this.attributes.size());
/* 186 */     for (Attribute attribute : this)
/* 187 */       clone.attributes.put(attribute.getKey(), attribute.clone());
/* 188 */     return clone;
/*     */   }
/*     */ 
/*     */   private static String dataKey(String key)
/*     */   {
/* 247 */     return "data-" + key;
/*     */   }
/*     */ 
/*     */   private class Dataset extends AbstractMap<String, String>
/*     */   {
/*     */     private Dataset()
/*     */     {
/* 194 */       if (Attributes.this.attributes == null)
/* 195 */         Attributes.this.attributes = new LinkedHashMap(2);
/*     */     }
/*     */ 
/*     */     public Set<Map.Entry<String, String>> entrySet() {
/* 199 */       return new EntrySet(null);
/*     */     }
/*     */ 
/*     */     public String put(String key, String value)
/*     */     {
/* 204 */       String dataKey = Attributes.dataKey(key);
/* 205 */       String oldValue = Attributes.this.hasKey(dataKey) ? ((Attribute)Attributes.this.attributes.get(dataKey)).getValue() : null;
/* 206 */       Attribute attr = new Attribute(dataKey, value);
/* 207 */       Attributes.this.attributes.put(dataKey, attr);
/* 208 */       return oldValue;
/*     */     }
/*     */ 
/*     */     private class DatasetIterator
/*     */       implements Iterator<Map.Entry<String, String>>
/*     */     {
/* 226 */       private Iterator<Attribute> attrIter = Attributes.this.attributes.values().iterator();
/*     */       private Attribute attr;
/*     */ 
/*     */       private DatasetIterator()
/*     */       {
/*     */       }
/*     */ 
/*     */       public boolean hasNext()
/*     */       {
/* 229 */         while (this.attrIter.hasNext()) {
/* 230 */           this.attr = ((Attribute)this.attrIter.next());
/* 231 */           if (this.attr.isDataAttribute()) return true;
/*     */         }
/* 233 */         return false;
/*     */       }
/*     */ 
/*     */       public Map.Entry<String, String> next() {
/* 237 */         return new Attribute(this.attr.getKey().substring("data-".length()), this.attr.getValue());
/*     */       }
/*     */ 
/*     */       public void remove() {
/* 241 */         Attributes.this.attributes.remove(this.attr.getKey());
/*     */       }
/*     */     }
/*     */ 
/*     */     private class EntrySet extends AbstractSet<Map.Entry<String, String>>
/*     */     {
/*     */       private EntrySet()
/*     */       {
/*     */       }
/*     */ 
/*     */       public Iterator<Map.Entry<String, String>> iterator()
/*     */       {
/* 213 */         return new Attributes.Dataset.DatasetIterator(Attributes.Dataset.this, null);
/*     */       }
/*     */ 
/*     */       public int size() {
/* 217 */         int count = 0;
/* 218 */         Iterator iter = new Attributes.Dataset.DatasetIterator(Attributes.Dataset.this, null);
/* 219 */         while (iter.hasNext())
/* 220 */           count++;
/* 221 */         return count;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.nodes.Attributes
 * JD-Core Version:    0.6.2
 */