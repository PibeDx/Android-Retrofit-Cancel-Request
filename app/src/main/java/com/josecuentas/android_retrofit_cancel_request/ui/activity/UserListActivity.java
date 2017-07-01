package com.josecuentas.android_retrofit_cancel_request.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
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
    private static final int pageSize = 10;
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

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = ContextCompat.getDrawable(mRecyclerView.getContext(), R.color.row_user);
                xMark = ContextCompat.getDrawable(mRecyclerView.getContext(), R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) mRecyclerView.getContext().getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                mUserAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mUserAdapter.changeViewDelete(viewHolder.getAdapterPosition());
            }

            @Override public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Log.d(TAG, " ");
                Log.d(TAG, " ");
                Log.d(TAG, " ");
                Log.d(TAG, " ");
                Log.d(TAG, " ");
                Log.d(TAG, " ");
                //region Transparencia
                int width = itemView.getRight();
                float positionWidth = width - Math.abs(dX);
                float porcentaje = positionWidth * 100 / width;
                itemView.setAlpha(porcentaje / 100);
                //endregion

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }
                Log.d(TAG, "onChildDraw: dX: " + dX);
                Log.d(TAG, "onChildDraw: dY: " + dY);
                // draw red background
                if (dX < 0) {
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    background.draw(c);
                } else {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
                    background.draw(c);
                }


                if (dX != 0) {
                    // draw x mark
                    int itemHeight = itemView.getBottom() - itemView.getTop();
                    int intrinsicWidth = xMark.getIntrinsicWidth();
                    int intrinsicHeight = xMark.getIntrinsicWidth();

                    Log.d(TAG, "onChildDraw: getTop: " + itemView.getTop());
                    Log.d(TAG, "onChildDraw: getBottom: " + itemView.getBottom());
                    Log.d(TAG, "onChildDraw: itemHeight: " + itemHeight);
                    Log.d(TAG, "onChildDraw: intrinsicWidth: " + intrinsicWidth);
                    Log.d(TAG, "onChildDraw: intrinsicHeight: " + intrinsicHeight);

                    int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                    int xMarkRight = itemView.getRight() - xMarkMargin;
                    int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                    int xMarkBottom = xMarkTop + intrinsicHeight;
                    xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                    xMark.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
