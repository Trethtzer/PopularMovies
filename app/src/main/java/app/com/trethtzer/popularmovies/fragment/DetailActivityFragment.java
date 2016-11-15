package app.com.trethtzer.popularmovies.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.com.trethtzer.popularmovies.R;
import app.com.trethtzer.popularmovies.utilities.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private String nameClass = "DetailActivityFragment";

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        Movie m = (Movie) bundle.getSerializable("movie");

        TextView tv1 = (TextView) rootView.findViewById(R.id.title);
        tv1.setText(m.getTitle());
        TextView tv2 = (TextView) rootView.findViewById(R.id.releaseDate);
        tv2.setText(m.getReleaseDate());
        TextView tv3 = (TextView) rootView.findViewById(R.id.rate);
        tv3.setText("Vote average: " + m.getVote_average());
        TextView tv4 = (TextView) rootView.findViewById(R.id.synopsis);
        tv4.setText(m.getOverview());

        ImageView iv = (ImageView) rootView.findViewById(R.id.imageView_poster_detail);
        Picasso.with(getActivity()).load(m.getUrl()).into(iv);


        return rootView;
    }
}
