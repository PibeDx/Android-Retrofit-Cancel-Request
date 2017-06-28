package com.josecuentas.android_retrofit_cancel_request.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.josecuentas.android_retrofit_cancel_request.rest.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcuentas on 28/06/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    List<UserEntity> mUserEntities;

    public UserAdapter() {
        mUserEntities = new ArrayList<>();
    }

    public void setUserEntities(List<UserEntity> userEntities) {
        mUserEntities = userEntities;
    }

    @Override public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        return new UserViewHolder(view);
    }

    @Override public void onBindViewHolder(UserViewHolder holder, int position) {
        UserEntity userEntity = mUserEntities.get(position);
        holder.text.setText(userEntity.name);
    }

    @Override public int getItemCount() {
        return mUserEntities.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public UserViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
