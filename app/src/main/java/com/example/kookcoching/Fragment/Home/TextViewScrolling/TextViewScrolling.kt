package com.example.kookcoching.Fragment.Home.TextViewScrolling

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.example.kookcoching.R
import kotlinx.android.synthetic.main.home_text_view_scrolling.view.*

// 2020.10.04 / 노용준 / 슬롯머신 애니메이션
// 4개의 단어가 슬롯머신처럼 돌아가면서 랜덤으로 하나가 선택된다.
class TextViewScrolling : FrameLayout {
    // 현재 가리키는 Text의 value
    internal var oldValue = 0

    // Slot의 Action 주기
    companion object {
        private val ANIMATION_DURATION = 200
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    // 생성자 함수를 통해, 클래스가 호출되는 즉시 실행됨
    // 즉, 클래스 객체를 만드는 순간 home_text_view_scrolling을 자신의 content로 갖게됨
    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.home_text_view_scrolling, this)
    }

    // TextView의 animate()를 사용하여 단어들이 아래에서 위로 움직일 수 있게 구현
    fun setValueRandom(text: Int, num_rotate: Int) {

        currentText.animate()
            .translationY((-height).toFloat())
            .setDuration(ANIMATION_DURATION.toLong()).start()

        nextText.translationY = nextText.height.toFloat()

        nextText.animate().translationY(0f).setDuration(ANIMATION_DURATION.toLong())
            .setListener(object: Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {
                }
                // 애니메이션이 끝나는 부분
                override fun onAnimationEnd(animation: Animator?) {
                    // 단어 랜덤 선택
                    setText(currentText, oldValue%4)
                    // 위치 고정
                    currentText.translationY = 0f

                    // 끝났는지 확인
                    if(oldValue != num_rotate) {
                        setValueRandom(text, num_rotate)
                        oldValue++
                    }
                    else {
                        currentText.visibility = View.INVISIBLE
                        setText(nextText, text)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }


            }).start()
    }

    // text의 value를 확인하고 텍스트를 변환함
    // Util Object에 구현되어있음
    private fun setText(img: TextView?, value: Int) {
        if (value == Util.app)
            img!!.setText("앱")
        else if (value == Util.web)
            img!!.setText("웹")
        else if (value == Util.algo)
            img!!.setText("알고리즘")
        else if (value == Util.major)
            img!!.setText("전공")
    }


}
