package com.raul.androidapps.softwareteststarling.di.modules

import com.raul.androidapps.softwareteststarling.network.NetworkServiceFactory
import com.raul.androidapps.softwareteststarling.network.NetworkServiceFactoryImpl
import com.raul.androidapps.softwareteststarling.persistence.PersistenceManager
import com.raul.androidapps.softwareteststarling.persistence.PersistenceManagerImpl
import com.raul.androidapps.softwareteststarling.preferences.PreferencesManager
import com.raul.androidapps.softwareteststarling.preferences.PreferencesManagerImpl
import com.raul.androidapps.softwareteststarling.resources.ResourcesManager
import com.raul.androidapps.softwareteststarling.resources.ResourcesManagerImpl
import com.raul.androidapps.softwareteststarling.security.Encryption
import com.raul.androidapps.softwareteststarling.security.EncryptionImpl
import dagger.Binds
import dagger.Module


@Module(includes = [(ViewModelModule::class)])
abstract class StarlingBindsModule {

    @Binds
    abstract fun provideResourcesManager(resourcesManagerImpl: ResourcesManagerImpl): ResourcesManager

    @Binds
    abstract fun providePersistenceManager(persistenceManagerImpl: PersistenceManagerImpl): PersistenceManager

    @Binds
    abstract fun providePreferencesManager(preferencesManagerImpl: PreferencesManagerImpl): PreferencesManager

    @Binds
    abstract fun provideNetworkServiceFactory(networkServiceFactoryImp: NetworkServiceFactoryImpl): NetworkServiceFactory

    @Binds
    abstract fun provideEncryption(encryptionImpl: EncryptionImpl): Encryption

}