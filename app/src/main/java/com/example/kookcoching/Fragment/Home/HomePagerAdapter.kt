package com.example.kookcoching.Fragment.Home

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
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
        val item : ArrayList<String> = items[position].split(" ") as ArrayList<String>;
        val card_view = v.findViewById<View>(R.id.card_view) as CardView
        var check = 0

        val name = v.findViewById<View>(R.id.name) as TextView
        val rank = v.findViewById<View>(R.id.rank) as TextView
        val rating = v.findViewById<View>(R.id.rating) as TextView
        val change = v.findViewById<View>(R.id.change) as TextView

        if (item.size == 6) {
            name.setText(item[2] + "\n" + item[3])
            check = 1
        }
        else name.setText(item[2])

        rank.setText(item[0] + "위")
        rating.setText(item[3 + check])
        change.setText(item[4 + check])

        if (item[2].length > 9) name.setTextSize(30F)

        val vp = container as ViewPager
        vp.addView(v, 0)

        when {
            position == 0 -> card_view.setBackgroundColor(Color.parseColor("#FFD700"))
            position == 1 -> card_view.setBackgroundColor(Color.parseColor("#C0C0C0"))
            position == 2 -> card_view.setBackgroundColor(Color.parseColor("#BF8970"))
            else -> card_view.setCardBackgroundColor(Color.parseColor("#2F2F7E"))
        }


        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)
    }
}