package com.codecx.educationsystem.activities

import android.os.Bundle
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityLoginBinding
import com.codecx.educationsystem.dataclasses.ProgressDataClass
import com.codecx.educationsystem.dataclasses.UserDataClass
import com.codecx.educationsystem.enums.AccountType
import com.codecx.educationsystem.utils.showToast
import com.codecx.educationsystem.utils.startNewActivity
import com.codecx.zipunzip.interfaces.ResultCallBacks

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnLogin.setOnClickListener {
                val email = txtEmail.editText?.text.toString()
                val password = txtPassword.editText?.text.toString()
                if (email.isEmpty()) {
                    txtEmail.error = "Required..."
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    txtPassword.error = "Required..."
                    return@setOnClickListener
                }
                txtEmail.error = null
                txtPassword.error = null
                startLogin(email, password)
            }
            btnSignUp.setOnClickListener {
                startNewActivity<SignUpActivity>(false)
            }
        }
    }

    private fun startLogin(email: String, password: String) {
        mDialogUtil?.showProgressDialog(ProgressDataClass("Logging..."), false)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                checkUserType()
            } else {
                mDialogUtil?.dismissDialog()
                showToast("Username or password is incorrect")

            }
        }.addOnFailureListener {
            showToast("Account not found")
            mDialogUtil?.dismissDialog()
        }
    }

    private fun checkUserType() {
        //request to get user account info
        mViewModel.requestForUserInfo(object : ResultCallBacks<UserDataClass> {
            override fun onFail(message: Exception) {
                mDialogUtil?.dismissDialog()
                showToast("Fail to start app")
                finishAffinity()
            }

            override fun onSuccess(result: UserDataClass) {
                mDialogUtil?.dismissDialog()
                mDataHolder.setData(result)
                startNewActivity<MainActivity>(true)

            }
        })
    }

}