package com.codecx.educationsystem.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecx.educationsystem.R
import com.codecx.educationsystem.databinding.CoursesitemlayoutBinding
import com.codecx.educationsystem.databinding.LearneritemlayoutBinding
import com.codecx.educationsystem.databinding.RequestcouseitemlayoutBinding
import com.codecx.educationsystem.dataclasses.CoursesDataClass
import com.codecx.educationsystem.utils.getMyColorStateList
import com.codecx.educationsystem.utils.getResourceColor
import com.codecx.educationsystem.utils.showToast
import com.codecx.educationsystem.viewholders.RequestCourseViewHolder
import com.codecx.educationsystem.viewholders.CoursesViewHolder
import com.google.android.material.button.MaterialButton
import kotlin.math.sin

class RequestCourseRecyclerAdaptor(val context: Context) :
    RecyclerView.Adapter<RequestCourseViewHolder>() {
    private var mList: List<CoursesDataClass> = ArrayList()
    var onCheckClick: ((CoursesDataClass) -> Unit?)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestCourseViewHolder {
        return RequestCourseViewHolder(
            RequestcouseitemlayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RequestCourseViewHolder, position: Int) {
        val singleValue = mList[position]
        holder.binding.apply {
            tvLeanerEmailAndName.text =
                "Course Name: ${singleValue.courseName}"
            btnCheck.setOnClickListener {
                (it as MaterialButton)
                if (it.text == "Submitted") {
                    context.showToast("Request Already submited")
                } else {
                    onCheckClick?.invoke(singleValue)
                    it.text = "Submitted"
                    it.backgroundTintList = context.getMyColorStateList(R.color.purple_500)
                    it.setTextColor(context.getResourceColor(R.color.white))

                }


            }
        }
    }

    fun submitData(newList: List<CoursesDataClass>) {
        mList = newList
        notifyDataSetChanged()
    }
}