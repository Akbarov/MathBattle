package com.tutacademy.mathbattle.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tutacademy.mathbattle.R
import com.tutacademy.mathbattle.databinding.StartingLayoutBinding

/**
 *Created by Zohidjon Akbarov
 */
class StartFragment : Fragment() {

    private var _binding: StartingLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = StartingLayoutBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_playFragment)
        }
    }

}