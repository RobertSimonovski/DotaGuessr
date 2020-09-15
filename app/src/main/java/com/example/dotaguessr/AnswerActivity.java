package com.example.dotaguessr;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.squareup.picasso.Picasso;

public class AnswerActivity extends AppCompatActivity {

    private boolean answered = false;
    long guessedID, correctID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_answer);
        ImageView imageView = findViewById(R.id.answerImageView);
        Picasso.get().load(intent.getStringExtra("heroIconUrl")).into(imageView);
        imageView.setPadding(0,8, 0, 8);
        imageView.setId(View.generateViewId());

        TextView textView = findViewById(R.id.answerTextView);
        textView.setText(intent.getStringExtra("heroName"));
        textView.setTextColor(intent.getIntExtra("nameColor", -1));

        findViewById(R.id.linearLayoutButtons).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(answered)
            PlayerRow.revealAnswers();
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void yesBtn(View view){
        answered = true;
        findViewById(R.id.areYouSureTextView).setVisibility(View.GONE);
        findViewById(R.id.buttonYes).setVisibility(View.GONE);
        findViewById(R.id.buttonNo).setVisibility(View.GONE);

        Intent intent = getIntent();
        TextView textView = findViewById(R.id.correctTextView);
        guessedID = intent.getLongExtra("guessedID", -1);
        correctID = intent.getLongExtra("correctID", -2);

        Log.d("ANSWER", "yesBtn: correctID = " + correctID + " guessedID = " + guessedID);

        if(guessedID == correctID)
            textView.setText("Correct!");
        else{
            textView.setText("Incorrect!");
            textView.setTextColor(getResources().getColor(R.color.direRed));
        }
        findViewById(R.id.linearLayoutButtons).setVisibility(View.VISIBLE);
    }

    public void noBtn(View view){
        onBackPressed();
    }

    public void quitBtn(View view){
        PlayerRow.hideAnswers();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void backBtn(View view){
        onBackPressed();
    }

    public void nextBtn(View view){
        PlayerRow.hideAnswers();
        Intent intent = new Intent(this, DisplayGame.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("playerID", correctID);
        //intent.putExtra("matchID", MatchHistoryRepository.getInstance().getRandomID());
        startActivity(intent);
        finish();
    }
}
