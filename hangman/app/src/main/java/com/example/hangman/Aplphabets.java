package com.example.hangman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Aplphabets extends BaseAdapter {
    private String[] letters;
    private Context mcontext;
    public Aplphabets(Context context, String[] letters) {
        this.mcontext = context;
        this.letters = letters;
    }

    @Override
    public int getCount() {
        return letters.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return letters[position];
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String letter = letters[position];
        if(convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
            convertView = layoutInflater.inflate(R.layout.lettersview, null);
        }
        final TextView letterview = convertView.findViewById(R.id.letter);
        letterview.setText(letter);
        return convertView;
    }
}
