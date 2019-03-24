package com.example.hangman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HowToPlay extends AppCompatActivity {
    TextView mhowToplay;
    TextView mhead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        mhowToplay = findViewById(R.id.howToPlay);
        mhead =  findViewById(R.id.head);
        mhead.setText("HOW TO PLAY");
        mhowToplay.setText("Hangman is a classic word game in which you must guess the secret word one letter at a time.Guess one letter at a time to reveal the secret word.\n" +
                "Each incorrect guess adds another part to the hangman. You only get 8 incorrect guesses.\nHint is displayed for word. Reveal word is available for revealing letters.");
    }
}
