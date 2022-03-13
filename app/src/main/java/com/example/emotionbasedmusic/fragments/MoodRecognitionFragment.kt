package com.example.emotionbasedmusic.fragments

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emotionbasedmusic.MainActivity
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.adapter.emojiAdapter
import com.example.emotionbasedmusic.container.AppContainer
import com.example.emotionbasedmusic.dataSource.emojiData
import com.example.emotionbasedmusic.databinding.FragmentFaceProceedOrRetakeBinding
import com.example.emotionbasedmusic.databinding.FragmentMoodRecognitionBinding
import com.example.emotionbasedmusic.eventBus.MessageEvent
import com.example.emotionbasedmusic.helper.*
import com.example.emotionbasedmusic.viewModel.MusicViewModel

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class MoodRecognitionFragment : Fragment(), View.OnClickListener, Dialog.IListener,
    emojiAdapter.Ilistener, BottomSheetDialog.SBottom, MainActivity.RequestPermissionEventListener,
    PermissionHelper.Listener {

    private val model: MusicViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    @Inject
    lateinit var appContainer: AppContainer
    private lateinit var dialog: Dialog
    private var permissionHelper: PermissionHelper? = null
    private var bitmap: Bitmap? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var isFromGallery: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer.repo.initSharedPreferences()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoodRecognitionBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        lateinit var binding: FragmentMoodRecognitionBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        checkForPI()
        permissionHelper = PermissionHelper(requireActivity())
        model.getData()
        initToolbar()
        initData()
        initRecyclerView()
        binding.apply {
            btnAddImage.setOnClickListener(this@MoodRecognitionFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        if(!(EventBus.getDefault().isRegistered(this))) { EventBus.getDefault().register(this)}
        if(permissionHelper?.getListeners()?.contains(this)==false){permissionHelper?.registerListener(this)}
        if(!(requireActivity() as MainActivity).getActivityPermissionListener().contains(this)){registerPermissionListener(this)}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterPermissionListener(this)
        permissionHelper?.unregisterListener(this)
        EventBus.getDefault().unregister(this)
    }


    private fun checkForPI() {
        when (model.progressIndicator) {
            true -> {
                binding.pIndicator.makeVisible()
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        when (event?.getString()) {
            Constants.EXECUTE_CAMERA_PERM -> {
                startCamera()
            }
        }
    }

    private fun initRecyclerView() {
        val emojiDataSet = emojiData().loadEmoji();
        val emojiRecyclerView = view?.findViewById<RecyclerView>(R.id.emoji_recycler_view)
        emojiRecyclerView?.adapter = emojiAdapter(this, emojiDataSet);
        emojiRecyclerView?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initData() {
        auth = FirebaseAuth.getInstance()
        permissionHelper = PermissionHelper(requireActivity())
        appContainer.repo.initSharedPreferences()
        database = FirebaseDatabase.getInstance()
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }


    private fun initToolbar() {
        binding.tbSignUp.tbCommon.setTitleTextColor(resources.getColor(R.color.white))
        model.name.observe(viewLifecycleOwner) {
            binding.tbSignUp.tbCommon.title = "Hi  $it"
        }
        binding.apply {
            tbSignUp.tbCommon.makeVisible()
            tbSignUp.tbCommon.setOnMenuItemClickListener(object : Toolbar.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.sign_out -> {
                            signOutBottom()
                        }
                        R.id.favorites -> {
                            toFavorites()
                        }
                        R.id.profile -> {
                            toProfileFragment()
                        }
                        R.id.how_to_use -> {
                            toHowToUseFrag()
                        }
                        R.id.request_a_song -> {
                            toRequestSongFragment()
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun toRequestSongFragment() {
        findNavController().navigate(R.id.requestSongFragment)
    }

    private fun toHowToUseFrag() {
        findNavController().navigate(R.id.action_moodRecognitionFragment_to_fragmentHowToUse)
    }


    private fun signOutBottom() {
        initBottomSheet()
    }

    private fun initBottomSheet() {
        bottomSheetDialog = BottomSheetDialog(null, requireParentFragment(), null, null, this, this)
        bottomSheetDialog.initBottomSheet(Constants.SIGN_OUT_BOTTOM)
    }

    private fun toProfileFragment() {
        findNavController().navigate(R.id.action_moodRecognitionFragment_to_profileFragment)
    }

    private fun toFavorites() {
        findNavController().navigate(R.id.action_moodRecognitionFragment_to_favoritesFragment)
    }

    private fun signOut() {
        (requireActivity() as MainActivity).stopForegroundService()
        appContainer.repo.clearSharedPreferences()
        googleSignInClient.signOut()
        auth.signOut()
        toCheckFragment()
    }

    private fun toCheckFragment() {
        findNavController().navigate(R.id.action_moodRecognitionFragment_to_checkFragment)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnAddImage -> {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        dialog = Dialog(requireContext(), this)
        dialog.showDialog()
    }

    override fun btnCamera() {
        dismissDialog()
        checkForCamPerm()
    }

    private fun checkForCamPerm() {
        if (permissionHelper?.isPermissionGranted(Manifest.permission.CAMERA) == true) {
            EventBus.getDefault().post(MessageEvent(Constants.EXECUTE_CAMERA_PERM))
        } else {
            permissionHelper?.requestPermission(
                arrayOf(Manifest.permission.CAMERA),
                Constants.CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun registerPermissionListener(listener: MainActivity.RequestPermissionEventListener) {
        (requireActivity() as MainActivity).registerPermissionListener(listener)
    }

    private fun unregisterPermissionListener(listener: MainActivity.RequestPermissionEventListener) {
        (requireActivity() as MainActivity).unregisterPermissionListener(listener)
    }

    override fun btnGallery() {
        dismissDialog()
        galleryCall()
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    isFromGallery = true
                    toFaceProceedFragment(data.data!!.toString(), isFromGallery)
                }
            }
        }


    private fun startCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        } else {
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }
        startActivityForResult(intent, Constants.CAMERA_REQUEST_CODE)
    }

    private fun dismissDialog() {
        dialog.dismissDialog()
    }

    private fun toFaceProceedFragment(uri: String?, boolean: Boolean) {
        val action =
            MoodRecognitionFragmentDirections.actionMoodRecognitionFragmentToFaceProceedOrRetakeFragment(
                uri,
                boolean,
            )
        findNavController().navigate(action)
    }

    private fun galleryCall() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        launcher.launch(Intent.createChooser(intent, "Choose Image"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            model.setBitmap(bitmap!!)
            isFromGallery = false
            toFaceProceedFragment(null, isFromGallery)
        }
    }


    override fun onItemClick(mood: String) {
        when(mood) {
            Constants.NONE -> {
                toResultSongFragment(true)
            }
            else -> {
                model.getSongs(mood)
                toResultSongFragment(false)
            }
        }
    }

    private fun toResultSongFragment(bool: Boolean) {
        val action = MoodRecognitionFragmentDirections.actionMoodRecognitionFragmentToResultSongsFragment(bool)
        findNavController().navigate(action)
    }

    override fun onNoClick() {
        bottomSheetDialog.dismiss()
    }

    override fun onYesClick() {
        bottomSheetDialog.dismiss()
        signOut()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.size == 1) {
                Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT)
                    .show()
                EventBus.getDefault().post(MessageEvent(Constants.EXECUTE_CAMERA_PERM))
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResults(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper?.onPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onPermissionGranted(permission: String?, requestCode: Int) {
        when (requestCode) {
            Constants.CAMERA_PERMISSION_CODE -> {
                startCamera()
            }
        }
    }

    override fun onPermissionDenied(permission: String?, requestCode: Int) {
        when (requestCode) {
            Constants.CAMERA_PERMISSION_CODE -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_perm_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onPermissionDeniedPermanent(permission: String?, requestCode: Int) {
        when (requestCode) {
            Constants.CAMERA_PERMISSION_CODE -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_perm_denied_permanently),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}