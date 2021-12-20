package io.github.collins993.learnersguide.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.collins993.learnersguide.databinding.CourseItemsBinding
import io.github.collins993.learnersguide.databinding.SuggestionLayoutBinding
import io.github.collins993.learnersguide.db.entity.Courses
import io.github.collins993.learnersguide.model.SuggestedCourses

class SuggestedCourseAdapter() : RecyclerView.Adapter<SuggestedCourseAdapter.CourseViewHolder>() {


    inner class CourseViewHolder(val binding: SuggestionLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<SuggestedCourses>() {

        override fun areItemsTheSame(oldItem: SuggestedCourses, newItem: SuggestedCourses): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: SuggestedCourses, newItem: SuggestedCourses): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(SuggestionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val suggestedCourses = differ.currentList[position]

        holder.binding.courseTitle.text = suggestedCourses.title
        holder.binding.date.text = suggestedCourses.date.toString()

        Glide.with(holder.itemView.context)
            .load(suggestedCourses.img)
            .into(holder.binding.profileImg)

        holder.itemView.setOnClickListener {


            onItemClickListener?.let { it(suggestedCourses) }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((SuggestedCourses) -> Unit)? = null


    fun setOnItemClickListener(listener: (SuggestedCourses) -> Unit){
        onItemClickListener = listener
    }


}