package com.deva.github_issues

import java.io.Serializable


data class GithubViewModel(val userImg: String, val uname: String,val title:String,val desc:String,val time:String,val cmntUrl:String) :Serializable{

}
