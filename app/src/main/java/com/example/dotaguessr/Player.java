package com.example.dotaguessr;

import com.google.gson.annotations.SerializedName;

public class Player {
    private long account_id;
    @SerializedName("player_slot")
    private int playerSlot;
    private int hero_id;
    private int item_0;
    private int item_1;
    private int item_2;
    private int item_3;
    private int item_4;
    private int item_5;
    private int backpack_0;
    private int backpack_1;
    private int backpack_2;
    private int item_neutral;
    private int kills;
    private int deaths;
    private int assists;
    private int leaver_status;
    private int last_hits;
    private int denies;
    @SerializedName("gold_per_min")
    private int goldPerMin;
    @SerializedName("xp_per_min")
    private int xpPerMin;
    private int level;
    private String persona;

    public long getAccountId() {
        return account_id;
    }

    public int getPlayerSlot() {
        return playerSlot;
    }

    public Hero getHero() {
        return Hero.getHero(hero_id);
    }

    public Item[] getItems(){
        Item[] items = new Item[6];

        items[0] = Item.getItem(item_0);
        items[1] = Item.getItem(item_1);
        items[2] = Item.getItem(item_2);
        items[3] = Item.getItem(item_3);
        items[4] = Item.getItem(item_4);
        items[5] = Item.getItem(item_5);

        return items;
    }

    public Item[] getBackpack() {
        Item[] items = new Item[3];

        items[0] = Item.getItem(backpack_0);
        items[1] = Item.getItem(backpack_1);
        items[2] = Item.getItem(backpack_2);

        return items;
    }

    public Item getItem_neutral() {
        return Item.getItem(item_neutral);
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getLeaver_status() {
        return leaver_status;
    }

    public int getLast_hits() {
        return last_hits;
    }

    public int getDenies() {
        return denies;
    }

    public int getNetWorth(){
        int net = 0;

        for (Item i : getItems())
            net += i.getCost();
        for (Item i : getBackpack())
            net += i.getCost();
        return net;
    }

    public int getGoldPerMin() {
        return goldPerMin;
    }

    public int getXpPerMin() { return xpPerMin; }

    public int getLevel() { return level;  }

    public String getPersona() { return  persona; }
}
