package com.example.hadasp.firelist;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import android.widget.*;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.hadasp.firelist.PublicVariables.ANONYMOUS;
import static com.example.hadasp.firelist.PublicVariables.RC_SIGN_IN;
import static com.example.hadasp.firelist.PublicVariables.mIdOfList;
import static com.example.hadasp.firelist.PublicVariables.mNameOfList;
import static com.example.hadasp.firelist.PublicVariables.mOtherUser;
import static com.example.hadasp.firelist.PublicVariables.mUsername;
import static com.example.hadasp.firelist.PublicVariables.updatelist;


public class ListActivity extends AppCompatActivity implements ListAdapter.ListAdapterInteraction{

    private ListAdapter mListAdapter;
    private RecyclerView recyclerView;
    private TextView mBtnAddNote;
    private EditText mEditTextNewListTitle;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mNotesDatabaseReference;
    private DatabaseReference mListsDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    private ChildEventListener mChildEventListener;

    private java.util.List<List> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        //mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mListsDatabaseReference = mFirebaseDatabase.getReference().child("lists");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mBtnAddNote = findViewById(R.id.btnAddNote);
        mBtnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddListBtnClick();
            }
        });

        mEditTextNewListTitle = findViewById(R.id.et_new_list_title);

        recyclerView = (RecyclerView) findViewById(R.id.rv_lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        lists = new ArrayList<>();
        mListAdapter = new ListAdapter(this, lists);
        recyclerView.setAdapter(mListAdapter);

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
                int position = (int) viewHolder.itemView.getTag();
                //List currentList = getList().get(position);
                Log.e("ListActivity", position + "=" + direction);
                //onDeleteList(currentList);
                //java.util.List<List> updatedList = getList();
                //mListAdapter.updateList(updatedList);
            }

        }).attachToRecyclerView(recyclerView);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //user is signed in
                    onSignedInInitialize(user.getEmail());
                    Toast.makeText(ListActivity.this,
                            "You are now signed in",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //user is signed out
                    onSignedOutCleanup();
                    // Choose authentication providers
                    java.util.List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };
        if (getIntent().getBooleanExtra(updatelist, false)){
            List list = new List(mNameOfList, mUsername, mOtherUser);
            list.setListId(mIdOfList);
            onUpdateList(list);
        }

    }

    private void onAddListBtnClick() {

        //AppDatabase.getInstance(this).listDao().insertList( createList("List"));
        Log.e("ListActivity", "add list !");
        //java.util.List<List> updatedList = getList();
        String title = mEditTextNewListTitle.getText().toString();
        if (title.equals("")){
            List newList = new List("new list", mUsername, "");
            mListsDatabaseReference.push().setValue(newList);
        } else {
            List newList = new List(mEditTextNewListTitle.getText().toString(), mUsername, "");
            mListsDatabaseReference.push().setValue(newList);
        }

    }

    //public java.util.List<List> getList() {
        //return AppDatabase.getInstance(this).listDao().getAllLists();
    //}

    //private List createList(String title) {
    //    List list = new List();
    //    list.setTitle(title);
    //    return list;
    //}

    //public void onDeleteList(List list) {
    //    java.util.List<Note> notes =
    //            AppDatabase.getInstance(this).noteDao().getNotesByList(list.getId());
    //    for (int i =0; i< notes.size(); i++){
    //        AppDatabase.getInstance(this).noteDao().delete(notes.get(i));
    //    }
    //    AppDatabase.getInstance(this).listDao().deleteList(list);
    //}

    @Override
    protected void onDestroy() {
        //AppDatabase.destroyInstance();
        super.onDestroy();
    }

    @Override
    public void onUpdateList(List list) {
        String key = list.getListId();
        //list.setListId(null);

        Map<String, Object> listValues = list.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/lists/" + key, listValues);

        mListsDatabaseReference.getRoot().updateChildren(childUpdates);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);

            detachDatabaseReadListener();

        }
    }


    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
    }
    private void onSignedInInitialize(String username){
        mUsername = username;
        attachDatabaseReadListener();
        //attachPhotoPickerListener();
    }
    private void attachDatabaseReadListener(){
        if (mChildEventListener == null){
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    List newList = dataSnapshot.getValue(List.class);
                    newList.setListId(dataSnapshot.getKey());
                    Log.e("ListActivity", "newList.getUserId1() = " + newList.getUserId1()
                     + "mUsername = " + mUsername);
                    if (newList.getUserId1().equals(mUsername) || newList.getUserId2().equals(mUsername)){
                        lists.add(newList);
                        mListAdapter.updateList(lists);
                    }

                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    List newList = dataSnapshot.getValue(List.class);
                    if (newList != null){
                        if (newList.getUserId1().equals(mUsername) || newList.getUserId2().equals(mUsername)) {
                            Log.e("ListActivity", "onChildChanged newNote != null");
                            newList.setListId(dataSnapshot.getKey());
                            if (newList.getListId() != null) {
                                if (newList.getListId().equals(mIdOfList)) {
                                    for (int i = 0; i < lists.size(); i++) {
                                        if (lists.get(i).getListId().equals(newList.getListId())) {
                                            lists.set(i, newList);
                                        }
                                    }
                                    mListAdapter.updateList(lists);
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

            mListsDatabaseReference.addChildEventListener(mChildEventListener);

        }
    }
    private void detachDatabaseReadListener() {
        lists = new ArrayList<>();
        mListAdapter.updateList(lists);
        if (mChildEventListener != null){
            mListsDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //Sign out
                onSignedOutCleanup();
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
