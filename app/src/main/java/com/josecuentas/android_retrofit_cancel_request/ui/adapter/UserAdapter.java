package com.josecuentas.android_retrofit_cancel_request.ui.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.josecuentas.android_retrofit_cancel_request.R;
import com.josecuentas.android_retrofit_cancel_request.rest.entity.UserEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jcuentas on 28/06/17.
 */

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int VIEW_TYPE_ROW = 1;
    public static final int VIEW_TYPE_ROW_DELETE = 2;
    private Map<Integer, Boolean> mHistoryDrag;

    private List<UserEntity> mUserEntities;

    public UserAdapter() {
        mUserEntities = new ArrayList<>();
        mHistoryDrag = new HashMap<>();
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        mUserEntities = userEntities;
    }

    public List<UserEntity> getUserEntities() {
        return mUserEntities;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ROW:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
                return new UserViewHolder(view);
            case VIEW_TYPE_ROW_DELETE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_delete, parent, false);
                return new UserDeleteViewHolder(view);
            default: return null;
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final UserEntity userEntity = mUserEntities.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ROW:
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.text.setText(userEntity.name);
                break;
            case VIEW_TYPE_ROW_DELETE:
                final UserDeleteViewHolder userDeleteViewHolder = (UserDeleteViewHolder) holder;
                userDeleteViewHolder.mButDelete.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        mHistoryDrag.clear();
                        mUserEntities.remove(position);
                        notifyItemRemoved(position);
                    }
                });

                userDeleteViewHolder.mButUndo.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        mHistoryDrag.clear();
                        notifyItemChanged(position);
                    }
                });
        }

    }

    @Override public int getItemCount() {
        return mUserEntities.size();
    }

    @Override public int getItemViewType(int position) {
        Boolean aBoolean = mHistoryDrag.get(position);
        if (aBoolean == null){
            return VIEW_TYPE_ROW;
        } else {
            return VIEW_TYPE_ROW_DELETE;
        }
    }

    public void changeViewDelete(int position) {
        for (Integer s : mHistoryDrag.keySet()) {
            mHistoryDrag.clear();
            notifyItemChanged(s);
        }
        mHistoryDrag.put(position, true);
        notifyItemChanged(position);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public UserViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    public class UserDeleteViewHolder extends RecyclerView.ViewHolder {
        Button mButUndo;
        Button mButDelete;
        public UserDeleteViewHolder(View itemView) {
            super(itemView);
            mButUndo = (Button) itemView.findViewById(R.id.butUndo);
            mButDelete = (Button) itemView.findViewById(R.id.butDelete);
        }
    }
}
