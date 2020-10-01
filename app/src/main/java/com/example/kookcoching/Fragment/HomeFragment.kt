package com.example.kookcoching.Fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Fragment.Home.HomePagerAdapter
import com.example.kookcoching.R
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Exception
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    internal lateinit var viewPager: ViewPager
    var items : ArrayList<String> = arrayListOf()

    var currentPage : Int = 0
    lateinit var timer : Timer
    var DELAY_MS : Long = 1000
    var PERIOD_MS : Long = 1000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val deferred : Deferred<ArrayList<String>> = async {
                var url = "https://www.tiobe.com/tiobe-index/"
                var doc = Jsoup.connect(url).timeout(1000 * 3).get()
                val elements : Elements = doc.select("table tbody tr")

                for (i in 0..19) {
                    val item_temp = elements[i].select("td").text()

                    items.add(item_temp)
                }
                items
            }
        }

        Thread.sleep(2000L)

        for ( i in 0..items.size ) {
            items[0] = items[0].replace("  "," ")
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.viewpager_slider) as ViewPager

        val adapter = HomePagerAdapter(context, items)

        viewPager.adapter = adapter
        updatePage(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPage = position
            }

        })
        return view
    }

    override fun getContext(): Context {
        return super.getContext()!!
    }

    fun updatePage(view : ViewPager) {
        var handler = Handler()

        val Update: Runnable = Runnable {
            if (currentPage == 20) {
                currentPage = 0
            }
            view.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        },DELAY_MS, PERIOD_MS)
    }
}