package com.codecx.educationsystem.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecx.educationsystem.activities.educator.DetailTaskQuizActivity
import com.codecx.educationsystem.activities.learner.TaskQuizActivityForLearner
import com.codecx.educationsystem.adaptors.AssignedRecyclerAdaptor
import com.codecx.educationsystem.adaptors.RequestRecyclerAdaptor
import com.codecx.educationsystem.adaptors.TaskAdaptor
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityCourseDetailBinding
import com.codecx.educationsystem.databinding.AddlearnerdialogBinding
import com.codecx.educationsystem.databinding.NewtaskdialoglayoutBinding
import com.codecx.educationsystem.databinding.RequestlearnerdialogBinding
import com.codecx.educationsystem.dataclasses.AssignedDataClass
import com.codecx.educationsystem.dataclasses.TaskDataClass
import com.codecx.educationsystem.fragments.CoursesFragment.Companion.selectedCourse
import com.codecx.educationsystem.utils.beGone
import com.codecx.educationsystem.utils.beVisible
import com.codecx.educationsystem.utils.showToast
import com.codecx.educationsystem.utils.startNewActivity
import com.codecx.zipunzip.interfaces.ResultCallBacks
import java.lang.Exception

class CourseDetailActivity : BaseActivity() {
    private var mTaskAdapter: TaskAdaptor? = null
    private lateinit var binding: ActivityCourseDetailBinding
    private var newtaskdialoglayoutBinding: NewtaskdialoglayoutBinding? = null
    private var taskImageUri: Uri = Uri.EMPTY
    private var mAssignedRecyclerAdaptor: AssignedRecyclerAdaptor? = null
    private var mRequestRecyclerAdaptor: RequestRecyclerAdaptor? = null
    val requestlearnerdialogBinding: RequestlearnerdialogBinding by lazy {
        RequestlearnerdialogBinding.inflate(
            layoutInflater
        )
    }

    val addlearnerdialogBinding: AddlearnerdialogBinding by lazy {
        AddlearnerdialogBinding.inflate(layoutInflater)
    }

    companion object {
        var selectedTask: TaskDataClass? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            mViewModel.loadTasks(selectedCourse?.courseUid)
            mViewModel.loadAssignedLearners(selectedCourse?.courseUid)
            mViewModel.loadRequested(selectedCourse?.courseUid)
            mViewModel.loadLearners()
        }
        initValues()




