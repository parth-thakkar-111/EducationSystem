package com.codecx.educationsystem.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecx.educationsystem.databinding.CoursesitemlayoutBinding
import com.codecx.educationsystem.dataclasses.CoursesDataClass
import com.codecx.educationsystem.viewholders.CoursesViewHolder
import kotlin.math.sin

class CoursesAdaptor(val context: Context) : RecyclerView.Adapter<CoursesViewHolder>() {
    private var mList: List<CoursesDataClass> = ArrayList()
    var onItemClick: ((CoursesDataClass) -> Unit?)? = null
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
            tvCourseName.text = singleValue.courseName
            mCard.setOnClickListener {
                onItemClick?.invoke(singleValue)
            }
        }
    }

    fun submitData(newList: List<CoursesDataClass>) {
        mList = newList
        notifyDataSetChanged()
    }
}