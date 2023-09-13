package com.tutacademy.mathbattle.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tutacademy.mathbattle.R
import com.tutacademy.mathbattle.data.ResultModel
import com.tutacademy.mathbattle.databinding.PlayLayoutBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random


/**
 *Created by Zohidjon Akbarov
 */
class PlayFragment : Fragment() {
    private var _binding: PlayLayoutBinding? = null
    private val binding get() = _binding!!

    private val operations = listOf("+", "-")
    private var score = 0
    private var currentAnswer = 0
    private val maxTime = 10_000L
    private var remainTime = maxTime
    private var customTimer: CustomTimer? = null
    private var pref: SharedPreferences? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vibrator = activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PlayLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("result", Context.MODE_PRIVATE)
        binding.progressBar.progress = 100
        binding.scoreTv.text = getString(R.string.score, score)
        generateEquation()

        updateTimer()

        binding.truBtn.setOnClickListener {
            check(true)
            generateEquation()
        }

        binding.falseBtn.setOnClickListener {
            check(false)
            generateEquation()
        }
    }

    private fun check(expectedResult: Boolean) {
        val equationResult = binding.answerTv.text.removePrefix("= ").toString().toInt()
        if ((equationResult == currentAnswer) == expectedResult) {
            score++
            binding.scoreTv.text = getString(R.string.score, score)
            remainTime += 2_000
            remainTime = min(maxTime, remainTime)
        } else {
            remainTime -= 4000
            remainTime = max(remainTime, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibration =
                    VibrationEffect.createOneShot(200L, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator?.vibrate(vibration)
            } else {
                vibrator?.vibrate(200L)
            }
        }
        updateTimer()
    }

    private fun generateEquation() {
        val currentOperation = operations.random()
        val firstNumber = Random.nextInt((score + 1) * 10)
        val secondNumber =
            if (currentOperation == "-") Random.nextInt(firstNumber + 1) else Random.nextInt((score + 1) * 10)
        currentAnswer = getResult(firstNumber, secondNumber, currentOperation)
        val chooseList = listOf(
            currentAnswer,
            Random.nextInt(currentAnswer - (score + 1) * 10, currentAnswer + (score + 1) * 10)
        )
        binding.equationTv.text =
            getString(R.string.equation, firstNumber, currentOperation, secondNumber)
        binding.answerTv.text = "= ${chooseList.random()}"

    }

    private fun getResult(firstNumber: Int, secondNumber: Int, operation: String): Int {
        return when (operation) {
            "+" -> firstNumber + secondNumber
            else -> firstNumber - secondNumber
        }
    }

    private fun updateTimer() {
        customTimer?.cancel()
        customTimer = CustomTimer(remainTime)
        customTimer?.start()
    }

    private fun navigateToResultPage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                binding.progressBar.setProgress(0,true)
                val stringResult = pref?.getString("list", null)
                val type = object : TypeToken<MutableList<ResultModel>?>() {}.type
                val list =
                    Gson().fromJson<MutableList<ResultModel>>(stringResult, type) ?: mutableListOf()
                list.add(ResultModel(System.currentTimeMillis(), score))
                list.sortByDescending { it.score }
                val gsonString = Gson().toJson(list.take(5))
                pref?.edit()?.putString("list", gsonString)?.apply()

                delay(1000L)
                findNavController().navigate(
                    R.id.action_playFragment_to_resultFragment,
                    bundleOf("score" to score)
                )
            }
        }

    }

    inner class CustomTimer(futureTime: Long) : CountDownTimer(futureTime, 1L) {
        override fun onTick(time: Long) {
            remainTime = time
            binding.progressBar.setProgress((time / 100).toInt(), true)
        }

        override fun onFinish() {
            navigateToResultPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customTimer?.cancel()
        customTimer = null
        vibrator?.cancel()
    }
}