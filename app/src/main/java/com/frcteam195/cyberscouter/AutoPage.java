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
    private final int[] didnotshowButtons = {R.id.button_DidNotShowYes, R.id.button_DidNotShowNo};
    private final int[] penaltyButtons = {R.id.button_PenaltiesNo, R.id.button_PenaltiesYes};



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

        button = findViewById(R.id.button_DidNotShowYes);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                didnotshowYes();
            }
        });

        button = findViewById(R.id.button_DidNotShowNo);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                didnotshowNo();
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

        didnotshowNo();

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

        Intent intent = new Intent(this, SandstormPage.class);
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


    public void didnotshowYes() {
        FakeRadioGroup.buttonPressed(this, 1, didnotshowButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
        Intent intent = new Intent(this, SubmitPage.class);
        startActivity(intent);
    }

    public void didnotshowNo() {
        FakeRadioGroup.buttonPressed(this, 0, didnotshowButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
    }

    public void penaltiesYes() {
        FakeRadioGroup.buttonPressed(this, 1, penaltyButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);
    }

    public void penaltiesNo() {
        FakeRadioGroup.buttonPressed(this, 0, penaltyButtons, CyberScouterContract.MatchScouting.COLUMN_NAME_AUTODIDNOTSHOW, SELECTED_BUTTON_TEXT_COLOR, defaultButtonTextColor);

    }
}

