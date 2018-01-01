package com.example.hadasp.firelist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.hadasp.firelist.PublicVariables.mNameOfList;
import static com.example.hadasp.firelist.PublicVariables.mOtherUser;
import static com.example.hadasp.firelist.PublicVariables.updatelist;

public class EditListActivity extends AppCompatActivity {

    private EditText etListTitle;
    private EditText etCollaborateWith;
    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        etListTitle = (EditText) findViewById(R.id.et_list_title);
        etCollaborateWith = (EditText) findViewById(R.id.et_useremail);
        bSave = (Button) findViewById(R.id.b_save);

        etListTitle.setText(mNameOfList);
        etCollaborateWith.setText(mOtherUser);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameOfList = etListTitle.getText().toString();
                mOtherUser = etCollaborateWith.getText().toString();

                Intent intent = new Intent(EditListActivity.this, ListActivity.class);
                intent.putExtra(updatelist, true);
                startActivity(intent);
            }
        });

    }
}
