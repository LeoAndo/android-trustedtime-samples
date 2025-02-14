package com.example.kotlinsample

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.time.TrustedTimeClient

class MyApp : Application() {
    lateinit var trustedTimeClient: TrustedTimeClient

    override fun onCreate() {
        super.onCreate()

        TrustedTimeClientAccessor.getTrustedTimeClientTask(this)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    trustedTimeClient = task.getResult()
                } else {
                    // Handle error
                    // jp) エラーになるケースの具体例がわからないため、予期せぬ問題としてクラッシュさせる
                    // en) Since I do not know the specific example of the case where an error occurs, I crash as an unexpected problem.
                    val exception = task.exception
                    Log.e("MyApp", "error", exception)
                    throw IllegalStateException("TrustedTimeClient is not available", exception)
                }
            })
    }
}