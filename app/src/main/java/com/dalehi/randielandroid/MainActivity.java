package com.dalehi.randielandroid;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Objects;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String tag = MainActivity.class.getSimpleName();
    private static String BASE_URL = "http://sinodegmit.or.id/wp-json/wp/v2/";
    private List<DataSet> list = new ArrayList<DataSet> ();
    private Adapter adapter;
    private ListView listView;
    int current_page = 1;
    int CatIdUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );

        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener ( toggle );
        toggle.syncState ();

        NavigationView navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( this );

        final ListView listView = (ListView) findViewById ( R.id.list );
        adapter = new Adapter(this, list);
        listView.setAdapter(adapter);
        String url = BASE_URL+"posts?_embed&page=1";

        JsonArrayRequest jsonreq = new JsonArrayRequest (url,
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
                                if (featureImageObj != null) {
                                    String imgObj = featureImageObj.getString ( "source_url" );
                                    dataSet.setSourceUrl ( imgObj );
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
                AlertDialog.Builder add = new AlertDialog.Builder(MainActivity.this);
                add.setMessage(error.getMessage()).setCancelable(true);
                AlertDialog alert = add.create();
                alert.setTitle("Error!!!");
                alert.show();
            }
        });
        Controller.getPermission().addToRequestQueue(jsonreq);

        final Button btnLoadMore = new Button (this);
        btnLoadMore.setText(R.string.load_more);

        listView.addFooterView(btnLoadMore);

        btnLoadMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (arg0 == btnLoadMore) {
                    current_page = current_page + 1;
                    JSONRequest ();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        if (drawer.isDrawerOpen ( GravityCompat.START )) {
            drawer.closeDrawer ( GravityCompat.START );
        } else {
            // super.onBackPressed();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Tutup Aplikasi")
                    .setCancelable(false)
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate ( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId ();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected ( item );
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId ();

        if (id == R.id.nav_categories) {
            AlertCat();
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        drawer.closeDrawer ( GravityCompat.START );
        return true;
    }

    private void AlertCat() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.ic_info);

        builderSingle.setTitle("Piih Kategori:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Berita Hari ini");
        arrayAdapter.add("Liturgi");
        arrayAdapter.add("Khotbah");
        arrayAdapter.add("Suara Gembala");
        arrayAdapter.add("Video");
        arrayAdapter.add("Artikel");

        builderSingle.setNegativeButton("batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
       builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                String strBerita = "Berita Hari ini";
                String strLiturgi = "Liturgi" ;
                String strKhotbah =  "Khotbah";
                String strSuara =  "Suara Gembala" ;
                String strVideo = "Video" ;
                String strArtikel = "Artikel" ;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Objects.equals ( strName, strArtikel )){
                        CatIdUrl=8;
                    } else if (Objects.equals ( strName, strBerita )){
                        CatIdUrl=18;
                    } else if (Objects.equals ( strName, strLiturgi )){
                        CatIdUrl=7;
                    }else if (Objects.equals ( strName, strKhotbah )){
                        CatIdUrl=11;
                    }else if (Objects.equals ( strName, strSuara )){
                        CatIdUrl=12;
                    } else if (Objects.equals ( strName, strVideo )){
                        CatIdUrl=35;
                    }
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT ) {
                    if (strName != null) {
                        if (strName.equals (strArtikel)) {
                            CatIdUrl = 8;
                        } else if (strName.equals ( strBerita )) {
                            CatIdUrl = 18;
                        } else if (strName.equals ( strLiturgi )) {
                            CatIdUrl = 7;
                        } else if (strName.equals ( strKhotbah )) {
                            CatIdUrl = 11;
                        } else if (strName.equals (  strSuara )) {
                            CatIdUrl = 12;
                        } else if (strName.equals ( strVideo )) {
                            CatIdUrl = 35;
                        }
                    }
                }

                Bundle contain = new Bundle();
                contain.putString("catId", String.valueOf ( CatIdUrl ) );
                contain.putString ( "name", strName );

                Intent a  = new Intent(MainActivity.this, Categories.class);
                a.putExtras(contain);
                startActivity(a);
            }
        });
        builderSingle.show();
    }


    private void JSONRequest() {
        String Newurl = BASE_URL+"posts?_embed&page=" + current_page;
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
                                String imgObj = featureImageObj.getString ( "source_url" );
                                dataSet.setSourceUrl (imgObj);


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
                AlertDialog.Builder add = new AlertDialog.Builder(MainActivity.this);
                add.setMessage(error.getMessage()).setCancelable(true);
                AlertDialog alert = add.create();
                alert.setTitle("Error!!!");
                alert.show();
            }
        });
        Controller.getPermission().addToRequestQueue(jsonreq);

    }
    public void buttonClicked(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        if (drawer.isDrawerOpen ( GravityCompat.START )) {
            drawer.closeDrawer ( GravityCompat.START );
        }

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.search_post, null);
        final EditText searchField = alertLayout.findViewById(R.id.search_field);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Pencarian");
        alert.setView(alertLayout);

        alert.setCancelable(true);

        alert.setPositiveButton("Cari", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String query = searchField.getText().toString();

                Bundle contain = new Bundle();
                contain.putString("query", query);

                Intent searchIntent  = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.putExtras(contain);
                startActivity(searchIntent);

                dialog.dismiss ();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }



}
