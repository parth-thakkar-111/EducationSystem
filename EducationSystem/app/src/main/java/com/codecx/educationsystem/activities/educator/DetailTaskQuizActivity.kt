package com.codecx.educationsystem.activities.educator

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.codecx.educationsystem.activities.CourseDetailActivity.Companion.selectedTask
import com.codecx.educationsystem.adaptors.QuizAdaptor
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityDetailTaskQuizBinding
import com.codecx.educationsystem.dataclasses.QuizDataClass
import com.codecx.educationsystem.utils.beGone
import com.codecx.educationsystem.utils.beVisible
import com.codecx.educationsystem.utils.startNewActivity

class DetailTaskQuizActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailTaskQuizBinding
    private var quizAdaptor: QuizAdaptor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTaskQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initValues()
        if (savedInstanceState == null) {
            mViewModel.loadQuiz(selectedTask?.taskId)
        }
        if (!mDataHolder.isLearner()) {
            binding.btnCreateQuiz.beVisible()
        }
        binding.btnCreateQuiz.setOnClickListener {
            startNewActivity<CreateQuizActivity>()
        }
        mViewModel.quiz.observe(this) {
            it?.let { dd ->
                bindData(dd)
            }
        }
    }

    private fun bindData(dd: List<QuizDataClass>) {
        if (dd.isEmpty()) {
            binding.apply {
                mEmpty.beVisible()
                quizRecycler.beGone()
            }
        } else {
            binding.apply {
                mEmpty.beGone()
                quizRecycler.beVisible()
            }
            quizAdaptor?.submitData(dd)

        }
    }


    private fun initValues() {
        quizAdaptor = QuizAdaptor(this)
        binding.apply {
            headerLayout.tvHeader.text = selectedTask?.taskName
            quizRecycler.apply {
                layoutManager = LinearLayoutManager(
                    this@DetailTaskQuizActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = quizAdaptor
            }
        }
        quizAdaptor?.onItemClick = {
        }
        quizAdaptor?.onDeleteClick = {
            deleteQuiz(it)
        }
    }

    private fun deleteQuiz(it: QuizDataClass) {
        mViewModel.deleteQuiz(it)
    }

}