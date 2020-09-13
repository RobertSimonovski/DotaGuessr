package com.example.dotaguessr;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Game {
    @SerializedName("result")
    @Expose
    private ResultGame result;

    public ResultGame getResult()   {  return result;  }
}