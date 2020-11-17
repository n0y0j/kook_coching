package com.example.kookcoching.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kookcoching.Fragment.*

// 2020.09.23 / 노용준 / Bottomnavigation에서 선택된 아이템의 position에 따라 Fragment를 출력한다.
class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> ShareBoardFragment()
            2 -> MajorBoardFragment()
            else -> ProjectFragment()
        }
    }
    override fun getCount() = 5 // 전체 페이지 수
}
