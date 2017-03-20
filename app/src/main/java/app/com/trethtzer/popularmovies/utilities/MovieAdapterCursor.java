package app.com.trethtzer.popularmovies.utilities;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.fragment.MainActivityFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Trethtzer on 13/11/2016.
 */

public class MovieAdapterCursor extends CursorAdapter{

    public MovieAdapterCursor(Activity context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gridview_movie,viewGroup,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        Picasso.with(context).load(cursor.getString(MainActivityFragment.COL_POSTER_PATH)).into(viewHolder.image);
    }

    static class ViewHolder{
        @BindView(R.id.imageView_item_movie) ImageView image;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
