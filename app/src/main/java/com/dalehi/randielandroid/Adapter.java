package com.dalehi.randielandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

/**
 * Created by Dapoer_Kreatif on 05/04/2018.
 */

class Adapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<DataSet> DataList;
    private ImageLoader imageLoader = Controller.getPermission().getImageLoader();

    Adapter(Activity activity, List<DataSet> dataitem) {
        this.activity = activity;
        this.DataList = dataitem;
    }

    @Override
    public int getCount() {
        return DataList.size();
    }

    @Override
    public Object getItem(int location) {
        return DataList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null){
            try{
                if (inflater != null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                }
            } catch (Exception e){
                e.printStackTrace ();
            }
        }
        if (imageLoader == null)
            imageLoader = Controller.getPermission().getImageLoader();
        NetworkImageView thumbNail = null;
        if (convertView != null) {
            thumbNail = (NetworkImageView) convertView
                    .findViewById( R.id.thumbnail);


            thumbNail.setAdjustViewBounds(true);
            thumbNail.setScaleType( ImageView.ScaleType.FIT_CENTER);

            TextView name = (TextView) convertView.findViewById(R.id.name);
            TextView worth = (TextView) convertView.findViewById(R.id.worth);
            TextView source = (TextView) convertView.findViewById(R.id.source);
            TextView year = (TextView) convertView.findViewById(R.id.inYear);

            TextView idPost = (TextView) convertView.findViewById(R.id.idPost);

            DataSet m = DataList.get(position);
            thumbNail.setImageUrl(m.getSourceUrl (), imageLoader);
            name.setText(m.getTitle());
            source.setText( Html.fromHtml(" " + String.valueOf(m.getExcerpt ())));
            worth.setText(String.valueOf(m.getDate ()));
            year.setText(String.valueOf(m.getCategory ()));

            idPost.setText(String.valueOf(m.getId ()));
        }

        return convertView;
    }

}
