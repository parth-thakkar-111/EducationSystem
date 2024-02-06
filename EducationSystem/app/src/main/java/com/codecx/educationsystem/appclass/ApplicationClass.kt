package com.codecx.educationsystem.appclass

import android.app.Application
import com.codecx.educationsystem.repos.FireBaseRepo
import com.codecx.educationsystem.utils.FireBaseRefrences
import com.codecx.educationsystem.utils.UserDataHolder
import com.codecx.educationsystem.viewmodels.MainViewModel
import com.codecx.educationsystem.viewmodels.SingleSharedViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ApplicationClass)
            modules(listOf(mainModules, singleInstances))
        }
    }

    val singleInstances = module {
        single { FirebaseAuth.getInstance() }
        single { FireBaseRefrences(androidContext()) }
        single { UserDataHolder() }
        single { SingleSharedViewModel() }
    }

    val mainModules = module {
        viewModel { MainViewModel(FireBaseRepo(androidContext(), get(), get(), get())) }
    }
}