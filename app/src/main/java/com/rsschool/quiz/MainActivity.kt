package com.rsschool.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rsschool.quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), QuizFragment.OnQuestionPageChange, ResultFragment.OnRestartPressed {
    // this binding variant is from https://developer.android.com/topic/libraries/view-binding
    private lateinit var binding: ActivityMainBinding

    //  private val rightAnswers = arrayOf("Canada", "China", "Manilla", "Australia", "Vatican City", "Venezuela" )
//  private val rightAnswers = arrayOf(-1, 1, 2, 4, 4, 2, 1) // indexes from strings.xml
    private val rightAnswers = arrayOf(-1, 0, 1, 3, 3, 1, 0)
    private var currentAnswers = IntArray(7){-1}
    private var nextQuestion = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNextPageChange(choice: Int) {
        currentAnswers[nextQuestion] = choice
        nextQuestion++
        changePageAndTheme()
        Log.i("log", "choice was = $choice")
    }

    override fun onPreviousPageChange(choice: Int) {
        currentAnswers[nextQuestion] = choice
        nextQuestion--
        if (nextQuestion < 1) {
            nextQuestion = 1
        } else {
            changePageAndTheme()
            Log.i("log", "choice was = $choice")
        }
    }

    private fun changePageAndTheme() {
        val style = when (nextQuestion) {
            1 -> R.style.Theme_Quiz_First
            2 -> R.style.Theme_Quiz_Second
            3 -> R.style.Theme_Quiz_Third
            4 -> R.style.Theme_Quiz_Fourth
            5 -> R.style.Theme_Quiz_Fifth
            6 -> R.style.Theme_Quiz_Sixth
            7 -> R.style.Theme_Quiz_Seven
            else -> R.style.Theme_Quiz_First
        }
        theme.applyStyle(style, true)
        if (nextQuestion == 7) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainerView, ResultFragment
                    .newInstance(currentAnswers, getResult())
            ).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragmentContainerView, QuizFragment
                    .newInstance(currentAnswers[nextQuestion], nextQuestion)
            ).commit()
            Log.i("log", "current question = $nextQuestion")
        }
    }

    override fun onRestartPressed() {
        currentAnswers = IntArray(7){-1}
        nextQuestion = 1
        changePageAndTheme()
    }

    private fun getResult(): String {
        var result = 0
        for (i in 1 until currentAnswers.size) {
            if(currentAnswers[i] == rightAnswers[i])
                result++
        }
        return "Your result is: $result out of ${rightAnswers.size - 1}"
    }
}