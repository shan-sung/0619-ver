package com.example.myapplication.di

import com.example.myapplication.api.AuthApiService
import com.example.myapplication.api.ChatApiService
import com.example.myapplication.api.FriendApiService
import com.example.myapplication.api.FriendRepository
import com.example.myapplication.api.PlacesApiService
import com.example.myapplication.api.saved.SavedApiService
import com.example.myapplication.api.TripsApiService
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.util.LocalDateAdapter
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideAuthApiService(
        @Named("TripsRetrofit") retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFriendApiService(
        @Named("TripsRetrofit") retrofit: Retrofit
    ): FriendApiService {
        return retrofit.create(FriendApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFriendRepository(
        friendApiService: FriendApiService
    ): FriendRepository {
        return FriendRepository(friendApiService)
    }
    @Provides
    @Singleton
    fun providePlacesApi(
        @Named("PlacesRetrofit") retrofit: Retrofit
    ): PlacesApiService {
        return retrofit.create(PlacesApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideSavedApiService(
        @Named("TripsRetrofit") retrofit: Retrofit
    ): SavedApiService {
        return retrofit.create(SavedApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("TripsRetrofit")
    fun provideTripsRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()

        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                val token = CurrentUser.token
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client) // ✅ 設定客製 client
            .build()
    }


    @Provides
    @Singleton
    @Named("PlacesRetrofit")
    fun providePlacesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTripsApiService(
        @Named("TripsRetrofit") retrofit: Retrofit
    ): TripsApiService {
        return retrofit.create(TripsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApiService(
        @Named("TripsRetrofit") retrofit: Retrofit
    ): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }
}