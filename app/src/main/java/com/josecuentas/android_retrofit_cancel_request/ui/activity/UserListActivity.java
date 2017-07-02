package com.josecuentas.android_retrofit_cancel_request.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
            Drawable mBackground;
            Bitmap mMark;
            Paint mPaintIcon;
            int mMarkMargin;

            boolean initiated;

            private void init() {
                mBackground = ContextCompat.getDrawable(UserListActivity.this, R.color.row_user);
                mMark = BitmapFactory.decodeResource(getResources(),R.drawable.ic_clear_24dp);
                int mColorIcon = ContextCompat.getColor(UserListActivity.this, android.R.color.black);
                mPaintIcon = new Paint();
                mPaintIcon.setColorFilter(new PorterDuffColorFilter(mColorIcon, PorterDuff.Mode.SRC_IN));
                mMarkMargin = (int) UserListActivity.this.getResources().getDimension(R.dimen.ic_clear_margin);

                initiated = true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                if (viewHolder.getItemViewType() == UserAdapter.VIEW_TYPE_ROW_DELETE) {
                    return ItemTouchHelper.ACTION_STATE_IDLE;
                } else {
                    return makeMovementFlags(dragFlags, swipeFlags);
                }
            }

            @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if (viewHolder instanceof UserAdapter.UserDeleteViewHolder) {
                    return false;
                }
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

                //region transparencia al hacer drag en el item
                int width = itemView.getWidth();
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
                int itemHeight = itemView.getHeight();

                int itemLeft = itemView.getLeft();
                int itemRight = itemView.getRight();
                int itemTop = itemView.getTop();
                int itemBottom = itemView.getBottom();

                // draw red background
                if (dX > 1) { //drag right
                    mBackground.setBounds(itemLeft, itemTop, itemLeft + (int) dX, itemBottom);
                    mBackground.draw(c);
                } else { //drag left
                    mBackground.setBounds(itemRight + (int) dX, itemTop, itemRight, itemBottom);
                    mBackground.draw(c);
                }

                Bitmap base = Bitmap.createBitmap(itemRight, itemHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(base);
                if (dX > 1) { //drag right
                    //region draw mark
                    int leftIcon = itemLeft + mMarkMargin;
                    int topIcon = (itemHeight - mMark.getHeight()) / 2;

                    canvas.drawBitmap(mMark, leftIcon, topIcon, mPaintIcon);

                    Bitmap bitmap = Bitmap.createBitmap(base, 0, 0, (int) Math.abs(dX), itemHeight);
                    c.drawBitmap(bitmap, itemLeft, itemTop, new Paint());
                    //endregion
                } else if (dX < -1) { //drag left
                    //region draw mark
                    int leftIcon = itemRight - mMarkMargin - mMark.getWidth();
                    int topIcon = (itemHeight - mMark.getHeight()) / 2;

                    canvas.drawBitmap(mMark, leftIcon, topIcon, mPaintIcon);

                    Bitmap bitmap = Bitmap.createBitmap(base, itemRight - (int) Math.abs(dX), 0, (int) Math.abs(dX), itemHeight);
                    c.drawBitmap(bitmap, itemRight - Math.abs(dX), itemTop, new Paint());
                    //endregion
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