        if (!mDataHolder.isLearner()) {
            binding.btnAddTask.beVisible()
            binding.headerLayout.headerBtnsLayout.beVisible()
        }
        binding.headerLayout.apply {
            btnAddLearner.setOnClickListener {
                showAddLearnerDialog()
            }
            btnRequests.setOnClickListener {
                showRequestedLearners()
            }
        }
        binding.btnAddTask.setOnClickListener {
            showAddTaskDialog()
        }
        mViewModel.tasks.observe(this) {
            if (it.isNotEmpty()) {
                binding.apply {
                    mEmpty.beGone()
                    tasksRecycler.beVisible()
                }
                mTaskAdapter?.submitData(it)
            } else {
                binding.apply {
                    mEmpty.beVisible()
                    tasksRecycler.beGone()
                }
            }
        }
        mViewModel.requestedLearners.observe(this) {
            requestlearnerdialogBinding.apply {
                if (it.isEmpty()) {
                    mEmpty.beVisible()
                    learnersRecycler.beGone()
                } else {
                    mRequestRecyclerAdaptor?.submitData(it)
                    learnersRecycler.beVisible()
                    mEmpty.beGone()
                }

            }

        }
        mViewModel.assignedLearners.observe(this) {
            addlearnerdialogBinding.apply {
                if (it.isEmpty()) {
                    mEmpty.beVisible()
                    learnersRecycler.beGone()
                } else {
                    learnersRecycler.beVisible()
                    mEmpty.beGone()
                    mAssignedRecyclerAdaptor?.submitData(it)

                }
            }

        }
    }

    private fun showRequestedLearners() {


        mDialogUtil?.showDialog(requestlearnerdialogBinding.root)
    }

    private fun showAddLearnerDialog() {


        mDialogUtil?.showDialog(addlearnerdialogBinding.root)

    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
                taskImageUri = uri
                newtaskdialoglayoutBinding?.taskImage?.setImageURI(taskImageUri)
            }
        }

    private fun showAddTaskDialog() {
        newtaskdialoglayoutBinding =
            NewtaskdialoglayoutBinding.inflate(layoutInflater)
        mDialogUtil?.showDialog(newtaskdialoglayoutBinding?.root!!, true)
        newtaskdialoglayoutBinding?.apply {
            btnSelectImage.setOnClickListener {
                resultLauncher.launch(Intent(Intent.ACTION_PICK).also {
                    it.type = "image/*"
                })
            }
            btnCreateTask.setOnClickListener {
                val mTaskName = txtTaskName.editText?.text?.toString()
                if (mTaskName?.isEmpty() == true) {
                    txtTaskName.error = "Required..."
                    return@setOnClickListener
                }


                txtTaskName.error = null
                val dd = TaskDataClass(mTaskName)
                dd.taskCourse = selectedCourse
                mDialogUtil?.dismissDialog()
                mViewModel.addTask(dd, taskImageUri, object : ResultCallBacks<TaskDataClass> {
                    override fun onFail(message: Exception) {
                        taskImageUri = Uri.EMPTY
                        runOnUiThread {
                            showToast("Fail to create")
                        }
                        mViewModel.closeProgress()
                    }

                    override fun onSuccess(result: TaskDataClass) {
                        taskImageUri = Uri.EMPTY
                        runOnUiThread {
                            showToast("${result.taskName} create successful")
                        }
                        mViewModel.closeProgress()

                    }

                })
            }

        }
    }


    private fun initValues() {
        mAssignedRecyclerAdaptor = AssignedRecyclerAdaptor(this)
        mAssignedRecyclerAdaptor?.onDeleteClick = { value ->
            mViewModel.deleteAssginedLearner(value)
            Unit
        }

        mRequestRecyclerAdaptor = RequestRecyclerAdaptor(this)
        mRequestRecyclerAdaptor?.onDeleteClick = { value ->
            mViewModel.deleteRequest(value)
            Unit
        }
        mRequestRecyclerAdaptor?.onAcceptClick = { value ->
            val assignDataClass = AssignedDataClass()
            assignDataClass.user = value.user
            assignDataClass.course = value.course
            mViewModel.addLearnerIntoAssignedCourse(assignDataClass,
                object : ResultCallBacks<AssignedDataClass> {
                    override fun onFail(message: Exception) {
                        runOnUiThread {
                            showToast(message.message)
                        }
                    }

                    override fun onSuccess(result: AssignedDataClass) {
                        mViewModel.deleteRequest(value)
                        runOnUiThread {
                            showToast("${result.user?.name} add successful")
                        }
                    }

                })
            Unit
        }
        mTaskAdapter = TaskAdaptor(this)
        binding.apply {
            headerLayout.tvHeader.text = "Add Tasks"
            tvCourseName.text = selectedCourse?.courseName
            tvCourseDes.text = selectedCourse?.courseDescription
            tasksRecycler.apply {
                layoutManager = GridLayoutManager(
                    this@CourseDetailActivity,
                    2
                )
                adapter = mTaskAdapter
            }
        }
        mTaskAdapter?.onItemClick = {
            selectedTask = it
            if (mDataHolder.isLearner()) {
                startNewActivity<TaskQuizActivityForLearner>()
            } else {
                startNewActivity<DetailTaskQuizActivity>()

            }
        }

        requestlearnerdialogBinding.apply {
            learnersRecycler.apply {
                layoutManager = LinearLayoutManager(
                    this@CourseDetailActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = mRequestRecyclerAdaptor
            }

        }

        addlearnerdialogBinding.apply {
            learnersRecycler.apply {
                layoutManager = LinearLayoutManager(
                    this@CourseDetailActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = mAssignedRecyclerAdaptor
            }

            btnAddLearner.setOnClickListener {
                val mEmail = txtEmail.editText?.text?.toString()
                if (mEmail?.isEmpty() == true) {
                    txtEmail.error = "Required..."
                } else if (mEmail?.contains("@") == false) {
                    txtEmail.error = "Invalid Email Address"
                } else {
                    if (mViewModel.mLearners.isEmpty()) {
                        showToast("Their is no learner")
                    } else {
                        val learner = mViewModel.mLearners.filter { it.email == mEmail }
                        if (learner.isEmpty()) {
                            showToast("Learner not found")
                        } else {
                            val assignDataClass: AssignedDataClass = AssignedDataClass()
                            assignDataClass.user = learner.last()
                            assignDataClass.course = selectedCourse
                            mViewModel.addLearnerIntoAssignedCourse(assignDataClass,
                                object : ResultCallBacks<AssignedDataClass> {
                                    override fun onFail(message: Exception) {
                                        runOnUiThread {
                                            showToast(message.message)
                                        }
                                    }

                                    override fun onSuccess(result: AssignedDataClass) {
                                        runOnUiThread {
                                            showToast("${result.user?.name} add successful")

                                        }
                                    }

                                })
                        }
                    }
                }
            }
        }
    }
}