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

class CommentsAdapter(private val mList: List<CommentModel>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    var context: Context? =null

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        context=parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_item_row, parent, false)

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
            holder.userImg.setImageResource(R.drawable.git_logo)
        }

        holder.uname.text = ItemsViewModel.uname
        holder.cmnt.text=ItemsViewModel.cmnt

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val outputFormat = SimpleDateFormat("mm-dd-yyyy hh:mm a")
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
        val userImg: CircleImageView = itemView.findViewById(R.id.commenter_image_civ)
        val uname: TextView = itemView.findViewById(R.id.commenter_name_tv)
        val cmnt: TextView = itemView.findViewById(R.id.commment_tv)
        val time: TextView = itemView.findViewById(R.id.comment_time)
    }
}