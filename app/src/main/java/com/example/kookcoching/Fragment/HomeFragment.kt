package com.example.kookcoching.Fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Fragment.Home.HomePagerAdapter
import com.example.kookcoching.Fragment.Home.TextViewScrolling.HomeSlotAdapter
import com.example.kookcoching.R
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {
    // Image Slider ViewPager
    internal lateinit var slider_viewPager: ViewPager
    // TextView Slot ViewPager
    internal lateinit var slot_viewPager: ViewPager
    var items : ArrayList<String> = arrayListOf()

    var currentPage : Int = 0
    lateinit var timer : Timer
    var DELAY_MS : Long = 2000
    var PERIOD_MS : Long = 2000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        웹을 크롤링하기 위해 코루틴을 사용
//        Why? 웹을 파싱해 정보를 받아오는 동안 코드가 멈추면 안되기때문
//        코드를 비동기로 작성하지 않을 시 network 에러 발생
//        기본으로 사용하는 CoroutineDispatchers (Dispatchers.Default)
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            //  async, 즉 비동기로 실행된 코드의 결과를 Deferred 객체에 저장
            //  deferred 객체는 받고자하는 return 타입을 지정할 수 있음
            val deferred : Deferred<ArrayList<String>> = async {
                var url = "https://www.tiobe.com/tiobe-index/"
                // Jsoup 라이브러리를 활용한 크롤링, timeout 코드는 최대 3초까지 기다리겠다는 의미
                var doc = Jsoup.connect(url).timeout(1000 * 3).get()
                // html 태그의 table -> tbody -> tr의 정보를 받아옴
                val elements : Elements = doc.select("table tbody tr")

                // 1~10등 까지의 정보만을 파싱함
                for (i in 0..9) {
                    // 위 Elements 객체에서 td 태그의 정보들만 가져옴
                    // 현재 년도 순의, 작년 순위, 언어 이름, 사용빈도 등등
                    val item_temp = elements[i].select("td").text()
                    val item = item_temp.replace("  ", " ")
                    items.add(item)
                }
                // return 하는 부분
                items
            }
        }

        // items ArrayList가 값을 저장할 수 있게 기다리는 부분
        // 차후, 앱의 개발을 위해 수정이 필요한 부분
        Thread.sleep(3000L)


        val view = inflater.inflate(R.layout.fragment_home, container, false)
        slider_viewPager = view.findViewById(R.id.viewpager_slider) as ViewPager
        slot_viewPager = view.findViewById(R.id.viewpager_slot) as ViewPager
        println(items)

        // items List를 인자로 보냄
        val adapter = HomePagerAdapter(context, items)
        val slot_adapter = HomeSlotAdapter(context)

        slider_viewPager.adapter = adapter
        slot_viewPager.adapter = slot_adapter

        updatePage(slider_viewPager)

        slider_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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
            if (currentPage == 10) {
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