package com.rsschool.quiz

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentQuizBinding

private const val ARG_SAVED_CHOICE = "savedChoice"
private const val ARG_QUESTION_NUMBER = "questionNumber"

class QuizFragment : Fragment() {
    // this binding variant is from https://developer.android.com/topic/libraries/view-binding
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private var onQuestionPageChange: OnQuestionPageChange? = null
    private var currentQuizArray: Array<String?>? = null

    private var savedChoice: Int = -1
    private var questionNumber: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            savedChoice = it.getInt(ARG_SAVED_CHOICE) ?: -1
            questionNumber = it.getInt(ARG_QUESTION_NUMBER) ?: 1
        }
        onQuestionPageChange = context as OnQuestionPageChange
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater)
        binding.toolbar.title = "Question $questionNumber"
        val questionArrayFromStorage = when (questionNumber) {
            2 -> R.array.question_two
            3 -> R.array.question_three
            4 -> R.array.question_four
            5 -> R.array.question_five
            6 -> R.array.question_six
            else -> R.array.question_one
        }
        currentQuizArray = resources.getStringArray(questionArrayFromStorage)
        currentQuizArray?.let {
            with(binding) {
                question.setText(it[0])
                optionOne.setText(it[1])
                optionTwo.setText(it[2])
                optionThree.setText(it[3])
                optionFour.setText(it[4])
                optionFive.setText(it[5])
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRadioIndex()
        if (questionNumber == 6) {
            binding.nextButton.text = getString(R.string.button_submit)
        }
        if (questionNumber == 1) {
            binding.toolbar.navigationIcon = ResourcesCompat
                .getDrawable(resources, android.R.drawable.menuitem_background, null)
            binding.previousButton.isVisible = false
        } else {
            binding.previousButton.setOnClickListener {
                onQuestionPageChange?.onPreviousPageChange(getRadioIndex())
            }
            binding.toolbar.setNavigationOnClickListener {
                onQuestionPageChange?.onPreviousPageChange(getRadioIndex())
            }
        }

        if (binding.radioGroup.checkedRadioButtonId < 0) {
            binding.nextButton.isVisible = false
        } else {
            binding.nextButton.setOnClickListener {
                onQuestionPageChange?.onNextPageChange(getRadioIndex())
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            binding.nextButton.isVisible = true
            binding.nextButton.setOnClickListener {
                onQuestionPageChange?.onNextPageChange(getRadioIndex())
            }
        }
    }

    private fun getRadioIndex(): Int {
        val check = binding.radioGroup.checkedRadioButtonId
        val index = binding.radioGroup.indexOfChild(activity?.findViewById(check))
        return index
    }

    private fun setRadioIndex() {
        Log.i("log", "Saved choice received: $savedChoice")
        if (savedChoice in 0..4) {
            binding.radioGroup.check(binding.radioGroup.getChildAt(savedChoice).id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(savedChoice: Int, questionNumber: Int) =
            QuizFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SAVED_CHOICE, savedChoice)
                    putInt(ARG_QUESTION_NUMBER, questionNumber)
                }
            }
    }

    interface OnQuestionPageChange {
        fun onNextPageChange(choice: Int)
        fun onPreviousPageChange(choice: Int)
    }
}