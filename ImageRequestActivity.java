package com.alandy.volleybestpratice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

/**
 * Created by AlandyFeng on 2015/10/18.
 */
public class ImageRequestActivity extends Activity {
    private static final  String TAG = "ImageRequestActivity";
    private  Button mImageRequest;
    private ImageView mImageView;
    private Button mNetworkImage;
    private NetworkImageView mNetworkImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_request);

        mImageRequest = (Button) findViewById(R.id.btn_imageRequest);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mImageRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //imageRequest();
                imageLoader();
            }
        });

        mNetworkImage = (Button) findViewById(R.id.btn_network_image);
        mNetworkImageView = (NetworkImageView) findViewById(R.id.network_image_view);
        mNetworkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkImageView();

            }
        });

    }

    /**
     * ImageRequest的构造函数接收六个参数，第一个参数就是图片的URL地址。
     * 第二个参数是图片请求成功的回调，这里我们把返回的Bitmap参数设置到ImageView中。
     * 第三第四个参数分别用于指定允许图片最大的宽度和高度，如果指定的网络图片的宽度或高度大于这里的最大值，
     * 则会对图片进行压缩，指定成0的话就表示不管图片有多大，都不会进行压缩。
     * 第五个参数用于指定图片的颜色属性，Bitmap.Config下的几个常量都可以在这里使用，
     * 其中ARGB_8888可以展示最好的颜色属性，每个图片像素占据4个字节的大小，
     * 而RGB_565则表示每个图片像素占据2个字节大小。第六个参数是图片请求失败的回调，
     * 这里我们当请求失败时在ImageView中显示一张默认图片。
     */
    private void imageRequest() {
        ImageRequest imageRequest = new ImageRequest("https://www.baidu.com/img/bdlogo.png", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(ImageRequestActivity.this);
        requestQueue.add(imageRequest);
    }

    /**
     * mageLoader也可以用于加载网络上的图片，并且它的内部也是使用ImageRequest来实现的，
     * 不过ImageLoader明显要比ImageRequest更加高效，
     * 因为它不仅可以帮我们对图片进行缓存，还可以过滤掉重复的链接，避免重复发送请求。
     * 由于ImageLoader用法大致可以分为以下四步：
     * 1. 创建一个RequestQueue对象。
     * 2. 创建一个ImageLoader对象。
     * 3. 获取一个ImageListener对象。
     * 4. 调用ImageLoader的get()方法加载网络上的图片。
     */
    private void imageLoader(){
        RequestQueue requestQueue = Volley.newRequestQueue(ImageRequestActivity.this);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(mImageView, R.mipmap.default_image, R.mipmap.failed_image);
        imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg", imageListener);
    }

    /**
     * NetworkImageView的用法大致可以分为以下五步：
     * 1. 创建一个RequestQueue对象。
     * 2. 创建一个ImageLoader对象。
     * 3. 在布局文件中添加一个NetworkImageView控件。
     * 4. 在代码中获取该控件的实例。
     * 5. 设置要加载的图片地址。
     */
    private void networkImageView(){
        RequestQueue requestQueue = Volley.newRequestQueue(ImageRequestActivity.this);
        ImageLoader imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        mNetworkImageView.setDefaultImageResId(R.mipmap.default_image);
        mNetworkImageView.setErrorImageResId(R.mipmap.ic_launcher);
        mNetworkImageView.setImageUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
                imageLoader);
    }
    private class BitmapCache implements ImageLoader.ImageCache{
        private LruCache<String, Bitmap> mCache;
        public BitmapCache(){
            // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
            int maxMemory = (int) Runtime.getRuntime().maxMemory();
            Log.d(TAG, String.valueOf(maxMemory));
            // 使用最大可用内存值的1/8作为缓存的大小。
            int cacheSize = maxMemory / 8;
            mCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String s) {
            return mCache.get(s);
        }

        @Override
        public void putBitmap(String s, Bitmap bitmap) {
            mCache.put(s, bitmap);
        }
    }
}
