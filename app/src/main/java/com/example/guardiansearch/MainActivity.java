package com.example.guardiansearch;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final private String API_URL = "https://content.guardianapis.com/search?api-key=851d91c3-0ee4-457c-a1ec-8da8ddd9e8de";
    private ArrayList<NewsItem> elements = new ArrayList<>();
    private MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isTablet = findViewById(R.id.fragmentLocation) != null;

        TextView searchInput = findViewById(R.id.searchInput);
        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener((v) -> {
            if(!TextUtils.isEmpty(searchInput.getText())) {
                GuardianAPI req = new GuardianAPI();
                req.execute(String.format("%s&q=%s", API_URL, searchInput.getText().toString()));
            }
        });

        ListView theList = findViewById(R.id.listView);

        adapter = new MyListAdapter();

        theList.setAdapter(adapter);

        theList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            NewsItem listItem = elements.get(position);
            dataToPass.putString("TITLE", listItem.getTitle());
            dataToPass.putString("SECTION_NAME", listItem.getSectionName());
            dataToPass.putString("URL", listItem.getUrl());
            dataToPass.putString("DATE", listItem.getDate());

            if (isTablet) {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            } else //isPhone
            {
                Intent nextActivity = new Intent(MainActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

    }

    private class MyListAdapter extends BaseAdapter {
        public int getCount() { return elements.size();}

        public NewsItem getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            @SuppressLint("ViewHolder") View newView = inflater.inflate(R.layout.row_layout, parent, false);

            TextView title = newView.findViewById(R.id.newsTitle);
            title.setText( getItem(position).getTitle() );


            return newView;
        }
    }

    public class GuardianAPI extends AsyncTask<String, Integer, ArrayList<NewsItem>> {
        @Override
        protected  ArrayList<NewsItem> doInBackground(String... urls) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(urls[0]);
                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reading:
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String result = sb.toString(); //result is the whole string

                // Convert string to JSON:
                JSONObject json = new JSONObject(result);


                JSONArray results = json.getJSONObject("response").getJSONArray("results");

                ArrayList<NewsItem> list = new ArrayList<>();
                for (int i = 0; i < results.length(); i++) {
                    JSONObject object = results.getJSONObject(i);

                    list.add(new NewsItem(object.getString("id"), object.getString("sectionName"), object.getString("webTitle"), object.getString("webUrl"),object.getString("webPublicationDate")));
                }

                return list;
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(ArrayList<NewsItem> arrayList){
            elements = arrayList;
            adapter.notifyDataSetChanged();
        }

    }

    private static class NewsItem {
        private String id, sectionName, title, url, date;

        public NewsItem(String id, String sectionName, String title, String url, String date) {
            this.id = id;
            this.sectionName = sectionName;
            this.title = title;
            this.url = url;
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSectionName() {
            return sectionName;
        }

        public void setSectionName(String sectionName) {
            this.sectionName = sectionName;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}