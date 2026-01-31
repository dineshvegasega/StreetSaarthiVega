package com.vegasega.streetsaarthi

//import com.google.android.datatransport.runtime.scheduling.SchedulingConfigModule_ConfigFactory.config
//import com.google.firebase.FirebaseApp
//import com.google.firebase.remoteconfig.BuildConfig
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.vegasega.streetsaarthi.datastore.DataStoreUtil
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {
    companion object{
       // var scale10: Int = 0
    }
//   lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        DataStoreUtil.initDataStore(applicationContext)



//    FirebaseApp.initializeApp(applicationContext);
//        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

//        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
//            .setMinimumFetchIntervalInSeconds(3600)
//            .build()
//        mFirebaseRemoteConfig.fetchAndActivate()




//        ChiliPhotoPicker.init(
//            loader = CoilImageLoader(), authority = "com.vegasega.streetsaarthi.provider"
//        )
//        CallDataStore.initializeDataStore(
//            context = applicationContext,
//            dataBaseName = applicationContext.getString(R.string.app_name).replace(" ", "_")
//        )

//        var cacheExpiration = 43200L
//        if (BuildConfig.DEBUG) {
//            cacheExpiration = 0
//        } else {
//            cacheExpiration = 43200L // 12 hours same as the default value
//        }
//
//        val configSettings = FirebaseRemoteConfigSettings.Builder()
//            .setMinimumFetchIntervalInSeconds(cacheExpiration)
//            .build()
//
//        var config = FirebaseRemoteConfig.getInstance()
//        config.setConfigSettingsAsync(configSettings)
//        config.fetch(cacheExpiration).addOnCompleteListener {
//            Log.e("TAG", "addOnCompleteListener "+it)
//        }


    }


    override fun onTerminate() {
        super.onTerminate()
    }
}