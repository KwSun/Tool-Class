/*     */ package org.jsoup.helper;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import org.jsoup.Connection;
/*     */ import org.jsoup.Connection.Base;
/*     */ import org.jsoup.Connection.KeyVal;
/*     */ import org.jsoup.Connection.Method;
/*     */ import org.jsoup.Connection.Request;
/*     */ import org.jsoup.Connection.Response;
/*     */ import org.jsoup.HttpStatusException;
/*     */ import org.jsoup.UnsupportedMimeTypeException;
/*     */ import org.jsoup.nodes.Document;
/*     */ import org.jsoup.nodes.Document.OutputSettings;
/*     */ import org.jsoup.parser.Parser;
/*     */ import org.jsoup.parser.TokenQueue;
/*     */ 
/*     */ public class HttpConnection
/*     */   implements Connection
/*     */ {
/*     */   private static final int HTTP_TEMP_REDIR = 307;
/*     */   private Connection.Request req;
/*     */   private Connection.Response res;
/*     */ 
/*     */   public static Connection connect(String url)
/*     */   {
/*  29 */     Connection con = new HttpConnection();
/*  30 */     con.url(url);
/*  31 */     return con;
/*     */   }
/*     */ 
/*     */   public static Connection connect(URL url) {
/*  35 */     Connection con = new HttpConnection();
/*  36 */     con.url(url);
/*  37 */     return con;
/*     */   }
/*     */ 
/*     */   private static String encodeUrl(String url) {
/*  41 */     if (url == null)
/*  42 */       return null;
/*  43 */     return url.replaceAll(" ", "%20");
/*     */   }
/*     */ 
/*     */   private HttpConnection()
/*     */   {
/*  50 */     this.req = new Request(null);
/*  51 */     this.res = new Response();
/*     */   }
/*     */ 
/*     */   public Connection url(URL url) {
/*  55 */     this.req.url(url);
/*  56 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection url(String url) {
/*  60 */     Validate.notEmpty(url, "Must supply a valid URL");
/*     */     try {
/*  62 */       this.req.url(new URL(encodeUrl(url)));
/*     */     } catch (MalformedURLException e) {
/*  64 */       throw new IllegalArgumentException("Malformed URL: " + url, e);
/*     */     }
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection userAgent(String userAgent) {
/*  70 */     Validate.notNull(userAgent, "User agent must not be null");
/*  71 */     this.req.header("User-Agent", userAgent);
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection timeout(int millis) {
/*  76 */     this.req.timeout(millis);
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection maxBodySize(int bytes) {
/*  81 */     this.req.maxBodySize(bytes);
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection followRedirects(boolean followRedirects) {
/*  86 */     this.req.followRedirects(followRedirects);
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection referrer(String referrer) {
/*  91 */     Validate.notNull(referrer, "Referrer must not be null");
/*  92 */     this.req.header("Referer", referrer);
/*  93 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection method(Connection.Method method) {
/*  97 */     this.req.method(method);
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
/* 102 */     this.req.ignoreHttpErrors(ignoreHttpErrors);
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection ignoreContentType(boolean ignoreContentType) {
/* 107 */     this.req.ignoreContentType(ignoreContentType);
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection data(String key, String value) {
/* 112 */     this.req.data(KeyVal.create(key, value));
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection data(Map<String, String> data) {
/* 117 */     Validate.notNull(data, "Data map must not be null");
/* 118 */     for (Map.Entry entry : data.entrySet()) {
/* 119 */       this.req.data(KeyVal.create((String)entry.getKey(), (String)entry.getValue()));
/*     */     }
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection data(String[] keyvals) {
/* 125 */     Validate.notNull(keyvals, "Data key value pairs must not be null");
/* 126 */     Validate.isTrue(keyvals.length % 2 == 0, "Must supply an even number of key value pairs");
/* 127 */     for (int i = 0; i < keyvals.length; i += 2) {
/* 128 */       String key = keyvals[i];
/* 129 */       String value = keyvals[(i + 1)];
/* 130 */       Validate.notEmpty(key, "Data key must not be empty");
/* 131 */       Validate.notNull(value, "Data value must not be null");
/* 132 */       this.req.data(KeyVal.create(key, value));
/*     */     }
/* 134 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection data(Collection<Connection.KeyVal> data) {
/* 138 */     Validate.notNull(data, "Data collection must not be null");
/* 139 */     for (Connection.KeyVal entry : data) {
/* 140 */       this.req.data(entry);
/*     */     }
/* 142 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection header(String name, String value) {
/* 146 */     this.req.header(name, value);
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection cookie(String name, String value) {
/* 151 */     this.req.cookie(name, value);
/* 152 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection cookies(Map<String, String> cookies) {
/* 156 */     Validate.notNull(cookies, "Cookie map must not be null");
/* 157 */     for (Map.Entry entry : cookies.entrySet()) {
/* 158 */       this.req.cookie((String)entry.getKey(), (String)entry.getValue());
/*     */     }
/* 160 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection parser(Parser parser) {
/* 164 */     this.req.parser(parser);
/* 165 */     return this;
/*     */   }
/*     */ 
/*     */   public Document get() throws IOException {
/* 169 */     this.req.method(Connection.Method.GET);
/* 170 */     execute();
/* 171 */     return this.res.parse();
/*     */   }
/*     */ 
/*     */   public Document post() throws IOException {
/* 175 */     this.req.method(Connection.Method.POST);
/* 176 */     execute();
/* 177 */     return this.res.parse();
/*     */   }
/*     */ 
/*     */   public Connection.Response execute() throws IOException {
/* 181 */     this.res = Response.execute(this.req);
/* 182 */     return this.res;
/*     */   }
/*     */ 
/*     */   public Connection.Request request() {
/* 186 */     return this.req;
/*     */   }
/*     */ 
/*     */   public Connection request(Connection.Request request) {
/* 190 */     this.req = request;
/* 191 */     return this;
/*     */   }
/*     */ 
/*     */   public Connection.Response response() {
/* 195 */     return this.res;
/*     */   }
/*     */ 
/*     */   public Connection response(Connection.Response response) {
/* 199 */     this.res = response;
/* 200 */     return this;
/*     */   }
/*     */ 
/*     */   public static class KeyVal
/*     */     implements Connection.KeyVal
/*     */   {
/*     */     private String key;
/*     */     private String value;
/*     */ 
/*     */     public static KeyVal create(String key, String value)
/*     */     {
/* 686 */       Validate.notEmpty(key, "Data key must not be empty");
/* 687 */       Validate.notNull(value, "Data value must not be null");
/* 688 */       return new KeyVal(key, value);
/*     */     }
/*     */ 
/*     */     private KeyVal(String key, String value) {
/* 692 */       this.key = key;
/* 693 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public KeyVal key(String key) {
/* 697 */       Validate.notEmpty(key, "Data key must not be empty");
/* 698 */       this.key = key;
/* 699 */       return this;
/*     */     }
/*     */ 
/*     */     public String key() {
/* 703 */       return this.key;
/*     */     }
/*     */ 
/*     */     public KeyVal value(String value) {
/* 707 */       Validate.notNull(value, "Data value must not be null");
/* 708 */       this.value = value;
/* 709 */       return this;
/*     */     }
/*     */ 
/*     */     public String value() {
/* 713 */       return this.value;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 718 */       return this.key + "=" + this.value;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Response extends HttpConnection.Base<Connection.Response>
/*     */     implements Connection.Response
/*     */   {
/*     */     private static final int MAX_REDIRECTS = 20;
/*     */     private int statusCode;
/*     */     private String statusMessage;
/*     */     private ByteBuffer byteData;
/*     */     private String charset;
/*     */     private String contentType;
/* 409 */     private boolean executed = false;
/* 410 */     private int numRedirects = 0;
/*     */     private Connection.Request req;
/* 418 */     private static final Pattern xmlContentTypeRxp = Pattern.compile("application/\\w+\\+xml.*");
/*     */ 
/*     */     Response() {
/* 421 */       super();
/*     */     }
/*     */ 
/*     */     private Response(Response previousResponse) throws IOException {
/* 425 */       super();
/* 426 */       if (previousResponse != null) {
/* 427 */         previousResponse.numRedirects += 1;
/* 428 */         if (this.numRedirects >= 20)
/* 429 */           throw new IOException(String.format("Too many redirects occurred trying to load URL %s", new Object[] { previousResponse.url() }));
/*     */       }
/*     */     }
/*     */ 
/*     */     static Response execute(Connection.Request req) throws IOException {
/* 434 */       return execute(req, null);
/*     */     }
/*     */ 
/*     */     static Response execute(Connection.Request req, Response previousResponse) throws IOException {
/* 438 */       Validate.notNull(req, "Request must not be null");
/* 439 */       String protocol = req.url().getProtocol();
/* 440 */       if ((!protocol.equals("http")) && (!protocol.equals("https"))) {
/* 441 */         throw new MalformedURLException("Only http & https protocols supported");
/*     */       }
/*     */ 
/* 444 */       if ((req.method() == Connection.Method.GET) && (req.data().size() > 0))
/* 445 */         serialiseRequestUrl(req); HttpURLConnection conn = createConnection(req);
/*     */       Response res;
/*     */       try {
/* 449 */         conn.connect();
/* 450 */         if (req.method() == Connection.Method.POST) {
/* 451 */           writePost(req.data(), conn.getOutputStream());
/*     */         }
/* 453 */         int status = conn.getResponseCode();
/* 454 */         boolean needsRedirect = false;
/* 455 */         if (status != 200) {
/* 456 */           if ((status == 302) || (status == 301) || (status == 303) || (status == 307))
/* 457 */             needsRedirect = true;
/* 458 */           else if (!req.ignoreHttpErrors())
/* 459 */             throw new HttpStatusException("HTTP error fetching URL", status, req.url().toString());
/*     */         }
/* 461 */         res = new Response(previousResponse);
/* 462 */         res.setupFromConnection(conn, previousResponse);
/* 463 */         if ((needsRedirect) && (req.followRedirects())) {
/* 464 */           req.method(Connection.Method.GET);
/* 465 */           req.data().clear();
/*     */ 
/* 467 */           String location = res.header("Location");
/* 468 */           if ((location != null) && (location.startsWith("http:/")) && (location.charAt(6) != '/'))
/* 469 */             location = location.substring(6);
/* 470 */           req.url(new URL(req.url(), HttpConnection.encodeUrl(location)));
/*     */ 
/* 472 */           for (Map.Entry cookie : res.cookies.entrySet()) {
/* 473 */             req.cookie((String)cookie.getKey(), (String)cookie.getValue());
/*     */           }
/* 475 */           return execute(req, res);
/*     */         }
/* 477 */         res.req = req;
/*     */ 
/* 480 */         String contentType = res.contentType();
/* 481 */         if ((contentType != null) && (!req.ignoreContentType()) && (!contentType.startsWith("text/")) && (!contentType.startsWith("application/xml")) && (!xmlContentTypeRxp.matcher(contentType).matches()))
/*     */         {
/* 487 */           throw new UnsupportedMimeTypeException("Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml", contentType, req.url().toString());
/*     */         }
/*     */ 
/* 490 */         InputStream bodyStream = null;
/* 491 */         InputStream dataStream = null;
/*     */         try {
/* 493 */           dataStream = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
/* 494 */           bodyStream = (res.hasHeader("Content-Encoding")) && (res.header("Content-Encoding").equalsIgnoreCase("gzip")) ? new BufferedInputStream(new GZIPInputStream(dataStream)) : new BufferedInputStream(dataStream);
/*     */ 
/* 498 */           res.byteData = DataUtil.readToByteBuffer(bodyStream, req.maxBodySize());
/* 499 */           res.charset = DataUtil.getCharsetFromContentType(res.contentType);
/*     */         } finally {
/* 501 */           if (bodyStream != null) bodyStream.close();
/* 502 */           if (dataStream == null);
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 507 */         conn.disconnect();
/*     */       }
/*     */ 
/* 510 */       res.executed = true;
/* 511 */       return res;
/*     */     }
/*     */ 
/*     */     public int statusCode() {
/* 515 */       return this.statusCode;
/*     */     }
/*     */ 
/*     */     public String statusMessage() {
/* 519 */       return this.statusMessage;
/*     */     }
/*     */ 
/*     */     public String charset() {
/* 523 */       return this.charset;
/*     */     }
/*     */ 
/*     */     public String contentType() {
/* 527 */       return this.contentType;
/*     */     }
/*     */ 
/*     */     public Document parse() throws IOException {
/* 531 */       Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before parsing response");
/* 532 */       Document doc = DataUtil.parseByteData(this.byteData, this.charset, this.url.toExternalForm(), this.req.parser());
/* 533 */       this.byteData.rewind();
/* 534 */       this.charset = doc.outputSettings().charset().name();
/* 535 */       return doc;
/*     */     }
/*     */ 
/*     */     public String body() {
/* 539 */       Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
/*     */       String body;
/*     */       String body;
/* 542 */       if (this.charset == null)
/* 543 */         body = Charset.forName("UTF-8").decode(this.byteData).toString();
/*     */       else
/* 545 */         body = Charset.forName(this.charset).decode(this.byteData).toString();
/* 546 */       this.byteData.rewind();
/* 547 */       return body;
/*     */     }
/*     */ 
/*     */     public byte[] bodyAsBytes() {
/* 551 */       Validate.isTrue(this.executed, "Request must be executed (with .execute(), .get(), or .post() before getting response body");
/* 552 */       return this.byteData.array();
/*     */     }
/*     */ 
/*     */     private static HttpURLConnection createConnection(Connection.Request req) throws IOException
/*     */     {
/* 557 */       HttpURLConnection conn = (HttpURLConnection)req.url().openConnection();
/* 558 */       conn.setRequestMethod(req.method().name());
/* 559 */       conn.setInstanceFollowRedirects(false);
/* 560 */       conn.setConnectTimeout(req.timeout());
/* 561 */       conn.setReadTimeout(req.timeout());
/* 562 */       if (req.method() == Connection.Method.POST)
/* 563 */         conn.setDoOutput(true);
/* 564 */       if (req.cookies().size() > 0)
/* 565 */         conn.addRequestProperty("Cookie", getRequestCookieString(req));
/* 566 */       for (Map.Entry header : req.headers().entrySet()) {
/* 567 */         conn.addRequestProperty((String)header.getKey(), (String)header.getValue());
/*     */       }
/* 569 */       return conn;
/*     */     }
/*     */ 
/*     */     private void setupFromConnection(HttpURLConnection conn, Connection.Response previousResponse) throws IOException
/*     */     {
/* 574 */       this.method = Connection.Method.valueOf(conn.getRequestMethod());
/* 575 */       this.url = conn.getURL();
/* 576 */       this.statusCode = conn.getResponseCode();
/* 577 */       this.statusMessage = conn.getResponseMessage();
/* 578 */       this.contentType = conn.getContentType();
/*     */ 
/* 580 */       Map resHeaders = conn.getHeaderFields();
/* 581 */       processResponseHeaders(resHeaders);
/*     */ 
/* 584 */       if (previousResponse != null)
/* 585 */         for (Map.Entry prevCookie : previousResponse.cookies().entrySet())
/* 586 */           if (!hasCookie((String)prevCookie.getKey()))
/* 587 */             cookie((String)prevCookie.getKey(), (String)prevCookie.getValue());
/*     */     }
/*     */ 
/*     */     void processResponseHeaders(Map<String, List<String>> resHeaders)
/*     */     {
/* 593 */       for (Map.Entry entry : resHeaders.entrySet()) {
/* 594 */         String name = (String)entry.getKey();
/* 595 */         if (name != null)
/*     */         {
/* 598 */           List values = (List)entry.getValue();
/* 599 */           if (name.equalsIgnoreCase("Set-Cookie")) {
/* 600 */             for (String value : values)
/* 601 */               if (value != null)
/*     */               {
/* 603 */                 TokenQueue cd = new TokenQueue(value);
/* 604 */                 String cookieName = cd.chompTo("=").trim();
/* 605 */                 String cookieVal = cd.consumeTo(";").trim();
/* 606 */                 if (cookieVal == null) {
/* 607 */                   cookieVal = "";
/*     */                 }
/*     */ 
/* 610 */                 if ((cookieName != null) && (cookieName.length() > 0))
/* 611 */                   cookie(cookieName, cookieVal);
/*     */               }
/*     */           }
/* 614 */           else if (!values.isEmpty())
/* 615 */             header(name, (String)values.get(0));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private static void writePost(Collection<Connection.KeyVal> data, OutputStream outputStream) throws IOException {
/* 621 */       OutputStreamWriter w = new OutputStreamWriter(outputStream, "UTF-8");
/* 622 */       boolean first = true;
/* 623 */       for (Connection.KeyVal keyVal : data) {
/* 624 */         if (!first)
/* 625 */           w.append('&');
/*     */         else {
/* 627 */           first = false;
/*     */         }
/* 629 */         w.write(URLEncoder.encode(keyVal.key(), "UTF-8"));
/* 630 */         w.write(61);
/* 631 */         w.write(URLEncoder.encode(keyVal.value(), "UTF-8"));
/*     */       }
/* 633 */       w.close();
/*     */     }
/*     */ 
/*     */     private static String getRequestCookieString(Connection.Request req) {
/* 637 */       StringBuilder sb = new StringBuilder();
/* 638 */       boolean first = true;
/* 639 */       for (Map.Entry cookie : req.cookies().entrySet()) {
/* 640 */         if (!first)
/* 641 */           sb.append("; ");
/*     */         else
/* 643 */           first = false;
/* 644 */         sb.append((String)cookie.getKey()).append('=').append((String)cookie.getValue());
/*     */       }
/*     */ 
/* 647 */       return sb.toString();
/*     */     }
/*     */ 
/*     */     private static void serialiseRequestUrl(Connection.Request req) throws IOException
/*     */     {
/* 652 */       URL in = req.url();
/* 653 */       StringBuilder url = new StringBuilder();
/* 654 */       boolean first = true;
/*     */ 
/* 656 */       url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
/*     */ 
/* 662 */       if (in.getQuery() != null) {
/* 663 */         url.append(in.getQuery());
/* 664 */         first = false;
/*     */       }
/* 666 */       for (Connection.KeyVal keyVal : req.data()) {
/* 667 */         if (!first)
/* 668 */           url.append('&');
/*     */         else
/* 670 */           first = false;
/* 671 */         url.append(URLEncoder.encode(keyVal.key(), "UTF-8")).append('=').append(URLEncoder.encode(keyVal.value(), "UTF-8"));
/*     */       }
/*     */ 
/* 676 */       req.url(new URL(url.toString()));
/* 677 */       req.data().clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Request extends HttpConnection.Base<Connection.Request>
/*     */     implements Connection.Request
/*     */   {
/*     */     private int timeoutMilliseconds;
/*     */     private int maxBodySizeBytes;
/*     */     private boolean followRedirects;
/*     */     private Collection<Connection.KeyVal> data;
/* 321 */     private boolean ignoreHttpErrors = false;
/* 322 */     private boolean ignoreContentType = false;
/*     */     private Parser parser;
/*     */ 
/*     */     private Request()
/*     */     {
/* 325 */       super();
/* 326 */       this.timeoutMilliseconds = 3000;
/* 327 */       this.maxBodySizeBytes = 1048576;
/* 328 */       this.followRedirects = true;
/* 329 */       this.data = new ArrayList();
/* 330 */       this.method = Connection.Method.GET;
/* 331 */       this.headers.put("Accept-Encoding", "gzip");
/* 332 */       this.parser = Parser.htmlParser();
/*     */     }
/*     */ 
/*     */     public int timeout() {
/* 336 */       return this.timeoutMilliseconds;
/*     */     }
/*     */ 
/*     */     public Request timeout(int millis) {
/* 340 */       Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
/* 341 */       this.timeoutMilliseconds = millis;
/* 342 */       return this;
/*     */     }
/*     */ 
/*     */     public int maxBodySize() {
/* 346 */       return this.maxBodySizeBytes;
/*     */     }
/*     */ 
/*     */     public Connection.Request maxBodySize(int bytes) {
/* 350 */       Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
/* 351 */       this.maxBodySizeBytes = bytes;
/* 352 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean followRedirects() {
/* 356 */       return this.followRedirects;
/*     */     }
/*     */ 
/*     */     public Connection.Request followRedirects(boolean followRedirects) {
/* 360 */       this.followRedirects = followRedirects;
/* 361 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean ignoreHttpErrors() {
/* 365 */       return this.ignoreHttpErrors;
/*     */     }
/*     */ 
/*     */     public Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
/* 369 */       this.ignoreHttpErrors = ignoreHttpErrors;
/* 370 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean ignoreContentType() {
/* 374 */       return this.ignoreContentType;
/*     */     }
/*     */ 
/*     */     public Connection.Request ignoreContentType(boolean ignoreContentType) {
/* 378 */       this.ignoreContentType = ignoreContentType;
/* 379 */       return this;
/*     */     }
/*     */ 
/*     */     public Request data(Connection.KeyVal keyval) {
/* 383 */       Validate.notNull(keyval, "Key val must not be null");
/* 384 */       this.data.add(keyval);
/* 385 */       return this;
/*     */     }
/*     */ 
/*     */     public Collection<Connection.KeyVal> data() {
/* 389 */       return this.data;
/*     */     }
/*     */ 
/*     */     public Request parser(Parser parser) {
/* 393 */       this.parser = parser;
/* 394 */       return this;
/*     */     }
/*     */ 
/*     */     public Parser parser() {
/* 398 */       return this.parser;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class Base<T extends Connection.Base>
/*     */     implements Connection.Base<T>
/*     */   {
/*     */     URL url;
/*     */     Connection.Method method;
/*     */     Map<String, String> headers;
/*     */     Map<String, String> cookies;
/*     */ 
/*     */     private Base()
/*     */     {
/* 211 */       this.headers = new LinkedHashMap();
/* 212 */       this.cookies = new LinkedHashMap();
/*     */     }
/*     */ 
/*     */     public URL url() {
/* 216 */       return this.url;
/*     */     }
/*     */ 
/*     */     public T url(URL url) {
/* 220 */       Validate.notNull(url, "URL must not be null");
/* 221 */       this.url = url;
/* 222 */       return this;
/*     */     }
/*     */ 
/*     */     public Connection.Method method() {
/* 226 */       return this.method;
/*     */     }
/*     */ 
/*     */     public T method(Connection.Method method) {
/* 230 */       Validate.notNull(method, "Method must not be null");
/* 231 */       this.method = method;
/* 232 */       return this;
/*     */     }
/*     */ 
/*     */     public String header(String name) {
/* 236 */       Validate.notNull(name, "Header name must not be null");
/* 237 */       return getHeaderCaseInsensitive(name);
/*     */     }
/*     */ 
/*     */     public T header(String name, String value) {
/* 241 */       Validate.notEmpty(name, "Header name must not be empty");
/* 242 */       Validate.notNull(value, "Header value must not be null");
/* 243 */       removeHeader(name);
/* 244 */       this.headers.put(name, value);
/* 245 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean hasHeader(String name) {
/* 249 */       Validate.notEmpty(name, "Header name must not be empty");
/* 250 */       return getHeaderCaseInsensitive(name) != null;
/*     */     }
/*     */ 
/*     */     public T removeHeader(String name) {
/* 254 */       Validate.notEmpty(name, "Header name must not be empty");
/* 255 */       Map.Entry entry = scanHeaders(name);
/* 256 */       if (entry != null)
/* 257 */         this.headers.remove(entry.getKey());
/* 258 */       return this;
/*     */     }
/*     */ 
/*     */     public Map<String, String> headers() {
/* 262 */       return this.headers;
/*     */     }
/*     */ 
/*     */     private String getHeaderCaseInsensitive(String name) {
/* 266 */       Validate.notNull(name, "Header name must not be null");
/*     */ 
/* 268 */       String value = (String)this.headers.get(name);
/* 269 */       if (value == null)
/* 270 */         value = (String)this.headers.get(name.toLowerCase());
/* 271 */       if (value == null) {
/* 272 */         Map.Entry entry = scanHeaders(name);
/* 273 */         if (entry != null)
/* 274 */           value = (String)entry.getValue();
/*     */       }
/* 276 */       return value;
/*     */     }
/*     */ 
/*     */     private Map.Entry<String, String> scanHeaders(String name) {
/* 280 */       String lc = name.toLowerCase();
/* 281 */       for (Map.Entry entry : this.headers.entrySet()) {
/* 282 */         if (((String)entry.getKey()).toLowerCase().equals(lc))
/* 283 */           return entry;
/*     */       }
/* 285 */       return null;
/*     */     }
/*     */ 
/*     */     public String cookie(String name) {
/* 289 */       Validate.notNull(name, "Cookie name must not be null");
/* 290 */       return (String)this.cookies.get(name);
/*     */     }
/*     */ 
/*     */     public T cookie(String name, String value) {
/* 294 */       Validate.notEmpty(name, "Cookie name must not be empty");
/* 295 */       Validate.notNull(value, "Cookie value must not be null");
/* 296 */       this.cookies.put(name, value);
/* 297 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean hasCookie(String name) {
/* 301 */       Validate.notEmpty("Cookie name must not be empty");
/* 302 */       return this.cookies.containsKey(name);
/*     */     }
/*     */ 
/*     */     public T removeCookie(String name) {
/* 306 */       Validate.notEmpty("Cookie name must not be empty");
/* 307 */       this.cookies.remove(name);
/* 308 */       return this;
/*     */     }
/*     */ 
/*     */     public Map<String, String> cookies() {
/* 312 */       return this.cookies;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/apple/Desktop/jsoup-1.8.1.jar
 * Qualified Name:     org.jsoup.helper.HttpConnection
 * JD-Core Version:    0.6.2
 */