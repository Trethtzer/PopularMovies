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
import butterknife.BindView;
import butterknife.ButterKnife;



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

        ViewHolder holder;
        if(convertView != null){
            holder = (ViewHolder) convertView.getTag();
        }else{
            convertView = ((Activity)c).getLayoutInflater().inflate(R.layout.item_gridview_movie,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Movie movie = getItem(position);
        Picasso.with(c)
                .load(movie.getPosterPath())
                .into(holder.image);

        return convertView;
    }

    static class ViewHolder{
        @BindView(R.id.imageView_item_movie) ImageView image;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}