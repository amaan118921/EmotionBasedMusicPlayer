package com.example.emotionbasedmusic.fragments

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.emotionbasedmusic.MainActivity
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.adapter.SongImageAdapter
import com.example.emotionbasedmusic.data.Music
import com.example.emotionbasedmusic.databinding.NewMusicFragmentBinding
import com.example.emotionbasedmusic.eventBus.MessageEvent
import com.example.emotionbasedmusic.eventBus.PositionEvent
import com.example.emotionbasedmusic.eventBus.SongEvent
import com.example.emotionbasedmusic.helper.Constants
import com.example.emotionbasedmusic.helper.ScrollListenerHelper
import com.example.emotionbasedmusic.helper.makeVisible
import com.example.emotionbasedmusic.services.MusicService
import com.example.emotionbasedmusic.viewModel.MusicViewModel
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MusicFragment : Fragment(), View.OnClickListener, SeekBar.OnSeekBarChangeListener,
    ServiceConnection, ScrollListenerHelper.OnSnapPositionChangeListener {

    private val model: MusicViewModel by activityViewModels()
    private var song: Music? = null
    private var notificationManager: NotificationManager? = null
    private var songsList: MutableLiveData<List<Music>> = MutableLiveData<List<Music>>()
    private var shuffledList = MutableLiveData<MutableList<Music>>()
    private var isLooping = false
    private var key: Boolean? = true
    private var service: MusicService? = null
    private var isBounded: Boolean? = false
    private var isFromFavorite: Boolean = false
    val _likedSongs = MutableLiveData<MutableList<Music>>()
    private var pagerSnapHelper: PagerSnapHelper? = null
    private var snapPosition = 0
    private var prevPosition = 0
    private var scrollListenerHelper: ScrollListenerHelper? = null
    private var songImageAdapter: SongImageAdapter? = null
    private var urlList = mutableListOf<String>()
    private var musicIntent: Intent? = null
    companion object {
        lateinit var binding: NewMusicFragmentBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!(EventBus.getDefault().isRegistered(this))) {EventBus.getDefault().register(this)}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewMusicFragmentBinding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        key = (requireActivity() as MainActivity).key
        isFromFavorite = (requireActivity() as MainActivity).isFromFavorite
        shuffledList.value = mutableListOf()
        notificationManager =
            requireActivity().getSystemService(NotificationManager::class.java) as NotificationManager
        binding.sBar.setOnSeekBarChangeListener(this)
        songsList.value = mutableListOf()
        _likedSongs.value = mutableListOf()
        musicIntent = (requireActivity() as MainActivity).musicIntent
        model.musicData.observe(viewLifecycleOwner) {
            this.songsList.value = it
        }
        model._likedSongs.observe(viewLifecycleOwner) {
            this._likedSongs.value = it
        }
        this.song = model.getSong()
        song?.let { (requireActivity() as MainActivity).setSong(it) }
        songsList.value?.let { (requireActivity() as MainActivity).setSongList(it) }
        checkForKey()
        binding.apply {
            btnBack.setOnClickListener(this@MusicFragment)
            btnLike.setOnClickListener(this@MusicFragment)
            btnNextSong.setOnClickListener(this@MusicFragment)
            btnPlay.setOnClickListener(this@MusicFragment)
            ibLoop.setOnClickListener(this@MusicFragment)
            btnPreviousSong.setOnClickListener(this@MusicFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        when (event?.getString()) {
            getString(R.string.scroll_to) -> {
                scrollRecyclerView((event as PositionEvent).getIndex())
            }
            getString(R.string.update_song) -> {
                this.song = (event as SongEvent).getMusic()
            }
        }
    }

    private fun scrollRecyclerView(index: Int) {
        binding.rvMusic.smoothScrollToPosition(index)
    }

    private fun getShuffledList(): MutableList<String> {
        song?.let {
            urlList.add(it.imgUrl)
            shuffledList.value?.add(it)
        }
        songsList.value!!.forEach { music ->
            if (music != song) {
                urlList.add(music.imgUrl)
                shuffledList.value?.add(music)
            }
        }
        this.songsList.value = shuffledList.value
        return urlList
    }

    private fun setUpRecyclerViewWithPager(isFromFavorite: Boolean) {
        pagerSnapHelper = PagerSnapHelper()
        songImageAdapter = SongImageAdapter(requireContext())
        if(isFromFavorite){songImageAdapter?.bindList(getLikedShuffledList())} else {songImageAdapter?.bindList(getShuffledList())}
        binding.rvMusic.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            pagerSnapHelper?.attachToRecyclerView(this)
            adapter = songImageAdapter
        }
        pagerSnapHelper?.let { scrollListenerHelper = ScrollListenerHelper(this, it, snapPosition) }
        scrollListenerHelper?.let { binding.rvMusic.addOnScrollListener(it) }
        service?.setSongsList(this.songsList.value!!)
        service?.setImageUrlList(urlList)
    }

    private fun getLikedShuffledList(): List<String> {
        song?.let {
            urlList.add(it.imgUrl)
            shuffledList.value?.add(it)
        }
        _likedSongs.value!!.forEach { music ->
            if (music != song) {
                urlList.add(music.imgUrl)
                shuffledList.value?.add(music)
            }
        }
        this.songsList.value = shuffledList.value
        return urlList
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onSnapPositionChange(position: Int) {
        service!!.setTo(position)
        checkForLiked()
        getSong()
        prevPosition = position
        service!!.setPosition(position)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun checkForLiked() {
        if (key!!) {

        } else {
            this.song = service?.getSong()
        }
        when (_likedSongs.value!!.contains(song)) {
            true -> {
                binding.btnLike.setImageDrawable(resources.getDrawable(R.drawable.ic_outline_favorite_24))
            }
            false -> {
                binding.btnLike.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24_4))
            }
        }
        binding.btnLike.makeVisible()
    }

    private fun checkForKey() {
        when (key!!) {
            true -> {
                (requireActivity() as MainActivity).stopForegroundService()
                initView()
            }
            false -> {
                checkForService()
            }
        }
    }

    private fun checkForService() {
        bindToService()
    }

    private fun bindToService() {
        Intent(requireContext(), MusicService::class.java).also {
            requireActivity().bindService(it, this, Context.BIND_AUTO_CREATE)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        binding.apply {
            tvStartTime.text = getString(R.string.tools_text)
            tvEndTime.text = getText(R.string.tools_text)
            sBar.progress = 0
            btnPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24_b))
            tvSongName.text = song?.songName
            tvArtist.text = song?.artistName
            btnPlay.isEnabled = false
            btnPlay.isClickable = false
        }
        startMusicService()
    }

    private fun startMusicService() {
        startService()
        bindToService()
    }

    private fun startService() {
        musicIntent?.putExtra(Constants.SONG, this.song)
        musicIntent?.putExtra(Constants.INDEX, songsList.value!!.indexOf(song))
        musicIntent?.let { ContextCompat.startForegroundService(requireContext(), it)}
    }

    private fun pauseMediaPlayer() {
        service?.pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyResources()
    }

    private fun destroyResources() {
        requireActivity().unbindService(this)
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnPlay -> {
                when (MusicService.mediaPlayer.isPlaying) {
                    true -> {
                        pauseMediaPlayer()
                        binding.btnPlay.setImageDrawable(resources.getDrawable(R.drawable.play_icon))
                    }
                    false -> {
                        resumeMediaPlayer()
                        binding.btnPlay.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_pause_24_b))
                    }
                }
            }
            R.id.ibLoop -> {
                if (isLooping) {
                    isLooping = false
                    binding.ibLoop.setImageDrawable(resources.getDrawable(R.drawable.repeat_icon_new))
                    setLooping(isLooping)
                } else {
                    isLooping = true
                    binding.ibLoop.setImageDrawable(resources.getDrawable(R.drawable.repeat_button_blue))
                    setLooping(isLooping)
                }
            }
            R.id.btnNextSong -> {
                nextSong()
            }
            R.id.btnPreviousSong -> {
                prevSong()
            }
            R.id.btnLike -> {
                checkForFavorite()
            }
            R.id.btnBack -> {
                popBackStack()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun nextSong() {
        service?.nextSong()
        getSong()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun prevSong() {
        service?.prevSong()
        getSong()
    }

    private fun getSong() {
        this.song = service?.getSong()
    }

    private fun popBackStack() {
        (requireActivity() as MainActivity).navController.popBackStack()
    }

    private fun checkForFavorite() {
        if (key!!) {

        } else {
            this.song = service?.getSong()
        }
        if (this._likedSongs.value!!.contains(song)) {
            removeFromLiked(song!!)
        } else {
            addToLiked(song!!)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun removeFromLiked(song: Music) {
        binding.btnLike.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_favorite_border_24_4))
        model.removedFromLikedSongs(song)
        showSnackbar(Constants.REMOVED_LIKED_SONGS)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addToLiked(song: Music) {
        binding.btnLike.setImageDrawable(resources.getDrawable(R.drawable.ic_outline_favorite_24))
        model.addToLikedSongs(song)
        showSnackbar(Constants.ADDED_LIKED_SONGS)
    }

    private fun showSnackbar(msg: String) {
        Snackbar.make(binding.constraint, msg, Snackbar.LENGTH_LONG).setTextColor(resources.getColor(R.color.black))
            .setBackgroundTint(resources.getColor(R.color.white))
            .setAnchorView(binding.textLinearLayout)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    private fun setLooping(looping: Boolean) {
        service?.setLooping(looping)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeMediaPlayer() {
        service?.resumeMediaPlayer()
    }

    override fun onProgressChanged(sb: SeekBar?, progress: Int, p2: Boolean) {

    }

    private fun seekTo(seekPosition: Int) {
        service?.seekTo(seekPosition)
    }

    override fun onStartTrackingTouch(sb: SeekBar?) {

    }

    override fun onStopTrackingTouch(sb: SeekBar?) {
        val progress = sb?.progress
        seekTo(progress!!)
    }

    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.LocalBinder
        this.service = binder.getService()
        isBounded = true
        when (key!!) {
            false -> {
                this.songsList.value = this.service?.getSongsList()
                this.urlList = this.service?.getImageUrlList() as MutableList<String>
                this.song = this.service?.getSong()
                updateRecyclerView()
                checkForLiked()
                this.service?.getPagerPosition()?.let { binding.rvMusic.scrollToPosition(it)}
                this.service?.setUI()
            }
            true -> {
                checkForLiked()
                setUpRecyclerViewWithPager(isFromFavorite)
                when (isFromFavorite) {
                    true -> {
                        this.service?.setPosition(this.songsList.value?.indexOf(song)!!)
                        this.service?.setSongsList(this.songsList.value!!)
                    }
                    false -> {
                        this.service?.setPosition(this.songsList.value?.indexOf(song)!!)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateRecyclerView() {
        pagerSnapHelper = PagerSnapHelper()
        songImageAdapter = SongImageAdapter(requireContext())
        songImageAdapter?.bindList(urlList)
        binding.rvMusic.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            pagerSnapHelper?.attachToRecyclerView(this)
            adapter = songImageAdapter
        }
        pagerSnapHelper?.let { scrollListenerHelper =
            this.service?.getPagerPosition()?.let { it1 -> ScrollListenerHelper(this, it, it1) }
        }
        scrollListenerHelper?.let { binding.rvMusic.addOnScrollListener(it) }
    }


    override fun onServiceDisconnected(p0: ComponentName?) {
        isBounded = false
    }

}

