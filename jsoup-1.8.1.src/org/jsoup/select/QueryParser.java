/*     */ package org.jsoup.select;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.jsoup.helper.StringUtil;
/*     */ import org.jsoup.helper.Validate;
/*     */ import org.jsoup.parser.TokenQueue;
/*     */ 
/*     */ class QueryParser
/*     */ {
/*  16 */   private static final String[] combinators = { ",", ">", "+", "~", " " };
/*  17 */   private static final String[] AttributeEvals = { "=", "!=", "^=", "$=", "*=", "~=" };
/*     */   private TokenQueue tq;
/*     */   private String query;
/*  21 */   private List<Evaluator> evals = new ArrayList();
/*     */ 
/* 275 */   private static final Pattern NTH_AB = Pattern.compile("((\\+|-)?(\\d+)?)n(\\s*(\\+|-)?\\s*\\d+)?", 2);
/* 276 */   private static final Pattern NTH_B = Pattern.compile("(\\+|-)?(\\d+)");
/*     */ 
/*     */   private QueryParser(String query)
/*     */   {
/*  28 */     this.query = query;
/*  29 */     this.tq = new TokenQueue(query);
/*     */   }
/*     */ 
/*     */   public static Evaluator parse(String query)
/*     */   {
/*  38 */     QueryParser p = new QueryParser(query);
/*  39 */     return p.parse();
/*     */   }
/*     */ 
/*     */   Evaluator parse()
/*     */   {
/*  47 */     this.tq.consumeWhitespace();
/*     */ 
/*  49 */     if (this.tq.matchesAny(combinators)) {
/*  50 */       this.evals.add(new StructuralEvaluator.Root());
/*  51 */       combinator(this.tq.consume());
/*     */     } else {
/*  53 */       findElements();
/*     */     }
/*     */ 
/*  56 */     while (!this.tq.isEmpty())
/*     */     {
/*  58 */       boolean seenWhite = this.tq.consumeWhitespace();
/*     */ 
/*  60 */       if (this.tq.matchesAny(combinators))
/*  61 */         combinator(this.tq.consume());
/*  62 */       else if (seenWhite)
/*  63 */         combinator(' ');
/*     */       else {
/*  65 */         findElements();
/*     */       }
/*     */     }
/*     */ 
/*  69 */     if (this.evals.size() == 1) {
/*  70 */       return (Evaluator)this.evals.get(0);
/*     */     }
/*  72 */     return new CombiningEvaluator.And(this.evals);
/*     */   }
/*     */ 
/*     */   private void combinator(char combinator) {
/*  76 */     this.tq.consumeWhitespace();
/*  77 */     String subQuery = consumeSubQuery();
/*     */ 
/*  81 */     Evaluator newEval = parse(subQuery);
/*  82 */     boolean replaceRightMost = false;
/*     */     Evaluator currentEval;
/*     */     Evaluator rootEval;
/*  84 */     if (this.evals.size() == 1)
/*     */     {
/*     */       Evaluator currentEval;
/*  85 */       Evaluator rootEval = currentEval = (Evaluator)this.evals.get(0);
/*     */ 
/*  87 */       if (((rootEval instanceof CombiningEvaluator.Or)) && (combinator != ',')) {
/*  88 */         currentEval = ((CombiningEvaluator.Or)currentEval).rightMostEvaluator();
/*  89 */         replaceRightMost = true;
/*     */       }
/*     */     }
/*     */     else {
/*  93 */       rootEval = currentEval = new CombiningEvaluator.And(this.evals);
/*     */     }
/*  95 */     this.evals.clear();
/*     */ 
/*  98 */     if (combinator == '>') {
/*  99 */       currentEval = new CombiningEvaluator.And(new Evaluator[] { newEval, new StructuralEvaluator.ImmediateParent(currentEval) });
/* 100 */     } else if (combinator == ' ') {
/* 101 */       currentEval = new CombiningEvaluator.And(new Evaluator[] { newEval, new StructuralEvaluator.Parent(currentEval) });
/* 102 */     } else if (combinator == '+') {
/* 103 */       currentEval = new CombiningEvaluator.And(new Evaluator[] { newEval, new StructuralEvaluator.ImmediatePreviousSibling(currentEval) });
/* 104 */     } else if (combinator == '~') {
/* 105 */       currentEval = new CombiningEvaluator.And(new Evaluator[] { newEval, new StructuralEvaluator.PreviousSibling(currentEval) });
/* 106 */     } else if (combinator == ',')
/*     */     {
/*     */       CombiningEvaluator.Or or;
/* 108 */       if ((currentEval instanceof CombiningEvaluator.Or)) {
/* 109 */         CombiningEvaluator.Or or = (CombiningEvaluator.Or)currentEval;
/* 110 */         or.add(newEval);
/*     */       } else {
/* 112 */         or = new CombiningEvaluator.Or();
/* 113 */         or.add(currentEval);
/* 114 */         or.add(newEval);
/*     */       }
/* 116 */       currentEval = or;
/*     */     }
/*     */     else {
/* 119 */       throw new Selector.SelectorParseException("Unknown combinator: " + combinator, new Object[0]);
/*     */     }
/* 121 */     if (replaceRightMost)
/* 122 */       ((CombiningEvaluator.Or)rootEval).replaceRightMostEvaluator(currentEval);
/* 123 */     else rootEval = currentEval;
/* 124 */     this.evals.add(rootEval);
/*     */   }
/*     */ 
/*     */   private String consumeSubQuery() {
/* 128 */     StringBuilder sq = new StringBuilder();
/* 129 */     while (!this.tq.isEmpty())
/* 130 */       if (this.tq.matches("(")) {
/* 131 */         sq.append("(").append(this.tq.chompBalanced('(', ')')).append(")");
/* 132 */       } else if (this.tq.matches("[")) {
/* 133 */         sq.append("[").append(this.tq.chompBalanced('[', ']')).append("]"); } else {
/* 134 */         if (this.tq.matchesAny(combinators)) {
/*     */           break;
/*     */         }
/* 137 */         sq.append(this.tq.consume());
/*     */       }
/* 139 */     return sq.toString();
/*     */   }
/*     */ 
/*     */   private void findElements() {
/* 143 */     if (this.tq.matchChomp("#"))
/* 144 */       byId();
/* 145 */     else if (this.tq.matchChomp("."))
/* 146 */       byClass();
/* 147 */     else if (this.tq.matchesWord())
/* 148 */       byTag();
/* 149 */     else if (this.tq.matches("["))
/* 150 */       byAttribute();
/* 151 */     else if (this.tq.matchChomp("*"))
/* 152 */       allElements();
/* 153 */     else if (this.tq.matchChomp(":lt("))
/* 154 */       indexLessThan();
/* 155 */     else if (this.tq.matchChomp(":gt("))
/* 156 */       indexGreaterThan();
/* 157 */     else if (this.tq.matchChomp(":eq("))
/* 158 */       indexEquals();
/* 159 */     else if (this.tq.matches(":has("))
/* 160 */       has();
/* 161 */     else if (this.tq.matches(":contains("))
/* 162 */       contains(false);
/* 163 */     else if (this.tq.matches(":containsOwn("))
/* 164 */       contains(true);
/* 165 */     else if (this.tq.matches(":matches("))
/* 166 */       matches(false);
/* 167 */     else if (this.tq.matches(":matchesOwn("))
/* 168 */       matches(true);
/* 169 */     else if (this.tq.matches(":not("))
/* 170 */       not();
/* 171 */     else if (this.tq.matchChomp(":nth-child("))
/* 172 */       cssNthChild(false, false);
/* 173 */     else if (this.tq.matchChomp(":nth-last-child("))
/* 174 */       cssNthChild(true, false);
/* 175 */     else if (this.tq.matchChomp(":nth-of-type("))
/* 176 */       cssNthChild(false, true);
/* 177 */     else if (this.tq.matchChomp(":nth-last-of-type("))
/* 178 */       cssNthChild(true, true);
/* 179 */     else if (this.tq.matchChomp(":first-child"))
/* 180 */       this.evals.add(new Evaluator.IsFirstChild());
/* 181 */     else if (this.tq.matchChomp(":last-child"))
/* 182 */       this.evals.add(new Evaluator.IsLastChild());
/* 183 */     else if (this.tq.matchChomp(":first-of-type"))
/* 184 */       this.evals.add(new Evaluator.IsFirstOfType());
/* 185 */     else if (this.tq.matchChomp(":last-of-type"))
/* 186 */       this.evals.add(new Evaluator.IsLastOfType());
/* 187 */     else if (this.tq.matchChomp(":only-child"))
/* 188 */       this.evals.add(new Evaluator.IsOnlyChild());
/* 189 */     else if (this.tq.matchChomp(":only-of-type"))
/* 190 */       this.evals.add(new Evaluator.IsOnlyOfType());
/* 191 */     else if (this.tq.matchChomp(":empty"))
/* 192 */       this.evals.add(new Evaluator.IsEmpty());
/* 193 */     else if (this.tq.matchChomp(":root"))
/* 194 */       this.evals.add(new Evaluator.IsRoot());
/*     */     else
/* 196 */       throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", new Object[] { this.query, this.tq.remainder() });
/*     */   }
/*     */ 
/*     */   private void byId()
/*     */   {
/* 201 */     String id = this.tq.consumeCssIdentifier();
/* 202 */     Validate.notEmpty(id);
/* 203 */     this.evals.add(new Evaluator.Id(id));
/*     */   }
/*     */ 
/*     */   private void byClass() {
/* 207 */     String className = this.tq.consumeCssIdentifier();
/* 208 */     Validate.notEmpty(className);
/* 209 */     this.evals.add(new Evaluator.Class(className.trim().toLowerCase()));
/*     */   }
/*     */ 
/*     */   private void byTag() {
/* 213 */     String tagName = this.tq.consumeElementSelector();
/* 214 */     Validate.notEmpty(tagName);
/*     */ 
/* 217 */     if (tagName.contains("|")) {
/* 218 */       tagName = tagName.replace("|", ":");
/*     */     }
/* 220 */     this.evals.add(new Evaluator.Tag(tagName.trim().toLowerCase()));
/*     */   }
/*     */ 
/*     */   private void byAttribute() {
/* 224 */     TokenQueue cq = new TokenQueue(this.tq.chompBalanced('[', ']'));
/* 225 */     String key = cq.consumeToAny(AttributeEvals);
/* 226 */     Validate.notEmpty(key);
/* 227 */     cq.consumeWhitespace();
/*     */ 
/* 229 */     if (cq.isEmpty()) {
/* 230 */       if (key.startsWith("^"))
/* 231 */         this.evals.add(new Evaluator.AttributeStarting(key.substring(1)));
/*     */       else
/* 233 */         this.evals.add(new Evaluator.Attribute(key));
/*     */     }
/* 235 */     else if (cq.matchChomp("=")) {
/* 236 */       this.evals.add(new Evaluator.AttributeWithValue(key, cq.remainder()));
/*     */     }
/* 238 */     else if (cq.matchChomp("!=")) {
/* 239 */       this.evals.add(new Evaluator.AttributeWithValueNot(key, cq.remainder()));
/*     */     }
/* 241 */     else if (cq.matchChomp("^=")) {
/* 242 */       this.evals.add(new Evaluator.AttributeWithValueStarting(key, cq.remainder()));
/*     */     }
/* 244 */     else if (cq.matchChomp("$=")) {
/* 245 */       this.evals.add(new Evaluator.AttributeWithValueEnding(key, cq.remainder()));
/*     */     }
/* 247 */     else if (cq.matchChomp("*=")) {
/* 248 */       this.evals.add(new Evaluator.AttributeWithValueContaining(key, cq.remainder()));
/*     */     }
/* 250 */     else if (cq.matchChomp("~="))
/* 251 */       this.evals.add(new Evaluator.AttributeWithValueMatching(key, Pattern.compile(cq.remainder())));
/*     */     else
/* 253 */       throw new Selector.SelectorParseException("Could not parse attribute query '%s': unexpected token at '%s'", new Object[] { this.query, cq.remainder() });
/*     */   }
/*     */ 
/*     */   private void allElements()
/*     */   {
/* 258 */     this.evals.add(new Evaluator.AllElements());
/*     */   }
/*     */ 
/*     */   private void indexLessThan()
/*     */   {
/* 263 */     this.evals.add(new Evaluator.IndexLessThan(consumeIndex()));
/*     */   }
/*     */ 
/*     */   private void indexGreaterThan() {
/* 267 */     this.evals.add(new Evaluator.IndexGreaterThan(consumeIndex()));
/*     */   }
/*     */ 
/*     */   private void indexEquals() {
/* 271 */     this.evals.add(new Evaluator.IndexEquals(consumeIndex()));
/*     */   }
/*     */ 
/*     */   private void cssNthChild(boolean backwards, boolean ofType)
/*     */   {
/* 279 */     String argS = this.tq.chompTo(")").trim().toLowerCase();
/* 280 */     Matcher mAB = NTH_AB.matcher(argS);
/* 281 */     Matcher mB = NTH_B.matcher(argS);
/*     */     int b;
/* 283 */     if ("odd".equals(argS)) {
/* 284 */       int a = 2;
/* 285 */       b = 1;
/*     */     }
/*     */     else
/*     */     {
/*     */       int b;
/* 286 */       if ("even".equals(argS)) {
/* 287 */         int a = 2;
/* 288 */         b = 0;
/*     */       }
/*     */       else
/*     */       {
/*     */         int b;
/* 289 */         if (mAB.matches()) {
/* 290 */           int a = mAB.group(3) != null ? Integer.parseInt(mAB.group(1).replaceFirst("^\\+", "")) : 1;
/* 291 */           b = mAB.group(4) != null ? Integer.parseInt(mAB.group(4).replaceFirst("^\\+", "")) : 0;
/*     */         }
/*     */         else
/*     */         {
/*     */           int b;
/* 292 */           if (mB.matches()) {
/* 293 */             int a = 0;
/* 294 */             b = Integer.parseInt(mB.group().replaceFirst("^\\+", ""));
/*     */           } else {
/* 296 */             throw new Selector.SelectorParseException("Could not parse nth-index '%s': unexpected format", new Object[] { argS });
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     int b;
/*     */     int a;
/* 298 */     if (ofType) {
/* 299 */       if (backwards)
/* 300 */         this.evals.add(new Evaluator.IsNthLastOfType(a, b));
/*     */       else
/* 302 */         this.evals.add(new Evaluator.IsNthOfType(a, b));
/*     */     }
/* 304 */     else if (backwards)
/* 305 */       this.evals.add(new Evaluator.IsNthLastChild(a, b));
/*     */     else
/* 307 */       this.evals.add(new Evaluator.IsNthChild(a, b));
/*     */   }
/*     */ 
/*     */   private int consumeIndex()
/*     */   {
/* 312 */     String indexS = this.tq.chompTo(")").trim();
/* 313 */     Validate.isTrue(StringUtil.isNumeric(indexS), "Index must be numeric");
/* 314 */     return Integer.parseInt(indexS);
/*     */   }
/*     */ 
/*     */   private void has()
/*     */   {
/* 319 */     this.tq.consume(":has");
/* 320 */     String subQuery = this.tq.chompBalanced('(', ')');
/* 321 */     Validate.notEmpty(subQuery, ":has(el) subselect must not be empty");
/* 322 */     this.evals.add(new StructuralEvaluator.Has(parse(subQuery)));
/*     */   }
/*     */ 
/*     */   private void contains(boolean own)
/*     */   {
/* 327 */     this.tq.consume(own ? ":containsOwn" : ":contains");
/* 328 */     String searchText = TokenQueue.unescape(this.tq.chompBalanced('(', ')'));
/* 329 */     Validate.notEmpty(searchText, ":contains(text) query must not be empty");
/* 330 */     if (own)
/* 331 */       this.evals.add(new Evaluator.ContainsOwnText(searchText));
/*     */     else
/* 333 */       this.evals.add(new Evaluator.ContainsText(searchText));
/*     */   }
/*     */ 
/*     */   private void matches(boolean own)
/*     */   {
/* 338 */     this.tq.consume(own ? ":matchesOwn" : ":matches");
/* 339 */     String regex = this.tq.chompBalanced('(', ')');
/* 340 */     Validate.notEmpty(regex, ":matches(regex) query must not be empty");
/*     */ 
/* 342 */     if (own)
/* 343 */       this.evals.add(new Evaluator.MatchesOwn(Pattern.compile(regex)));
/*     */     else
/* 345 */       this.evals.add(new Evaluator.Matches(Pattern.compile(regex)));
/*     */   }
/*     */ 
/*     */   private void not()
/*     */   {
/* 350 */     this.tq.consume(":not");
/* 351 */     String subQuery = this.tq.chompBalanced('(', ')');
/* 352 */     Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");
/*     */ 
/* 354 */     this.evals.add(new StructuralEvaluator.Not(parse(subQuery)));
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.select.QueryParser
 * JD-Core Version:    0.6.2
 */