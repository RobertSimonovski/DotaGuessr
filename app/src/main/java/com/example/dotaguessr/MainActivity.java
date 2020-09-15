package com.example.dotaguessr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private long playerID;
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
        sharedPreferences = getSharedPreferences(this.getLocalClassName(), Context.MODE_PRIVATE);
        playerID = sharedPreferences.getLong("playerID", -1);
        setViews();

        MatchHistoryViewModel myViewModel =
                new ViewModelProvider(this, new MatchHistoryViewModelFactory(this.getApplication(), playerID))
                        .get(MatchHistoryViewModel.class);
    }
    public void play(View view){
        if(playerID != -1){
            Intent intent = new Intent(this, DisplayGame.class);
            intent.putExtra("playerID", playerID);
            startActivity(intent);
        }
    }
    public void enterID(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Dota friend ID (Not Steam ID!)");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        if(sharedPreferences.contains("playerID"))
            input.setText("" + sharedPreferences.getLong("playerID", -1));
        builder.setView(input);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (text.isEmpty()) {
                    editor.remove("playerID");
                    playerID = -1;
                }
                else {
                    try {
                        playerID = Long.decode(text);
                        editor.putLong("playerID", playerID);
                    } catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                setViews();
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void setViews(){
        //sharedPreferences = getSharedPreferences(this.getLocalClassName(), Context.MODE_PRIVATE);
        Log.d("TAG", "setViews: "+ sharedPreferences.contains("playerID"));
        TextView textView = findViewById(R.id.connectedTextView);
        Button button = findViewById(R.id.enterIDButton);
        if(playerID == -1){
            //findViewById(R.id.friendIDTextView).setVisibility(View.INVISIBLE);
            findViewById(R.id.friendIDTV).setVisibility(View.INVISIBLE);
            textView.setText("No saved ID:");
            button.setText("Enter ID");
        }
        else{
            //findViewById(R.id.friendIDTextView).setVisibility(View.VISIBLE);
            textView = findViewById(R.id.friendIDTV);
            textView.setVisibility(View.VISIBLE);
            textView.setText("" + playerID);
            button.setText("Change ID");
            textView.setText("Saved ID:");
        }
    }
}
