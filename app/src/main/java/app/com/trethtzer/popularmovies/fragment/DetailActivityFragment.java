package app.com.trethtzer.popularmovies.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.trethtzer.popularmovies.R;

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
        String s = getActivity().getIntent().getStringExtra("title");
        TextView tv = (TextView) rootView.findViewById(R.id.textView);
        tv.setText(s);

        return rootView;
    }
}
