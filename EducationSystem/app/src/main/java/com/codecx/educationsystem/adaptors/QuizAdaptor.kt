package com.codecx.educationsystem.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecx.educationsystem.databinding.CoursesitemlayoutBinding

import com.codecx.educationsystem.dataclasses.QuizDataClass
import com.codecx.educationsystem.dataclasses.TaskDataClass
import com.codecx.educationsystem.utils.beVisible
import com.codecx.educationsystem.utils.setImage
import com.codecx.educationsystem.viewholders.CoursesViewHolder
import kotlin.math.sin

class QuizAdaptor(val context: Context) : RecyclerView.Adapter<CoursesViewHolder>() {
    private var mList: List<QuizDataClass> = ArrayList()
    var onItemClick: ((QuizDataClass) -> Unit?)? = null
    var onDeleteClick: ((QuizDataClass) -> Unit?)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoursesViewHolder {
        return CoursesViewHolder(
            CoursesitemlayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: CoursesViewHolder, position: Int) {
        val singleValue = mList[position]
        holder.binding.apply {
            tvCourseName.text = singleValue.quizTitle
            mCard.setOnClickListener {
                onItemClick?.invoke(singleValue)
            }
            btnDeleteQuiz.beVisible()
            btnDeleteQuiz.setOnClickListener {
                onDeleteClick?.invoke(singleValue)
            }
        }
    }

    fun submitData(newList: List<QuizDataClass>) {
        mList = newList
        notifyDataSetChanged()
    }
}