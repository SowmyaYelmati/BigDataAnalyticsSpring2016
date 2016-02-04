package com.example.sowmy.robo_me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wowwee.robome.RoboMe;
import com.wowwee.robome.RoboMeCommands;
import com.wowwee.robome.SensorStatus;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements RoboMe.RoboMeListener {

    private RoboMe roboMe;
    private TextView txtSpeechInput;
    private ImageButton mic;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roboMe = new RoboMe(this,this);
        roboMe.setVolume(12);
        roboMe.startListening();

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        mic = (ImageButton) findViewById(R.id.mic);


        mic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        txtSpeechInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendNotification(s.toString());
                int notificationId = 200;
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("Received Text is")
                        .setContentText(s.toString());
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId, notificationBuilder.build());
            }
        });

        Button wearButton = (Button) findViewById(R.id.newButton);
        wearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int notificationId = 200;
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                        .setSmallIcon(R.drawable.notificationicon)
                        .setContentTitle("First Android Notification")
                        .setContentText("Testing Wearable Notifcation");
                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

                notificationManagerCompat.notify(notificationId, notificationBuilder.build());

            }
        });
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(String text){
        int notificationId = 100;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                .setContentTitle("Instruction to RoboMe")
                .setContentText(text);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);

        notificationManagerCompat.notify(notificationId, notificationBuilder.build());
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    @Override
    public void commandReceived(RoboMeCommands.IncomingRobotCommand incomingRobotCommand) {
        android.util.Log.d("RoboMe", "Received event " + incomingRobotCommand);

        if (incomingRobotCommand.isSensorStatus()) {
            SensorStatus status = incomingRobotCommand.readSensorStatus();
            Log.d("RoboMe",String.format("Edge: %b Chest 20cm: %b 50cm: %b 100cm: %b", status.edge, status.chest_20cm, status.chest_50cm, status.chest_100cm));
        }
        if(incomingRobotCommand.isHandshakeStatus()) {
            promptSpeechInput();
            Log.d("RoboMe","Handshaked");
        }
    }



    @Override
    public void roboMeConnected() {
        android.util.Log.d("RoboMe", "Received event " + roboMe.isRoboMeConnected() );
    }

    @Override
    public void roboMeDisconnected() {

    }

    @Override
    public void headsetPluggedIn() {
        Log.d("RoboMe","HeadSet Plugged In");
    }

    @Override
    public void headsetUnplugged() {
        Log.d("RoboMe","HeadSet UnPlugged");
    }

    @Override
    public void volumeChanged(float v) {
        Log.d("RoboMe","Volume Changed");
    }
}
