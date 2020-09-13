package com.example.dotaguessr;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Hero {
    private static HashMap<Integer, Hero> heroMap;
    @SerializedName("localized_name")
    private final String name;
    @SerializedName("name")
    private final String simpleName;
    private final int id;


    public Hero(String name, String simpleName, int id) {
        this.name = name;
        this.simpleName = simpleName;
        this.id = id;
    }

    public static void setHeroes(InputStream jsonStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream, StandardCharsets.UTF_8));
        String line;
        StringBuilder jsonString = new StringBuilder();
        while ((line = reader.readLine()) != null){
            jsonString.append(line.trim());
        }
        reader.close();

        Gson gson = new Gson();
        Hero[] heroes = gson.fromJson(jsonString.toString(), Hero[].class);

        heroMap = new HashMap<>();
        for (Hero i : heroes){
            heroMap.put(i.getId(), i);
        }
    }

    public static Hero getHero(int key){
        return heroMap.get(key);
    }
    @Override
    public String toString(){
        return "Name \"" + name + "\' Simple name: \"" + simpleName + "\"\n iconURL: \"" + getIconUrl() + "\"";
    }

    public String getIconUrl() {
        return "http://cdn.dota2.com/apps/dota2/images/heroes/"
                + simpleName.substring(14)
                +"_lg.png";
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public int getId() {
        return id;
    }

}
