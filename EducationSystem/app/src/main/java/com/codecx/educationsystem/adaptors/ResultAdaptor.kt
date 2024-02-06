package com.codecx.educationsystem.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecx.educationsystem.R
import com.codecx.educationsystem.databinding.CoursesitemlayoutBinding
import com.codecx.educationsystem.databinding.ResultitemlayoutBinding
import com.codecx.educationsystem.databinding.ResultlayoutBinding
import com.codecx.educationsystem.dataclasses.ScoreDataClass
import com.codecx.educationsystem.utils.setImage
import com.codecx.educationsystem.viewholders.ResultViewHolder
import kotlin.math.sin

class ResultAdaptor(val context: Context) : RecyclerView.Adapter<ResultViewHolder>() {
    private var mList: List<ScoreDataClass> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(
            ResultitemlayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val singleValue = mList[position]
        holder.binding.apply {
            val sb = java.lang.StringBuilder()
            sb.apply {
                append("Learner Name: ${singleValue.userDataClass?.name}, Learner Email: ${singleValue.userDataClass?.email}\n")
                append("Course: ${singleValue.task?.taskCourse?.courseName}, Task: ${singleValue.task?.taskName}\n")
                append("Score: ${singleValue.score}")
            }
            tvResultInfo.text = sb.toString()
            answeredLayout.removeAllViews()
            singleValue.answeredQuiz.forEach {
                val view = ResultlayoutBinding.inflate(LayoutInflater.from(context))
                view.apply {
                    tvOption.text = it.value.selectedOption
                    tvQuestion.text = it.value.quizQuestion
                    if (it.value.isCorrect) {
                        mImage.setImage(R.drawable.checked)
                    } else {
                        mImage.setImage(R.drawable.cross)
                    }
                }
                answeredLayout.addView(view.root)
            }
        }
    }

    fun submitData(newList: List<ScoreDataClass>) {
        mList = newList
        notifyDataSetChanged()
    }
}