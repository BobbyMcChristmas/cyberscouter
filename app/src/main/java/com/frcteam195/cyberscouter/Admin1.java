package com.frcteam195.cyberscouter;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

public class Admin1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button button;
        AutoCompleteTextView actv;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin1);

        final Spinner spnr = findViewById(R.id.spinner);
        String[] spnr_items = {"--please select--", "Red 1", "Red 2", "Red 3", "Blue 1", "Blue 2", "Blue 3", "Pit", "Review"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_text_items, spnr_items);

        spnr.setAdapter(adapter);
        spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (spnr.getSelectedItemPosition() != 0) {
                    String item = spnr.getSelectedItem().toString();
                    spinnerChanged(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        button = findViewById(R.id.Button_MainPage);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        button = findViewById(R.id.button7);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setUpSync();
            }
        });

        actv = findViewById(R.id.autoCompleteTextView);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, DbInfo.initalEndpointArray);
        actv.setThreshold(1);
        actv.setAdapter(adapter);

        actv = findViewById(R.id.autoCompleteTextView2);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, DbInfo.initalDatabaseArray);
        actv.setThreshold(1);
        actv.setAdapter(adapter);

        actv = findViewById(R.id.autoCompleteTextView3);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, DbInfo.initalUsernameArray);
        actv.setThreshold(1);
        actv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        if (null != cfg) {
            Spinner spnr = findViewById(R.id.spinner);
            ArrayAdapter<String> adapter = (ArrayAdapter<String>)spnr.getAdapter();
            int spinnerPosition = adapter.getPosition(cfg.getRole());
            if (-1 != spinnerPosition)
                spnr.setSelection(spinnerPosition);
        }

    }

    public void openMainActivity() {
        this.finish();
    }


    public void setTestMode() {
    }

    public void updateCode() {
    }

    public void setUpSync() {
    }

    public void spinnerChanged(String val) {
        try {
            CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CyberScouterContract.ConfigEntry.COLUMN_NAME_ROLE, val);
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

}
