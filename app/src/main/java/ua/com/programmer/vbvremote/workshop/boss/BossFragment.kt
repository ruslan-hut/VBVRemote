package ua.com.programmer.vbvremote.workshop.boss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentBossBinding
import ua.com.programmer.vbvremote.network.AuthResponse
import ua.com.programmer.vbvremote.network.STATUS_OK
import ua.com.programmer.vbvremote.shared.SharedViewModel

@AndroidEntryPoint
class BossFragment: Fragment(), MenuProvider {

    private val shared: SharedViewModel by activityViewModels()
    private val viewModel: BossViewModel by viewModels()
    private lateinit var binding: FragmentBossBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_boss, container, false)

        val menuHost : MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

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

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.documents_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_reload) {
            shared.authenticate { showResponseResult(it) }
            return true
        }
        if (menuItem.itemId == R.id.action_expand) {
            shared.toggleExpandDocumentContent()
            return true
        }
        return false
    }

    private fun showResponseResult(response: AuthResponse) {
        if (response.status != STATUS_OK) {
            val error = response.errors.firstOrNull()?.message ?: ""
            if (error.isNotEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            } else {
                showNoResponse()
            }
        } else {
            Toast.makeText(requireContext(), R.string.success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNoResponse() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.error_no_response
            )
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
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