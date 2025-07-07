package com.example.myapplication.di

import com.example.myapplication.api.PlacesApiService
import com.example.myapplication.api.TripsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("PlacesRetrofit")
    fun providePlacesRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePlacesApiService(
        @Named("PlacesRetrofit") retrofit: Retrofit
    ): PlacesApiService =
        retrofit.create(PlacesApiService::class.java)

    @Provides
    @Singleton
    @Named("TripsRetrofit")
    fun provideTripsRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // 模擬器對應 localhost
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTripsApiService(
        @Named("TripsRetrofit") retrofit: Retrofit
    ): TripsApiService {
        return retrofit.create(TripsApiService::class.java)
    }
}