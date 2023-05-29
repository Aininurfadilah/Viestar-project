package com.example.viestar.ui.home.tab

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.viestar.ui.home.HomeFragment
import com.example.viestar.ui.home.tab.local.LocalFragment
import com.example.viestar.ui.home.tab.streaming.StreamingFragment

class SectionsPagerAdapter(activity: HomeFragment) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = LocalFragment()
            1 -> fragment = StreamingFragment()
        }
        return fragment as Fragment
    }

}