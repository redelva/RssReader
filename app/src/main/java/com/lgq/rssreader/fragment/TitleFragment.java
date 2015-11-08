package com.lgq.rssreader.fragment;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.lgq.rssreader.R;

public class TitleFragment extends Fragment
{
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    private TextView titleView;

    public void setTitle(String title)
    {
        titleView.setText(title);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_title, container, false);
        titleView = (TextView)view.findViewById(R.id.title);
        titleView.setText(getArguments().getString(TitleFragment.EXTRA_TITLE));
        return view;
    }
}

