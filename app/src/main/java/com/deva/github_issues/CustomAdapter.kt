package com.deva.github_issues

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class CustomAdapter(private val mList: List<GithubViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    var context: Context? =null

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        context=parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.issue_item_row, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        //load user image
        val imgUrl = ItemsViewModel.userImg
        if (imgUrl !== null) {
            context?.let {
                Glide.with(it)
                    .load(imgUrl)
                    .into(holder.userImg)
            }
        } else {
            holder.userImg.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.uname.text = ItemsViewModel.uname
        holder.title.text=ItemsViewModel.title
        holder.description.text=ItemsViewModel.desc

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val parsedDate = inputFormat.parse(ItemsViewModel.time)
        val det = outputFormat.format(parsedDate)
        holder.time.text= det   //ItemsViewModel.time


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userImg: CircleImageView = itemView.findViewById(R.id.user_image_civ)
        val uname: TextView = itemView.findViewById(R.id.user_name_tv)
        val title: TextView = itemView.findViewById(R.id.title_issue_tv)
        val description: TextView = itemView.findViewById(R.id.desc_issue_tv)
        val time: TextView = itemView.findViewById(R.id.updated_time)
    }
}
