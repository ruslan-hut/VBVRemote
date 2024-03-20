
package ua.com.programmer.vbvremote.about

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentAboutBinding
import ua.com.programmer.vbvremote.settings.SettingsHelper

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    private val viewModel: AboutViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = SettingsHelper(requireContext())
        viewModel.setUserID(settings.userID())

        binding.aboutViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.userIdText.setOnLongClickListener {
            sendUserId()
        }
        binding.rulesText.setOnClickListener {
            // launch intent to open link
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = "https://play.google.com/store/apps/developer?id=Ruslan+Khut".toUri()
            startActivity(intent)
        }
    }

    private fun sendUserId(): Boolean {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, viewModel.userID)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
        return true
    }
}
