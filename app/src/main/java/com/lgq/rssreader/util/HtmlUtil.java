package com.lgq.rssreader.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

    private static final String videoStart = "data:image/png;base64,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";

    public static String wrapHtml(String content){
        {
            String deviceWidth = "360";

            String fontFamily = "font-family:arial;";

            StringBuilder sb = new StringBuilder();

            String html =
                    "<html>" +
                            "<head>" +
                            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">" +
                            "<meta name=\"viewport\" id=\"viewer\" content=\"width=" + deviceWidth + ",target-densitydpi=medium-dpi,initial-scale=1.0,minimum-scale=1.0,user-scalable=no\" />" +
                            "<script type=\"text/javascript\">" +
                            "function fontsize(size) {" +
                            "document.getElementById('body').style.fontSize = size + 'pt';" +
                            "}" +
                            "function lineheight(height) {" +
                            "document.getElementById('body').style.lineHeight = height + '%';" +
                            "}" +
                            "function backgroundColor(color) {" +
                            "document.getElementById('body').style.backgroundColor = color ;" +
                            "}" +
                            "function fontColor(color) {" +
                            "document.getElementById('body').style.color = color;" +
                            "}" +
                            "function fontFamily(family) {" +
                            "document.getElementById('body').style.fontFamily = family;" +
                            "}" +
                            "function moveToTop() {" +
                            "window.scrollTo(0,0);" +
                            "}" +
                            "function linkHandle() {" +
                            "var e = this.event;" +
                            "if(e.srcElement.getAttribute('videoLink') != null)" +
                            " window.external.notifyJava('LinkHandle' + e.srcElement.getAttribute('videoLink')); " +
                            "else if(e.srcElement.href != undefined)" +
                            " window.external.notifyJava('LinkHandle' + e.srcElement.href); " +
                            "if (e && e.preventDefault) " +
                            " e.preventDefault(); " +
                            "else " +
                            " window.event.returnValue = false; " +
                            " return false; " +
                            "}" +
                            "function replaceFlash(id, html, title, enableSeperate) {" +
                            "var x = document.getElementById('msg' + id); " +
                            "if(x == undefined)" +
                            "return;" +
                            "if(html.length == 0){ " +
                            "var y = document.getElementById('click' + id + '0'); " +
                            "y.parentNode.removeChild(y); " +
                            "x.innerHTML = title; " +
                            "x.style.color = 'red'; " +
                            "x.style.display = 'block'; " +
                            "}" +
                            "if(html.length != 0){ " +
                            "x.style.display = 'none'; " +
                            "if(html.indexOf('____')!=-1){" +
                            "var result = html.split('____');" +
                            "var y = document.getElementById('click' + id + '0'); " +
                            "if(enableSeperate == 'True' && result[0].indexOf('|')!=-1){" +
                            "var links = result[0].split('|');" +
                            "var y = document.getElementById('click' + id + '0'); " +
                            "y.href = links[0]; " +
                            "y.innerHTML = title + 0; " +
                            "var count = links.length;" +
                            "if(count > 20)" +
                            "count = 20;" +
                            "for(var i=1; i<count;i++){" +
                            "var br = document.createElement('br'); " +
                            "var newLink = document.getElementById('click' + id + i.toString()); " +
                            "newLink.href = links[i];" +
                            "newLink.innerHTML = title + i;" +
                            "y.parentNode.insertBefore(br,newLink);" +
                            "}" +
                            "var preview = document.createElement('img'); " +
                            "preview.src = result[1];" +
                            //"preview.style.background = \"url('\" + result[1] + \"') 0px 0;\";" +
                            "preview.style.width = \"98%\";" +
                            "preview.style.margin = 5;" +
                            "y.parentNode.appendChild(preview);" +
                            "var z = document.getElementById('flash' + id); " +
                            "z.parentNode.removeChild(z);" +

                            "}" +
                            "else{" +
                            //Due to the limitation of tudou video should play in same UA, try to use html5 video
                            "if(result[0].length > 0 && (result[0].indexOf('vr.tudou') != -1 || result[0].indexOf('ku6') != -1)){" +
                            "var video = document.createElement('video'); " +
                            "video.src= result[0];" +
                            "video.poster= result[1]; " +
                            "video.style.width=\"100%\";" +
                            "video.controls= \"controls\"; " +
                            "video.type= \"video/mp4\"; " +

                            "var y = document.getElementById('click' + id + '0'); " +
                            "y.parentNode.appendChild(video);" +
                            "}else{" +

                            "var y = document.getElementById('click' + id + '0'); " +
                            "y.href = result[0]; " +
                            "y.parentNode.style.height = 270; " +
                            "y.parentNode.style.width = '100%'; " +
                            "y.style.left = 8; " +
                            "y.style.position = 'absolute';" +
                            "var imgDiv = document.createElement('div'); " +
                            "imgDiv.style.position = 'absolute';" +
                            "imgDiv.style.magin = 'auto';" +
                            "imgDiv.style.height = 270;" +
                            "imgDiv.style.width = '100%';" +

                            "imgDiv.setAttribute('videoLink', result[0]); " +
                            "var img = document.createElement('img'); " +

                            "if(result[1].length > 0) " +
                            "img.src = result[1];" +
                            "else{" +
                            "imgDiv.style.background = \"black\";" +
                            "}" +
                            "img.style.left = 0;" +
                            "img.style.bottom = 0;" +
                            "img.style.top = 0;" +
                            "img.style.right = 0;" +
                            "img.style.width = \"100%\";" +
                            "img.style.position = 'absolute';" +
                            "img.style.margin = 'auto';" +
                            "img.setAttribute('videoLink', result[0]); " +
                            "imgDiv.appendChild(img);" +

                            "var coverDiv = document.createElement('div'); " +
                            "coverDiv.style.position = 'absolute';" +
                            "coverDiv.style.left = 0;" +
                            "coverDiv.style.top = 0;" +
                            "coverDiv.setAttribute('videoLink', result[0]); " +
                            "var cover = document.createElement('img'); " +
                            "cover.setAttribute('videoLink', result[0]); " +
                            "cover.style.height = \"auto\";" +
                            "cover.setAttribute('src', '" + videoStart + "'); " +
                            //"cover.style.width = \"" + deviceWidth + "\";" +
                            "cover.style.width = '100%';" +
                            "coverDiv.appendChild(cover);" +

                            "y.appendChild(imgDiv);" +
                            "y.appendChild(coverDiv);" +
                            "y.style.width = \"96%\";" +
                            "y.style.height = \"auto\";" +

                            "}" +
//										"window.external.notifyJava(\"y.width=\" + y.style.width + \"img.width=\" + img.style.width + \"imgDiv.width=\" + imgDiv.style.width+ \"window.width=\" + document.width);" +
                            "}" +
                            "}" +
                            "}" +
                            "}" +
                            "function setImageSize() {" +
                            "var images = content.getElementsByTagName('img');" +
                            "for (var i = 0; i < images.length; i++) {" +
                            "images[i].removeAttribute(\"src\");" +
                            "images[i].removeAttribute(\"class\");" +
                            "images[i].removeAttribute(\"style\");" +
                            "images[i].removeAttribute(\"width\");" +
                            "images[i].removeAttribute(\"height\");" +
                            "cur.style.width = '" + deviceWidth + "';" +
                            "cur.style.height = 'auto';" +
                            "};" +
                            "};" +
                            "var scrollLoad = function (options) {" +
                            "var defaults = (arguments.length == 0) ? { src: 'xSrc', time: 300} : { src: options.src || 'xSrc', time: options.time ||300}; " +
                            "var camelize = function (s) {" +
                            "return s.replace(/-(\\w)/g, function (strMatch, p1) {" +
                            "return p1.toUpperCase();" +
                            "});" +
                            "};" +
                            "this.getClient = function(){" +
                            "var l,t,w,h;" +
                            "l = document.documentElement.scrollLeft || document.body.scrollLeft;" +
                            "t = document.documentElement.scrollTop || document.body.scrollTop;" +
                            "w = document.documentElement.clientWidth;" +
                            "h = document.documentElement.clientHeight;" +
                            "return {'left':l,'top':t,'width':w,'height':h};" +
                            "};" +
                            "this.getSubClient = function(p){" +
                            "var l = 0,t = 0,w,h;" +
                            "w = p.offsetWidth ;" +
                            "h = p.offsetHeight;" +
                            "while(p.offsetParent){" +
                            "l += p.offsetLeft;" +
                            "t += p.offsetTop;" +
                            "p = p.offsetParent;" +
                            "}" +
                            "return {'left':l,'top':t,'width':w,'height':h };" +
                            "};" +
                            "this.intens = function(rec1,rec2){" +
                            "return (rec2.top > rec1.top && rec2.top < (rec1.top + document.body.clientHeight));" +
                            "};" +
                            "this.getStyle = function (element, property) {" +
                            "if (arguments.length != 2) return false;" +
                            "var value = element.style[camelize(property)];" +
                            "if (!value) {" +
                            "if (document.defaultView && document.defaultView.getComputedStyle) {" +
                            "var css = document.defaultView.getComputedStyle(element, null);" +
                            "value = css ? css.getPropertyValue(property) : null;" +
                            "} else if (element.currentStyle) {" +
                            "value = element.currentStyle[camelize(property)];" +
                            "}" +
                            "}" +
                            "return value == 'auto' ? '' : value;" +
                            "};" +
                            "var _init = function () {" +
                            "var rec1 = getClient();" +
                            "docImg = document.images;" +
                            "_len = docImg.length;" +
                            "if (!_len) return false;" +
                            "for (var i = 0; i < _len; i++) {" +
                            "var attrSrc = docImg[i].getAttribute(defaults.src);" +
                            "var o = docImg[i];" +
                            "var tag = o.nodeName.toLowerCase();" +
                            "if (o) {" +
                            "var rec2 =  getSubClient(o);" +
                            "if(intens(rec1,rec2)) {" +
                            "if (tag === \"img\" && attrSrc !== null) {" +
                            "clearImage(o);" +
                            "loadImage(o, attrSrc," +
                            "function (cur, img, cached) {" +
                            "cur.src = img.src; " +
                            "if(img.width > 50)" +
                            //"cur.style.width = " + deviceWidth + ";" +
                            "cur.style.width = '98%';" +
                            "cur.style.margin = '5px auto';" +
                            "}," +
                            "function (cur, img) {" +
                            "cur.style.display = \"none\";" +
                            "}" +
                            ");" +
                            "resetImage(o);" +
                            "}" +
                            "o = null;" +
                            "}" +
                            "}" +
                            "};" +
                            "window.onscroll = function () {" +
                            "setTimeout(function () {" +
                            "_init();" +
                            "}, defaults.time);" +
                            "}" +
                            "};" +
                            "return _init();" +
                            "};" +
                            "function LoadContent(content,title,type) {" +
                            "window.external.notifyJava('LoadComplted'); " +
                            "document.getElementById('error').style.display='none';" +
                            "document.getElementById('content').style.display='block';" +
                            "document.getElementById('content').innerHTML=content;" +
                            //"formatImages();" +
                            "scrollLoad();" +
                            "window.scrollTo(0,0);" +
                            "window.external.loadComplete(type); " +
                            "}" +
                            "function LoadError(errorTitle,errorContent, errorLoad) {" +
                            "document.getElementById('content').style.display='none';" +
                            "document.getElementById('error').style.display='block';" +
                            "document.getElementById('errorTitle').innerHTML = errorTitle;" +
                            "document.getElementById('errorContent').innerHTML = errorContent;" +
                            "document.getElementById('errorLoad').innerHTML = errorLoad;" +
                            "window.scrollTo(0,0);" +
                            "window.external.loadComplete('content'); " +
                            "}" +
                            "var onInit = function () {" +
                            "scrollLoad();" +
                            "window.external.loadComplete('init'); " +
                            //"window.external.notifyJava('init'); " +
                            "};" +
                            "var loadImage = function (cur, url, callback, onerror) {" +
                            "var img = new Image();" +
                            "img.src = url;" +
                            "if (img.complete) {" +
                            "callback && callback(cur, img, true);" +
                            "return;" +
                            "}" +
                            "img.onload = function () {" +
                            "window.external.notifyJava(img.width + '-' + img.src); " +
                            "callback && callback(cur, img, false);" +
                            "return;" +
                            "};" +
                            "if (typeof onerror == \"function\") {" +
                            "img.onerror = function () {" +
                            "onerror && onerror(cur, img);" +
                            "}" +
                            "}" +
                            "};" +
                            "var clearImage = function (img) {" +
                            "img.removeAttribute(\"src\");" +
                            "img.removeAttribute(\"class\");" +
                            "img.removeAttribute(\"style\");" +
                            "img.removeAttribute(\"width\");" +
                            "img.removeAttribute(\"height\");" +
                            "img.src=\"Loading.gif\";" +
                            "img.setAttribute(\"style\",\"margin:5px auto\");" +
                            "};" +
                            "var resetImage = function (img) {" +
                            "img.onclick = function () {" +
                            "var imgSrc = this.src;" +
                            "window.external.notifyJava('SaveToMediaLibrary'+imgSrc); " +
                            "if (e && e.preventDefault) " +
                            " e.preventDefault(); " +
                            "else " +
                            " window.event.returnValue = false; " +
                            "return false;" +
                            "};" +
                            "};" +
                            "var srcs = [];" +
                            "var formatImages = function () {" +
                            "srcs = [];" +
                            "var content = document.getElementById(\"content\");" +
                            "var images = content.getElementsByTagName('img');" +
                            "for (var i = 0; i < images.length; i++) {" +
                            "srcs[i] = images[i].src;" +
                            "clearImage(images[i]);" +
                            "loadImage(images[i], srcs[i]," +
                            "function (cur, img, cached) {" +
                            "cur.src = img.src;" +
                            "if(img.width > 50){" +
                            //"cur.style.width = '"+deviceWidth+"';" +
                            "cur.style.width = '98%';" +
                            "cur.style.height = 'auto';" +
                            "}" +
                            "}," +
                            "function (cur, img) {" +
                            "cur.style.display = \"none\";" +
                            "}" +
                            ");" +
                            "resetImage(images[i]);" +
                            "}" +
                            "};" +
                            "</script>" +
                            "<style type=\"text/css\"> " +
//	                    "@font-face { "+
//	                        "font-family: \"Hiragino\"; "+
//	                        "src: url('file:///android_asset/font/Hiragino Sans GB W3.otf'); "+
//	                    "} "+
//	                    "@font-face { "+
//	                        "font-family: \"Dreamofgirl\"; "+
//	                        "src: url('file:///android_asset/font/wryhzt.ttf'); "+
//	                    "} "+

                            sb.toString() +

                            "window,html,body{ " +
                            //"overflow-x:hidden !important; "+
                            //"-webkit-overflow-scrolling: touch !important;"+
                            //"overflow: scroll !important;"+
                            "word-wrap:break-word;" +
                            "word-break:break-all;" +
                            "}" +
                            "body, menu, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, code, form, fieldset, input, textarea, p, blockquote, th, td { margin: 0; padding: 0; }" +
                            "body { background: #f3f3f3;" + fontFamily + " }" +
                            "table { border-collapse: collapse; border-spacing: 0; }" +
                            "fieldset, img { border: 0; }" +
                            "address, caption, cite, code, dfn, em, strong, th, var { font-style: normal; font-weight: normal; }" +
                            "em { font-style: italic; }" +
                            "strong { font-weight: bold; }" +
                            "ol, ul { list-style: none; }" +
                            "caption, th { text-align: left; }" +
                            "h1, h2, h3, h4, h5, h6 { font-size: 100%; font-weight: normal; }" +
                            "q:before, q:after { content: ''; }" +
                            "abbr, acronym { border: 0; }" +
                            "strike { display: inline; }" +
                            "pre { display: block; visibility: visible; table-layout: auto; white-space:pre;} " +
                            "A:link { color: #3EC8EF; } " +
                            "A:visited { color: #3EC8EF; } " +
                            "img {text-align: center; display: block; border:0px; margin-top:2px; margin-bottom:2px;overflow:hidden;}" +
                            ".error {margin:50px; margin: auto;}" +
                            ".error h1 {margin: 20px 0 0;}" +
                            ".error p {margin: 10px 0; padding: 0;}" +
                            ".error a {color: #9caa6d; text-decoration:none;}" +
                            ".error a:hover {color: #9caa6d; text-decoration:underline;}" +
                            "</style>" +
                            "</head>" +
                            "<body id=\"body\" onload='onInit()' style=\"margin:8;font-size:14pt;" +
                            "color:black;background-color:white;>" +
                            "<div id=\"debug\" ></div>" +
                            "<div id=\"content\">" + content + "</div>" +
                            "</div>" +
                            "</body>" +
                            "</html>";
            return html;
        }
    }

    private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签

    /**
     *
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     * <p>
     *
     * @param str
     * @return String
     */
    public static String filterHtml(String str) {
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     *
     * 基本功能：过滤指定标签
     * <p>
     *
     * @param str
     * @param tag
     *            指定标签
     * @return String
     */
    public static String fiterHtmlTag(String str, String tag) {
        String regxp = "<\\s*" + tag + "\\s+([^>]*)\\s*>";
        Pattern pattern = Pattern.compile(regxp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     *
     * 基本功能：替换指定的标签
     * <p>
     *
     * @param str
     * @param beforeTag
     *            要替换的标签
     * @param tagAttrib
     *            要替换的标签属性值
     * @param startTag
     *            新标签开始标记
     * @param endTag
     *            新标签结束标记
     * @return String
     * @如：替换img标签的src属性值为[img]属性值[/img]
     */
    public static String replaceHtmlTag(String str, String beforeTag, String tagAttrib, String startTag, String endTag) {
        String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
        String regxpForTagAttrib = tagAttrib + "=\"([^\"]+)\"";
        Pattern patternForTag = Pattern.compile(regxpForTag);
        Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
        Matcher matcherForTag = patternForTag.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        while (result) {
            StringBuffer sbreplace = new StringBuffer();
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
                    .group(1));
            if (matcherForAttrib.find()) {
                matcherForAttrib.appendReplacement(sbreplace, startTag
                        + matcherForAttrib.group(1) + endTag);
            }
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }

    /**
     *
     * 基本功能：过滤多余的空格
     * <p>
     *
     * @param s
     *            要替换的内容
     * @return String
     */
    public static String trim(String s)
    {
        return s.replace("\n"," ").replace("'", " ");
    }

    /**
     *
     * 基本功能：对字符串进行转义
     * <p>
     *
     * @param s
     *            要转义的内容
     * @return String
     */
    public static String unescape (String s)
    {
        while (true)
        {
            int n=s.indexOf("&#");
            if (n<0) break;
            int m=s.indexOf(";",n+2);
            if (m<0) break;
            try
            {
                s=s.substring(0,n)+(char)(Integer.parseInt(s.substring(n+2,m)))+
                        s.substring(m+1);
            }
            catch (Exception e)
            {
                return s;
            }
        }
        s=s.replace("&quot;","\"");
        s=s.replace("&lt;","<");
        s=s.replace("&gt;",">");
        s=s.replace("&amp;","&");
        return s;
    }

    /**
     *
     * 基本功能：对字符串进行url编码，但是全部转成大写字母
     * <p>
     *
     * @param str
     *            要转义的内容
     * @return String
     */
    public static String UrlEncodeUpper(String str)
    {
        StringBuilder builder = new StringBuilder();
        for(char c : str.toCharArray())
        {
            if (TextUtils.htmlEncode(String.valueOf(c)).length() > 1)
            {
                builder.append(TextUtils.htmlEncode(String.valueOf(c)).toUpperCase());
            }
            else
            {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}

