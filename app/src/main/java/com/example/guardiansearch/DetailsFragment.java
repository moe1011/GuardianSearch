package com.example.guardiansearch;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class DetailsFragment extends Fragment {
    private Bundle dataFromActvity;
    private AppCompatActivity parentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActvity = getArguments();

        assert dataFromActvity != null;

        String title = dataFromActvity.getString("TITLE");
        String sectionName = dataFromActvity.getString("SECTION_NAME");
        String url = dataFromActvity.getString("URL");
        String date = dataFromActvity.getString("DATE");

        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_details, container, false);

        TextView titleView = result.findViewById(R.id.newsTitle);
        titleView.setText( title );

        TextView sectionNameView = result.findViewById(R.id.newsSectionName);
        sectionNameView.setText( sectionName );

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        TextView dateView = result.findViewById(R.id.newsPublishDate);

        try {
            dateView.setText( dateFormat.parse(date).toLocaleString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView urlView = result.findViewById(R.id.newsURL);
        urlView.setText( url );

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }
}