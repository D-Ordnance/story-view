package deeosoft.library.adapter

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class StoryAdapter(fm: FragmentManager, var fragments: List<Int>): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return getStoryPagerClass(fragments[position])
    }

    private fun getStoryPagerClass(@LayoutRes layout: Int): Fragment {
        return Fragment(layout)
    }
}