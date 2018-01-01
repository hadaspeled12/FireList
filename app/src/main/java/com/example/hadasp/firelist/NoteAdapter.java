package com.example.hadasp.firelist;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hadasp on 10/12/2017.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {


    private List<Note> mNoteList;
    private final NotesAdapterInteraction mListener;

    private View.OnClickListener checkboxListener;

    public NoteAdapter(NotesAdapterInteraction notesAdapterInteraction, List<Note> noteList) {
        mListener = notesAdapterInteraction;
        mNoteList = noteList;
    }

    public void updateList(List<Note> updatedList) {
        mNoteList = updatedList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final EditText etBody;
        public final AppCompatCheckBox accbChecked;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            etBody = view.findViewById(R.id.et_note);
            accbChecked = view.findViewById(R.id.accb_checked);
        }
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NoteAdapter.ViewHolder holder, int position) {
        holder.etBody.setText(mNoteList.get(position).getBody());
        holder.accbChecked.setChecked(mNoteList.get(position).getChecked());

        holder.mView.setTag(position);

        holder.accbChecked.setEnabled(true);
        holder.etBody.setEnabled(true);
        holder.accbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mNoteList.get(holder.getAdapterPosition()).setChecked(true);
                } else {
                    mNoteList.get(holder.getAdapterPosition()).setChecked(false);
                }
                mListener.onUpdateNote(mNoteList.get(holder.getAdapterPosition()));
            }
        });
        holder.etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                Note note = mNoteList.get(holder.getAdapterPosition());
                note.setBody(editable.toString());
                mNoteList.set(holder.getAdapterPosition(), note);
                //updateList(mNoteList);
                mListener.onUpdateNote(note);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public interface NotesAdapterInteraction {
        void onUpdateNote(Note note);
    }
}
