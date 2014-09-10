package com.lemon.android.gson;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

/**
 * Created by lemon on 14-9-10.
 */
public class GsonRequest<T> extends Request<T> {
    private Gson mGson;
    private Class mJavaClass;
    private Response.Listener<T> mListener;

    public GsonRequest(int method, String url, Class<T> cls, String requestBody,
                       Response.Listener<T> slistener,
                       Response.ErrorListener listener) {
        super(method, url, listener);
        mJavaClass = cls;
        mListener = slistener;
        mGson = new Gson();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            T parsedGSON = (T) mGson.fromJson(jsonString, mJavaClass);
            return Response.success(parsedGSON, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
