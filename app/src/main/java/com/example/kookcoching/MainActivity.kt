package com.example.kookcoching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.kookcoching.Adapter.MainFragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

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
                R.id.menu_news -> view_pager.currentItem = 3
                R.id.menu_information -> view_pager.currentItem = 4
            }
            true
        }

    }



}

