package com.deva.github_issues

import java.io.Serializable

data class CommentModel(val userImg: String, val uname: String,val cmnt:String,val time:String) :
    Serializable {

}