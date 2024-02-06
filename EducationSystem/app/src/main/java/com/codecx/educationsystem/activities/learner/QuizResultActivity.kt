package com.codecx.educationsystem.activities.learner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codecx.educationsystem.R
import com.codecx.educationsystem.activities.CourseDetailActivity.Companion.selectedTask
import com.codecx.educationsystem.blassclasses.BaseActivity
import com.codecx.educationsystem.databinding.ActivityQuizResultBinding
import com.codecx.educationsystem.databinding.ResultlayoutBinding
import com.codecx.educationsystem.dataclasses.ResultDataClass
import com.codecx.educationsystem.dataclasses.ScoreDataClass
import com.codecx.educationsystem.utils.setImage
import com.codecx.educationsystem.viewmodels.SingleSharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizResultActivity : BaseActivity() {
    private lateinit var binding: ActivityQuizResultBinding
    private val mSharedViewModel: SingleSharedViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            mHeader.tvHeader.text = "Your Result"
        }
        if (savedInstanceState == null) {
            mSharedViewModel.loadResult()
        }
        lifeCycleObserver(savedInstanceState)
    }

    private fun lifeCycleObserver(savedInstanceState: Bundle?) {
        mSharedViewModel.result.observe(this) {
            if (savedInstanceState == null) {
                uploadResult(it)
            }
            bindData(it)
        }
    }

    private fun bindData(resultList: List<ResultDataClass>?) {
        resultList?.let { mList ->
            binding.apply {
                val sb = java.lang.StringBuilder()
                sb.append("Total Question: ${mList.size}, Correct Questions: ${resultList.filter { it.isCorrect }.size}, Incorrect Questions: ${resultList.filter { !it.isCorrect }.size}\n")
                sb.append("Your Score is : ${resultList.filter { it.isCorrect }.size} out of ${mList.size}\n")
                sb.append("Answered Questions are:-")
                tvResultInfo.text=sb.toString()
            }
            mList.forEach {
                val view = ResultlayoutBinding.inflate(layoutInflater)
                view.apply {
                    tvOption.text = it.selectedOption
                    tvQuestion.text = it.quizQuestion
                    if (it.isCorrect) {
                        mImage.setImage(R.drawable.checked)
                    } else {
                        mImage.setImage(R.drawable.cross)
                    }
                }
                binding.answeredLayout.addView(view.root)
            }
        }
    }

    private fun uploadResult(resultList: List<ResultDataClass>?) {
        resultList?.let { mList ->
            val obj = ScoreDataClass()
            obj.apply {
                this.task = selectedTask
                this.score = mList.filter { it.isCorrect }.size
                this.userDataClass = mDataHolder.getUserData()
            }
            val mp = HashMap<String, ResultDataClass>()
            mList.forEach {
                val key = fireBaseRefrences.scoreRef.push().key
                it.resultId = key
                mp[key!!] = it
            }
            obj.answeredQuiz = mp

           mViewModel.uploadResultInfo(obj)
        }
    }
}