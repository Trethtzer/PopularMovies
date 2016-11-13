package app.com.trethtzer.popularmovies.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.trethtzer.popularmovies.R;

/**
 * Created by Trethtzer on 13/11/2016.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    Context c;

    public MovieAdapter(Activity context,int layoutResourceId, List<Movie> list){
        super(context,layoutResourceId,list);
        c = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);
        View rootView = ((Activity)c).getLayoutInflater().inflate(R.layout.item_gridview_movie,parent,false);

        ImageView iconView = (ImageView) rootView.findViewById(R.id.imageView_item_movie);
//        Picasso.with(c).load("http://i.imgur.com/DvpvklR.png").into(iconView);
        iconView.setImageResource(R.drawable.imagen);

        return rootView;
    }
}
