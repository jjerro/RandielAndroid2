package com.dalehi.randielandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dalehi.randielandroid.PostDetails.BASE_URL;


public class Categories extends AppCompatActivity {

    private List<DataSet> list = new ArrayList<DataSet> ();
    private Adapter adapter;

    String catId, catName;
    Integer current_page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setContentView ( R.layout.activity_categories );

        if (toolbar != null && getSupportActionBar () != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            getSupportActionBar().setTitle(catName);
        }

        Bundle bundle = getIntent ().getExtras ();

        if (bundle != null) {
            catId = bundle.getString ( "catId" );
            catName = bundle.getString ( "name" );
        }



        final ListView listView = (ListView) findViewById ( R.id.list );
        adapter = new Adapter(this, list);
        listView.setAdapter(adapter);


        JSONCatReq ();

        final Button btnLoadMore = new Button (this);
        btnLoadMore.setText(R.string.load_more);

        listView.addFooterView(btnLoadMore);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (arg0 == btnLoadMore) {
                    current_page = current_page + 1;
                    JSONCatReq ();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener () {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                DataSet post =(DataSet) list.get(position);
                String idPost = String.valueOf ( post.getId (  ));
                Intent intent = new Intent(getApplicationContext (), PostDetails.class);
                intent.putExtra("idPost", idPost);
                startActivity(intent);
            }
        });

    }

    private void JSONCatReq() {
        String Newurl = BASE_URL+"posts?_embed&categories="+catId+"&page=" + current_page;
        JsonArrayRequest jsonreq = new JsonArrayRequest (Newurl,
                new Response.Listener<JSONArray>() {
                    @SuppressLint({"TimberArgCount", "SimpleDateFormat"})
                    @Override
                    public void onResponse(final JSONArray response) {
                        for (int i = 0; i < response.length(); i++)
                            try {
                                JSONObject obj = response.getJSONObject ( i );
                                final DataSet dataSet = new DataSet ();

                                JSONObject excerptObj = obj.getJSONObject ( "excerpt" );
                                JSONObject titleObj = obj.getJSONObject ( "title" );

                                JSONObject featureImage = obj.getJSONObject ( "_embedded" );
                                JSONArray featureImageUrl = featureImage.getJSONArray ( "wp:featuredmedia" );
                                JSONObject featureImageObj = featureImageUrl.getJSONObject ( 0 );

                                if (featureImageObj != null){
                                    String imgObj = featureImageObj.getString ( "source_url" );
                                    dataSet.setSourceUrl (imgObj);
                                } else{
                                    dataSet.setSourceUrl ( "http://sinodegmit.or.id/wp-content/uploads/2018/03/android-icon.png" );
                                }

                                JSONObject categoryObj = obj.getJSONObject ( "_embedded" );
                                JSONArray categoryUrl = categoryObj.getJSONArray ( "wp:term" );
                                JSONArray catObjectParent = categoryUrl.getJSONArray ( 0 );
                                JSONObject catObj = catObjectParent.getJSONObject ( 0 );
                                String catName = catObj.getString ( "name" );
                                dataSet.setCategory ( catName );


                                String date = String.valueOf ( obj.getString ( "date" ) );

                                SimpleDateFormat smf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                ParsePosition pos = new ParsePosition (0);
                                Date dt=smf.parse(date, pos);
                                smf= new SimpleDateFormat("dd-MMM-yyyy");
                                String newDate=smf.format(dt);

                                dataSet.setDate (newDate );

                                dataSet.setTitle ( titleObj.getString ( "rendered" ) );
                                dataSet.setExcerpt ( excerptObj.getString ( "rendered" ) );
                                dataSet.setId ( obj.getInt ( "id" ) );


                                list.add ( dataSet );
                            } catch (JSONException e) {
                                e.printStackTrace ();
                            }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(getApplicationContext (), "Semua Post Sudah tampil!",
                            Toast.LENGTH_LONG)
                            .show();
            }
        });
        Controller.getPermission().addToRequestQueue(jsonreq);
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
        Categories.super.onBackPressed ();
    }
}
