package com.lemon.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.*;
import com.lemon.android.gson.GsonRequest;
import com.lemon.android.model.LoginJsonResponse;
import org.json.JSONObject;

public class HelloAndroidActivity extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(HelloAndroidActivity.this);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        Log.d("Demo", "max memory size is " + maxMemory);
        final LruCache<String, Bitmap> mImageCache = new LruCache<String, Bitmap>(cacheSize);
        mImageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return mImageCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mImageCache.put(url, bitmap);
            }
        });
        demo();
    }

    RequestQueue queue;
    ImageLoader mImageLoader;
    String url = "http://edu.gehealthcare.cn/api/v1/login?mobile=13333333333&password=333333";
    String imageUrl = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.lemon.android.R.menu.main, menu);
        return true;
    }

    private void demo() {
        (findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                ((EditText) findViewById(R.id.editText)).setText(response.toString());
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((EditText) findViewById(R.id.editText)).setText("error response");
                    }
                });

                queue.add(jsonObjRequest);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                GsonRequest gRequest = new GsonRequest<LoginJsonResponse>(Request.Method.GET, url,
                        LoginJsonResponse.class, null, new Response.Listener<LoginJsonResponse>() {

                    @Override
                    public void onResponse(LoginJsonResponse response) {
                        imageUrl = response.userinfo.getAvatar();
                        imageUrl = imageUrl.replace(":8080", "");
                        ((EditText) findViewById(R.id.editText)).setText(imageUrl);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((EditText) findViewById(R.id.editText)).setText("ERROR GSON Response.");
                    }
                });

                queue.add(gRequest);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ImageRequest imgRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap response) {
                        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((EditText) findViewById(R.id.editText)).setText("Error loading");
                    }
                });

                queue.add(imgRequest);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageLoader.ImageListener listener = ImageLoader.getImageListener((ImageView) findViewById(R.id.imageView), R.drawable.ic_launcher, R.drawable.ic_launcher);
                mImageLoader.get(imageUrl, listener);
            }
        });
    }

}

