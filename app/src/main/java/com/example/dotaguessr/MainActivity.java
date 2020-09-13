package com.example.dotaguessr;

import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Item.setItems(getResources().openRawResource(R.raw.items));
            Hero.setHeroes(getResources().openRawResource(R.raw.heroes));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editText = findViewById(R.id.enterMatchId);
        editText.setText(R.string.exampleMatchId);
    }
    public void play(View view){
        editText = findViewById(R.id.enterMatchId);
        if(editText.getText().toString().matches("[0-9]+")){
            Intent intent = new Intent(this, DisplayGame.class);
            intent.putExtra("playerID", Long.decode(editText.getText().toString()));
            startActivity(intent);
        }
    }
}
