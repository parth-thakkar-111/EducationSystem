package com.codecx.educationsystem.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codecx.educationsystem.databinding.CoursesitemlayoutBinding
import com.codecx.educationsystem.databinding.LearneritemlayoutBinding
import com.codecx.educationsystem.dataclasses.RequestDataClass
import com.codecx.educationsystem.utils.beVisible
import com.codecx.educationsystem.viewholders.AssignViewHolder
import com.codecx.educationsystem.viewholders.CoursesViewHolder
import kotlin.math.sin

class RequestRecyclerAdaptor(val context: Context) : RecyclerView.Adapter<AssignViewHolder>() {
    private var mList: List<RequestDataClass> = ArrayList()
    var onDeleteClick: ((RequestDataClass) -> Unit?)? = null
    var onAcceptClick: ((RequestDataClass) -> Unit?)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignViewHolder {
        return AssignViewHolder(
            LearneritemlayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: AssignViewHolder, position: Int) {
        val singleValue = mList[position]
        holder.binding.apply {
            tvLeanerEmailAndName.text =
                "Name: ${singleValue.user?.name}\nEmail: ${singleValue.user?.email}"
            btnRemove.setOnClickListener {
                onDeleteClick?.invoke(singleValue)
            }
            btnCheck.beVisible()
            btnCheck.setOnClickListener {
                onAcceptClick?.invoke(singleValue)
            }
        }
    }

    fun submitData(newList: List<RequestDataClass>) {
        mList = newList
        notifyDataSetChanged()
    }
}