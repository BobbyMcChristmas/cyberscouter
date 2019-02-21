package com.frcteam195.cyberscouter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class ScoutingPage extends AppCompatActivity implements NamePickerDialog.NamePickerDialogListener {
    private Button button;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scouting_page);

        button = (Button) findViewById(R.id.button5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAuto();

            }
        });

        button = (Button) findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNamePickerPage();

            }
        });
        button = (Button) findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainMenu();

            }
        });

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setFieldRedLeft(1);
                setFieldImage(R.drawable.red_left);
            }
        });

        imageButton = (ImageButton) findViewById(R.id.imageButton2);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setFieldRedLeft(0);
                setFieldImage(R.drawable.red_right);
            }
        });

        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        if(null != cfg && null != cfg.getUsername()) {
            button = (Button) findViewById(R.id.button6);
            button.setText(cfg.getUsername());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        if (null != cfg && cfg.isFieldRedLeft())
            setFieldImage(R.drawable.red_left);
        else
            setFieldImage(R.drawable.red_right);

        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getRole()));

        if (null != csm) {
            TextView tv = (TextView) findViewById(R.id.textView7);
            tv.setText(getString(R.string.tagMatch, csm.getTeamMatchNo()));
            tv = (TextView) findViewById(R.id.textView9);
            tv.setText(getString(R.string.tagTeam, csm.getTeam()));
            CyberScouterMatchScouting[] csma = CyberScouterMatchScouting.getCurrentMatchAllTeams(db, csm.getTeamMatchNo());
            if(null != csma && 6 == csma.length) {
                tv = (TextView) findViewById(R.id.textView20);
                tv.setText(csma[0].getTeam());
                tv = (TextView) findViewById(R.id.textView21);
                tv.setText(csma[1].getTeam());
                tv = (TextView) findViewById(R.id.textView22);
                tv.setText(csma[2].getTeam());
                tv = (TextView) findViewById(R.id.textView35);
                tv.setText(csma[3].getTeam());
                tv = (TextView) findViewById(R.id.textView27);
                tv.setText(csma[4].getTeam());
                tv = (TextView) findViewById(R.id.textView26);
                tv.setText(csma[5].getTeam());
            }
        }

    }

    @Override
    public void onNameSelected(String val, int idx) {
        setUsername(val, idx);
        button = (Button) findViewById(R.id.button6);
        button.setText(val);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    public void openAuto(){
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        if(null == cfg) {
            MessageBox.showMessageBox(this, "Configuration Not Found Alert", "openAuto", "This device is not configured properly to begin scouting.  Cannot continue.");
        } else if(CyberScouterConfig.UNKNOWN_USER_IDX == cfg.getUser_id()) {
            MessageBox.showMessageBox(this, "User Not Set Alert", "openAuto", "You must pick your name off the name picker before you can start scouting!");
        } else {
            Intent intent = new Intent(this, AutoPage.class);
            startActivity(intent);
        }

    }
    public void openNamePickerPage(){
//        Intent intent = new Intent(this, NamePickerPageActivity.class);
//        startActivity(intent);

        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        if(!cfg.isOffline()) {
            FragmentManager fm = getSupportFragmentManager();
            NamePickerDialog npd = new NamePickerDialog();
            npd.show(fm, "namepicker");
        }
    }

    public void returnToMainMenu(){
        this.finish();
    }

    public void setUsername(String val, int idx) {
        try {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERNAME, val);
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_USERID, idx);
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

    private void setFieldRedLeft(int val) {
        try {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_FIELD_REDLEFT, val);
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

    private void setFieldImage(int img) {
        ImageView imageView = findViewById(R.id.imageView2);
        imageView.setImageResource(img);
    }
}
