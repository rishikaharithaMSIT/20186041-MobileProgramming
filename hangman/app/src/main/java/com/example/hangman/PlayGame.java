package com.example.hangman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayGame extends AppCompatActivity {
    String word;
    TextView mwordDisplay;
    TextView mhintDisplay;
    String displayWord = "";
    int chances = 0;
    ImageView mimageView;
    int coins = 3;
    private DatabaseReference mDatabase;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        mwordDisplay = findViewById(R.id.wordDisplay);
        mimageView =  findViewById(R.id.imageView);
        mhintDisplay = findViewById(R.id.showHint);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDatabase.child("/");
        System.out.println(myRef);
        loading = new ProgressDialog(this);
        loading.setTitle("Loading Words");
        loading.setMessage("Please wait, while we create the best word for you");
        loading.show();
        loading.setCancelable(false);
        //to retrive data from firebase
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loading.dismiss();
                Map<String, String> wordsMap = (Map<String, String>) dataSnapshot.getValue();
                Object[] crunchifyKeys = wordsMap.keySet().toArray();
                Object key = crunchifyKeys[new Random().nextInt(crunchifyKeys.length)];
                System.out.println(key);
                System.out.println(wordsMap.get(key));
                word = key.toString().toUpperCase();
                displayWord(word);
                String  hint = wordsMap.get(key).replace("\\n"," ");
                hint = hint.replaceAll("\\s+", " ");
                mhintDisplay.setText(hint);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//                mFailToast.makeText(MainActivity.this,"Database Connectivity Failed", Toast.LENGTH_LONG);
//                mFailToast.show();
            }
        });
        //word = getWord();




    }
    private void displayWord(String word){
        int length = word.length();
        String w = "";
        for(int i = 0;i<length;i++){
            w = w + " _";
        }
        displayWord = w;
        mwordDisplay.setText(w);
    }
    private void displayWord(String word, int index, String letter) {
        System.out.println(word+ ", "+index+","+letter);
        int length = displayWord.length();
        index = index + index + 1;
        String w = "";
        for(int i=0;i<length;i++){

            if(i == index){
                w = w + letter;
            } else {
                w = w + displayWord.charAt(i);
            }
        }
        displayWord = w;
        mwordDisplay.setText(displayWord);
    }

//    private String getWord() {
//        Random rand = new Random();
//        int number = rand.nextInt(5);
//        return "HANGMAN";
//    }

    public void checkLetter(View view) {
        //int id = view.getId();
        //System.out.println(id);
        boolean check;
        String letter = view.getResources().getResourceEntryName(view.getId());
        int resID = getResources().getIdentifier(letter, "id", getPackageName());
        Button myBtn = findViewById(resID);
        boolean letterInWord = checkLetterInWord(letter);

        if(letterInWord){
            int index = word.indexOf(letter);
            displayWord(word, index, letter);
            check = checkIfDone();
            if(check){
                setWinGifImage("ten");
                Intent intent = new Intent(this, WinGame.class);
                intent.putExtra("word",word);
                startActivity(intent);

            }
            while (index >= 0) {
                System.out.println(index);
                index = word.indexOf(letter, index + 1);
                displayWord(word, index, letter);
                check = checkIfDone();
                if(check){
                    setWinGifImage("ten");
                    Intent intent = new Intent(this, WinGame.class);
                    intent.putExtra("word",word);
                    startActivity(intent);
                }
            }
        } else {
            chances++;
            if(chances<9){
                String fileName = getFileName(chances);
                setGifImage(fileName);
            }
            if(chances>= 8){
                setGifImage("nine");
                Intent intent = new Intent(this, LostGame.class);
                intent.putExtra("word",word);
                startActivity(intent);
            }
        }
        myBtn.setEnabled(false);

    }

    private boolean checkIfDone() {
        if(!displayWord.contains("_")){
            return true;
        }
        return false;
    }

    private String getFileName(int chances) {
        String name = "";
        switch (chances){
            case 1:
                return "one";
            case 2:
                return "two";
            case 3:
                return "three";
            case 4:
                return "four";
            case 5:
                return "five";
            case 6:
                return "six";
            case 7:
                return "seven";
            case 8:
                return "eight";
        }
        return name;
    }

    private boolean checkLetterInWord(String letter) {
        return word.contains(letter);
    }
    public void setWinGifImage(String imageName) {
        Glide.with(this).load(this.getResources().getIdentifier("drawable/" + imageName, null,getPackageName())).into(mimageView);
    }
    public void setGifImage(String imageName){

        Glide.with(this).load(this.getResources().getIdentifier("drawable/" + imageName, null,getPackageName())).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ((GifDrawable)resource).setLoopCount(1);
                return false;
            }
        }).into(mimageView);
    }

    public void revealLetters(View view) {
        boolean check;
        coins--;
        if(word != null && coins >=0){
            System.out.println(word);
            System.out.println(displayWord);
            int len = word.length();
            String str = displayWord.replaceAll(" ","");
            System.out.println(str);
            for(int i = len-1; i>=0;i--){
                String ch = word.charAt(i)+"";
                if(!str.contains(ch)){
                    int index = word.indexOf(ch);
                    displayWord(word,index,ch);
                    String buttonID = ch;
                    int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                    Button btn = findViewById(resID);
                    btn.setEnabled(false);
                    check = checkIfDone();
                    if(check){
                        setWinGifImage("ten");
                        Intent intent = new Intent(this, WinGame.class);
                        intent.putExtra("word",word);
                        startActivity(intent);
                    }
                    while (index >= 0) {
                        System.out.println(index);
                        index = word.indexOf(ch, index + 1);
                        displayWord(word, index, ch);
                        String buttonnID = ch;
                        int ressID = getResources().getIdentifier(buttonnID, "id", getPackageName());
                        Button btnn = findViewById(ressID);
                        btnn.setEnabled(false);
                        check = checkIfDone();
                        if(check){
                            setWinGifImage("ten");
                            Intent intent = new Intent(this, WinGame.class);
                            intent.putExtra("word",word);
                            startActivity(intent);
                        }
                    }
                    return;
                }
            }
        }
        if(coins <= 0){
            Button b = findViewById(R.id.reveal);
            b.setEnabled(false);
        }

    }
}
