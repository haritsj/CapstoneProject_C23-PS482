package com.project.capstoneapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.capstoneapp.data.remote.response.RecommendationResponse
import com.project.capstoneapp.databinding.ItemRecommendationListBinding
import java.text.DecimalFormat

class RecommendationListAdapter (
    private val recommendationList: ArrayList<RecommendationResponse>
) : RecyclerView.Adapter<RecommendationListAdapter.RecommendationListViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecommendationListViewHolder {
        val binding = ItemRecommendationListBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return RecommendationListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationListViewHolder, position: Int) {
        val item = recommendationList[position]
        holder.setItemData(item)
        holder.itemView.setOnClickListener{
            onItemClickCallback?.onItemClicked(recommendationList[holder.adapterPosition])
        }
    }

    override fun getItemCount() = recommendationList.size

    class RecommendationListViewHolder(private var itemBinding: ItemRecommendationListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun setItemData(recommendationResponse: RecommendationResponse) {
            itemBinding.tvRecommendationName.text = recommendationResponse.exercise
            itemBinding.tvRecommendationTime.text = StringBuilder("${recommendationResponse.timeMinute} Mins")
            itemBinding.tvRecommendationCalorie.text = StringBuilder("${
                DecimalFormat("0.00").format(recommendationResponse.calorie)} Cal")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRecomendationList(recommendationList: ArrayList<RecommendationResponse>) {
        this.recommendationList.clear()
        this.recommendationList.addAll(recommendationList)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: RecommendationResponse)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}