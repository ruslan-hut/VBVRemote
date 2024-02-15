package ua.com.programmer.vbvremote.workshop.boss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentBossBinding
import ua.com.programmer.vbvremote.shared.SharedViewModel

@AndroidEntryPoint
class BossFragment: Fragment() {

    private val shared: SharedViewModel by activityViewModels()
    private val viewModel: BossViewModel by viewModels()
    private lateinit var binding: FragmentBossBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_boss, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bossViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val pager = ViewPagerAdapter(this)
        binding.container.adapter = pager
        TabLayoutMediator(binding.screenTabs, binding.container) { tab, position ->
            tab.text = when (position) {
                1 -> getString(R.string.employee)
                else -> getString(R.string.boss)
            }
        }.attach()
    }
}

private class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            1 -> DocumentsWork()
            else -> DocumentsPlan()
        }
        return fragment
    }

}