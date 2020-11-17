package com.example.kookcoching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Adapter.MainFragmentPagerAdapter
import com.example.kookcoching.Fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.random.Random

// 2020.09.23 / 노용준 / BottomNavigationView와 Viewpager를 이용하여 4개의 Fragment 화면을 전환한다.
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = MainFragmentPagerAdapter(supportFragmentManager)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> view_pager.currentItem = 0
                R.id.menu_share_board -> view_pager.currentItem = 1
                R.id.menu_major_board -> view_pager.currentItem = 2
                R.id.menu_project -> view_pager.currentItem = 3
            }
            true
        }
    }
}

