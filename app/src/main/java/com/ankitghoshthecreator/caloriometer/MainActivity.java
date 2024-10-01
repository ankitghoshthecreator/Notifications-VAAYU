package com.ankitghoshthecreator.caloriometer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "caloriometer_channel";
    private ImageView imageViewGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views
        EditText editText = findViewById(R.id.editTextText);
        Button sendButton = findViewById(R.id.sendtext);
        imageViewGif = findViewById(R.id.imageViewGif);

        // Create notification channel
        createNotificationChannel();

        // Request notification permission on Android 13 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Set the button click listener
        sendButton.setOnClickListener(v -> {
            String input = editText.getText().toString();
            if (!input.isEmpty()) {
                int x = Integer.parseInt(input);
                showGifAndSendNotification(x);
            } else {
                Log.d("MainActivity", "No input entered.");
            }
        });
    }

    // Function to display GIF and notification based on calorie input
    private void showGifAndSendNotification(int x) {
        Random random = new Random();
        int gifResId;
        String notificationMessage;

        Map<Integer, String> gifMessageMap = new HashMap<>();

        if (x < 1800) {
            // Pair low-calorie GIFs with specific messages
            gifMessageMap.put(R.drawable.low1, "I hope u will finish the goal of the 1800 calories by today");
            gifMessageMap.put(R.drawable.low2, "You can do '" + x + "' calories then u can do '" + x + "' more (make bio proud😤)");
            gifMessageMap.put(R.drawable.low3, "Fast! I don't got all day waiting for you to finish '" + x + "' more calories");
        } else if (x >= 1801 && x <= 2100) {
            // Pair mid-calorie GIF with specific messages
            gifMessageMap.put(R.drawable.mid1, "FINALLY !!!!!!!! You ACHIEVED the long-awaited '" + x + "' calories");
        } else {
            // Pair high-calorie GIFs with specific messages
            gifMessageMap.put(R.drawable.fat1, "Mota good lord, now you've just outscaled the weighing machine with those '" + x + "' calories");
            gifMessageMap.put(R.drawable.fat2, "My brother in Christ, please run a little, because the '" + x + "' calories you consumed may kill your goals");
            gifMessageMap.put(R.drawable.fat3, "'" + x + "' calories, a little more and you can weigh as much as the planet");
        }

        // Randomly select a GIF from the available ones
        Object[] keys = gifMessageMap.keySet().toArray();
        gifResId = (int) keys[random.nextInt(keys.length)];

        // Get the corresponding message for the selected GIF
        notificationMessage = gifMessageMap.get(gifResId);

        // Load GIF into ImageView using Glide
        Glide.with(this)
                .asGif()
                .load(gifResId)
                .into(imageViewGif);

        // Send the corresponding notification
        sendNotification(notificationMessage);
    }

    // Function to send a specific notification message
    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Food Item Entered")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Caloriometer Channel";
            String description = "Channel for sending food item notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
