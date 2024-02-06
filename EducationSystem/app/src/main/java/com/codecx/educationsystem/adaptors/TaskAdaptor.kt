package com.codecx.educationsystem.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecx.educationsystem.databinding.CoursesitemlayoutBinding
import com.codecx.educationsystem.databinding.TaskitemlayoutBinding
import com.codecx.educationsystem.dataclasses.TaskDataClass
import com.codecx.educationsystem.utils.setImage
import com.codecx.educationsystem.viewholders.TaskViewHolder
import kotlin.math.sin

class TaskAdaptor(val context: Context) : RecyclerView.Adapter<TaskViewHolder>() {
    private var mList: List<TaskDataClass> = ArrayList()
    var onItemClick: ((TaskDataClass) -> Unit?)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskitemlayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val singleValue = mList[position]
        holder.binding.apply {
            tvTaskName.text = singleValue.taskName
            taskImage.setImage(singleValue.taskImageUrl)
            mTaskCard.setOnClickListener {
                onItemClick?.invoke(singleValue)
            }
        }
    }

    fun submitData(newList: List<TaskDataClass>) {
        mList = newList
        notifyDataSetChanged()
    }
}