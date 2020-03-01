package com.frcteam195.cyberscouter;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;

public class FakeRadioGroup {
    private static Button button;

    static public void buttonDisplay(Activity acty, int val, int[] bs, int SELECTED_BUTTON_TEXT_COLOR, int defaultButtonTextColor) {

        for(int i = 0 ; i<bs.length ; ++i){
            button = acty.findViewById(bs[i]);
            if(-1 != val && i == val) {
                button.setBackgroundColor(SELECTED_BUTTON_TEXT_COLOR);
            } else {
                button.setBackgroundColor(defaultButtonTextColor);
            }
        }
    }

    static public void buttonPressed(Activity acty, int val, int[] bs, String col, int SELECTED_BUTTON_TEXT_COLOR, int defaultButtonTextColor){
        for(int i = 0 ; i<bs.length ; ++i){
            button = acty.findViewById(bs[i]);
            if(-1 != val && i == val) {
                if(SELECTED_BUTTON_TEXT_COLOR == button.getCurrentTextColor()) {
                } else {
                    button.setBackgroundColor(SELECTED_BUTTON_TEXT_COLOR);
                }
            } else {
                button.setBackgroundColor(defaultButtonTextColor);
            }
        }
    }

    static public void buttonDisplay(Activity acty, View view, int val, int[] bs, int SELECTED_BUTTON_TEXT_COLOR, int defaultButtonTextColor) {

        for(int i = 0 ; i<bs.length ; ++i){
            button = view.findViewById(bs[i]);
            if(-1 != val && i == val) {
                button.setBackgroundColor(SELECTED_BUTTON_TEXT_COLOR);
            } else {
                button.setBackgroundColor(defaultButtonTextColor);
            }
        }
    }

    static public void buttonPressed(Activity acty, View view, int val, int[] bs, String col, int SELECTED_BUTTON_TEXT_COLOR, int defaultButtonTextColor){
        for(int i = 0 ; i<bs.length ; ++i){
            button = view.findViewById(bs[i]);
            if(-1 != val && i == val) {
                if(SELECTED_BUTTON_TEXT_COLOR == button.getCurrentTextColor()) {
                } else {
                    button.setBackgroundColor(SELECTED_BUTTON_TEXT_COLOR);
                }
            } else {
                button.setBackgroundColor(defaultButtonTextColor);
            }
        }
    }

    static void updateMetric(Activity acty, String col, int val) {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(acty);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getAlliance_station()));

        if (null != csm) {
            try {
                String[] cols = {col};
                Integer[] vals = {val};
                CyberScouterMatchScouting.updateMatchMetric(db, cols, vals, cfg);
            } catch(Exception e) {
                MessageBox.showMessageBox(acty, "Update Metric Failed Alert", "FakeButtonGroup.updateMetric",
                        "Update of " + col + " failed!\n\n" +
                                "The error is:\n" + e.getMessage());
            }
        }
    }


}
