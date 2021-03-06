package app.com.trethtzer.popularmovies.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.trethtzer.popularmovies.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Trethtzer on 25/03/2017.
 */

public class VideoAdapter extends ArrayAdapter<String> {

    Context c;

    public VideoAdapter(Activity context, int layoutResourceId, List<String> list){
        super(context,layoutResourceId,list);
        c = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        ViewHolder holder;
        if(convertView != null){
            holder = (ViewHolder) convertView.getTag();
        }else{
            convertView = ((Activity)c).getLayoutInflater().inflate(R.layout.item_listview,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        // String author = getAuthor(getItem(position));
        holder.tx.setText("Trailer nº " + (position + 1));

        return convertView;
    }

    static class ViewHolder{
        @BindView(R.id.item_listView)
        TextView tx;
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}