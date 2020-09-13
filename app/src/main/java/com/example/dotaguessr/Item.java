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


public class Item {
    private static HashMap<Integer, Item> itemMap;
    private final int id;
    @SerializedName("localized_name")
    private final String name;
    @SerializedName("name")
    private final String simpleName;
    private final int cost;
    public Item(int id, String name, String simpleName, int cost) {
        this.id = id;
        this.name = name;
        this.simpleName = simpleName;
        this.cost = cost;
    }
    public static void setItems(InputStream jsonStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream, StandardCharsets.UTF_8));
        String line = null;
        StringBuilder jsonString = new StringBuilder("");
        while ((line = reader.readLine()) != null){
            jsonString.append(line.trim());
        }
        reader.close();

        Gson gson = new Gson();
        Item[] items = gson.fromJson(jsonString.toString(), Item[].class);

        itemMap = new HashMap<>();
        for (Item i : items){
            itemMap.put(i.getId(), i);
        }
    }
    @Override
    public String toString(){
        return "Name: \"" + name + "\" shortName: \"" + simpleName + "\" cost: \"" + cost + "\"\nURL: \"" + getIconUrl() + "\"";
    }

    public static Item getItem(int key){
        return itemMap.get(key);
    }

    public int getId() { return id;  }

    public String getName() { return name; }

    public String getSimpleName() { return simpleName; }

    public String getIconUrl() {
        return "http://cdn.dota2.com/apps/dota2/images/items/"
                + simpleName.substring(simpleName.indexOf("_")+1)
                +"_lg.png";
    }

    public int getCost() { return cost; }
}
