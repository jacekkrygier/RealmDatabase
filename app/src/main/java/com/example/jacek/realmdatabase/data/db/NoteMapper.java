package com.example.jacek.realmdatabase.data.db;

import com.example.jacek.realmdatabase.data.pojo.Note;
import com.example.jacek.realmdatabase.data.realm.NoteRealm;

public class NoteMapper {
    Note fromRealm(NoteRealm noteRealm) {
        Note note = new Note();
        note.setNoteText(noteRealm.getNoteText());
        return note;
    }
}