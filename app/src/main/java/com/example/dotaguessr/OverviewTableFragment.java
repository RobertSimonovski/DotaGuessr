package com.example.dotaguessr;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static java.lang.Integer.*;
import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewTableFragment extends Fragment {

    private DisplayGame activity;

    private ResultGame game;

    private int maxK, minD, maxA, maxGold;

    public OverviewTableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DisplayGame)getActivity();
        new CheckGame().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview_table, container, false);
    }

    private class CheckGame extends AsyncTask<Game, Game, Game> {
        @Override
        protected Game doInBackground(Game... games) {
            try {
                synchronized (activity.lock) {
                    while (activity.getGame() == null)
                        activity.lock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return activity.getGame();
        }

        @Override
        protected void onPostExecute(Game receivedGame) {
            if(isAdded()) {
                game = receivedGame.getResult();
                Player[] players = game.getPlayers();

                maxA = maxK = maxGold = 0;
                minD = MAX_VALUE;

                for(Player p : players){
                    maxA = max(maxA, p.getAssists());
                    minD = min(minD, p.getDeaths());
                    maxK = max(maxK, p.getKills());
                    maxGold = max(maxGold, p.getNetWorth());
                }

                TableLayout tableLayout = activity.findViewById(R.id.radiantTable);
                for (int i = 0; i < 5; i++) {
                    tableLayout.addView(generateRow(players[i]));
                }
                tableLayout = activity.findViewById(R.id.direTable);
                for (int i = 5; i < 10; i++) {
                    tableLayout.addView(generateRow(players[i]));
                }
            }
        }
    }

    private LinearLayout generateRow(Player player){
        PlayerRow tableRow = new PlayerRow(activity, player);

        TextView textView = new TextView(activity);
        textView.setText(Integer.toString(player.getKills()));
        if(player.getKills() == maxK) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(Color.WHITE);
        tableRow.addView(textView);

        textView = new TextView(activity);
        textView.setText(Integer.toString(player.getDeaths()));
        if(player.getDeaths() == minD) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(Color.GRAY);
        tableRow.addView(textView);

        textView = new TextView(activity);
        textView.setText(Integer.toString(player.getAssists()));
        if(player.getAssists() == maxA) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(Color.WHITE);
        tableRow.addView(textView);

        textView = new TextView(activity);
        String nw = String.format("%.1fk", player.getNetWorth()/1000.0);
        if(nw.endsWith(".0k")) nw = nw.substring(0, nw.indexOf(".")) + "k";
        textView.setText(nw);
        if(player.getNetWorth() == maxGold) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(getResources().getColor(R.color.goldOrange));
        tableRow.addView(textView);

//        imageView = new ImageView(activity);
//        imageView.setImageDrawable(getResources().getDrawable(R.drawable.coins, activity.getApplicationContext().getTheme()));
//        tableRow.addView(imageView);

        //tableRow.setOnClickListener();

        return tableRow;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
