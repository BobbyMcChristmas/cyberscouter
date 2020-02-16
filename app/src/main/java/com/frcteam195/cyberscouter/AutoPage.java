package com.frcteam195.cyberscouter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AutoPage extends AppCompatActivity {
    private Button button;
    private int defaultButtonTextColor;
    private final int SELECTED_BUTTON_TEXT_COLOR = Color.GREEN;
    private final int[] moveBonusButtons = {R.id.button_moveBonusNo, R.id.button_moveBonusYes};
    private final int[] penaltyButtons = {R.id.button_PenaltiesNo, R.id.button_PenaltiesYes};
    private int innerGoalCount = 0;
    private int outerGoalCount = 0;
    private int lowerGoalCount = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_page);

        Intent intent = getIntent();
        int field_orientation = intent.getIntExtra("field_orientation", 0);

        button = findViewById(R.id.button_startMatch);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StartMatch();
            }
        });

        button = findViewById(R.id.buttonPrevious);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ReturnToScoutingPage();
            }
        });
        defaultButtonTextColor = button.getCurrentTextColor();

        button = findViewById(R.id.button_moveBonusYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonusYes();
            }
        });

        button = findViewById(R.id.button_moveBonusNo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                moveBonusNo();
            }
        });

        button = findViewById(R.id.button_PenaltiesYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                penaltiesYes();
            }
        });

        button = findViewById(R.id.button_PenaltiesNo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                penaltiesNo();
            }
        });

        button = findViewById(R.id.InnerGoalMinus_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                innerGoalMinus();
            }
        });

        button = findViewById(R.id.InnerGoalPlus_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                innerGoalPlus();
            }
        });

        button = findViewById(R.id.OuterGoalMinus_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                outerGoalMinus();
            }
        });

        button = findViewById(R.id.OuterGoalPlus_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                outerGoalPlus();
            }
        });

        button = findViewById(R.id.LowerGoalMinus_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lowerGoalMinus();
            }
        });

        button = findViewById(R.id.LowerGoalPlus_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                lowerGoalPlus();
            }
        });


        
    }

    @Override
    protected void onResume() {
        super.onResume();

        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

//        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getRole()));
        CyberScouterMatchScouting csm = null;

        if (null != csm) {
            TextView tv = findViewById(R.id.textView7);
            tv.setText(getString(R.string.tagMatch, csm.getTeamMatchNo()));
            tv = findViewById(R.id.textView9);
            tv.setText(getString(R.string.tagTeam, csm.getTeam()));

//            FakeRadioGroup.buttonDisplay(this, csm.getAutoMoveBonus(), moveBonusButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);

//            FakeRadioGroup.buttonDisplay(this, csm.getAutoStartPos(), startingPosButtons, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        }
    }

    public void StartMatch() {

        Intent intent = new Intent(this, EndPage.class);
        startActivity(intent);
    }


    public void ReturnToScoutingPage() {
        this.finish();
    }



    public void skipMatch() {
        CyberScouterDbHelper mDbHelper = new CyberScouterDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        CyberScouterConfig cfg = CyberScouterConfig.getConfig(db);

        CyberScouterMatchScouting csm = CyberScouterMatchScouting.getCurrentMatch(db, TeamMap.getNumberForTeam(cfg.getRole()));

        if (null != csm) {
            try {
                CyberScouterMatchScouting.skipMatch(db, csm.getMatchScoutingID());
                this.onResume();
            } catch (Exception e) {
                MessageBox.showMessageBox(this, "Skip Match Failed Alert", "skipMatch",
                        "Update of UploadStatus failed!\n\n" +
                                "The error is:\n" + e.getMessage());
            }
        }
    }


    public void moveBonusYes() {
//        FakeRadioGroup.buttonPressed(this, 1, moveBonusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        FakeRadioGroup.buttonPressed(this, 1, moveBonusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
    }

    public void moveBonusNo() {
//        FakeRadioGroup.buttonPressed(this, 0, moveBonusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        FakeRadioGroup.buttonPressed(this, 0, moveBonusButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOMOVEBONUS, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
    }


    public void penaltiesYes() {
        FakeRadioGroup.buttonPressed(this, 1, penaltyButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPENALTIES, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
    }

    public void penaltiesNo() {
        FakeRadioGroup.buttonPressed(this, 0, penaltyButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTOPENALTIES, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);

    }

    public void innerGoalMinus() {
        button = findViewById(R.id.InnerCounter);
        if (innerGoalCount > 0)
            innerGoalCount --;
        button.setText(String.valueOf(innerGoalCount));
    }
    public void innerGoalPlus() {
        button = findViewById(R.id.InnerCounter);
        innerGoalCount ++;
        button.setText(String.valueOf(innerGoalCount));

    }
    public void outerGoalMinus() {
        button = findViewById(R.id.OuterCounter);
        if (outerGoalCount > 0)
            outerGoalCount --;
        button.setText(String.valueOf(outerGoalCount));
    }
    public void outerGoalPlus() {
        button = findViewById(R.id.OuterCounter);
        outerGoalCount ++;
        button.setText(String.valueOf(outerGoalCount));
    }
    public void lowerGoalMinus() {
        button = findViewById(R.id.LowerCounter);
        if (lowerGoalCount > 0)
            lowerGoalCount --;
        button.setText(String.valueOf(lowerGoalCount));
    }
    public void lowerGoalPlus() {
        button = findViewById(R.id.LowerCounter);
        lowerGoalCount ++;
        button.setText(String.valueOf(lowerGoalCount));
    }

}

