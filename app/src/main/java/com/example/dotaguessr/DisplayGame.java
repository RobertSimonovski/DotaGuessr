package com.example.dotaguessr;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Bundle;
import androidx.fragment.app.*;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.tabs.TabLayout;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DisplayGame extends FragmentActivity {
    private Game game;
    public final Object lock = new Object();
    TabLayout tabLayout;
    private long playerID;
    private MatchHistoryViewModel matchHistory;

    public Game getGame() { return game; }
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game);

        tabLayout = findViewById(R.id.tabLayout);

//        final ViewPager viewPager = findViewById(R.id.viewPager);
//        final PagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
//
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

        final LinearLayout fragmentContainer = findViewById(R.id.fragment_container);
        if (fragmentContainer.getChildCount() == 0)
            replaceFragment(new OverviewTableFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        replaceFragment(new OverviewTableFragment());
                        break;
                    case 1:
                        replaceFragment(new FarmTableFragment());
                        break;
                    case 2:
                        replaceFragment(new ItemsTableFragment());
                        break;
//                    case 3:
//                        replaceFragment(new DamageTableFragment());
//                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        matchHistory = ViewModelProviders.of(this).get(MatchHistoryViewModel.class);
        if(!matchHistory.setMatchHistory(getIntent().getLongExtra("playerID", -1))){
            failedCallMessage();
            onBackPressed();
        }
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                try{
                    synchronized (matchHistory.getLock()){
                        while(matchHistory.getMatchHistory() == null)
                            matchHistory.getLock().wait();
                    }
                } catch (InterruptedException e) { e.printStackTrace(); }

                return matchHistory.getRandomMatch();
            }

            @Override
            protected void onPostExecute(Long aLong) {
                getGameData(aLong);
            }
        }.execute();
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        transaction.commit();
    }

    private void failedCallMessage(){
        DialogFragment dialog = new FailedCallFragment();
        dialog.show(getSupportFragmentManager(),"failed call");
    }

    private void getGameData(long matchID){
        //Intent intent = getIntent();
        //String matchID = intent.getStringExtra("matchID");

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.steampowered.com/IDOTA2Match_570/")
                .addConverterFactory(GsonConverterFactory.create())
                //.client(okHttpClient)
                .build();
        SteamWebApi steamWebApi = retrofit.create(SteamWebApi.class);

        Call<Game> call = steamWebApi.GetMatchDetails(getString(R.string.SteamWebAPIKey), matchID, 1);
        call.enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if(!response.isSuccessful()){
                    failedCallMessage();
                    return;
                }
                synchronized (lock) {
                    game = response.body();
                    lock.notifyAll();
                }

                assert game != null;
                ResultGame result = game.getResult();

                TextView view;

                view = findViewById(R.id.winner);
                if(result.getRadiantWin()){
                    view.setText(R.string.RadiantWin);
                    view.setTextColor(getResources().getColor(R.color.radiantGreen));
                }
                else{
                    view.setText(R.string.DireWin);
                    view.setTextColor(getResources().getColor(R.color.direRed));
                }

                view = findViewById(R.id.direKills);
                view.setText(Integer.toString(result.getDireKills()));

                view = findViewById(R.id.radiantKills);
                view.setText(Integer.toString(result.getRadiantKills()));

                view = findViewById(R.id.matchDuration);
                view.setText(formatTime(result.getDuration()));

                view = findViewById(R.id.tvGameMode);
                view.setText(result.getGameMode());

                view = findViewById(R.id.tvRegion);
                view.setText(result.getRegion());

                drawMap();

            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                Log.d("DisplayGame", "Entering onFailure(): Message: " + t.getLocalizedMessage());
                failedCallMessage();
            }
        });
    }

    private String formatTime(long totalSeconds){
        int hours = 0, minutes = 0, seconds = 0;
        StringBuilder duration = new StringBuilder("");

        hours = (int)(totalSeconds / 3600);
        minutes = (int)((totalSeconds % 3600) / 60);
        seconds = (int)(totalSeconds - hours*3600 - minutes*60);

        if(hours > 0) {
            duration.append(hours);
            duration.append(":");
        }
        duration.append(minutes);
        duration.append(":");
        duration.append(seconds);

        return duration.toString();
    }
    private void drawMap(){
        Bitmap mapBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dota_map);
        Bitmap tempBitmap = Bitmap.createBitmap(mapBitmap.getWidth(), mapBitmap.getHeight(), mapBitmap.getConfig());
        Canvas canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(mapBitmap, 0, 0, null);
        Paint paint = new Paint();

        Bitmap direTowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dire_tower);
        direTowerBitmap = Bitmap.createScaledBitmap(direTowerBitmap, 80, 80, false);
        Bitmap direBarracksBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dire_barracks);
        Bitmap direAncientBitmap = Bitmap.createScaledBitmap(direBarracksBitmap, 90, 70, false);
        direBarracksBitmap = Bitmap.createScaledBitmap(direBarracksBitmap, 50, 40, false);
        Bitmap radiantTowerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radiant_tower);
        radiantTowerBitmap = Bitmap.createScaledBitmap(radiantTowerBitmap, 80, 80, false);
        Bitmap radiantBarracksBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.radiant_barracks);
        Bitmap radiantAncientBitmap = Bitmap.createScaledBitmap(radiantBarracksBitmap, 90, 70, false);
        radiantBarracksBitmap = Bitmap.createScaledBitmap(radiantBarracksBitmap, 50, 40, false);


        ResultGame result = game.getResult();
        Building[] direTowers = Building.buildBuildings(result.getDireTowers(), direTowerBitmap, new int[]{120,50, 590,50, 950,50, 700,550, 850,400, 1000,250, 1200,800, 1225,550, 1225,370, 1140,90, 1180,110});
        Building[] direBarracks = Building.buildBuildings(result.getDireBarracks(), direBarracksBitmap, new int[]{1010,100, 1010,60, 1070,285, 1050,250, 1270,360, 1220,360});
        Building[] radiantTowers = Building.buildBuildings(result.getRadiantTowers(), radiantTowerBitmap, new int[]{40,400, 40,700, 20,925, 500,750, 350,900, 215,1015, 1100,1175, 600,1175, 300,1200, 80,1115, 120,1145});
        Building[] radiantBarracks = Building.buildBuildings(result.getRadiantBarracks(), radiantBarracksBitmap, new int[]{285,1250, 285,1225, 220,1065, 200,1055, 60,970, 10,970});
        Building direAncient = new Building(1200, 100, direAncientBitmap, !result.getRadiantWin());
        Building radiantAncient = new Building(60, 1160, radiantAncientBitmap, result.getRadiantWin());

        Building.drawBarracks(direBarracks,Building.Type.RANGED, Building.Side.DIRE, canvas, paint);
        direBarracks[4].draw(canvas, paint);
        Building.drawTowers(direTowers, canvas, paint, direAncient);
        Building.drawBarracks(direBarracks, Building.Type.MELEE, Building.Side.DIRE, canvas, paint);

        Building.drawBarracks(radiantBarracks,Building.Type.RANGED, Building.Side.RADIANT, canvas, paint);
        Building.drawTowers(radiantTowers, canvas, paint, radiantAncient);
        radiantBarracks[5].draw(canvas, paint);
        Building.drawBarracks(radiantBarracks, Building.Type.MELEE, Building.Side.RADIANT, canvas, paint);


        ImageView imageView = findViewById(R.id.imageViewMap);
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }
    private static class Building {
        enum Side {RADIANT, DIRE};
        enum Type {RANGED(1), MELEE(0);
            int value;
            Type(int i) { value = i; }
        }
        private static final int[] TOWER_STATUS_KEY = {1, 2, 4, 8, 16, 32, 64 , 128, 256, 512, 1024};
        int x, y;
        int alpha;
        Bitmap shape;

        Building (int x, int y, Bitmap shape, boolean alive){
            this.x = x;
            this.y = y;
            this.shape = shape;
            alpha = alive?255:100;
        }

        static Building[] buildBuildings(int status, Bitmap shape, int[] coordinates){
            Building[] buildings = new Building[coordinates.length/2];
            for (int i=0; i<coordinates.length; i+=2){
                buildings[i/2] = new Building(coordinates[i], coordinates[i+1], shape,  (status & TOWER_STATUS_KEY[i/2]) == TOWER_STATUS_KEY[i/2]);
            }
            return buildings;
        }
        static void drawTowers(Building[] towers, Canvas canvas, Paint paint, Building ancient){
            for(int i = 0; i < towers.length; i++){
                if(i == towers.length - 1)
                    ancient.draw(canvas,paint);
                towers[i].draw(canvas, paint);
            }
        }
        static void drawBarracks(Building[] barracks, Type type, Side side, Canvas canvas, Paint paint){
            for (int i=type.value; i<barracks.length; i+=2){
                if(side == Side.RADIANT && i == barracks.length -1) continue;
                if(side == Side.DIRE && i == barracks.length -2) continue;
                barracks[i].draw(canvas, paint);
            }
        }
        private void draw(Canvas canvas, Paint paint){
            paint.setAlpha(this.alpha);
            canvas.drawBitmap(this.shape, this.x, this.y, paint);
        }
        static void drawBuilding(int x, int y, Bitmap shape, boolean alive, Canvas canvas, Paint paint){
            new Building(x, y, shape, alive).draw(canvas, paint);
        }
    }
    public void revealAnswer(Player guessedPlayer, int nameColor){
        Intent intent = new Intent(this, AnswerActivity.class);
        intent.putExtra("guessedID", guessedPlayer.getAccountId());
        intent.putExtra("correctID", playerID);
        intent.putExtra("heroIconUrl", guessedPlayer.getHero().getIconUrl());
        intent.putExtra("heroName", guessedPlayer.getHero().getName());
        intent.putExtra("nameColor", nameColor);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PlayerRow.hideAnswers();
    }
}
