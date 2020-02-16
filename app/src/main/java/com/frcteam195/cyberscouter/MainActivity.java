package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DriverManager;

public class MainActivity extends AppCompatActivity {
    private final Integer REQUEST_ENABLE_BT = 1;
    private final int PERMISSION_REQUEST_FINE_LOCATION = 319;
    private BluetoothAdapter _bluetoothAdapter;

    private Button button;
    private TextView textView;

    private uploadMatchScoutingResultsTask g_backgroundUpdater;
    private static Integer g_backgroundProgress;

    static final private String g_adminPassword = "HailRobotOverlords";

    BroadcastReceiver mConfigReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterconfig");
            processConfig(ret);
        }
    };

    BroadcastReceiver mOnlineStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int color = intent.getIntExtra("onlinestatus", Color.RED);
            updateStatusIndicator(color);
        }
    };

    BroadcastReceiver mUsersReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ret = intent.getStringExtra("cyberscouterusers");
            updateUsers(ret);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BluetoothComm.setbLastBTCommFailed(FakeBluetoothServer.bUseFakeBluetoothServer);

        button = findViewById(R.id.button_scouting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScouting();

            }
        });

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        _bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (_bluetoothAdapter == null || !_bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mConfigReceiver, new IntentFilter(CyberScouterConfig.CONFIG_UPDATED_FILTER));
        registerReceiver(mOnlineStatusReceiver, new IntentFilter(BluetoothComm.ONLINE_STATUS_UPDATED_FILTER));
        registerReceiver(mUsersReceiver, new IntentFilter(CyberScouterUsers.USERS_UPDATED_FILTER));
        CyberScouterConfig.getConfigRemote(this);
        CyberScouterUsers.getUsersRemote(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (REQUEST_ENABLE_BT == requestCode) {
//            Toast.makeText(getApplicationContext(), String.format("Result: %d", resultCode), Toast.LENGTH_LONG).show();
//        }
    }

    @Override
    protected void onDestroy() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        mDbHelper.close();
        unregisterReceiver(mConfigReceiver);
        unregisterReceiver(mOnlineStatusReceiver);
        unregisterReceiver(mUsersReceiver);
        super.onDestroy();
    }

    private void processConfig(String config_json) {
        try {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if (null != config_json) {
                JSONObject jo = new JSONObject(config_json);
                textView = findViewById(R.id.textView_eventString);
                textView.setText(jo.getString("EventName") + ", " + jo.getString("EventLocation"));
                textView = findViewById(R.id.textView_roleString);
                textView.setText(jo.getString("AllianceStation"));
                CyberScouterConfig.setConfigLocal(db, jo);
            }
            try {
                Intent backgroundIntent = new Intent(getApplicationContext(), BackgroundUpdater.class);
                ComponentName cn = startService(backgroundIntent);
                if (null == cn) {
                    MessageBox.showMessageBox(MainActivity.this, "Start Service Failed Alert", "processConfig", "Attempt to start background update service failed!");
                }
            } catch (Exception e) {
                MessageBox.showMessageBox(MainActivity.this, "Start Service Failed Alert", "processConfig", "Attempt to start background update service failed!\n\n" +
                        "The error is:\n" + e.getMessage());
            }

            if (0 == 0) return;

            /* if there's no existing local configuration, we're going to assume the tablet
            is "online", meaning that it can talk to the SQL Server database.  If there is a
            configuration record, we'll use the offline setting from that to determine whether we
            should query the SQL Server database for the current event.
             */
//            if ((null == cfg) || (!cfg.isOffline())) {
//                getEventTask eventTask = new getEventTask(new IOnEventListener<CyberScouterEvent>() {
//                    @Override
//                    public void onSuccess(CyberScouterEvent result) {
//                        CyberScouterConfig cfg2 = cfg;
//                        if (null != cfg) {
//                            if (null != result && (result.getEventID() != cfg.getEvent_id())) {
//                                setEvent(result);
//                                cfg2 = CyberScouterConfig.getConfig(db);
//                            }
//                            setFieldsFromConfig(cfg2);
//                        } else {
//                            setFieldsToDefaults(db, result.getEventName());
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        if (null == e) {
//                            if (null == cfg || null == cfg.getEvent()) {
//                                button = findViewById(R.id.button_scouting);
//                                button.setEnabled(false);
//                                MessageBox.showMessageBox(MainActivity.this, "Event Not Found Alert", "processConfig", "No current event found!  Cannot continue.");
//                            }
//                        } else {
//                            setFieldsFromConfig(cfg);
//                            MessageBox.showMessageBox(MainActivity.this, "Fetch Event Failed Alert", "getEventTask", "Fetch of Current Event information failed!\n\n" +
//                                    "You may want to consider working offline.\n\n" + "The error is:\n" + e.getMessage());
//
//                        }
//                    }
//                });
//
//                eventTask.execute();
//            } else {
//                setFieldsFromConfig(cfg);
//            }
        } catch (Exception e) {
            MessageBox.showMessageBox(this, "Exception Caught", "processConfig", "An exception occurred: \n" + e.getMessage());
            e.printStackTrace();
        }
    }

    void setFieldsFromConfig(CyberScouterConfig cfg) {
        TextView tv;
        tv = findViewById(R.id.textView_roleString);
        String tmp = cfg.getRole();
        if (tmp.startsWith("Blu"))
            tv.setTextColor(Color.BLUE);
        else if (tmp.startsWith("Red"))
            tv.setTextColor(Color.RED);
        else
            tv.setTextColor(Color.BLACK);
        tv.setText(cfg.getRole());

        tv = findViewById(R.id.textView_eventString);
        tv.setText(cfg.getEvent());

    }

    void setFieldsToDefaults(SQLiteDatabase db, String eventName) {
        TextView tv;
        String tmp;
        ContentValues values = new ContentValues();
        tmp = CyberScouterConfig.UNKNOWN_ROLE;
        tv = findViewById(R.id.textView_roleString);
        tv.setText(tmp);
        values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_ROLE, tmp);
        if (null != eventName)
            tmp = eventName;
        else
            tmp = CyberScouterConfig.UNKNOWN_EVENT;
        tv = findViewById(R.id.textView_eventString);
        tv.setText(tmp);
        values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT, tmp);
        values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_TABLET_NUM, 0);
        values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_OFFLINE, 0);
        values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_FIELD_REDLEFT, 1);
        values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERID, CyberScouterConfig.UNKNOWN_USER_IDX);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(CyberScouterContract.ConfigEntry.TABLE_NAME, null, values);
    }

    public void openScouting() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        Class nextIntent = null;
        switch (cfg.getComputer_type_id()) {
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_1_SCOUTER):
                nextIntent = ScoutingPage.class;
                break;
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_2_SCOUTER):
                nextIntent = WordCloudActivity.class;
                break;
            case (CyberScouterConfig.CONFIG_COMPUTER_TYPE_LEVEL_PIT_SCOUTER):
                nextIntent = PitScoutingActivity.class;
                break;
            default:
                nextIntent = ScoutingPage.class;
        }

        Intent intent = new Intent(this, nextIntent);
        startActivity(intent);
    }

    public void syncData() {
        try {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            final SQLiteDatabase db = mDbHelper.getWritableDatabase();

            final CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

            if (null != cfg) {

                if (cfg.isOffline()) {
                    MessageBox.showMessageBox(this, "Offline Alert", "syncData", "You are currently offline. If you want to sync, please get online!");
                } else {
                    getMatchScoutingTask scoutingTask = new getMatchScoutingTask(new IOnEventListener<CyberScouterMatchScouting[]>() {
                        @Override
                        public void onSuccess(CyberScouterMatchScouting[] result) {
                            try {
                                CyberScouterMatchScouting.deleteOldMatches(MainActivity.this, cfg.getEvent_id());
                                String tmp = CyberScouterMatchScouting.mergeMatches(MainActivity.this, result);

                                Toast t = Toast.makeText(MainActivity.this, "Data synced successfully! -- " + tmp, Toast.LENGTH_SHORT);
                                t.show();
                            } catch (Exception ee) {
                                MessageBox.showMessageBox(MainActivity.this, "Fetch Match Scouting Failed Alert", "syncData.getMatchScoutingTask.onSuccess",
                                        "Attempt to update local match information failed!\n\n" +
                                                "The error is:\n" + ee.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (null != e) {
                                MessageBox.showMessageBox(MainActivity.this, "Fetch Match Scouting Failed Alert", "getMatchScoutingTask",
                                        "Fetch of Match Scouting information failed!\n\n" +
                                                "You may want to consider working offline.\n\n" +
                                                "The error is:\n" + e.getMessage());
                            }
                        }
                    }, cfg.getEvent_id());

                    scoutingTask.execute();

                    CyberScouterUsers.deleteUsers(db);
                    getUserNamesTask namesTask = new getUserNamesTask(new IOnEventListener<CyberScouterUsers[]>() {
                        @Override
                        public void onSuccess(CyberScouterUsers[] result) {
//                            CyberScouterUsers.setUsers(db, result);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (null != e) {
                                MessageBox.showMessageBox(MainActivity.this, "Fetch Event Failed Alert", "getUsersTask", "Fetch of User information failed!\n\n" +
                                        "You may want to consider working offline.\n\n" + "The error is:\n" + e.getMessage());
                            }

                        }
                    });

                    namesTask.execute();

                    CyberScouterQuestions.deleteQuestions(db);
                    getQuestionsTask questionsTask = new getQuestionsTask(new IOnEventListener<CyberScouterQuestions[]>() {
                        @Override
                        public void onSuccess(CyberScouterQuestions[] result) {
                            CyberScouterQuestions.setLocalQuestions(db, result);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            if (null != e) {
                                MessageBox.showMessageBox(MainActivity.this, "Fetch Questions Failed Alert", "getQuestionsTask", "Fetch of Question information failed!\n\n" +
                                        "You may want to consider working offline.\n\n" + "The error is:\n" + e.getMessage());
                            }
                        }
                    }, cfg.getEvent_id());

                    questionsTask.execute();
                }
            }

        } catch (Exception e_m) {
            e_m.printStackTrace();
            MessageBox.showMessageBox(this, "Fetch Event Info Failed Alert", "syncData", "Sync with data source failed!\n\n" +
                    "You may want to consider working offline.\n\n" + "The error is:\n" + e_m.getMessage());
        }
    }

    private static class getEventTask extends AsyncTask<Void, Void, CyberScouterEvent> {
        private IOnEventListener<CyberScouterEvent> mCallBack;
        private Exception mException;

        getEventTask(IOnEventListener<CyberScouterEvent> mListener) {
            super();
            mCallBack = mListener;
        }

        @Override
        protected CyberScouterEvent doInBackground(Void... arg) {
            Connection conn = null;

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"
                        + DbInfo.MSSQLServerAddress + "/" + DbInfo.MSSQLDbName, DbInfo.MSSQLUsername, DbInfo.MSSQLPassword);

                CyberScouterEvent cse = new CyberScouterEvent();
                CyberScouterEvent cse2 = cse.getCurrentEvent(conn);

                if (null != cse2)
                    return (cse2);

            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            } finally {
                if (null != conn) {
                    try {
                        if (!conn.isClosed())
                            conn.close();
                    } catch (Exception e2) {
                        // do nothing
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(CyberScouterEvent cse) {
            if (null != mCallBack) {
                if (null != mException || null == cse) {
                    mCallBack.onFailure(mException);
                } else {
                    mCallBack.onSuccess(cse);
                }
            }
        }
    }

    private class getUserNamesTask extends AsyncTask<Void, Void, CyberScouterUsers[]> {
        private IOnEventListener<CyberScouterUsers[]> mCallBack;
        private Exception mException;

        getUserNamesTask(IOnEventListener<CyberScouterUsers[]> mListener) {
            super();
            mCallBack = mListener;
        }

        @Override
        protected CyberScouterUsers[] doInBackground(Void... arg) {

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"
                        + DbInfo.MSSQLServerAddress + "/" + DbInfo.MSSQLDbName, DbInfo.MSSQLUsername, DbInfo.MSSQLPassword);

                CyberScouterUsers[] csua = null;

                conn.close();

                if (null != csua)
                    return csua;

            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(CyberScouterUsers[] csua) {
            if (null != mCallBack) {
                if (null != mException || null == csua) {
                    mCallBack.onFailure(mException);
                } else {
                    mCallBack.onSuccess(csua);
                }
            }
        }
    }

    private class getQuestionsTask extends AsyncTask<Void, Void, CyberScouterQuestions[]> {
        private IOnEventListener<CyberScouterQuestions[]> mCallBack;
        private Exception mException;
        private int mCurrentEventId;

        getQuestionsTask(IOnEventListener<CyberScouterQuestions[]> mListener, int l_currentEventID) {
            super();
            mCallBack = mListener;
            mCurrentEventId = l_currentEventID;
        }

        @Override
        protected CyberScouterQuestions[] doInBackground(Void... arg) {

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"
                        + DbInfo.MSSQLServerAddress + "/" + DbInfo.MSSQLDbName, DbInfo.MSSQLUsername, DbInfo.MSSQLPassword);

                CyberScouterQuestions[] csqa = CyberScouterQuestions.getQuestions(conn, mCurrentEventId);

                conn.close();

                if (null != csqa)
                    return csqa;

            } catch (Exception e) {
                e.printStackTrace();
                mException = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(CyberScouterQuestions[] csqa) {
            if (null != mCallBack) {
                if (null != mException || null == csqa) {
                    mCallBack.onFailure(mException);
                } else {
                    mCallBack.onSuccess(csqa);
                }
            }
        }
    }

    private void setEvent(CyberScouterEvent cse) {
        try {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT, cse.getEventName());
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_EVENT_ID, cse.getEventID());
            int count = db.update(
                    CyberScouterContract.ConfigEntry.TABLE_NAME,
                    values,
                    null,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }

    }

    private static class getMatchScoutingTask extends AsyncTask<Void, Void, CyberScouterMatchScouting[]> {
        private IOnEventListener<CyberScouterMatchScouting[]> mCallBack;
        private Exception mException;
        private CyberScouterMatchScouting[] l_matches;
        private int currentEventId;

        getMatchScoutingTask(IOnEventListener<CyberScouterMatchScouting[]> mListener, int l_currentEventId) {
            super();
            mCallBack = mListener;
            currentEventId = l_currentEventId;
        }

        @Override
        protected CyberScouterMatchScouting[] doInBackground(Void... arg) {

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"
                        + DbInfo.MSSQLServerAddress + "/" + DbInfo.MSSQLDbName, DbInfo.MSSQLUsername, DbInfo.MSSQLPassword);

//                l_matches = CyberScouterMatchScouting.getMatches(conn, currentEventId);

                conn.close();

            } catch (Exception e) {
                mException = e;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(CyberScouterMatchScouting[] cse) {
            if (null != mCallBack) {
                if (null != mException || null == l_matches) {
                    mCallBack.onFailure(mException);
                } else {
                    mCallBack.onSuccess(l_matches);
                }
            }
        }

    }

    private void startBackgroundUpdaterTask() {
        g_backgroundProgress = 0;
        g_backgroundUpdater = new uploadMatchScoutingResultsTask(new IOnEventListener<Void>() {
            @Override
            public void onSuccess(Void v) {
            }

            @Override
            public void onFailure(Exception e) {
                if (null == e) {
                } else {
                    MessageBox.showMessageBox(MainActivity.this, "Launch Background Updater Failed Alert", "startBackgroundUpdaterTask", "Launch of background updater failed!\n\n" +
                            "The error is:\n" + e.getMessage());
                }
            }
        }, this);


        g_backgroundUpdater.execute();
    }

    private static class uploadMatchScoutingResultsTask extends AsyncTask<Void, Integer, Void> {
        private IOnEventListener<Void> mCallBack;
        private Exception mException;
        private WeakReference<Activity> ref_lacty;

        uploadMatchScoutingResultsTask(IOnEventListener<Void> mListener, Activity acty) {
            super();
            mCallBack = mListener;
            ref_lacty = new WeakReference<>(acty);
        }

        @Override
        protected Void doInBackground(Void... arg) {
            int cnt = 0;

            while (true) { // forever
                try {
                    cnt++;
                    publishProgress(cnt);
                    if (isCancelled())
                        return null;
                    // look for matches to upload
                    if (isCancelled())
                        return null;
                    // upload matches
                    if (isCancelled())
                        return null;

                    Thread.sleep(60000);
                } catch (Exception e) {
                    mException = e;
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Void v) {
            if (null != mCallBack) {
                if (null != mException) {
                    mCallBack.onFailure(mException);
                } else {
                    mCallBack.onSuccess(v);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            g_backgroundProgress = progress[0];
        }

    }

    private void updateStatusIndicator(int color) {
        Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawCircle(16, 16, 12, paint);
        ImageView iv = findViewById(R.id.imageView_btIndicator);
        iv.setImageBitmap(bitmap);
    }

    private void updateUsers(String json){
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        CyberScouterUsers.setUsers(db, json);
    }
}
