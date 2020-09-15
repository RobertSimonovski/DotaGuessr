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

import static java.lang.Math.max;
import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 */
public class FarmTableFragment extends Fragment {

    private int maxLh, maxDn, maxGpm, maxXpm;

    private DisplayGame activity;

    private ResultGame game;

    public FarmTableFragment() {
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
        return inflater.inflate(R.layout.fragment_farm_table, container, false);
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
                maxLh = maxDn = maxGpm = maxXpm = 0;
                for (Player p : players){
                    maxLh = max(maxLh, p.getLast_hits());
                    maxDn = max(maxDn, p.getDenies());
                    maxGpm = max(maxGpm, p.getGoldPerMin());
                    maxXpm = max(maxXpm, p.getXpPerMin());
                }

                int direPlayers = 0, radiantPlayers = 0;
                for (Player p : players){
                    if(p.getPlayerSlot() < 100)
                        radiantPlayers++;
                    else
                        direPlayers++;
                }

                TableLayout tableLayout = activity.findViewById(R.id.radiantTable);
                for (int i = 0; i < radiantPlayers; i++) {
                    tableLayout.addView(generateRow(players[i]));
                }
                tableLayout = activity.findViewById(R.id.direTable);
                for (int i = radiantPlayers; i < radiantPlayers + direPlayers; i++) {
                    tableLayout.addView(generateRow(players[i]));
                }
            }
        }
    }

    private LinearLayout generateRow(Player player){
        PlayerRow tableRow = new PlayerRow(activity, player);

        TextView textView = new TextView(activity);
        textView.setText(Integer.toString(player.getLast_hits()));
        textView.setTextColor(Color.WHITE);
        if(player.getLast_hits() == maxLh) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tableRow.addView(textView);

        textView = new TextView(activity);
        textView.setText(Integer.toString(player.getDenies()));
        textView.setTextColor(Color.GRAY);
        if(player.getDenies() == maxDn) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tableRow.addView(textView);

        textView = new TextView(activity);
        textView.setText(Integer.toString(player.getGoldPerMin()));
        textView.setTextColor(Color.WHITE);
        if(player.getGoldPerMin() == maxGpm) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tableRow.addView(textView);

        textView = new TextView(activity);
        textView.setText(Integer.toString(player.getXpPerMin()));
        textView.setTextColor(Color.GRAY);
        if(player.getXpPerMin() == maxXpm) textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tableRow.addView(textView);

        return tableRow;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
