package com.example.kotlinsample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlinsample.TrustedTimeClientAccessor.getTrustedTimeClientTask
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.time.TrustedTimeClient
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Use TrustedTimeClient anywhere in your app
        findViewById<View>(R.id.button1).setOnClickListener(View.OnClickListener { v ->
            // Retrieve the TrustedTimeClient from your application class
            val myApp = applicationContext as MyApp

            // In this example, System.currentTimeMillis() is used as a fallback if the
            // client is null (i.e. client creation task failed) or when there is no time
            // signal available. You may not want to do this if using the system clock is
            // not suitable for your use case.
            val currentTimeMillis = myApp.trustedTimeClient!!.computeCurrentUnixEpochMillis()
            val instant = myApp.trustedTimeClient!!.computeCurrentInstant()
            // trustedTimeClient.computeCurrentInstant() can be used if Instant is
            // preferred to long for Unix epoch times and you are able to use the APIs.
            val currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            val formattedString =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentTime)
            Log.d("MainActivity", "currentTimeMillis: $currentTimeMillis")
            Log.d("MainActivity", "formattedString: $formattedString")

            findViewById<TextView>(R.id.textView1).text = "" + currentTimeMillis
            findViewById<TextView>(R.id.textView2).text = formattedString
        })

        // Use in short-lived components like Activity
        findViewById<View>(R.id.button2).setOnClickListener(View.OnClickListener { v ->
            getTrustedTimeClientTask(this).addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val trustedTimeClient: TrustedTimeClient = task.getResult()!!
                    val currentTimeMillis = trustedTimeClient.computeCurrentUnixEpochMillis()
                    val instant = trustedTimeClient.computeCurrentInstant()
                    val currentTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                    val formattedString =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(currentTime)
                    Log.d("MainActivity", "currentTimeMillis: $currentTimeMillis")
                    Log.d("MainActivity", "formattedString: $formattedString")

                    findViewById<TextView>(R.id.textView1).text = "" + currentTimeMillis
                    findViewById<TextView>(R.id.textView2).text = formattedString
                } else {
                    // Handle error
                    // jp) エラーになるケースの具体例がわからないため、予期せぬ問題としてクラッシュさせる
                    // en) Since I do not know the specific example of the case where an error occurs, I crash as an unexpected problem.
                    val exception = task.exception
                    Log.e("MainActivity", "error", exception)
                    throw IllegalStateException("TrustedTimeClient is not available", exception)
                }
            })
        })
    }
}