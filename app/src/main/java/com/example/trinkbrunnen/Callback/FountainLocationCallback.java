package com.example.trinkbrunnen.Callback;

import com.parse.ParseObject;

import java.util.List;

public interface FountainLocationCallback {
    void onFountainLocationFound(List<ParseObject> object);
}
