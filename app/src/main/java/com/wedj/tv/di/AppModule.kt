package com.wedj.tv.di

import android.content.Context
import androidx.work.WorkManager
import androidx.work.multiprocess.RemoteWorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wedj.tv.data.PreferenceManager
import com.wedj.tv.util.AppCoroutineDispatchers
import com.wedj.tv.util.PermissionManager
import com.wedj.tv.util.StorageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideCoroutineDispatchers() = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main
    )

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager {
        return PreferenceManager(
            context.getSharedPreferences(
                "seeMyTask_prefs",
                Context.MODE_PRIVATE
            )
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)


    @Provides
    @Singleton
    fun provideRemoteWorkManager(@ApplicationContext context: Context): RemoteWorkManager =
        RemoteWorkManager.getInstance(context)

    @Provides
    fun providePermissionManager(@ApplicationContext context: Context): PermissionManager =
        PermissionManager(context)

    @Provides
    fun provideStorageManager(@ApplicationContext context: Context): StorageManager =
        StorageManager(context)

    @Provides
    fun contextProvider(@ApplicationContext context: Context) = context
}