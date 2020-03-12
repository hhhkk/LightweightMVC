package com.yzk.lightweightmvc.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.yzk.lightweightmvc.base.BaseApp;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import timber.log.Timber;

public class CookiesManager implements CookieJar {

    private static Map<String, List<Cookie>> map = new ArrayMap<>();
    private static SharedPreferences ck;
    private static List emptyList = new ArrayList();

    public static void clearCookies() {
        map.clear();
        ck.edit().clear().commit();
        Timber.e("clearCookies");
    }


    public CookiesManager() {
        ck = BaseApp.getAppContext().getSharedPreferences("cookies", Context.MODE_PRIVATE);
        Map<String, ?> all = ck.getAll();
        Set<String> strings = all.keySet();
        for (String temp : strings) {
            String string = ck.getString(temp, null);
            Timber.e(temp+" "+string);
            List<Cookie> cookies = loadForRequest(string);
            map.put(temp, cookies);
        }
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        String host = httpUrl.host();
        if (map.containsKey(host)) {
            return loadForRequest(host);
        }
        return new ArrayList<>();
    }

    private synchronized List<Cookie> loadForRequest(String host) {
        if (map.containsKey(host)) {
            Timber.e("loadForRequest");
            return map.get(host);
        } else if (ck.contains("host")) {
            String string = ck.getString(host, null);
            List<BaseCookie> douZuanCookies = JSON.parseArray(string, BaseCookie.class);
            if (douZuanCookies != null && douZuanCookies.size() > 0) {
                ArrayList<Cookie> cookies = new ArrayList<>();
                for (BaseCookie douZuanCookie : douZuanCookies) {
                    String name = douZuanCookie.name;
                    String value = douZuanCookie.value;
                    long expiresAt = douZuanCookie.expiresAt;
                    String domain = douZuanCookie.domain;
                    String path = douZuanCookie.path;
                    boolean secure = douZuanCookie.secure;
                    boolean httpOnly = douZuanCookie.httpOnly;
                    boolean hostOnly = douZuanCookie.hostOnly;
                    Cookie.Builder builder = new Cookie.Builder();
                    builder = builder.name(name);
                    builder = builder.value(value);
                    builder = builder.expiresAt(expiresAt);
                    builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
                    builder = builder.path(path);
                    builder = secure ? builder.secure() : builder;
                    builder = httpOnly ? builder.httpOnly() : builder;
                    Cookie build = builder.build();
                    cookies.add(build);
                }
                return cookies;
            } else {
                return emptyList;
            }
        }
        return emptyList;
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
        ArrayList<BaseCookie> douZuanCookies = new ArrayList<>();
        for (Cookie cookie : list) {
            BaseCookie douZuanCookie = new BaseCookie(cookie);
            douZuanCookies.add(douZuanCookie);
        }
        String host = httpUrl.host();
        map.put(host, list);
        String s = JSON.toJSONString(douZuanCookies);
        ck.edit().putString(host, s).commit();
        Timber.e(s);
    }
}
