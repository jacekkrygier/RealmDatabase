package com.example.jacek.realmdatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.jacek.realmdatabase.data.db.NoteDao;
import com.example.jacek.realmdatabase.data.pojo.Note;
import com.example.jacek.realmdatabase.data.realm.NoteRealm;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText newNoteText;
    private ListView noteList;
    private NoteDao noteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicjalizujemy pola klasy
        newNoteText = (EditText) findViewById(R.id.new_note_text);
        noteList = (ListView) findViewById(R.id.note_list);
        // tworzymy obiekt DAO
        noteDao = new NoteDao(this);
        // wyświetlamy listę notatek
        reloadNotesList();
    }

    @Override
    protected void onDestroy() {
        // zamykamy instancję Realma
        noteDao.close();
        super.onDestroy();
    }

    // dodaje nową notatkę do bazy danych i odświeża listę
    public void addNewNote(View view) {
        Note note = new Note();
        String text = newNoteText.getText().toString();
        if (text.length() > 0) {
            note.setNoteText(text);
        }

        // wywołanie metody insertNote() jest takie jak w poprzedniej lekcji,
        // gdzie korzystaliśmy z SQLite
        noteDao.insertNote(note);
        reloadNotesList();
    }

    // usuwa notatkę z bazy i odświeża listę
    public void removeNote(Note note) {
        noteDao.deleteNoteById(note.getId());
        reloadNotesList();
    }

    // pokazuje listę notatek
    private void reloadNotesList() {
        // pobieramy z bazy danych listę notatek
        List<Note> allNotes = noteDao.getAllNotes();

        // ustawiamy adapter listy
        noteList.setAdapter(new ArrayAdapter<Note>(this, R.layout.note_layout, allNotes) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final View noteView = super.getView(position, convertView, parent);

                // po kliknięciu na notatkę zostanie ona usunięta
                noteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeNote(getItem(position));
                    }
                });

                return noteView;
            }
        });
    }

    // usuwa wszystkie notatki z bazy
    public void deleteAll(View view) {
        noteDao.deleteAllNotes();
        reloadNotesList();
    }

    // pobiera wyłącznie notatki, które zawierają frazę "filtered"
    // i wyświetla je na ekranie
    public void filterNotes(View view) {
        // pobieramy z bazy danych listę notatek z frazą "filtered"
        List<Note> filteredNotes = noteDao.getNotesLike("filtered");

        // ustawiamy adapter listy
        noteList.setAdapter(new ArrayAdapter<Note>(this, R.layout.note_layout, filteredNotes) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        });
    }

    // pokazuje listę obiektów typu NoteRealm
    public void showRealmNotes(View view) {
        noteList.setAdapter(new ArrayAdapter<NoteRealm>(this, R.layout.note_layout, noteDao.getRawNotes()) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }
        });
    }
}