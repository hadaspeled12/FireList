package com.example.hadasp.firelist;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.example.hadasp.firelist.PublicVariables.id;
import static com.example.hadasp.firelist.PublicVariables.mIdOfList;
import static com.example.hadasp.firelist.PublicVariables.mNameOfList;
import static com.example.hadasp.firelist.PublicVariables.mOtherUser;
import static com.example.hadasp.firelist.PublicVariables.mUsername;
import static com.example.hadasp.firelist.PublicVariables.name;

/**
 * Created by hadasp on 17/12/2017.
 */

public class ListAdapter  extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements View.OnClickListener {


    private java.util.List<List> mList;
    private final ListAdapter.ListAdapterInteraction mListener;

    public ListAdapter(ListAdapter.ListAdapterInteraction listAdapterInteraction, java.util.List<List> lists) {
        mListener = listAdapterInteraction;
        mList = lists;
    }

    public void updateList(java.util.List<List> updatedList) {
        mList = updatedList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = view.findViewById(R.id.tv_list);
        }
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ListAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(mList.get(position).getTitle());

        holder.mView.setTag(position);
        holder.mView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        final int position = (int) view.getTag();
        List list = mList.get(position);

        Intent intent = new Intent(view.getContext(), NoteActivity.class);
        mNameOfList = list.getTitle();
        mIdOfList = list.getListId();
        if (list.getUserId1().equals(mUsername)){
            mOtherUser = list.getUserId2();
        } else {
            mOtherUser = list.getUserId1();
        }

        //intent.putExtra(otheruser, list.getUserId2());

        startActivity(view.getContext(),intent,null);

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public interface ListAdapterInteraction {
        void onUpdateList(List list);
    }
}
