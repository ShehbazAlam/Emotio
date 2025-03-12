package com.shehbaz.emotio.adaptors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shehbaz.emotio.databinding.ListItemAnalystBinding
import com.shehbaz.emotio.models.Analyst

class AnalystAdapter(private val analystList: List<Analyst>) :
    RecyclerView.Adapter<AnalystAdapter.AnalystViewHolder>() {

    inner class AnalystViewHolder(val binding: ListItemAnalystBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalystViewHolder {
        val binding = ListItemAnalystBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnalystViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnalystViewHolder, position: Int) {
        val analyst = analystList[position]
        holder.binding.tvName.text = analyst.name
        holder.binding.tvAge.text = "Age: ${analyst.age}"
        holder.binding.tvGender.text = "Gender: ${analyst.gender}"
        holder.binding.tvCell.text = "Cell: ${analyst.cell}"
    }

    override fun getItemCount(): Int = analystList.size
}
