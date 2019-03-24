package com.example.hangman;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class LostGame extends AppCompatActivity {
    TextView mDisplayWord;
    ImageView mimageView;
    String word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_game);
        this.setFinishOnTouchOutside(false);
        mDisplayWord = findViewById(R.id.word2);
        Intent intent = getIntent();
        String word = intent.getStringExtra("word");
        this.word = word;
        mDisplayWord.setText(word);
        mimageView = findViewById(R.id.lostImage);
        String imageName="nine";
        Glide.with(this).load(this.getResources().getIdentifier("drawable/" + imageName, null,getPackageName())).into(mimageView);

    }
    public void redirectHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void playAgain(View view) {
        Intent intent = new Intent(this, PlayGame.class);
        startActivity(intent);
    }

    public void knowMore(View view) {
        String urlAsString = "https://www.google.com/search?q="+word;
        openWebPage(urlAsString);
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {

        return;
    }
}
