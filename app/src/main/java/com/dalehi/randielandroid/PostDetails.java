package com.dalehi.randielandroid;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Dapoer_Kreatif on 09/04/2018.
 */

public class PostDetails extends AppCompatActivity{
    WebView webView;

    final static String BASE_URL ="http://sinodegmit.or.id/wp-json/wp/v2/posts/";
    String newId="";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setContentView(R.layout.post_details);
        if (toolbar != null && getSupportActionBar () != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        webView = (WebView) findViewById ( R.id.content );

        webView.getSettings ().setJavaScriptEnabled ( true );
        webView.getSettings ().setUseWideViewPort ( true );
        webView.getSettings ().setAppCacheEnabled ( true );
        webView.getSettings().setLoadWithOverviewMode(true);

        WebSettings webSettings = webView.getSettings ();
        webSettings.setJavaScriptEnabled ( true );
        webSettings.setRenderPriority ( WebSettings.RenderPriority.HIGH );
        webSettings.setLayoutAlgorithm ( WebSettings.LayoutAlgorithm.NARROW_COLUMNS );
        webSettings.setCacheMode ( WebSettings.LOAD_CACHE_ELSE_NETWORK );

        Bundle bundle = getIntent ().getExtras ();

        if (bundle != null) {
            newId = bundle.getString ( "idPost" );
        }
        String newUrl = BASE_URL+newId;


        final TextView textView = (TextView)findViewById ( R.id.postTitle );

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                ( Request.Method.GET, newUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                            try{
                                JSONObject titleObj = response.getJSONObject ( "title" );
                                String titleString = titleObj.getString ( "rendered" );
                                String linkObj = response.getString ( "link" );

                                getSupportActionBar().setTitle(titleString);
                                if (savedInstanceState == null) {
                                    webView.loadUrl ( linkObj );
                                }
                            } catch (Exception e){
                                e.printStackTrace ();
                            }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder add = new AlertDialog.Builder(PostDetails.this);
                add.setMessage(error.getMessage()).setCancelable(true);
                AlertDialog alert = add.create();
                alert.setTitle("Error!!!");
                alert.show();
            }
        });
        Controller.getPermission().addToRequestQueue(jsonObjectRequest);

        webView.setWebViewClient ( new WebViewClient () {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals("facebook.com")
                        ||Uri.parse(url).getHost().contains("/sharer/sharer.php?u=")) {
                    try {
                        ApplicationInfo info = getPackageManager().
                                getApplicationInfo("com.facebook.katana", 0 );
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    } catch(PackageManager.NameNotFoundException e ) {
                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        e.printStackTrace ();
                    }
                } else {
                    view.loadUrl ( url );
                    return false;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        } );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        PostDetails.super.onBackPressed ();
    }
}
