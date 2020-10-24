package com.example.kookcoching.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kookcoching.Fragment.*
import com.example.kookcoching.Fragment.Information.InformationFragment

class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1 -> ShardBoardFragment()
            2 -> MajorBoardFragment()
            3 -> NewsFragment()
            else -> InformationFragment()
        }
    }
    override fun getCount() = 5 // 전체 페이지 수
}
