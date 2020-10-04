package com.example.kookcoching.Fragment.Home.ImageViewScrolling

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.kookcoching.R
import kotlinx.android.synthetic.main.home_image_view_scrolling.view.*

class ImageViewScrolling : FrameLayout {
    internal var oldValue = 0

    companion object {
        private val ANIMATION_DURATION = 200
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.home_image_view_scrolling, this)
    }

    fun setValueRandom(image: Int, num_rotate: Int) {

        currentImage.animate()
            .translationY((-height).toFloat())
            .setDuration(ANIMATION_DURATION.toLong()).start()

        nextImage.translationY = nextImage.height.toFloat()

        nextImage.animate().translationY(0f).setDuration(ANIMATION_DURATION.toLong())
            .setListener(object: Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    setImage(currentImage, oldValue%4)

                    currentImage.translationY = 0f

                    if(oldValue != num_rotate) {
                        setValueRandom(image, num_rotate)
                        oldValue++
                    }
                    else {
                        currentImage.visibility = View.INVISIBLE
                        setImage(nextImage, image)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }


            }).start()
    }

    private fun setImage(img: ImageView?, value: Int) {

        if (value == Util.app)
            img!!.setImageResource(R.drawable.app)
        else if (value == Util.web)
            img!!.setImageResource(R.drawable.web)
        else if (value == Util.algo)
            img!!.setImageResource(R.drawable.algo)
        else if (value == Util.major)
            img!!.setImageResource(R.drawable.major)
    }


}
