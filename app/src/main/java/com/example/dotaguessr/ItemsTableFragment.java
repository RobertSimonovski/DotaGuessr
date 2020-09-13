package com.example.dotaguessr;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;

import static java.lang.Math.max;
import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsTableFragment extends Fragment {

    private DisplayGame activity;

    private ResultGame game;
    private ImageView exampleImageView;

    public ItemsTableFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (DisplayGame)getActivity();
        exampleImageView = new ImageView(activity);
        Picasso.get().load("http://cdn.dota2.com/apps/dota2/images/items/radiance_lg.png").into(exampleImageView);
        new CheckGame().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items_table, container, false);
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

        TableLayout itemsTable = new TableLayout(activity);
        LinearLayout itemRow = itemRow = new TableRow(activity);;
        Item[] items = player.getItems();
        ImageView imageView;
        for (int i=0; i<6; i++){
            if(i == 3) {
                itemsTable.addView(itemRow);
                itemRow = new TableRow(activity);
            }
            imageView = new ImageView(activity);
//            if(items[i].getId() == 0) imageView.setImageResource(R.drawable.empty_item_slot);
            if(items[i].getId() == 0) {
                Picasso.get().load("https://i.imgur.com/4KpOU1T.png").into(imageView);
//                imageView.setLayoutParams(exampleImageView.getLayoutParams());
            }
            else {
                Picasso.get().load(items[i].getIconUrl()).into(imageView);
            }
            itemRow.addView(imageView);

        }
        itemsTable.addView(itemRow);
        int dpValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, activity.getResources().getDisplayMetrics());
        itemsTable.setPadding(0, dpValue, 0, dpValue);
        tableRow.addView(itemsTable);
        return tableRow;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
