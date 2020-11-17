package com.example.kookcoching.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Fragment.Home.HomePagerAdapter
import com.example.kookcoching.Fragment.Home.TextViewScrolling.HomeSlotAdapter
import com.example.kookcoching.InfoActivity
import com.example.kookcoching.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    lateinit var myBtn : Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    var currentPage : Int = 0
    lateinit var timer : Timer
    var DELAY_MS : Long = 2000
    var PERIOD_MS : Long = 2000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        myBtn = view.findViewById(R.id.btn_my)

        var info_name: String ?= null
        var info_email: String ?= null
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 현재 접속한 계정의 이메일과 닉네임 전달
        var docRef = firestore!!.collection("user").document(firebaseAuth.currentUser?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                info_name = document.get("name").toString()
                info_email = document.get("id").toString()
            }

        myBtn.setOnClickListener {
            val intent = Intent(activity, InfoActivity::class.java)
            intent.putExtra("info_name", info_name)
            intent.putExtra("info_email", info_email)
            startActivity(intent)
        }

//        2020.11.01 / 노용준 / TIOBE 사이트 웹 크롤링
//        웹을 크롤링하기 위해 코루틴을 사용
//        Why? 웹을 파싱해 정보를 받아오는 동안 코드가 멈추면 안되기때문
//        코드를 비동기로 작성하지 않을 시 network 에러 발생
//        기본으로 사용하는 CoroutineDispatchers (Dispatchers.Default)
        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            //  async, 즉 비동기로 실행된 코드의 결과를 Deferred 객체에 저장
            //  deferred 객체는 받고자하는 return 타입을 지정할 수 있음
            val deferred : Deferred<Elements> = async {
                var url = "https://www.tiobe.com/tiobe-index/"
                // Jsoup 라이브러리를 활용한 크롤링, timeout 코드는 최대 3초까지 기다리겠다는 의미
                var doc = Jsoup.connect(url).timeout(1000 * 3).get()
                // html 태그의 table -> tbody -> tr의 정보를 받아옴
                val elements : Elements = doc.select("table tbody tr")

                // return 하는 부분
                elements
            }

            val job : Job = async {
                val elements = deferred.await();
                // 1~10등 까지의 정보만을 파싱함
                for (i in 0..9) {
                    // 위 Elements 객체에서 td 태그의 정보들만 가져옴
                    // 현재 년도 순의, 작년 순위, 언어 이름, 사용빈도 등등
                    val item_temp = elements[i].select("td").text()
                    val item = item_temp.replace("  ", " ")
                    items.add(item)
                }
            }

            job.join()

            // Main Thread 이외의 Thread에서 UI를 바꾸기 위함
            activity?.runOnUiThread(Runnable {
                slider_viewPager = view.findViewById(R.id.viewpager_slider) as ViewPager
                slot_viewPager = view.findViewById(R.id.viewpager_slot) as ViewPager

                // items List를 인자로 보냄
                val adapter = HomePagerAdapter(context, items)
                val slot_adapter = HomeSlotAdapter(context)

                slider_viewPager.adapter = adapter
                slot_viewPager.adapter = slot_adapter

                // 이미지 슬라이더 함수 실행 부분
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
            })
        }

        return view
    }

    override fun getContext(): Context {
        return super.getContext()!!
    }

    // 2020.11.01 / 노용준 / ViewPager 자동 변경
    fun updatePage(view : ViewPager) {
        var handler = Handler()

        // Viewpager가 변경되는 부분
        val Update: Runnable = Runnable {
            // 무한반복
            if (currentPage == 10) {
                currentPage = 0
            }
            // 페이지 변경
            view.setCurrentItem(currentPage++, true)
        }

        // 2초마다 페이지가 넘어가게 구현
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        },DELAY_MS, PERIOD_MS)
    }
}