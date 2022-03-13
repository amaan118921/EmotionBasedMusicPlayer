package com.example.emotionbasedmusic.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.emotionbasedmusic.MainActivity
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.container.AppContainer
import com.example.emotionbasedmusic.data.MessageBody
import com.example.emotionbasedmusic.data.NotificationBody
import com.example.emotionbasedmusic.data.RequestModel
import com.example.emotionbasedmusic.databinding.FragmentRequestSongBinding
import com.example.emotionbasedmusic.eventBus.MessageEvent
import com.example.emotionbasedmusic.helper.Constants
import com.example.emotionbasedmusic.helper.makeVisible
import com.example.emotionbasedmusic.viewModel.MusicViewModel
import com.google.firebase.installations.Utils
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.Util
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.time.Duration
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RequestSongFragment : Fragment(), View.OnClickListener {

    companion object {
        lateinit var binding: FragmentRequestSongBinding
    }
    private val viewModel: MusicViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)
    }

    @Inject
    lateinit var appContainer: AppContainer
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestSongBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity()).onBackPressedDispatcher.addCallback(this) {
            popBackStack()
        }
        appContainer.repo.initSharedPreferences()
        binding.apply {
            ivClose.setOnClickListener(this@RequestSongFragment)
            btnRequestSong.setOnClickListener(this@RequestSongFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun removeFragment() {
        requireActivity().supportFragmentManager.commit {
            requireActivity().supportFragmentManager.findFragmentByTag(Constants.REQUEST_SONG_FRAG)
                ?.let { remove(it) }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnRequestSong -> {
                validate()
            }
            R.id.ivClose -> popBackStack()
        }
    }

    private fun validate() {
        binding.apply {
            val songName = etSongName.text.toString().trim()
            val artistName = etArtistName.text.toString().trim()
            if (songName.isNotEmpty() && artistName.isNotEmpty()) {
                binding.apply {
                    clProgress.makeVisible()
                    pbRequestSong.progressBar.makeVisible()
                }
                postNotification(songName, artistName)
            } else showToast("Enter all fields", Toast.LENGTH_SHORT)
        }
    }

    @Subscribe
    fun onMessageEvent(event: MessageEvent) {
        when(event.getString()) {
            getString(R.string.pop_back_stack) -> popBackStack()
        }
    }


    private fun postNotification(songName: String, artistName: String) {
        val deviceToken = appContainer.repo.getSharedPreferences(Constants.DEVICE_TOKEN)
        val body = "$songName,$artistName,$deviceToken"
        val requestModel = RequestModel(songName, artistName, deviceToken, 0, Calendar.getInstance().timeInMillis.toString())
        val messageBody =  MessageBody("Request for a song", body)
        val notificationBody = viewModel._developerToken.value?.let { NotificationBody(it, messageBody) }
        viewModel.postNotification(notificationBody, requestModel)
    }

    private fun showToast(msg: String, duration: Int) {
        Toast.makeText(requireContext(), msg, duration).show()
    }
}