package com.yzk.lightweightmvc.config;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;

public class HttpClientConfigMode {

    private static ConfigCallAdapterFactory configCallAdapterFactory;

    private static ConfigConverterFactory configConverterFactory;

    private static ConfigHttpBaseUrl configHttpBaseUrl;

    private static ConfigHttpInterceptor configHttpInterceptor;

    private static ConfigOkHttpClient configOkHttpClient;

    private static ConfigCookieJar configCookieJar;


    public static void setConfigConverterFactory(ConfigConverterFactory configConverterFactory) {
        HttpClientConfigMode.configConverterFactory = configConverterFactory;
    }

    public static void setConfigHttpBaseUrl(ConfigHttpBaseUrl configHttpBaseUrl) {
        HttpClientConfigMode.configHttpBaseUrl = configHttpBaseUrl;
    }

    public static void setConfigHttpInterceptor(ConfigHttpInterceptor configHttpInterceptor) {
        HttpClientConfigMode.configHttpInterceptor = configHttpInterceptor;
    }

    public static void setConfigOkHttpClient(ConfigOkHttpClient configOkHttpClient) {
        HttpClientConfigMode.configOkHttpClient = configOkHttpClient;
    }

    public static void setConfigCookieJar(ConfigCookieJar configCookieJar) {
        HttpClientConfigMode.configCookieJar = configCookieJar;
    }

    public static Interceptor getInterceptor() {
        if (configHttpInterceptor == null) {
            return null;
        }
        return configHttpInterceptor.setInterceptor();
    }

    private static boolean HttpClientLog = true;

    public static boolean isHttpClientLog() {
        return HttpClientLog;
    }

    public static void setHttpClientLog(boolean httpClientLog) {
        HttpClientLog = httpClientLog;
    }

    public static OkHttpClient getOkHttpClient() {
        if (configOkHttpClient == null) {
            return null;
        }
        return configOkHttpClient.setOkHttpClient();
    }

    public void setConfigCallAdapterFactory(ConfigCallAdapterFactory configCallAdapterFactory) {
        this.configCallAdapterFactory = configCallAdapterFactory;
    }

    public static CookieJar getCookieManager() {
        if (configCookieJar == null) {
            return null;
        }
        return configCookieJar.setCookieJar();
    }

    public static String getBaseUrl() {
        if (configHttpBaseUrl == null) {
            return null;
        }
        return configHttpBaseUrl.setUrl();
    }

    public static Converter.Factory getConverterFactory() {
        if (configConverterFactory == null) {
            return null;
        }
        return configConverterFactory.setConverterFactory();
    }

    public static CallAdapter.Factory getCallAdapterFactory() {
        if (configCallAdapterFactory == null) {
            return null;
        }
        return configCallAdapterFactory.setCallAdapterFactory();
    }

    public interface ConfigCallAdapterFactory {
        CallAdapter.Factory setCallAdapterFactory();
    }

    public interface ConfigConverterFactory {
        Converter.Factory setConverterFactory();
    }

    public interface ConfigHttpBaseUrl {
        String setUrl();
    }

    public interface ConfigHttpInterceptor {
        Interceptor setInterceptor();
    }

    public interface ConfigOkHttpClient {
        OkHttpClient setOkHttpClient();
    }

    public interface ConfigCookieJar {
        CookieJar setCookieJar();
    }

}
