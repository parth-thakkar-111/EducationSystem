package com.codecx.educationsystem.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecx.educationsystem.R
import com.codecx.educationsystem.activities.CourseDetailActivity
import com.codecx.educationsystem.adaptors.CoursesAdaptor
import com.codecx.educationsystem.adaptors.RequestCourseRecyclerAdaptor
import com.codecx.educationsystem.blassclasses.BaseFragment
import com.codecx.educationsystem.databinding.FragmentCoursesBinding
import com.codecx.educationsystem.databinding.NewcoursedialoglayoutBinding
import com.codecx.educationsystem.databinding.RequestcoursedialogBinding
import com.codecx.educationsystem.dataclasses.CoursesDataClass
import com.codecx.educationsystem.dataclasses.RequestDataClass
import com.codecx.educationsystem.sealdclasses.DataState
import com.codecx.educationsystem.utils.beGone
import com.codecx.educationsystem.utils.beVisible
import com.codecx.educationsystem.utils.showToast
import com.codecx.zipunzip.interfaces.ResultCallBacks
import kotlinx.coroutines.flow.collectLatest
import java.lang.Exception


class CoursesFragment : BaseFragment(R.layout.fragment_courses) {
    private lateinit var binding: FragmentCoursesBinding
    private var mCourseAdapter: CoursesAdaptor? = null
    private var requestCourseRecyclerAdaptor: RequestCourseRecyclerAdaptor? = null

    companion object {
        var selectedCourse: CoursesDataClass? = null
    }

    private val requestcoursedialogBinding: RequestcoursedialogBinding by lazy {
        RequestcoursedialogBinding.inflate(
            layoutInflater
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCoursesBinding.bind(view)
        setUpView()
        if (savedInstanceState == null) {
            if (mUserDataHolder.isLearner()) {
                mSharedViewModel.loadAssignedCourses()
                mSharedViewModel.loadCoursesForRequest()
            } else {
                mSharedViewModel.loadCourses()
            }
        }
        binding.root.setOnRefreshListener {
            if (mUserDataHolder.isLearner()) {
                mSharedViewModel.loadAssignedCourses()
                mSharedViewModel.loadCoursesForRequest()
            } else {
                mSharedViewModel.loadCourses()
            }
        }
        binding.btnAddCourse.beVisible()

        if (mUserDataHolder.isLearner()) {
            binding.btnAddCourse.text = "Request for course"
        }
        binding.btnAddCourse.setOnClickListener {
            if (mUserDataHolder.isLearner()) {
                showCoursesDialog()
            } else {
                showAddCourseDialog()
            }
        }
        lifeCycleObserver()
    }

    private fun showCoursesDialog() {
        mDialogUtil?.showDialog(requestcoursedialogBinding.root)

    }

    private fun showAddCourseDialog() {
        val newcoursedialoglayoutBinding: NewcoursedialoglayoutBinding =
            NewcoursedialoglayoutBinding.inflate(layoutInflater)
        mDialogUtil?.showDialog(newcoursedialoglayoutBinding.root, true)
        newcoursedialoglayoutBinding.apply {
            btnCreate.setOnClickListener {
                val mTextCourseName = txtCourseName.editText?.text?.toString()
                val mTextCourseDescription = txtCourceDescription.editText?.text?.toString()
                if (mTextCourseName?.isEmpty() == true) {
                    txtCourseName.error = "Required..."
                    return@setOnClickListener
                }
                if (mTextCourseDescription?.isEmpty() == true) {
                    txtCourceDescription.error = "Required..."
                    return@setOnClickListener
                }
                txtCourseName.error = null
                txtCourceDescription.error = null
                val dd = CoursesDataClass(mTextCourseName, mTextCourseDescription ?: "")
                mDialogUtil?.dismissDialog()
                mSharedViewModel.addCourse(dd, object : ResultCallBacks<CoursesDataClass> {
                    override fun onFail(message: Exception) {
                        requireActivity().runOnUiThread {
                            requireContext().showToast("Fail to create")
                        }


                    }

                    override fun onSuccess(result: CoursesDataClass) {
                        requireActivity().runOnUiThread {
                            requireContext().showToast("${result.courseName} create successful")
                        }


                    }

                })
            }

        }
    }

    private fun lifeCycleObserver() {
        mSharedViewModel.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                courses.collectLatest {
                    when (it) {
                        is DataState.Loading -> {
                            binding.root.isRefreshing = true
                        }
                        is DataState.Initial -> {

                        }
                        is DataState.Error -> {
                            binding.root.isRefreshing = false
                        }
                        is DataState.Result<*> -> {
                            val list = it.result as List<CoursesDataClass>
                            bindData(list)
                        }
                    }
                }
            }
            coursesForRequest.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    requestcoursedialogBinding.apply {
                        mEmpty.beVisible()
                        learnersRecycler.beGone()
                    }
                } else {
                    requestCourseRecyclerAdaptor?.submitData(it)
                    requestcoursedialogBinding.apply {
                        mEmpty.beGone()
                        learnersRecycler.beVisible()
                    }
                }
            }
        }
    }

    private fun bindData(list: List<CoursesDataClass>) {
        with(binding) {
            root.isRefreshing = false
            if (list.isNotEmpty()) {
                mEmpty.beGone()
                mCourseAdapter?.submitData(list)
                mCoursesRecycler.beVisible()
            } else {
                mEmpty.beVisible()
                mCoursesRecycler.beGone()
            }
        }


    }

    private fun setUpView() {
        requestCourseRecyclerAdaptor = RequestCourseRecyclerAdaptor(requireContext())
        mCourseAdapter = CoursesAdaptor(requireContext())
        if (!mUserDataHolder.isLearner()) {
            binding.btnAddCourse.beVisible()
        }
        binding.mCoursesRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = mCourseAdapter
        }
        mCourseAdapter?.onItemClick = { value ->
            selectedCourse = value
            startActivity(Intent(requireActivity(), CourseDetailActivity::class.java))
        }
        requestcoursedialogBinding.learnersRecycler.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = requestCourseRecyclerAdaptor
        }
        requestCourseRecyclerAdaptor?.onCheckClick = { value ->
            val requestDataClass = RequestDataClass()
            requestDataClass.course = value
            requestDataClass.user = mUserDataHolder.getUserData()
            mSharedViewModel.submitRequest(requestDataClass,
                object : ResultCallBacks<RequestDataClass> {
                    override fun onFail(message: Exception) {
                        requireActivity().runOnUiThread {
                            requireContext().showToast(message.message)
                        }
                    }

                    override fun onSuccess(result: RequestDataClass) {
                        requireActivity().runOnUiThread {
                            requireContext().showToast("Request Submitted Successfully")
                        }
                    }

                })
            Unit
        }
    }

}