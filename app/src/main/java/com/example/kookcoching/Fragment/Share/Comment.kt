package com.example.kookcoching.Fragment.Share

import com.google.firebase.Timestamp

// 2020.10.26 / 문성찬 / 댓글 클래스
data class Comment (var comment: String, var author: String, var nickname: String) {
}

data class getComment(var comment: String, var time: Long, var author: String, var nickname: String) {}