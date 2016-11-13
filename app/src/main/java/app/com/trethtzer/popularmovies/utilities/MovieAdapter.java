package app.com.trethtzer.popularmovies.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
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

    public MovieAdapter(Activity context, List<Movie> list){
        super(context,0,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Movie movie = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_gridview_movie,parent,true);

        ImageView iconView = (ImageView) rootView.findViewById(R.id.imageView_item_movie);
        Picasso.with(getContext()).load("http://i.imgur.com/DvpvklR.png").into(iconView);

        return rootView;
    }
}
