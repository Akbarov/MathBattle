package com.tutacademy.mathbattle.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tutacademy.mathbattle.R
import com.tutacademy.mathbattle.utils.ResultAdapter
import com.tutacademy.mathbattle.data.ResultModel
import com.tutacademy.mathbattle.databinding.ResultLayoutBinding


/**
 *Created by Zohidjon Akbarov
 */
class ResultFragment : Fragment() {
    private var _binding: ResultLayoutBinding? = null
    private val binding get() = _binding!!
    private var pref: SharedPreferences? = null
    private var resultAdapter: ResultAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ResultLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("result", Context.MODE_PRIVATE)

        binding.replayBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_playFragment)
        }
        resultAdapter = ResultAdapter()
        val score = arguments?.getInt("score")
        binding.titleTv.text = getString(R.string.you_scored, score)

        val stringResult = pref?.getString("list", null)
        val type = object : TypeToken<List<ResultModel>?>() {}.type
        val list = Gson().fromJson<List<ResultModel>>(stringResult, type)
        resultAdapter?.submitList(list)

        binding.resultListRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = resultAdapter
        }
    }
}