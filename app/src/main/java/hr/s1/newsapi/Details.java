package hr.s1.newsapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {
    TextView tvdesc,tvtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String mdesc,irl,title;
        ImageView imageView= findViewById(R.id.imageview);
        tvdesc = findViewById(R.id.opis);
        tvtitle = findViewById(R.id.titleTV);

        Intent intent = getIntent();
        mdesc = intent.getStringExtra("desc");
        irl = intent.getStringExtra("irl");
        title = intent.getStringExtra("title");

        Picasso.get().load(irl).into(imageView);

        tvdesc.setText(mdesc);
        tvtitle.setText(title);
    }
}