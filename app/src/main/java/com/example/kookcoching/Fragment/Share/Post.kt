package com.example.kookcoching.Fragment.Share


data class Post(var title: String, var content: String, var image: ArrayList<String>, var tag: String, var author: String, var nickname: String, var goodCount: ArrayList<String>, var scrapCount: ArrayList<String>){
}

data class getPost(var title: String, var content: String, var time: Long, var image: ArrayList<String>, var tag: String, var author: String, var nickname: String, var goodCount: ArrayList<String>, var scrapCount: ArrayList<String>) {}