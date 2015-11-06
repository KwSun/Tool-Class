/*     */ package org.jsoup.helper;
/*     */ 
/*     */ public final class Validate
/*     */ {
/*     */   public static void notNull(Object obj)
/*     */   {
/*  15 */     if (obj == null)
/*  16 */       throw new IllegalArgumentException("Object must not be null");
/*     */   }
/*     */ 
/*     */   public static void notNull(Object obj, String msg)
/*     */   {
/*  25 */     if (obj == null)
/*  26 */       throw new IllegalArgumentException(msg);
/*     */   }
/*     */ 
/*     */   public static void isTrue(boolean val)
/*     */   {
/*  34 */     if (!val)
/*  35 */       throw new IllegalArgumentException("Must be true");
/*     */   }
/*     */ 
/*     */   public static void isTrue(boolean val, String msg)
/*     */   {
/*  44 */     if (!val)
/*  45 */       throw new IllegalArgumentException(msg);
/*     */   }
/*     */ 
/*     */   public static void isFalse(boolean val)
/*     */   {
/*  53 */     if (val)
/*  54 */       throw new IllegalArgumentException("Must be false");
/*     */   }
/*     */ 
/*     */   public static void isFalse(boolean val, String msg)
/*     */   {
/*  63 */     if (val)
/*  64 */       throw new IllegalArgumentException(msg);
/*     */   }
/*     */ 
/*     */   public static void noNullElements(Object[] objects)
/*     */   {
/*  72 */     noNullElements(objects, "Array must not contain any null objects");
/*     */   }
/*     */ 
/*     */   public static void noNullElements(Object[] objects, String msg)
/*     */   {
/*  81 */     for (Object obj : objects)
/*  82 */       if (obj == null)
/*  83 */         throw new IllegalArgumentException(msg);
/*     */   }
/*     */ 
/*     */   public static void notEmpty(String string)
/*     */   {
/*  91 */     if ((string == null) || (string.length() == 0))
/*  92 */       throw new IllegalArgumentException("String must not be empty");
/*     */   }
/*     */ 
/*     */   public static void notEmpty(String string, String msg)
/*     */   {
/* 101 */     if ((string == null) || (string.length() == 0))
/* 102 */       throw new IllegalArgumentException(msg);
/*     */   }
/*     */ 
/*     */   public static void fail(String msg)
/*     */   {
/* 110 */     throw new IllegalArgumentException(msg);
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.helper.Validate
 * JD-Core Version:    0.6.2
 */