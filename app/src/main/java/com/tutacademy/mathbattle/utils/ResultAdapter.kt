package com.tutacademy.mathbattle.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tutacademy.mathbattle.data.ResultModel
import com.tutacademy.mathbattle.databinding.ResultItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ResultDiff = object : DiffUtil.ItemCallback<ResultModel>() {
    override fun areItemsTheSame(oldItem: ResultModel, newItem: ResultModel): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: ResultModel, newItem: ResultModel): Boolean {
        return oldItem == newItem
    }
}


class ResultAdapter() : ListAdapter<ResultModel, ResultAdapter.ResultViewHolder>(ResultDiff) {
    inner class ResultViewHolder(private val binding: ResultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(resultModel: ResultModel) {
            binding.scoreTv.text = "${resultModel.score}"
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            binding.dateTv.text = dateFormat.format(Date(resultModel.time))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = ResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}