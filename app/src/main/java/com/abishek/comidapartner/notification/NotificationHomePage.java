package com.abishek.comidapartner.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abishek.comidapartner.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationHomePage extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private static final String TAG = "NotificationHomepage";
    private List<MyNotificationTable> notificationList;
    private RecyclerView notificationRecycler;
    private NotificationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_home_page);
        notificationRecycler = findViewById(R.id.notification_recycler);
        notificationList = new ArrayList<>();

        new FetchNotificationsFromRoom(ComidaDatabase.getDatabase(NotificationHomePage.this)).execute();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if (viewHolder instanceof NotificationAdapter.NotificationViewHolder) {
            int notificationIdInRoom = notificationList.get(viewHolder.getAdapterPosition()).getNotiId();
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            new DeleteNotificationFromRoom(ComidaDatabase.getDatabase(NotificationHomePage.this),notificationIdInRoom).execute();
        }
    }

    class FetchNotificationsFromRoom extends AsyncTask<Void, Void, Void> {

        private NotificationDao notificationDao;

        public FetchNotificationsFromRoom(ComidaDatabase instance) {
            notificationDao = instance.getMyNotificationDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (notificationList != null)
                notificationList.clear();
            notificationList = notificationDao.getAllNotifications();
            Log.e(TAG, ".....size: " + notificationList.size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startAdapter();
            Log.e(TAG, ".....done");
        }
    }

    private void startAdapter() {



        if(notificationList.size() == 0) {
            findViewById(R.id.no_notification).setVisibility(View.VISIBLE);
            findViewById(R.id.notification_recycler).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.notification_recycler).setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationHomePage.this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            mAdapter = new NotificationAdapter(notificationList, NotificationHomePage.this);

            notificationRecycler.setLayoutManager(linearLayoutManager);
            notificationRecycler.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, NotificationHomePage.this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notificationRecycler);
        }

    }


    class DeleteNotificationFromRoom extends AsyncTask<Void,Void,Void> {

        private NotificationDao notificationDao;
        private Integer id;

        public DeleteNotificationFromRoom(ComidaDatabase instance,Integer notificationId) {
            notificationDao = instance.getMyNotificationDao();
            this.id=notificationId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(notificationList !=null){
                if(id==-1)
                    notificationDao.deleteAllNotifications();
                else
                    notificationDao.deleteNotificationById(id);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // fetchNotificationFromRoomAndSetAdapter();
        }
    }


}