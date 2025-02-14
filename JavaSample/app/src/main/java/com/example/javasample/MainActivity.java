package com.example.javasample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.time.TrustedTimeClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Use TrustedTimeClient anywhere in your app
        findViewById(R.id.button1).setOnClickListener(v -> {
            // Retrieve the TrustedTimeClient from your application class
            var myApp = (MyApp) getApplicationContext();

            // In this example, System.currentTimeMillis() is used as a fallback if the
            // client is null (i.e. client creation task failed) or when there is no time
            // signal available. You may not want to do this if using the system clock is
            // not suitable for your use case.
            var currentTimeMillis = myApp.trustedTimeClient.computeCurrentUnixEpochMillis();
            var instant = myApp.trustedTimeClient.computeCurrentInstant();
            // trustedTimeClient.computeCurrentInstant() can be used if Instant is
            // preferred to long for Unix epoch times and you are able to use the APIs.
            var currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

            var formattedString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentTime);
            Log.d("MainActivity", "currentTimeMillis: " + currentTimeMillis);
            Log.d("MainActivity", "formattedString: " + formattedString);

            ((TextView) findViewById(R.id.textView1)).setText("" + currentTimeMillis);
            ((TextView) findViewById(R.id.textView2)).setText(formattedString);
        });

        // Use in short-lived components like Activity
        findViewById(R.id.button2).setOnClickListener(v -> {
            TrustedTimeClientAccessor.getTrustedTimeClientTask(this).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    var trustedTimeClient = task.getResult();
                    var currentTimeMillis = trustedTimeClient.computeCurrentUnixEpochMillis();
                    var instant = trustedTimeClient.computeCurrentInstant();
                    var currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                    var formattedString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentTime);
                    Log.d("MainActivity", "currentTimeMillis: " + currentTimeMillis);
                    Log.d("MainActivity", "formattedString: " + formattedString);

                    ((TextView) findViewById(R.id.textView1)).setText("" + currentTimeMillis);
                    ((TextView) findViewById(R.id.textView2)).setText(formattedString);
                } else {
                    // Handle error
                    // jp) エラーになるケースの具体例がわからないため、予期せぬ問題としてクラッシュさせる
                    // en) Since I do not know the specific example of the case where an error occurs, I crash as an unexpected problem.
                    var exception = task.getException();
                    Log.e("MainActivity", "error", exception);
                    throw new IllegalStateException("TrustedTimeClient is not available", exception);
                }
            });
        });
    }
}