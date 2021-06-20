package com.rsschool.quiz

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rsschool.quiz.databinding.FragmentResultBinding


private const val ARG_ANSWERS = "answers"
private const val ARG_RESULT = "result"


class ResultFragment : Fragment() {
    // this binding variant is from https://developer.android.com/topic/libraries/view-binding
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private var onRestartPressed: OnRestartPressed? = null

    private var answers: IntArray? = null
    private var result: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            answers = it.getIntArray(ARG_ANSWERS)
            result = it.getString(ARG_RESULT) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewResult.text = result

        binding.imageViewShare.setOnClickListener {
            val mailIntent: Intent = Intent(Intent.ACTION_SEND)
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Quiz results")
            mailIntent.putExtra(Intent.EXTRA_TEXT, "${createMessage()}")
            mailIntent.setData(Uri.parse("mailto:"));
            mailIntent.setType("text/plain");
            startActivity(Intent.createChooser(mailIntent, "Choose an Email client :"));
        }

        binding.imageViewRestart.setOnClickListener {
            onRestartPressed = context as OnRestartPressed
            onRestartPressed?.onRestartPressed()
        }

        binding.imageViewQuit.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun createMessage(): String {
        val str: StringBuilder = java.lang.StringBuilder()
        var questions: Array<String> = Array(6) { "" }

        str.append(result).append("\n\n")
        for (i in 1..6) {
            when (i) {
                1 -> questions = resources.getStringArray(R.array.question_one)
                2 -> questions = resources.getStringArray(R.array.question_two)
                3 -> questions = resources.getStringArray(R.array.question_three)
                4 -> questions = resources.getStringArray(R.array.question_four)
                5 -> questions = resources.getStringArray(R.array.question_five)
                6 -> questions = resources.getStringArray(R.array.question_six)
            }
            str.append("$i) ").append(questions[0]).append("\n")
            answers?.let {
                str.append("Your answer: ").append(questions[it[i] + 1]).append("\n\n")
            }
        }
        return str.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(answers: IntArray, result: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putIntArray(ARG_ANSWERS, answers)
                    putString(ARG_RESULT, result)
                }
            }
    }

    interface OnRestartPressed {
        fun onRestartPressed()
    }
}