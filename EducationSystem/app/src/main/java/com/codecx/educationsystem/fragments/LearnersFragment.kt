package com.codecx.educationsystem.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecx.educationsystem.R
import com.codecx.educationsystem.activities.CourseDetailActivity
import com.codecx.educationsystem.adaptors.CoursesAdaptor
import com.codecx.educationsystem.adaptors.ResultAdaptor
import com.codecx.educationsystem.blassclasses.BaseFragment
import com.codecx.educationsystem.databinding.FragmentLearnersBinding
import com.codecx.educationsystem.utils.beGone
import com.codecx.educationsystem.utils.beVisible


class LearnersFragment : BaseFragment(R.layout.fragment_learners) {
    private lateinit var binding: FragmentLearnersBinding
    private var resultAdaptor: ResultAdaptor? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLearnersBinding.bind(view)
        initRecycler()
        binding.root.isRefreshing=true
        if (savedInstanceState == null) {
            mSharedViewModel.loadResults()
        }
        binding.root.setOnRefreshListener {
            mSharedViewModel.loadResults()

        }
        mSharedViewModel.result.observe(viewLifecycleOwner) {
            binding.apply {
                root.isRefreshing=false
                if (it.isEmpty()) {
                    mEmpty.beVisible()
                    mResultRecycler.beGone()
                } else {
                    if (!mUserDataHolder.isLearner()) {
                        resultAdaptor?.submitData(it)
                    } else {
                        val ll =
                            it.filter { dd -> dd.userDataClass?.uId == mUserDataHolder.getUserData()?.uId }
                        if (ll.isEmpty()) {
                            mEmpty.beVisible()
                            mResultRecycler.beGone()
                        } else {
                            mEmpty.beGone()
                            mResultRecycler.beVisible()
                            resultAdaptor?.submitData(ll)
                        }
                    }
                    mEmpty.beGone()
                    mResultRecycler.beVisible()
                }
            }

        }
    }

    private fun initRecycler() {
        resultAdaptor = ResultAdaptor(requireContext())

        binding.mResultRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = resultAdaptor
        }

    }

}