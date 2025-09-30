package com.example.digitalclusterapp.core.data.di

import com.example.digitalclusterapp.core.data.remote.mqtt.MqttClusterBinder
import com.example.digitalclusterapp.core.data.repository.MqttClusterRepository
import com.example.digitalclusterapp.core.data.repository.MqttClusterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MqttModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideMqttClusterBinder(scope: CoroutineScope): MqttClusterBinder {
        return MqttClusterBinder(scope)
    }

    @Provides
    @Singleton
    fun provideClusterRepository(
        binder: MqttClusterBinder
    ): MqttClusterRepository {
        return MqttClusterRepositoryImpl(binder)
    }
}