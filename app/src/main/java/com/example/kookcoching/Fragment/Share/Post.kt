package com.example.kookcoching.Fragment.Share

// 2020.10.26 / 문성찬 / 포스트 클래스
data class Post(var title: String, var content: String, var image: ArrayList<String>, var tag: String, var author: String, var nickname: String, var goodCount: ArrayList<String>, var scrapCount: ArrayList<String>){
}

data class getPost(var title: String, var content: String, var time: Long, var image: ArrayList<String>, var tag: String, var author: String, var nickname: String, var goodCount: ArrayList<String>, var scrapCount: ArrayList<String>) {}