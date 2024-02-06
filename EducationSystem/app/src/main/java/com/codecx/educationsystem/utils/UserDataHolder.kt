package com.codecx.educationsystem.utils

import com.codecx.educationsystem.dataclasses.UserDataClass
import com.google.android.gms.common.internal.AccountType

class UserDataHolder {
    private var mUserData: UserDataClass? = null
    fun setData(user: UserDataClass) {
        mUserData = user
    }

    fun getUserData(): UserDataClass? {
        return mUserData
    }

    fun isLearner(): Boolean {
        return mUserData?.userType == com.codecx.educationsystem.enums.AccountType.Learner.name
    }
}