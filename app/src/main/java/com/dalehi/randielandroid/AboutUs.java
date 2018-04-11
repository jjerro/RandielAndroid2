package com.dalehi.randielandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setContentView ( R.layout.activity_about_us );

        Intent intent = getIntent();

        if (toolbar != null && getSupportActionBar () != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
            getSupportActionBar().setTitle("Tentang Aplikasi");
        }

        ImageView imgFb = (ImageView) findViewById(R.id.linkSocialfb);
        ImageView imgYt = (ImageView) findViewById ( R.id.linkSocialYoutube );
        ImageView imgGmail = (ImageView) findViewById ( R.id.linkSocialGmail );

        imgFb.setOnClickListener(new View.OnClickListener () {
            public void onClick(View v) {
                try {
                    ApplicationInfo info = getPackageManager().
                            getApplicationInfo("com.facebook.katana", 0 );
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://group/120418061956765"));
                    startActivity(intent);
                } catch(PackageManager.NameNotFoundException e ) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/120418061956765/")));
                }
            }
        });

        imgYt.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new  Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.youtube");
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCsju1dUyqWBoz39HibxA2TA"));
                startActivity(intent);
            }
        } );

        imgGmail.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new  Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://plus.google.com/104163471097869402658"));
                startActivity(intent);
            }
        } );
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
        AboutUs.super.onBackPressed ();
    }
}
