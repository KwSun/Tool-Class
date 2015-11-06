/*     */ package org.jsoup.parser;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Element;
/*     */ import org.jsoup.nodes.Node;
/*     */ 
/*     */ public class Parser
/*     */ {
/*     */   private static final int DEFAULT_MAX_ERRORS = 0;
/*     */   private TreeBuilder treeBuilder;
/*  17 */   private int maxErrors = 0;
/*     */   private ParseErrorList errors;
/*     */ 
/*     */   public Parser(TreeBuilder treeBuilder)
/*     */   {
/*  25 */     this.treeBuilder = treeBuilder;
/*     */   }
/*     */ 
/*     */   public Document parseInput(String html, String baseUri) {
/*  29 */     this.errors = (isTrackErrors() ? ParseErrorList.tracking(this.maxErrors) : ParseErrorList.noTracking());
/*  30 */     Document doc = this.treeBuilder.parse(html, baseUri, this.errors);
/*  31 */     return doc;
/*     */   }
/*     */ 
/*     */   public TreeBuilder getTreeBuilder()
/*     */   {
/*  40 */     return this.treeBuilder;
/*     */   }
/*     */ 
/*     */   public Parser setTreeBuilder(TreeBuilder treeBuilder)
/*     */   {
/*  49 */     this.treeBuilder = treeBuilder;
/*  50 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean isTrackErrors()
/*     */   {
/*  58 */     return this.maxErrors > 0;
/*     */   }
/*     */ 
/*     */   public Parser setTrackErrors(int maxErrors)
/*     */   {
/*  67 */     this.maxErrors = maxErrors;
/*  68 */     return this;
/*     */   }
/*     */ 
/*     */   public List<ParseError> getErrors()
/*     */   {
/*  76 */     return this.errors;
/*     */   }
/*     */ 
/*     */   public static Document parse(String html, String baseUri)
/*     */   {
/*  89 */     TreeBuilder treeBuilder = new HtmlTreeBuilder();
/*  90 */     return treeBuilder.parse(html, baseUri, ParseErrorList.noTracking());
/*     */   }
/*     */ 
/*     */   public static List<Node> parseFragment(String fragmentHtml, Element context, String baseUri)
/*     */   {
/* 104 */     HtmlTreeBuilder treeBuilder = new HtmlTreeBuilder();
/* 105 */     return treeBuilder.parseFragment(fragmentHtml, context, baseUri, ParseErrorList.noTracking());
/*     */   }
/*     */ 
/*     */   public static List<Node> parseXmlFragment(String fragmentXml, String baseUri)
/*     */   {
/* 116 */     XmlTreeBuilder treeBuilder = new XmlTreeBuilder();
/* 117 */     return treeBuilder.parseFragment(fragmentXml, baseUri, ParseErrorList.noTracking());
/*     */   }
/*     */ 
/*     */   public static Document parseBodyFragment(String bodyHtml, String baseUri)
/*     */   {
/* 129 */     Document doc = Document.createShell(baseUri);
/* 130 */     Element body = doc.body();
/* 131 */     List nodeList = parseFragment(bodyHtml, body, baseUri);
/* 132 */     Node[] nodes = (Node[])nodeList.toArray(new Node[nodeList.size()]);
/* 133 */     for (Node node : nodes) {
/* 134 */       body.appendChild(node);
/*     */     }
/* 136 */     return doc;
/*     */   }
/*     */ 
/*     */   public static String unescapeEntities(String string, boolean inAttribute)
/*     */   {
/* 146 */     Tokeniser tokeniser = new Tokeniser(new CharacterReader(string), ParseErrorList.noTracking());
/* 147 */     return tokeniser.unescapeEntities(inAttribute);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Document parseBodyFragmentRelaxed(String bodyHtml, String baseUri)
/*     */   {
/* 158 */     return parse(bodyHtml, baseUri);
/*     */   }
/*     */ 
/*     */   public static Parser htmlParser()
/*     */   {
/* 169 */     return new Parser(new HtmlTreeBuilder());
/*     */   }
/*     */ 
/*     */   public static Parser xmlParser()
/*     */   {
/* 178 */     return new Parser(new XmlTreeBuilder());
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.parser.Parser
 * JD-Core Version:    0.6.2
 */