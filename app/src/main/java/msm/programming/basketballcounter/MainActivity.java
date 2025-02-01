/*
 * Basketball Counter App (Android)
 *
 * Description:
 * This Android app allows users to track various basketball statistics for players
 * in real-time. Users can track the number of shots made, misses, assists, steals,
 * turnovers, jumps, and rebounds, and the app provides options to clear, undo, and
 * export the data to a file.
 *
 * Key Features:
 * - Real-time counters for basketball statistics.
 * - Undo functionality for the last action taken.
 * - Idea submission feature that allows users to send feedback via my public email address.
 * - Data export functionality to save basketball game stats to a text file.
 * - Clear all counters with a confirmation dialog.
 *
 * Components:
 * - Buttons for each stat (shots, assists, steals, etc.).
 * - TextViews for displaying the counters.
 * - An undo button in the menu to reverse the last action.
 * - An "ideas" button to submit feedback or suggestions via email.
 * - A clear button that resets all counters after a confirmation.
 * - Data export to a file stored on the device's external storage.
 *
 * Dependencies:
 * - Android API (No external libraries required)
 *
 * Usage:
 * - The app is designed for basketball coaches or parents who want to track various
 *   player statistics during games or practice sessions.
 */

//TODO: create a file to save all games and then add a way to bring all games into one final statistic
 package msm.programming.basketballcounter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {


    private int mShotCounter = 0,
            mMissCounter = 0,
            mAssistCounter = 0,
            mStealCounter = 0,
            mTurnoverCounter = 0,
            mJumpCounter = 0,
            mReboundsCounter = 0;

    private TextView shotsTV, missTV, assistTV, stealsTV, turnoverTV, reboundsTV, jumpTV;
        Button shotBTN, missBTN, stealBTN, assistBTN, clearBTN, turnoverBTN, reboundsBTN, jumpBTN;

        EditText editTX;
        private Stack<Action> actionStack = new Stack<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



       shotBTN = findViewById(R.id.shotBTN);
       missBTN = findViewById(R.id.missBTN);
       assistBTN = findViewById(R.id.assistBTN);
       stealBTN = findViewById(R.id.stealBTN);
       clearBTN = findViewById(R.id.clearBTN);

        shotsTV = findViewById(R.id.shotsTV);
       missTV = findViewById(R.id.missTV);
       assistTV = findViewById(R.id.assistTV);
       stealsTV = findViewById(R.id.stealsTV);
       editTX = findViewById(R.id.editTX);
       turnoverBTN = findViewById(R.id.turnoverBTN);
       jumpBTN = findViewById(R.id.jumpBTN);
       reboundsBTN = findViewById(R.id.reboundstBTN);
       turnoverTV = findViewById(R.id.turnoverTV);
       reboundsTV = findViewById(R.id.reboundsTV);
       jumpTV = findViewById(R.id.jumpTV);

        shotBTN.setOnClickListener(view -> updateCounter("shot"));
        missBTN.setOnClickListener(view -> updateCounter("miss"));
        assistBTN.setOnClickListener(view -> updateCounter("assist"));
        stealBTN.setOnClickListener(view -> updateCounter("steal"));
        turnoverBTN.setOnClickListener(view -> updateCounter("turnover"));
        jumpBTN.setOnClickListener(view -> updateCounter("jump"));
        reboundsBTN.setOnClickListener(view -> updateCounter("rebound"));


        clearBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearButtonClicked();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.export){
            exportBTnClicked();
            return true;
        }
        if (id == R.id.undo){
            undoLastAction();
            return true;
        }
        if (id == R.id.ideas){
            showIdeaSubmissionDialog();
            return true;
        }
        else if (id == R.id.action_about){
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showIdeaSubmissionDialog() {
        final EditText ideaInput = new EditText(this);
        ideaInput.setHint("Write your idea here");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Idea")
                .setView(ideaInput)
                .setPositiveButton("Send", (dialog, id) -> sendIdeaViaEmail(ideaInput.getText().toString()))
                .setNegativeButton("Cancel",(dialog, id) -> dialog.dismiss())
                .show();
    }

    private void sendIdeaViaEmail(String ideaContent) {
        if (ideaContent.isEmpty()) {
            Toast.makeText(this, "Please write an idea before submitting.", Toast.LENGTH_SHORT).show();
        } else {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mike.miltner28@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Idea Submission");
            emailIntent.putExtra(Intent.EXTRA_TEXT, ideaContent);
            try {
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showAboutDialog(){
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("This app was developed to help parents keep track of their kids basketball statistics. It's a work in progress and I hope this fits the criteria of what everyone is looking for.")
                .setPositiveButton("Ok",(dialog,which)->dialog.dismiss())
                .show();
    }
    private void updateCounter(String type) {
        int previousValue;

        switch (type){
            case "shot":
                previousValue = mShotCounter;
                mShotCounter++;
                shotsTV.setText("Shots made:  "+mShotCounter);
                actionStack.push(new Action("shot",previousValue));
                break;
            case "miss":
                previousValue = mMissCounter;
                mMissCounter++;
                missTV.setText("Misses: " + mMissCounter);
                actionStack.push(new Action("miss", previousValue));
                break;

            case "assist":
                previousValue = mAssistCounter;
                mAssistCounter++;
                assistTV.setText("Assists: " + mAssistCounter);
                actionStack.push(new Action("assist", previousValue));
                break;

            case "steal":
                previousValue = mStealCounter;
                mStealCounter++;
                stealsTV.setText("Steals: " + mStealCounter);
                actionStack.push(new Action("steal", previousValue));
                break;

            case "turnover":
                previousValue = mTurnoverCounter;
                mTurnoverCounter++;
                turnoverTV.setText("Turnovers: " + mTurnoverCounter);
                actionStack.push(new Action("turnover", previousValue));
                break;

            case "jump":
                previousValue = mJumpCounter;
                mJumpCounter++;
                jumpTV.setText("Jumps: " + mJumpCounter);
                actionStack.push(new Action("jump", previousValue));
                break;

            case "rebound":
                previousValue = mReboundsCounter;
                mReboundsCounter++;
                reboundsTV.setText("Rebounds: " + mReboundsCounter);
                actionStack.push(new Action("rebound", previousValue));
                break;
        }
    }
    private void undoLastAction() {
        if (!actionStack.isEmpty()) {
            Action lastAction = actionStack.pop(); // Get last action

            switch (lastAction.type) {
                case "shot":
                    mShotCounter = lastAction.previousValue;
                    shotsTV.setText("Shots made: " + mShotCounter);
                    break;

                case "miss":
                    mMissCounter = lastAction.previousValue;
                    missTV.setText("Misses: " + mMissCounter);
                    break;

                case "assist":
                    mAssistCounter = lastAction.previousValue;
                    assistTV.setText("Assists: " + mAssistCounter);
                    break;

                case "steal":
                    mStealCounter = lastAction.previousValue;
                    stealsTV.setText("Steals: " + mStealCounter);
                    break;

                case "turnover":
                    mTurnoverCounter = lastAction.previousValue;
                    turnoverTV.setText("Turnovers: " + mTurnoverCounter);
                    break;

                case "jump":
                    mJumpCounter = lastAction.previousValue;
                    jumpTV.setText("Jumps: " + mJumpCounter);
                    break;

                case "rebounds":
                    mReboundsCounter = lastAction.previousValue;
                    reboundsTV.setText("Rebounds: " + mReboundsCounter);
                    break;
            }
        } else {
            Toast.makeText(this, "No actions to undo!", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearButtonClicked(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to clear the fields?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"You chose YES!",Toast.LENGTH_SHORT).show();
                        clear();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"You chose NO!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(true).show();
    }

    private void exportBTnClicked(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to export the data?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"You chose YES!",Toast.LENGTH_SHORT).show();
                        addDataToFile();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"You chose NO!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setCancelable(true).show();
    }

    public void clear(){
        editTX.getText().clear();
        mShotCounter = 0;
        mMissCounter = 0;
        mAssistCounter = 0;
        mStealCounter = 0;
        mTurnoverCounter =0;
        mJumpCounter = 0;
        mReboundsCounter =0;
        shotsTV.setText("Shots made:  "+Integer.toString(mShotCounter));
        stealsTV.setText("Steals:  "+Integer.toString(mStealCounter));
        missTV.setText("Missed shots:  "+Integer.toString(mMissCounter));
        assistTV.setText("Assists:  "+Integer.toString(mAssistCounter));
        turnoverTV.setText("Turnover:  "+Integer.toString(mTurnoverCounter));
        jumpTV.setText("Jumpballs:  "+Integer.toString((mJumpCounter)));
        reboundsTV.setText("Rebounds:  "+Integer.toString((mReboundsCounter)));
    }


    private void addDataToFile (){

        String teamName = editTX.getText().toString();

        String[] data = {"*****  "+teamName+ "  *****",
                "Shots made:  "+Integer.toString(mShotCounter),
                "Steals:  "+Integer.toString(mStealCounter),
                "Missed shots:  "+Integer.toString(mMissCounter),
                "Assists:  "+Integer.toString(mAssistCounter),
                "Turnovers:  "+Integer.toString(mTurnoverCounter),
                "Jumpballs: "+Integer.toString(mJumpCounter),
                "Rebounds: "+Integer.toString(mReboundsCounter)};
        try {
            File externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if(!externalDir.exists()){
                externalDir.mkdir();
            }
            File file = new File(externalDir,"basketballGames.txt");
            FileOutputStream fileout = new FileOutputStream(file, true);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            for (String line:data){
                outputWriter.write(line+"\n");
            }
            outputWriter.close();
            Toast.makeText(MainActivity.this,"File successfully saved!",Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(MainActivity.this,"Uh Oh something went wrong" + e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    // Class to store action history
    private static class Action {
        String type;
        int previousValue;

        Action(String type, int previousValue) {
            this.type = type;
            this.previousValue = previousValue;
        }
    }

}