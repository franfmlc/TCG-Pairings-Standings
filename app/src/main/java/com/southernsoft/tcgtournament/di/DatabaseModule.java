package com.southernsoft.tcgtournament.di;

import android.content.Context;

import javax.inject.Singleton;

import com.southernsoft.tcgtournament.LocalDatabase;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DatabaseModule {

    @Provides
    @Singleton
    static LocalDatabase providesDatabase(@ApplicationContext Context context) {
        return LocalDatabase.getDatabase(context);
    }
}
