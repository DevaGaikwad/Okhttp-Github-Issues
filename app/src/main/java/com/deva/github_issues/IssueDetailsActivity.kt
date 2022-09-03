package com.deva.github_issues

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_github.*
import kotlinx.android.synthetic.main.activity_issue_details.*
import kotlinx.android.synthetic.main.activity_issue_details.progress_bar
import org.json.JSONArray
import org.json.JSONException

class IssueDetailsActivity : AppCompatActivity() {

    private var requestQueue: RequestQueue? = null
    val data = ArrayList<CommentModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_details)

        val issue = intent.getSerializableExtra("issue_item") as? GithubViewModel

        requestQueue = Volley.newRequestQueue(this)
        progress_bar.visibility= View.GONE

        val imgUrl = issue?.userImg
        if (imgUrl !== null) {
            this?.let {
                Glide.with(it)
                    .load(imgUrl)
                    .into(user_image_civ)
            }
        } else {
            user_image_civ.setImageResource(R.drawable.git_logo)
        }
        user_name_tv.text=issue?.uname

        updated_time.text=issue?.time
        title_issue_tv.text=issue?.title
        desc_issue_tv.text=issue?.desc

        if (isOnline(this))
            issue?.cmntUrl?.let { callComments(it) }
        else
            localGithubIssues()

        back_iv.setOnClickListener{
            onBackPressed()
        }
    }

    private fun localGithubIssues() {
        Toast.makeText(this,"Device is offline so loading offline content", Toast.LENGTH_LONG).show()
        val sharedPreference = getSharedPreferences("github_issues", Context.MODE_PRIVATE)
        val data_string = sharedPreference.getString("comments", "")
        if (data_string != null) {
            try {
                val jsonArray = JSONArray(data_string)
                for (i in 0 until jsonArray.length()) {
                    val issue = jsonArray.getJSONObject(i)
                    val comment = issue.getString("body")
                    val updated_time = issue.getString("updated_at")

                    val user = issue.getJSONObject("user")
                    val uname = user.getString("login")
                    val uImage = user.getString("avatar_url")

                    val item = CommentModel(uImage, uname,comment,updated_time)
                    data.add(item)
                }

                val adapter = CommentsAdapter(data)

                comments_recycler.adapter = adapter

            } catch (e: JSONException) {
                Log.e("localGithubIssues", "" + e.message)
            }
        }
    }

    private fun callComments(url:String) {
        progress_bar.visibility= View.VISIBLE
        Log.e("IssueDeatils","CallingComments: $url")
        val sr: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                //Log.e("GithubIssueAct",response)
                progress_bar.visibility= View.GONE
                try {
                    val jsonArray= JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val issue = jsonArray.getJSONObject(i)
                        val comment=issue.getString("body")
                        val updated_time=issue.getString("updated_at")

                        val user=issue.getJSONObject("user")
                        val uname=user.getString("login")
                        val uImage=user.getString("avatar_url")

                        val item=CommentModel(uImage,uname,comment,updated_time)
                        data.add(item)
                    }

                    val adapter=CommentsAdapter(data)
                    comments_recycler.adapter=adapter

                    val sharedPreference =  getSharedPreferences("github_issues", Context.MODE_PRIVATE)
                    var editor = sharedPreference.edit()
                    editor.putString("comments",jsonArray.toString())
                    editor.apply()

                    //Log.e("githubissuesAct data op",data.toString())

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //your error
                progress_bar.visibility= View.GONE
                error.message?.let { Log.e("GithubAct", it) }
            }){}

        requestQueue?.add(sr)
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}