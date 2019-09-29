package org.clever.nashorn.internal;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.mapper.JsonWrapper;
import org.clever.nashorn.utils.ObjectConvertUtils;

import java.util.Collections;
import java.util.Map;

/**
 * 作者：lizw <br/>
 * 创建时间：2019/09/29 10:23 <br/>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public class HttpUtils {

    private static final org.clever.common.utils.HttpUtils Http_Utils = org.clever.common.utils.HttpUtils.getInner();

    public static final HttpUtils Instance = new HttpUtils();

    private HttpUtils() {
    }

    // --------------------------------------------------------------------------------------------
    // GET请求
    // --------------------------------------------------------------------------------------------

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回字符串
     *
     * @param url     请求url(非空)
     * @param headers 请求头(可选)
     * @param params  Url Query Parameter(可选)
     */
    public String getStr(final String url, final Map<String, String> headers, final Map<String, String> params) {
        return Http_Utils.getStr(url, headers, params);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回字符串
     *
     * @param url    请求url(非空)
     * @param params Url Query Parameter(可选)
     */
    public String getStr(final String url, Map<String, String> params) {
        return Http_Utils.getStr(url, params);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回字符串
     *
     * @param url 请求url(非空)
     */
    public String getStr(final String url) {
        return Http_Utils.getStr(url);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回Map
     *
     * @param url     请求url(非空)
     * @param headers 请求头(可选)
     * @param params  Url Query Parameter(可选)
     */
    public Map getMap(final String url, final Map<String, String> headers, final Map<String, String> params) {
        String json = getStr(url, headers, params);
        return stringToMap(json);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回Map
     *
     * @param url    请求url(非空)
     * @param params Url Query Parameter(可选)
     */
    public Map getMap(final String url, Map<String, String> params) {
        String json = getStr(url, params);
        return stringToMap(json);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回Map
     *
     * @param url 请求url(非空)
     */
    public Map getMap(final String url) {
        String json = getStr(url);
        return stringToMap(json);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回 Js Object
     *
     * @param url     请求url(非空)
     * @param headers 请求头(可选)
     * @param params  Url Query Parameter(可选)
     */
    public Object getObject(final String url, final Map<String, String> headers, final Map<String, String> params) {
        String json = getStr(url, headers, params);
        return stringToMap(json);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回 Js Object
     *
     * @param url    请求url(非空)
     * @param params Url Query Parameter(可选)
     */
    public Object getObject(final String url, Map<String, String> params) {
        String json = getStr(url, params);
        return stringToMap(json);
    }

    /**
     * 使用HTTP GET请求获取数据，支持参数，返回 Js Object
     *
     * @param url 请求url(非空)
     */
    public Object getObject(final String url) {
        String json = getStr(url);
        return stringToMap(json);
    }

    // --------------------------------------------------------------------------------------------
    // POST请求
    // --------------------------------------------------------------------------------------------

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回字符串
     *
     * @param url      请求url(非空)
     * @param headers  请求头(可选)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public String postStr(final String url, final Map<String, String> headers, final Map<String, String> params, String jsonBody) {
        return Http_Utils.postStr(url, headers, params, jsonBody);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回字符串
     *
     * @param url      请求url(非空)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public String postStr(final String url, final Map<String, String> params, String jsonBody) {
        return Http_Utils.postStr(url, params, jsonBody);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回字符串
     *
     * @param url      请求url(非空)
     * @param jsonBody Json Body(非空)
     */
    public String postStr(String url, String jsonBody) {
        return Http_Utils.postStr(url, jsonBody);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回Map
     *
     * @param url      请求url(非空)
     * @param headers  请求头(可选)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public Map postMap(final String url, final Map<String, String> headers, final Map<String, String> params, String jsonBody) {
        String json = postStr(url, params, jsonBody);
        return stringToMap(json);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回Map
     *
     * @param url      请求url(非空)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public Map postMap(final String url, final Map<String, String> params, String jsonBody) {
        String json = postStr(url, params, jsonBody);
        return stringToMap(json);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回Map
     *
     * @param url      请求url(非空)
     * @param jsonBody Json Body(非空)
     */
    public Map postMap(String url, String jsonBody) {
        String json = postStr(url, jsonBody);
        return stringToMap(json);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回 Js Object
     *
     * @param url      请求url(非空)
     * @param headers  请求头(可选)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public Object postObject(final String url, final Map<String, String> headers, final Map<String, String> params, String jsonBody) {
        String json = postStr(url, params, jsonBody);
        return stringToObject(json);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回 Js Object
     *
     * @param url      请求url(非空)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public Object postObject(final String url, final Map<String, String> params, String jsonBody) {
        String json = postStr(url, params, jsonBody);
        return stringToObject(json);
    }

    /**
     * 使用HTTP POST请求获取数据，支持参数，返回 Js Object
     *
     * @param url      请求url(非空)
     * @param jsonBody Json Body(非空)
     */
    public Object postObject(String url, String jsonBody) {
        String json = postStr(url, jsonBody);
        return stringToObject(json);
    }

    // --------------------------------------------------------------------------------------------
    // 其他类型的请求 -> PUT \ DELETE \ HEAD
    // --------------------------------------------------------------------------------------------

    /**
     * 使用HTTP PUT请求获取数据，支持参数，返回字符串
     *
     * @param url      请求url(非空)
     * @param headers  请求头(可选)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public String putStr(final String url, final Map<String, String> headers, final Map<String, String> params, String jsonBody) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(org.clever.common.utils.HttpUtils.MediaType_Json), jsonBody);
        final Request.Builder builder = org.clever.common.utils.HttpUtils.createRequestBuilder(url, headers, params);
        builder.put(requestBody);
        Request request = builder.build();
        return org.clever.common.utils.HttpUtils.executeReturnStr(Http_Utils.getOkHttpClient(), request);
    }

    /**
     * 使用HTTP DELETE请求获取数据，支持参数，返回字符串
     *
     * @param url      请求url(非空)
     * @param headers  请求头(可选)
     * @param params   Url Query Parameter(可选)
     * @param jsonBody Json Body(非空)
     */
    public String deleteStr(final String url, final Map<String, String> headers, final Map<String, String> params, String jsonBody) {
        RequestBody requestBody = null;
        if (StringUtils.isNotBlank(jsonBody)) {
            requestBody = RequestBody.create(MediaType.parse(org.clever.common.utils.HttpUtils.MediaType_Json), jsonBody);
        }
        final Request.Builder builder = org.clever.common.utils.HttpUtils.createRequestBuilder(url, headers, params);
        if (requestBody != null) {
            builder.delete(requestBody);
        } else {
            builder.delete();
        }
        Request request = builder.build();
        return org.clever.common.utils.HttpUtils.executeReturnStr(Http_Utils.getOkHttpClient(), request);
    }

    /**
     * 使用HTTP HEAD请求获取数据，支持参数，返回字符串
     *
     * @param url     请求url(非空)
     * @param headers 请求头(可选)
     * @param params  Url Query Parameter(可选)
     */
    public String headStr(final String url, final Map<String, String> headers, final Map<String, String> params) {
        final Request.Builder builder = org.clever.common.utils.HttpUtils.createRequestBuilder(url, headers, params);
        builder.head();
        Request request = builder.build();
        return org.clever.common.utils.HttpUtils.executeReturnStr(Http_Utils.getOkHttpClient(), request);
    }

    // --------------------------------------------------------------------------------------------
    // 内部方法
    // --------------------------------------------------------------------------------------------

    /**
     * 把Json字符串转换成 Map
     */
    private Map stringToMap(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.EMPTY_MAP;
        }
        JsonWrapper jsonWrapper = new JsonWrapper(json);
        return jsonWrapper.getInnerMap();
    }

    /**
     * 把Json字符串转换成 Js Object
     */
    private Object stringToObject(String json) {
        Map map = stringToMap(json);
        return ObjectConvertUtils.Instance.javaToJSObject(map);
    }
}
