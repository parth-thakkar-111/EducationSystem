package com.codecx.educationsystem.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codecx.educationsystem.R
import com.codecx.educationsystem.activities.LoginActivity
import com.codecx.educationsystem.blassclasses.BaseFragment
import com.codecx.educationsystem.databinding.FragmentProfileBinding
import com.codecx.educationsystem.utils.startNewActivity


class ProfileFragment : BaseFragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        with(binding) {
            btnLogOut.setOnClickListener {
                mAuth.signOut()
                requireActivity().startNewActivity<LoginActivity>(true)
            }
            tvEmail.text = mUserDataHolder.getUserData()?.email
            tvName.text = mUserDataHolder.getUserData()?.name
            tvPhoneNo.text = mUserDataHolder.getUserData()?.phoneNo
            tvAccountType.text = mUserDataHolder.getUserData()?.userType
        }
    }

}