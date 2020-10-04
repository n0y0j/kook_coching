package com.example.kookcoching.Fragment.Home

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.loader.content.AsyncTaskLoader
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Fragment.HomeFragment
import com.example.kookcoching.R
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.w3c.dom.Document

class HomePagerAdapter(private val context: Context, items : ArrayList<String>) : PagerAdapter() {
    private var layoutInflater : LayoutInflater? = null

    val items : ArrayList<String> = items;

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = layoutInflater!!.inflate(R.layout.home_viewpager_activity, null)
        val text = v.findViewById<View>(R.id.home_textview) as TextView

        text.setText(items[position])
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