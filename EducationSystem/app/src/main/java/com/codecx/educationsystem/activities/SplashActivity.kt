package com.codecx.educationsystem.activities

import android.os.Bundle
import android.os.Handler
import com.codecx.educationsystem.R
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.dataclasses.UserDataClass
import com.codecx.educationsystem.enums.AccountType
import com.codecx.educationsystem.utils.showLog
import com.codecx.educationsystem.utils.showToast
import com.codecx.educationsystem.utils.startNewActivity
import com.codecx.zipunzip.interfaces.ResultCallBacks
import java.lang.Exception

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //wait for 3sec
        Handler(mainLooper).postDelayed({
            //navigating next page
            if (firebaseAuth.currentUser != null) {
                //check user account type
                checkUserType()
            } else {

                startNewActivity<LoginActivity>(true)
            }
        }, 3000L)
    }

    private fun checkUserType() {
        //request to get user account info
        mViewModel.requestForUserInfo(object : ResultCallBacks<UserDataClass> {
            override fun onFail(message: Exception) {
                "mTagException".showLog(message.message.toString())
                showToast("Fail to start app")
                finishAffinity()
            }

            override fun onSuccess(result: UserDataClass) {
                mDataHolder.setData(result)
                startNewActivity<MainActivity>(true)

            }
        })
    }
}