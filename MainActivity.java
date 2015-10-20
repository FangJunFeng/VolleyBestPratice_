package com.alandy.volleybestpratice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String XML_URL = "http://flash.weather.com.cn/wmaps/xml/china.xml";
    private static final String STR_URL = "http://www.baidu.com";
    private static final String JSON_URL = "http://www.weather.com.cn/data/cityinfo/101010100.html";
    private static final String CUSTOM_JSON_URL = "http://www.weather.com.cn/adat/sk/101010100.html";
    private Button mImageRequestActivity;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageRequestActivity = (Button) findViewById(R.id.bt_image_request_activity);
        mImageRequestActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ImageRequestActivity.class);
                startActivity(intent);
            }
        });

        //StringRequest的用法
        stringRequest();
        //JsonRequest的用法
        jsonRequest();
        //自定义XMLRequest
        XMLRequestParser();
        //自定义GsonRequest
        customGsonRequest();
    }

    /**
     * 一个最基本的HTTP发送与响应的功能，主要就是进行了以下三步操作：
     * 1. 创建一个RequestQueue对象。
     * 2. 创建一个StringRequest对象。
     * 3. 将StringRequest对象添加到RequestQueue里面。
     */
    private void stringRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STR_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d(TAG, s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage());
            }
        }){
            //设置POST参数的方法,可以提交给服务器
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map= new HashMap<String, String>();
                map.put("params1", "value1");
                map.put("params2", "value2");
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    /**
     * JsonRequest也是继承自Request类的，不过由于JsonRequest是一个抽象类，
     * 因此我们无法直接创建它的实例，那么只能从它的子类入手了。
     * JsonRequest有两个直接的子类，JsonObjectRequest和JsonArrayRequest
     */
    private void jsonRequest(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JSON_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG, jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * 自定义XMLRequest
     */
    private void XMLRequestParser(){
        XMLRequest xmlRequest = new XMLRequest(XML_URL, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(), volleyError);
            }
        }, new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser xmlPullParser) {
                try {
                    int eventType = xmlPullParser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT){
                        switch (eventType){
                            case XmlPullParser.START_TAG:
                                String nodeName = xmlPullParser.getName();
                                if ("city".equals(nodeName)){
                                    String quName = xmlPullParser.getAttributeValue(0);
                                    Log.d(TAG,"quName:" + quName);
                                }
                                break;
                        }
                        eventType = xmlPullParser.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(xmlRequest);
    }

    /**
     * 自定义GsonRequest
     */
    private void customGsonRequest(){
        GsonRequest<Weather> weatherGsonRequest = new GsonRequest<Weather>(CUSTOM_JSON_URL, Weather.class, new Response.Listener<Weather>() {
            @Override
            public void onResponse(Weather weather) {
                WeatherInfo weatherInfo = weather.getWeatherinfo();
                Log.d(TAG, "City:" + weatherInfo.getCity());
                Log.d(TAG, "Temp:" + weatherInfo.getTemp());
                Log.d(TAG, "Time:" + weatherInfo.getTime());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, volleyError.getMessage(), volleyError);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(weatherGsonRequest);
    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
