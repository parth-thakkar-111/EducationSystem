package com.codecx.educationsystem.activities

import android.os.Bundle
import com.codecx.educationsystem.R
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivitySignUpBinding
import com.codecx.educationsystem.dataclasses.ProgressDataClass
import com.codecx.educationsystem.dataclasses.UserDataClass
import com.codecx.educationsystem.utils.showToast
import com.codecx.zipunzip.interfaces.ResultCallBacks

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var userType: com.codecx.educationsystem.enums.AccountType? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnSignUp.setOnClickListener {
                val email = txtEmail.editText?.text.toString()
                val name = txtName.editText?.text.toString()
                val phoneNo = txtPhone.editText?.text.toString()
                val password = txtPassword.editText?.text.toString()
                if (name.isEmpty()) {
                    txtName.error = "Required..."
                    return@setOnClickListener
                }
                if (phoneNo.isEmpty()) {
                    txtPhone.error = "Required..."
                    return@setOnClickListener
                }
                if (email.isEmpty()) {
                    txtEmail.error = "Required..."
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    txtPassword.error = "Required..."
                    return@setOnClickListener
                }
                if (userType == null) {
                    showToast("Select account type...")
                    return@setOnClickListener
                }
                txtEmail.error = null
                txtPassword.error = null
                txtName.error = null
                txtPhone.error = null
                startSignUp(email, name, password, phoneNo)
            }
            btnLogin.setOnClickListener {
                finish()
            }
            radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                if (i == R.id.btnEducator) {
                    userType = com.codecx.educationsystem.enums.AccountType.Educator
                } else {
                    userType = com.codecx.educationsystem.enums.AccountType.Learner
                }
            }
        }
    }

    private fun startSignUp(email: String, name: String, password: String, phoneNo: String) {
        mDialogUtil?.showProgressDialog(ProgressDataClass("Signing Up..."), false)
        mViewModel.signUpUser(
            UserDataClass(
                name = name,
                phoneNo = phoneNo,
                userType = this.userType?.name,
                email = email, password = password
            ), object : ResultCallBacks<String> {
                override fun onFail(message: Exception) {
                    runOnUiThread {
                        mDialogUtil?.dismissDialog()
                        showToast(message.message)
                    }
                }

                override fun onSuccess(result: String) {
                    runOnUiThread {
                        mDialogUtil?.dismissDialog()
                        showToast(result)
                        finish()
                    }
                }

            }
        )
    }
}