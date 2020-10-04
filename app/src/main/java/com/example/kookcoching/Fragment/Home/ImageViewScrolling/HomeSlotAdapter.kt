package com.example.kookcoching.Fragment.Home.ImageViewScrolling

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.R
import kotlinx.android.synthetic.main.home_viewpager_slot.view.*
import kotlin.random.Random


class HomeSlotAdapter(private val context: Context) : PagerAdapter() {
    private var layoutInflater : LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return 1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.home_viewpager_slot, null)
        val slot_image = v.findViewById<View>(R.id.slot_image) as ImageViewScrolling
        val slot_btn = v.findViewById<Button>(R.id.slot_btn) as Button

        slot_btn.setOnClickListener{
            slot_image.setValueRandom(Random.nextInt(4), Random.nextInt(10)+5)

        }

        val vp = container as ViewPager
        vp.addView(v, 0)

        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)
    }

}

