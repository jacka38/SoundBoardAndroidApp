package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private LinearLayout buttonContainer;
    private MediaPlayer mediaPlayer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonContainer = findViewById(R.id.buttonContainer);
    }

    public void onSelectFileClick(View view) {
        // Open file picker to select a sound file
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*"); // Only audio files
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData(); // Get the selected file's URI

            String fileName = "Custom Sound"; // Modify this with the desired button label
            addButton(fileName, fileUri);
        }
    }

    private void addButton(String fileName, Uri fileUri) {
        // Use an AlertDialog to get user input for the button name
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter sound name");

        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String buttonText = input.getText().toString();
                if (!TextUtils.isEmpty(buttonText)) {
                    createButton(buttonText, fileUri);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createButton(String buttonText, Uri fileUri) {
        Button newButton = new Button(this);
        newButton.setText(buttonText);
        newButton.setTextColor(Color.WHITE);
        newButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700)); // Set button color

        // Get dimensions from the hardcoded button
        Button hardcodedButton = findViewById(R.id.button);
        int width = hardcodedButton.getLayoutParams().width;
        int height = hardcodedButton.getLayoutParams().height;

        // Set layout parameters to match the dimensions of the hardcoded button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL; // Center horizontally
        newButton.setLayoutParams(layoutParams);

        // Add click listener to the dynamically created button
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                playCustomSound(fileUri);
            }
        });

        buttonContainer.addView(newButton);
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri != null) {
            // Retrieve the filename from the URI
            String path = uri.getPath();
            fileName = path.substring(path.lastIndexOf("/") + 1);
        }
        return fileName != null ? fileName : "Custom Sound"; // Default to "Custom Sound"
    }

    private void playCustomSound(Uri fileUri) {
        if (fileUri != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(this, fileUri);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void playSound(View view) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.latuaperkele);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    releaseMediaPlayer();
                }
            });
        }
        mediaPlayer.start();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
