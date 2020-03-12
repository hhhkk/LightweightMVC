package com.yzk.lightweightmvc.network;

import android.net.Uri;
import android.text.TextUtils;
import com.yzk.lightweightmvc.config.HttpClientConfigMode;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Source;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import timber.log.Timber;

/**
 * Author: lion
 * Date: 2018/4/3
 * Description: describe the class here
 */

public class HttpClient implements Interceptor {

    private static HttpClient httpClient = new HttpClient();

    private static Retrofit retrofit;

    public HttpClient() {

    }

    public static <T> T createRetrofitApi(final Class<T> service) {
        if (retrofit == null) {
            try {
                retrofit = httpClient.retrofit();
            } catch (Exception e) {
                e.printStackTrace();
                Timber.e("createRetrofitApi Error");
            }
        }
        return retrofit.create(service);
    }

    public static Retrofit getHttpClient() {
        if (retrofit == null) {
            try {
                retrofit = httpClient.retrofit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retrofit;
    }


    private synchronized Retrofit retrofit() {
        OkHttpClient defaultOkHttpClient = HttpClientConfigMode.getOkHttpClient();
        OkHttpClient defaultClient;
        if (defaultOkHttpClient == null) {
            defaultClient = defaultOkHttpClient;
            ArrayList<Interceptor> interceptors = new ArrayList<>();
            interceptors.addAll(defaultClient.interceptors());
            defaultClient.interceptors().clear();
            defaultClient.interceptors().add(this);
            defaultClient.interceptors().addAll(interceptors);
        } else {
            defaultClient = getDefaultClient();
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        Converter.Factory factory = HttpClientConfigMode.getConverterFactory();
        builder.addConverterFactory(factory != null ? factory : FastJsonConverterFactory.create());
        CallAdapter.Factory callAdapterFactory = HttpClientConfigMode.getCallAdapterFactory();
        builder.addCallAdapterFactory(callAdapterFactory != null ? callAdapterFactory : RxJava2CallAdapterFactory.create());
        builder.baseUrl(HttpClientConfigMode.getBaseUrl());
        builder.client(defaultClient);//默认客户端
        return builder.build();
    }

    /***
     * 默认-有日志，没缓存
     * @return
     */
    public static synchronized OkHttpClient getDefaultClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        //请求头
        builder.addInterceptor(httpClient);
        //设置连接超时
        int timeout = 20;
        builder.connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS);
        CookieJar cookieManager = HttpClientConfigMode.getCookieManager();
        builder.cookieJar(cookieManager == null ? new CookiesManager() : cookieManager);
        return builder.build();
    }

    /***
     * 重写拦截器 实现日志打印的目的
     * @param chain
     * @return
     * @throws IOException
     */
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Interceptor interceptor = HttpClientConfigMode.getInterceptor();
        Response response;
        if (interceptor != null) {
            response = interceptor.intercept(chain);
        } else {
            Request request = chain.request();
            response = chain.proceed(request);
            Buffer buffer = new Buffer();
            chain.request().body().writeTo(buffer);
            if (HttpClientConfigMode.isHttpClientLog()) {
                StringBuffer stringBuffer = new StringBuffer();
                printlnRequest(response.request(), stringBuffer);
                printlnResponse(response, stringBuffer);
            }
        }
        return response;
    }

    String REQUEST_PARMS_TAG = "Request Parms :";
    String REQUEST_PATH_TAG = "Request Path :";
    String REQUEST_HEADERS_TAG = "Request Header :";
    String REQUEST_METHOD_TAG = "Request method :";
    String RESPONSE_BODY_TAG = "ResponseBody";
    String MULTIPART = MediaType.get("multipart").type();

    /***
     * 打印请求头
     * @param request
     * @param stringBuffer
     */
    private void printlnRequest(Request request, StringBuffer stringBuffer) {
        RequestBody body = request.body();
        MediaType mediaType = body.contentType();
        String type = mediaType.type();
        if (TextUtils.equals(MULTIPART, type)) {
            Buffer buffer = new Buffer();
            try {
                body.writeTo(buffer);
                String s = buffer.readUtf8();
                Uri parse = Uri.parse(s);
                stringBuffer.append(REQUEST_PATH_TAG);
                stringBuffer.append("\n");
                stringBuffer.append(parse.getPath());
                stringBuffer.append("\n");
                stringBuffer.append(REQUEST_PARMS_TAG);
                stringBuffer.append("\n");
                Set<String> queryParameterNames = parse.getQueryParameterNames();
                for (String tempStr : queryParameterNames) {
                    stringBuffer.append(tempStr);
                    stringBuffer.append("=");
                    parse.getQueryParameter(tempStr);
                    stringBuffer.append("\n");
                }
                stringBuffer.append(REQUEST_HEADERS_TAG);
                stringBuffer.append("\n");
                Headers headers = request.headers();
                Set<String> names = headers.names();
                for (String tempStr : names) {
                    stringBuffer.append(tempStr);
                    stringBuffer.append("=");
                    headers.get(tempStr);
                    stringBuffer.append("\n");
                }
                stringBuffer.append(REQUEST_METHOD_TAG);
                stringBuffer.append("\n");
                stringBuffer.append(request.method());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Timber.e(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.length());
    }

    private void printlnResponse(Response response, StringBuffer stringBuffer) {
        ResponseBody body = response.body();
        MediaType mediaType = body.contentType();
        if (mediaType.equals(MULTIPART)) {
            Source source = Okio.source(body.byteStream());
            try {
                String s = Okio.buffer(source).readUtf8();
                stringBuffer.append(RESPONSE_BODY_TAG);
                stringBuffer.append("\n");
                stringBuffer.append(s);
                stringBuffer.delete(0, stringBuffer.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
