package com.example.composesample

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.time.TrustedTime
import com.google.android.gms.time.TrustedTimeClient

object TrustedTimeClientAccessor {
    @Volatile
    private var trustedTimeClientTask: Task<TrustedTimeClient>? = null

    /**
     * @see [Double-checked_locking](https://en.wikipedia.org/wiki/Double-checked_locking.Usage_in_Java)
     * @see [Singleton_pattern](https://en.wikipedia.org/wiki/Singleton_pattern)
     */
    fun getTrustedTimeClientTask(context: Context): Task<TrustedTimeClient> {
        if (trustedTimeClientTask != null) {
            return trustedTimeClientTask!!
        } else {
            synchronized(TrustedTimeClientAccessor::class.java) {
                if (trustedTimeClientTask == null) {
                    trustedTimeClientTask = TrustedTime.createClient(context)
                }
                return trustedTimeClientTask!!
            }
        }
    }
}