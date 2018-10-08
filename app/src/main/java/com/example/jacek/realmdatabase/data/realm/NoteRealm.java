package com.example.jacek.realmdatabase.data.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NoteRealm extends RealmObject {
    private String noteText;



    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}