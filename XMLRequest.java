package com.alandy.volleybestpratice;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by AlandyFeng on 2015/10/19.
 * A canned request for retrieving the response body at a given URL as a XmlPullParser.
 */
public class XMLRequest extends Request<XmlPullParser> {
    private final Response.Listener<XmlPullParser> mListener;

    /**
     * Creates a new GET request.
     * @param url URL to fetch the string at
     * @param errorListener Error listener, or null to ignore errors
     * @param listener Listener to receive the XmlPullParser response
     */
    public XMLRequest(String url, Response.ErrorListener errorListener, Response.Listener<XmlPullParser> listener) {
        this(Method.GET, url, errorListener, listener);
    }

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the XmlPullParser response
     * @param errorListener Error listener, or null to ignore errors
     */
    public XMLRequest(int method, String url, Response.ErrorListener errorListener, Response.Listener<XmlPullParser> listener) {
        super(method, url, errorListener);
        mListener = listener;
    }


    @Override
    protected Response<XmlPullParser> parseNetworkResponse(NetworkResponse networkResponse) {
        try {
            String xmlString = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlString));
            return Response.success(xmlPullParser, HttpHeaderParser.parseCacheHeaders(networkResponse));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (XmlPullParserException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(XmlPullParser xmlPullParser) {
        mListener.onResponse(xmlPullParser);

    }


}
