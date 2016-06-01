//package com.lgq.rssreader.util;
//
//import java.util.ArrayList;
//
///**
// * Created by redel on 2016-01-03.
// */
//public class YoukuUtil {
//    private String rc4(String a, String b){
//        String c;
//        String[] d= new String[];
//        int e=0;
//        String f="";
//        int g=0;
//        for(;256>g;g++)
//            d[g]=g;
//        for(g=0;256>g;g++) {
//            e = (e + d[g] + a.charCodeAt(g % a.length)) % 256,
//                    c = d[g], d[g] = d[e], d[e] = c;
//        }
//        g=0;
//        e=0;
//        for(int h=0; h < b.length;h++) {
//            g = (g + 1) % 256;
//            e = (e + d[g]) % 256;
//            c = d[g];
//            d[g] = d[e];
//            d[e] = c;
//            f += String.fromCharCode(b.charCodeAt(h) ^ d[(d[g] + d[e]) % 256]);
//        }
//        return f;
//    }
//
//    public String translate(String a, String b){
//        for(var c=[],d=0;d<a.length;d++){
//            var e = 0;
//            e = a[d] >= "a" && a[d] <= "z" ? a[d].charCodeAt(0) - "a".charCodeAt(0) : a[d] - "0" + 26;
//        }
//        for(var f=0;36>f;f++){
//            if(b[f]==e){
//                e=f;
//                break;
//            }
//            e>25 ? c[d]=e-26:c[d]=String.fromCharCode(e+97)
//        }
//        return c.join("");
//    }
//}
