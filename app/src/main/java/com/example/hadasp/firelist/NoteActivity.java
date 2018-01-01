package com.example.hadasp.firelist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.hadasp.firelist.PublicVariables.id;
import static com.example.hadasp.firelist.PublicVariables.mIdOfList;
import static com.example.hadasp.firelist.PublicVariables.mNameOfList;
import static com.example.hadasp.firelist.PublicVariables.name;
import static com.example.hadasp.firelist.PublicVariables.newnameoflist;
import static com.example.hadasp.firelist.PublicVariables.updatelist;

public class NoteActivity extends AppCompatActivity implements NoteAdapter.NotesAdapterInteraction {

    private NoteAdapter mNotesAdapter;
    private RecyclerView recyclerView;
    private TextView mBtnAddNote;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNotesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private ChildEventListener mChildEventListener;

    private java.util.List<Note> notes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mBtnAddNote = findViewById(R.id.btnAddNote);
        mBtnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNoteBtnClick();
            }
        });


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mNotesDatabaseReference = mFirebaseDatabase.getReference().child("notes");
        mFirebaseAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.rv_notes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        notes = new ArrayList<>();
        mNotesAdapter = new NoteAdapter(this, notes);
        recyclerView.setAdapter(mNotesAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.e("NoteActivity", "onSwiped was called");
                int position = (int) viewHolder.itemView.getTag();
                Note currentNote = notes.get(position);
                onDeleteNote(currentNote);
                notes.remove(position);
                mNotesAdapter.updateList(notes);
            }

        }).attachToRecyclerView(recyclerView);
        attachDatabaseReadListener();
        setTitle(mNameOfList);
    }

    private void onAddNoteBtnClick() {

    //    AppDatabase.getInstance(this).noteDao().insert( createNote( ""));

    //    List<Note> updatedList = getNoteList();
    //    mNotesAdapter.updateList(updatedList);
        Note note = createNote("");
        mNotesDatabaseReference.push().setValue(note);
        //note.setNoteId(mNotesDatabaseReference.getKey());
    }

    //public List<Note> getNoteList() {
    //    return AppDatabase.getInstance(this).noteDao().getNotesByList(mIdOfList);
    //}

    private Note createNote(String body) {
        return new Note(body, mIdOfList, false);
    }


    public void onDeleteNote(Note note) {
        String key = note.getNoteId();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/notes/" + key, null);
        mNotesDatabaseReference.getRoot().updateChildren(childUpdates);
    }

    @Override
    protected void onDestroy() {
     //   AppDatabase.destroyInstance();
        super.onDestroy();
    }

    @Override
    public void onUpdateNote(Note note) {
        String key = note.getNoteId();
        Map<String, Object> noteValues = note.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/notes/" + key, noteValues);

        mNotesDatabaseReference.getRoot().updateChildren(childUpdates);
    }

    private void attachDatabaseReadListener(){
        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Note newNote = dataSnapshot.getValue(Note.class);
                    if (newNote != null){
                        if (newNote.getListId().equals(mIdOfList)){
                            newNote.setNoteId(dataSnapshot.getKey());
                            if (newNote.getListId() != null){
                                if (newNote.getListId().equals(mIdOfList)){
                                    notes.add(newNote);
                                    mNotesAdapter.updateList(notes);
                                }
                            }
                        }
                    }
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Note newNote = dataSnapshot.getValue(Note.class);
                    if (newNote != null){
                        Log.e("NoteActivity", "onChildChanged newNote != null");
                        newNote.setNoteId(dataSnapshot.getKey());
                        if (newNote.getListId().equals(mIdOfList)){
                            if (newNote.getListId() != null){
                                if (newNote.getListId().equals(mIdOfList)){
                                    for (int i =0; i< notes.size(); i++){
                                        if (notes.get(i).getNoteId().equals(newNote.getNoteId())){
                                            notes.set(i, newNote);
                                        }
                                    }
                                    mNotesAdapter.updateList(notes);
                                }
                            }
                        }
                    }
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };

            mNotesDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }
    private void detachDatabaseReadListener() {
        notes = new ArrayList<>();
        mNotesAdapter.updateList(notes);
        if (mChildEventListener != null){
            mNotesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit_list:
                //go to edit list activity
                Intent intent = new Intent(NoteActivity.this, EditListActivity.class);
                //intent.putExtra(otheruser, mOtherUser);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
