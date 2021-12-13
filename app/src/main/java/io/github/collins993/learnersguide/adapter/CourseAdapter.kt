package io.github.collins993.learnersguide.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.github.collins993.learnersguide.databinding.CourseItemsBinding
import io.github.collins993.learnersguide.db.entity.Courses

class CourseAdapter() : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {


    inner class CourseViewHolder(val binding: CourseItemsBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Courses>() {

        override fun areItemsTheSame(oldItem: Courses, newItem: Courses): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Courses, newItem: Courses): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(CourseItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val courses = differ.currentList[position]

        holder.binding.title.text = courses.title
        holder.binding.headline.text = courses.headLine

        Glide.with(holder.itemView.context)
            .load(courses.image)
            .into(holder.binding.ivCourseImage)

        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(courses) }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Courses) -> Unit)? = null


    fun setOnItemClickListener(listener: (Courses) -> Unit){
        onItemClickListener = listener
    }


}