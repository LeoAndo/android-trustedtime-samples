package com.example.javasample;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.time.TrustedTimeClient;

public class MyApp extends Application {
    @NonNull
    TrustedTimeClient trustedTimeClient;

    @Override
    public void onCreate() {
        super.onCreate();

        TrustedTimeClientAccessor.getTrustedTimeClientTask(this).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                trustedTimeClient = task.getResult();
            } else {
                // Handle error
                // jp) エラーになるケースの具体例がわからないため、予期せぬ問題としてクラッシュさせる
                // en) Since I do not know the specific example of the case where an error occurs, I crash as an unexpected problem.
                var exception = task.getException();
                Log.e("MyApp", "error", exception);
                throw new IllegalStateException("TrustedTimeClient is not available", exception);
            }
        });
    }
}
