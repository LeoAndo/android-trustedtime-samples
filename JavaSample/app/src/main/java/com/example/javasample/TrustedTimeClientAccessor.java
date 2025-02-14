package com.example.javasample;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.time.TrustedTime;
import com.google.android.gms.time.TrustedTimeClient;

final class TrustedTimeClientAccessor {
    private static volatile Task<TrustedTimeClient> trustedTimeClientTask;

    private TrustedTimeClientAccessor() {
        throw new UnsupportedOperationException("This class is not to be instantiated");
    }

    /**
     * @see <a href="https://en.wikipedia.org/wiki/Double-checked_locking#Usage_in_Java">Double-checked_locking</a>
     * @see <a href="https://en.wikipedia.org/wiki/Singleton_pattern">Singleton_pattern</a>
     */
    @NonNull
    static Task<TrustedTimeClient> getTrustedTimeClientTask(@NonNull final Context context) {
        if (trustedTimeClientTask != null) {
            return trustedTimeClientTask;
        } else {
            synchronized (TrustedTimeClientAccessor.class) {
                if (trustedTimeClientTask == null) {
                    trustedTimeClientTask = TrustedTime.createClient(context);
                }
                return trustedTimeClientTask;
            }
        }
    }
}