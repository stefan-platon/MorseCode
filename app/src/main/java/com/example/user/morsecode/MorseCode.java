package com.example.user.morsecode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MorseCode extends AppCompatActivity {
    public boolean alphaToMorseFlag = true;
    private static final int CAMERA_REQUEST = 50;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse_code);

        ActivityCompat.requestPermissions(MorseCode.this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST);

        FloatingActionButton msg = findViewById(R.id.fab);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MorseCode.this, Message.class);
                startActivity(intent);
            }
        });

        ImageButton trButton = findViewById(R.id.translateBtn);
        ImageButton revButton = findViewById(R.id.reverseBtn);
        final EditText textToBeTanslated = findViewById(R.id.toBeTr);
        final TextView welcome = findViewById(R.id.welcome);

        trButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        text = textToBeTanslated.getText().toString();
                        welcome.setVisibility(View.GONE);
                        if(alphaToMorseFlag){
                            alphaToMorse.start();
                        }
                        else{
                            morseToAlpha.start();
                        }
                    }
                });

        revButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        alphaToMorseFlag = !alphaToMorseFlag;
                        TextView text = findViewById(R.id.infoText);
                        text.setText(alphaToMorseFlag ? "Alpha to Morse" : "Morse to Alpha");
                    }
                });
    }

    Thread alphaToMorse = new Thread(){
        public void run() {
            StringBuilder builder = new StringBuilder();
            String[] words = text.trim().split(" ");
            loop:
            for (String word : words) {
                for (int i = 0; i < word.length(); i++) {
                    String morse = MorseCodeTranslation.letterToMorse(word.substring(i, i + 1));
                    if (morse != null && !morse.isEmpty()) {
                        builder.append(morse).append("   ");
                        final String letterStr = word.substring(i, i + 1);
                        final String morseStr = morse;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                final TextView letterView = findViewById(R.id.letter);
                                final TextView morseView = findViewById(R.id.morse);
                                letterView.setText(letterStr);
                                morseView.setText(morseStr);
                            }
                        });
                        useFlashLight(morseStr);
                    } else {
                        Toast.makeText(MorseCode.this,
                                "The text could not be translated. Maybe you wrote something wrong?",
                                Toast.LENGTH_LONG).show();
                        break loop;
                    }
                }
                builder.append("       ");
            }
            String text = builder.toString();
            final String text2 = text.substring(0, text.length() - 10);
            runOnUiThread(new Runnable() {
                public void run() {
                    final TextView textTranslated = findViewById(R.id.translated);
                    textTranslated.setText(text2);
                }
            });
        }
    };

    Thread morseToAlpha = new Thread(){
        public void run() {
            final TextView letterView = findViewById(R.id.letter);
            StringBuilder builder = new StringBuilder();
            String[] words = text.trim().split(" {3}");
            loop:
            for (String word : words) {
                for (String letter : word.split(" ")) {
                    String alpha = MorseCodeTranslation.morseToletter(letter);
                    if (alpha != null && !alpha.isEmpty()) {
                        builder.append(alpha);
                        letterView.setText(alpha);
                    } else {
                        Toast.makeText(MorseCode.this,
                                "The text could not be translated. Maybe you wrote something wrong?",
                                Toast.LENGTH_LONG).show();
                        break loop;
                    }
                }
                builder.append(" ");
            }
            final String text = builder.toString();
            runOnUiThread(new Runnable() {
                public void run() {
                    final TextView textTranslated = findViewById(R.id.translated);
                    textTranslated.setText(text);
                }
            });
        }
    };

    void useFlashLight(String text) {
        int dot = 750;
        for(int i = 0; i < text.length(); i++){
            switch(text.charAt(i)) {
                case '.' :
                    flashLightOn();
                    SystemClock.sleep(dot);
                    break;
                case '-' :
                    flashLightOn();
                    SystemClock.sleep(dot * 3);
                    break;
                case ' ' :
                    flashLightOff();
                    SystemClock.sleep(dot);
                    continue;
            }
            flashLightOff();
            SystemClock.sleep(dot);
        }
    }

    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            assert cameraManager != null;
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            Log.v("Flashlight - ON", "Something went terribly wrong... ");
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            assert cameraManager != null;
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            Log.v("Flashlight - OFF", "Something went terribly wrong... ");
        }
    }
}
