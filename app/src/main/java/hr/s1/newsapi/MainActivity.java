package hr.s1.newsapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    SwipeRefreshLayout swipeLayout;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    static final String KEY_TITLE = "title";
    static final String KEY_DESCRIPTION = "description";
    static final String KEY_URL = "url";
    static final String KEY_URLTOIMAGE = "urlToImage";
//  String API_KEY = "";
    //mogu dodat gitignore za gradleprop
    String API_KEY = BuildConfig.APIKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String news_url = "https://newsapi.org/v2/top-headlines?country=us&apiKey="+API_KEY;

        listView =findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, Details.class);
                i.putExtra("desc", dataList.get(+position).get(KEY_DESCRIPTION));
                i.putExtra("irl", dataList.get(+position).get(KEY_URLTOIMAGE));
                i.putExtra("title", dataList.get(+position).get(KEY_TITLE));
                startActivity(i);
            }
        });
        new MainActivity.AsyncHttpTask().execute(news_url);

        swipeLayout = findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("Swipe", "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        new MainActivity.AsyncHttpTask().execute(news_url);
                    }
                }
        );
    }

    public class AsyncHttpTask extends AsyncTask <String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Api-Key", API_KEY);
                String response = streamTostring(urlConnection.getInputStream());
                return response;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute (String result){
            parseResult(result);
        }
    }

    String streamTostring (InputStream stream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String data;
        String result = "";
        while ((data=bufferedReader.readLine()) != null){
            result += data;
        }
        if(null != stream){
            stream.close();
        }
        return result;
    }

    private void parseResult(String result){
        JSONObject response;
        ArrayList<String> mylist = new ArrayList<>();

        try {
            response = new JSONObject(result);
            JSONArray articles = response.optJSONArray("articles");

            for (int i=0; i<articles.length();i++){
            JSONObject article=articles.optJSONObject(i);
            String title2 = article.optString("title");
            String image = article.optString("urlToImage");
            String desc = article.optString("description");

                mylist.add(title2);

                HashMap<String, String> map = new HashMap<>();
                map.put(KEY_TITLE, article.optString(KEY_TITLE));
                map.put(KEY_DESCRIPTION, article.optString(KEY_DESCRIPTION));
                map.put(KEY_URL, article.optString(KEY_URL));
                map.put(KEY_URLTOIMAGE, article.optString(KEY_URLTOIMAGE));
                dataList.add(map);

            Log.i("Titles",title2);
            Log.i("image",image);
            Log.i("desc",desc);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,mylist);
       listView.setAdapter(adapter);
       swipeLayout.setRefreshing(false);
    }
}
