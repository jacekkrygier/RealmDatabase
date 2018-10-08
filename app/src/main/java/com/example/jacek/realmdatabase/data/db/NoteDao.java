package com.example.jacek.realmdatabase.data.db;



import android.content.Context;

import com.example.jacek.realmdatabase.data.pojo.Note;
import com.example.jacek.realmdatabase.data.realm.NoteRealm;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class NoteDao {
    private Realm realm;
    private RealmConfiguration realmConfig;

    public NoteDao(Context context) {
        // inicjalizacja realma
        Realm.init(context);
        realmConfig = new RealmConfiguration.Builder().name("myrealm2.realm").build();
        realm = Realm.getInstance(realmConfig);
    }

    // zamknięcie realma
    public void close() {
        realm.close();
    }

    // wstawienie nowej notatki do bazy danych
    public void insertNote(final Note note) {
        // operacje zapisu muszą odbywać się w transakcji
        realm.beginTransaction();

        // tworzymy nowy obiekt przy pomocy metody createObject()
        NoteRealm noteRealm = realm.createObject(NoteRealm.class);
        noteRealm.setNoteText(note.getNoteText());

        // commitTransaction() zapisuje stan obiektów realmowych do bazy danych
        // jeśli więc stworzyliśmy nowy lub usunęliśmy stary, to w tym momencie
        // te operacje zostaną odwzorowane w bazie
        realm.commitTransaction();
    }

    // pobranie notatki na podstawie jej id
    public Note getNoteById(final int id) {
        // aby pobrać obiekt danej klasy korzystamy z metody where()
        // dodatkowe warunki zapytania definiujemy przy pomocy metod takich jak equalTo()
        // findFirst() lub findAll() to metody, które wykonują zdefiniowane zapytanie
        NoteRealm noteRealm = realm.where(NoteRealm.class).equalTo("id", id).findFirst();
        return new NoteMapper().fromRealm(noteRealm);
    }

    // aktualizacja notatki w bazie
    public void updateNote(final Note note) {
        NoteRealm noteRealm = realm.where(NoteRealm.class).equalTo("id", note.getId()).findFirst();
        realm.beginTransaction();
        noteRealm.setNoteText(note.getNoteText());
        realm.commitTransaction();
    }

    // usunięcie notatki z bazy
    public void deleteNoteById(final long id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(NoteRealm.class).equalTo("id", id).findFirst().deleteFromRealm();
            }
        });
    }

    // pobranie wszystkich notatek
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        NoteMapper mapper = new NoteMapper();

        RealmResults<NoteRealm> all = realm.where(NoteRealm.class).findAll();

        for (NoteRealm noteRealm : all) {
            notes.add(mapper.fromRealm(noteRealm));
        }

        return notes;
    }

    // pobranie wszystkich notatek, które zawierają w treści dany tekst
    public List<Note> getNotesLike(String text) {
        List<Note> notes = new ArrayList<>();
        NoteMapper mapper = new NoteMapper();

        RealmResults<NoteRealm> all = realm.where(NoteRealm.class)
                .contains("noteText", text)
                .findAll();

        for (NoteRealm noteRealm : all) {
            notes.add(mapper.fromRealm(noteRealm));
        }

        return notes;
    }

    public List<NoteRealm> getRawNotes(){
        return realm.where(NoteRealm.class).findAll();
    }

    // usunięcie wszystkich notatek
    public void deleteAllNotes() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(NoteRealm.class)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    // wygenerowanie nowego id dla notatki
    private int generateId() {
        return realm.where(NoteRealm.class).max("id").intValue() + 1;
    }
}