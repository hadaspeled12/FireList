package com.example.hadasp.firelist;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hadasp on 10/12/2017.
 */

@IgnoreExtraProperties
public class Note {

    private boolean checked;
    private String body;
    private String listId;
    private String noteId;

    public Note() {
    }

    public Note(String body, String listId, boolean checked) {
        this.body = body;
        this.listId = listId;
        this.checked = checked;
    }

    public String getListId() {
        return listId;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteId() {
        return noteId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("body", body);
        result.put("checked", checked);
        result.put("listId", listId);

        return result;
    }
}
