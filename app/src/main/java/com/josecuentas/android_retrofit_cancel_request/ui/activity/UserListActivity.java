package com.josecuentas.android_retrofit_cancel_request.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.josecuentas.android_retrofit_cancel_request.R;
import com.josecuentas.android_retrofit_cancel_request.rest.ApiClient;
import com.josecuentas.android_retrofit_cancel_request.rest.properties.Properties;
import com.josecuentas.android_retrofit_cancel_request.rest.response.UserResponse;
import com.josecuentas.android_retrofit_cancel_request.ui.adapter.UserAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int pageSize = 2;
    private static final int offSet = 1;
    private static final String props = "name,lastname";

    private UserAdapter mUserAdapter;
    RecyclerView mRecyclerView;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        injectView();
        setup();
        getUser();
    }

    private void injectView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.container);
    }

    private void setup() {
        mUserAdapter = new UserAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        mRecyclerView.setAdapter(mUserAdapter);
    }

    private void getUser() {
        ApiClient apiClient = new ApiClient();
        Call<UserResponse> user = apiClient.getClient().getUser(Properties.APPLICATION_ID, Properties.REST_API_KEY, pageSize, offSet, props);
        user.enqueue(new Callback<UserResponse>() {
            @Override public void onResponse(Call<UserResponse> call, final Response<UserResponse> response) {
                Log.i(TAG, "body: " + response.body().toString());
                if (response.isSuccessful()) {
                    Toast.makeText(UserListActivity.this.getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    mUserAdapter.setUserEntities(response.body());
                    mUserAdapter.notifyDataSetChanged();

                } else {
                    Log.i(TAG, "onResponse: fail");
                }
            }

            @Override public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
