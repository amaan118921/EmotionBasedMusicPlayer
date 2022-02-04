package com.example.emotionbasedmusic.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.example.emotionbasedmusic.MainActivity
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.databinding.FragmentCheckBinding
import com.example.emotionbasedmusic.helper.Constants
import com.example.emotionbasedmusic.helper.HelpRepo
import com.example.emotionbasedmusic.viewModel.MusicViewModel
import com.example.emotionbasedmusic.viewModel.MusicViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CheckFragment : Fragment() {
    private lateinit var binding: FragmentCheckBinding
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var isFromNotification: Boolean? = false
    private lateinit var navController: NavController
    private lateinit var repo: HelpRepo
    private val model: MusicViewModel by activityViewModels {
        MusicViewModelFactory(requireParentFragment())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        repo = HelpRepo(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isFromNotification = (requireActivity() as MainActivity).isFromNotification
        navController = (requireActivity() as MainActivity).navController
        initData()
        checkForUser()
    }

    private fun checkForUser() {
        if (repo.getSharedPreferences(Constants.IS_LOGGED_IN) == Constants.LOGGED_IN) {
            if (isFromNotification == true) {
                toFaceScanFragment()
                (requireActivity() as MainActivity).moveToMusic()
            } else {
                toFaceScanFragment()
            }
        } else {
            toSignUpFragment()
        }
    }


    private fun toSignUpFragment() {
        findNavController().navigate(R.id.action_checkFragment_to_signUpFragment)
    }

    private fun initData() {
        repo.initSharedPreferences()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)
    }

    private fun toFaceScanFragment() {
        findNavController().navigate(R.id.action_checkFragment_to_moodRecognitionFragment)
    }
}