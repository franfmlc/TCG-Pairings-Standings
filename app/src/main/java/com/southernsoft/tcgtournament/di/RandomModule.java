package com.southernsoft.tcgtournament.di;

import java.util.Random;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RandomModule {

    @Provides
    static Random providesRandom() {
        return new Random();
    }
}
