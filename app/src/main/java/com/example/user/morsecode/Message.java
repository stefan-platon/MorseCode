package com.example.user.morsecode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Message extends AppCompatActivity {
    static ArrayList<String> msgs = new ArrayList<>();
    static ArrayAdapter<String> adapter;
    smsListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Message.this, MorseCode.class);
                startActivity(intent);
            }
        });

        final ListView listView = findViewById(R.id.messages);
        adapter = new ArrayAdapter<>(this, R.layout.message, msgs) ;
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Message.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Message")
                        .setMessage(msgs.get(i))
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(R.drawable.alert);
                AlertDialog dialog = builder.show();

                TextView messageView = dialog.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER);
            }
        });

        listener = new smsListener();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(listener, filter);
    }

    String morseToAlpha(String text){
        StringBuilder builder = new StringBuilder();
        String[] words = text.trim().split(" {3}");
        loop:
        for (String word : words) {
            for (String letter : word.split(" ")) {
                String alpha = MorseCodeTranslation.morseToletter(letter);
                if (alpha != null && !alpha.isEmpty()) {
                    builder.append(alpha);
                } else {
                    Toast.makeText(Message.this,
                            "The text could not be translated. Maybe you wrote something wrong?",
                            Toast.LENGTH_LONG).show();
                    break loop;
                }
            }
            builder.append(" ");
        }
        return builder.toString();
    }

    void update(String number, String text){
        String translated = morseToAlpha(text);
        msgs.add(number + "\n" + text + "\n" + translated);
        adapter.notifyDataSetChanged();
    }
}
