package com.example.dotaguessr;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlayerRow extends TableRow {
    private static List<PlayerRow> playerRows;
    private final Player player;
    private TextView nameTextView;
    private int backgroundColor, nameColor;
    private static boolean answersRevealed = false;
    public PlayerRow(Context context){
        super(context);
        player = null;
    }
    public PlayerRow(DisplayGame context, Player player) {
        super(context);
        this.player = player;
        nameTextView = new TextView(context);
        if(player.getPlayerSlot() < 100){
            nameColor = getResources().getColor(R.color.radiantGreen);
            if(player.getPlayerSlot() %2 == 0)
                backgroundColor = getResources().getColor(R.color.radiantHeroBackground2);
            else
                backgroundColor = getResources().getColor(R.color.radiantHeroBackground1);
        }
        else{
            nameColor = getResources().getColor(R.color.direRed);
            if(player.getPlayerSlot() %2 == 0)
                backgroundColor = getResources().getColor(R.color.direHeroBackground2);
            else
                backgroundColor = getResources().getColor(R.color.direHeroBackground1);
        }
        setBackgroundColor(backgroundColor);

        ConstraintLayout constraintLayout = new ConstraintLayout(context);

        ImageView imageView = new ImageView(context);
        Picasso.get().load(player.getHero().getIconUrl()).into(imageView);
        imageView.setPadding(0,8, 0, 8);
        imageView.setId(View.generateViewId());
        constraintLayout.addView(imageView);


        nameTextView.setText(answersRevealed?player.getPersona():player.getHero().getName());
        nameTextView.setTextColor(nameColor);

        TextView level = new TextView(context);
        level.setBackgroundColor(Color.parseColor("#B9000000"));
        level.setTextColor(Color.WHITE);
        level.setText(""+player.getLevel());
        level.setId(View.generateViewId());
        level.setPadding(4,4,4,4);
        level.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        constraintLayout.addView(level);

        addView(constraintLayout);
        addView(nameTextView);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(level.getId(), ConstraintSet.END, imageView.getId(), ConstraintSet.END, 0);
        constraintSet.connect(level.getId(), ConstraintSet.BOTTOM, imageView.getId(), ConstraintSet.BOTTOM, 8);
        constraintSet.applyTo(constraintLayout);

        if(!answersRevealed)
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    performClick();


                    if(event.getAction() == MotionEvent.ACTION_DOWN)
                        setBackgroundColor(getResources().getColor(R.color.whiteTransparent));
                    else if(event.getAction() == MotionEvent.ACTION_CANCEL)
                        setBackgroundColor(backgroundColor);
                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                        setBackgroundColor(backgroundColor);
                        context.revealAnswer(player, nameColor);
                    }
                    return true;
                }
            });

        if(playerRows == null)
            playerRows = new ArrayList<>();
        playerRows.add(this);
    }
    public Hero getHero(){ return player.getHero(); }
    public Player getPlayer() { return player; }
    private void revealPersona(){
        nameTextView.setText(player.getPersona());
        setOnTouchListener(null);
    }
    public int getBackgroundColor() { return backgroundColor; }
    public int getNameColor() { return nameColor; }
    public static void revealAnswers(){
        for (PlayerRow playerRow : playerRows)
            playerRow.revealPersona();
        answersRevealed = true;
    }
    public static void hideAnswers(){ answersRevealed = false; }
}
