/*    */ package org.jsoup.helper;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ public class DescendableLinkedList<E> extends LinkedList<E>
/*    */ {
/*    */   public void push(E e)
/*    */   {
/* 24 */     addFirst(e);
/*    */   }
/*    */ 
/*    */   public E peekLast()
/*    */   {
/* 32 */     return size() == 0 ? null : getLast();
/*    */   }
/*    */ 
/*    */   public E pollLast()
/*    */   {
/* 40 */     return size() == 0 ? null : removeLast();
/*    */   }
/*    */ 
/*    */   public Iterator<E> descendingIterator()
/*    */   {
/* 48 */     return new DescendingIterator(size(), null);
/*    */   }
/*    */ 
/*    */   private class DescendingIterator<E> implements Iterator<E>
/*    */   {
/*    */     private final ListIterator<E> iter;
/*    */ 
/*    */     private DescendingIterator(int index) {
/* 56 */       this.iter = DescendableLinkedList.this.listIterator(index);
/*    */     }
/*    */ 
/*    */     public boolean hasNext()
/*    */     {
/* 64 */       return this.iter.hasPrevious();
/*    */     }
/*    */ 
/*    */     public E next()
/*    */     {
/* 72 */       return this.iter.previous();
/*    */     }
/*    */ 
/*    */     public void remove()
/*    */     {
/* 79 */       this.iter.remove();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.helper.DescendableLinkedList
 * JD-Core Version:    0.6.2
 */