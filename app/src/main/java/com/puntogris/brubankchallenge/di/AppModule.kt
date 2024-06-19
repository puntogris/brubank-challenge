package com.puntogris.brubankchallenge.di

import android.content.Context
import androidx.room.Room
import com.puntogris.brubankchallenge.data.local.AppDatabase
import com.puntogris.brubankchallenge.data.local.GenresDao
import com.puntogris.brubankchallenge.data.local.MoviesDao
import com.puntogris.brubankchallenge.data.remote.ApiService
import com.puntogris.brubankchallenge.data.repository.RepositoryImpl
import com.puntogris.brubankchallenge.domain.IRepository
import com.puntogris.brubankchallenge.utils.DispatcherProvider
import com.puntogris.brubankchallenge.utils.StandardDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val ROOM_DATABASE_NAME = "db"

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideRepository(
        apiService: ApiService,
        moviesDao: MoviesDao,
        genresDao: GenresDao,
        dispatcherProvider: DispatcherProvider
    ): IRepository {
        return RepositoryImpl(apiService, moviesDao, genresDao, dispatcherProvider)
    }

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${ApiService.API_KEY}")
                    .build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room
            .databaseBuilder(
                appContext,
                AppDatabase::class.java,
                ROOM_DATABASE_NAME
            )
            .build()
    }

    @Singleton
    @Provides
    fun providesMoviesDao(appDatabase: AppDatabase) = appDatabase.moviesDao

    @Singleton
    @Provides
    fun providesGenresDao(appDatabase: AppDatabase) = appDatabase.genresDao

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = StandardDispatchers()
}