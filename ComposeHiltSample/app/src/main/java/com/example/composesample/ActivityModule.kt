package com.example.composesample

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.time.TrustedTime
import com.google.android.gms.time.TrustedTimeClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule {
    @Provides
    fun provideTrustedTimeClientTask(@ActivityContext context: Context): Task<TrustedTimeClient> {
        return TrustedTime.createClient(context)
    }
}