package com.example.kookcoching.Fragment.Share

import com.google.firebase.Timestamp

data class Post(var title: String, var content: String, var image: ArrayList<String>, var tag: String){
}

data class getPost(var title: String, var content: String, var time: Long, var image: ArrayList<String>, var tag: String) {}