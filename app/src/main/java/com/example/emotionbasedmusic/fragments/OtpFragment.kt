package com.example.emotionbasedmusic.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.container.AppContainer
import com.example.emotionbasedmusic.databinding.FragmentOtpBinding
import com.example.emotionbasedmusic.helper.Constants
import com.example.emotionbasedmusic.helper.HelpRepo
import com.example.emotionbasedmusic.viewModel.MusicViewModel

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class OtpFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentOtpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userid: String
    private lateinit var userToken: PhoneAuthProvider.ForceResendingToken
    @Inject
    lateinit var appContainer: AppContainer
    private val model: MusicViewModel by activityViewModels()
    private val args: OtpFragmentArgs by navArgs()
    private lateinit var phone: String
    private lateinit var dialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phone = args.phone
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        appContainer.repo.initSharedPreferences()
        setTextChanger()
        getCallbacks()
        binding.apply {
            btnVerify.setOnClickListener(this@OtpFragment)
            tvResend.setOnClickListener(this@OtpFragment)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(object :
            OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (p0.isSuccessful) {
                    appContainer.repo.setSharedPreferences(Constants.IS_LOGGED_IN, Constants.LOGGED_IN)
                    appContainer.repo.setSharedPreferences(Constants.MOBILE, phone)
                    val newUser = p0.result!!.additionalUserInfo!!.isNewUser
                    if (newUser) {
                        dialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Verification Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        toAddProfileFragment()
                    } else {
                        appContainer.repo.setSharedPreferences(
                            Constants.IS_PROFILE_COMPLETE,
                            Constants.PROFILE_COMPLETE
                        )
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Welcome Back", Toast.LENGTH_SHORT).show()
                        toFaceScanFragment()
                    }
                } else {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }

            }

        })
    }

    private fun toAddProfileFragment() {
        findNavController().navigate(R.id.action_otpFragment_to_usersDataFragment)
    }

    private fun toFaceScanFragment() {
        findNavController().navigate(R.id.action_otpFragment_to_moodRecognitionFragment)
    }

    private fun callDialog() {
        dialog = ProgressDialog(requireContext())
        dialog.setMessage("Please Wait")
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun setTextChanger() {
        binding.ed1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.ed1.text.length == 1) {
                    binding.ed2.requestFocus()
                }
            }

        })
        binding.ed2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }


            override fun afterTextChanged(s: Editable?) {
                if (binding.ed2.text.length == 1) {
                    binding.ed3.requestFocus()
                }
            }

        })
        binding.ed3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.ed3.text.length == 1) {
                    binding.ed4.requestFocus()
                }
            }

        })
        binding.ed4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.ed4.text.length == 1) {
                    binding.ed5.requestFocus()
                }
            }

        })
        binding.ed5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.ed5.text.length == 1) {
                    binding.ed6.requestFocus()
                }
            }

        })
    }

    private fun getCallbacks() {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Toast.makeText(requireContext(), "Verification Successful", Toast.LENGTH_SHORT)
                    .show()
                signInWithPhoneAuthCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                if (p0 is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                        .show()
                }
                findNavController().navigateUp()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                userid = p0
                userToken = p1
            }

        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone).setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.btnVerify.id -> {
                verify()
            }
            binding.tvResend.id -> {
                getCallbacks()
            }
        }
    }

    private fun verify() {
        val readOtp =
            binding.ed1.text.toString() + binding.ed2.text.toString() + binding.ed3.text.toString() +
                    binding.ed4.text.toString() + binding.ed5.text.toString() + binding.ed6.text.toString()
        if (readOtp.length < 6) {
            Toast.makeText(requireContext(), "Enter 6 digit OTP", Toast.LENGTH_SHORT).show()
        } else {
            val credential = PhoneAuthProvider.getCredential(userid, readOtp)
            callDialog()
            signInWithPhoneAuthCredential(credential)
        }
    }

}