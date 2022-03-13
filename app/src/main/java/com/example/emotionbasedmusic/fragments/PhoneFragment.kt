package com.example.emotionbasedmusic.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.emotionbasedmusic.R
import com.example.emotionbasedmusic.databinding.FragmentPhoneBinding
import com.example.emotionbasedmusic.viewModel.MusicViewModel

import com.hbb20.CountryCodePicker


class PhoneFragment : Fragment() {
    private lateinit var binding: FragmentPhoneBinding
    private val model: MusicViewModel by activityViewModels()
    private lateinit var countryCodePicker: CountryCodePicker
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
    }

    private fun initData() {
        countryCodePicker = binding.cpPhone
        countryCodePicker.registerCarrierNumberEditText(binding.etlPhone)
        binding.apply {
            btnContinue.setOnClickListener {
                val phoneNumber = binding.etlPhone.text.toString()
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.enter_10, Toast.LENGTH_SHORT).show()
                } else {
                    toOtpFragment(countryCodePicker.fullNumberWithPlus)
                }
            }
        }
    }

    private fun toOtpFragment(phoneNumber: String) {
        val action = PhoneFragmentDirections.actionPhoneFragmentToOtpFragment(phoneNumber)
        findNavController().navigate(action)
    }
}