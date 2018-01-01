package com.example.hadasp.firelist;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hadasp on 17/12/2017.
 */
@IgnoreExtraProperties
public class List {

    private String title;
    private String userId1;
    private String userId2;
    private String listId;

    public List() {
    }

    public List(String title, String userId1, String userId2) {
        this.title = title;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getUserId1(){
        return userId1;
    }

    public String getUserId2(){
        return userId2;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListId() {
        return listId;
    }

    public void setUserId2(String userId2){
        this.userId2 = userId2;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("userId1", userId1);
        result.put("userId2", userId2);

        return result;
    }
}
