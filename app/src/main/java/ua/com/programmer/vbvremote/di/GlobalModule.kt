package ua.com.programmer.vbvremote.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.com.programmer.vbvremote.settings.SettingsHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GlobalModule {

    @Provides
    @Singleton
    fun provideSettings(@ApplicationContext context: Context): SettingsHelper {
        return SettingsHelper(context)
    }

}