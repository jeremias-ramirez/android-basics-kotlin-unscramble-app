package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel(){



    //private var _score = 0
    //se cambia por val y MutableLiveData(0)
    // es val porque el MutableLIveData no cambia, sino que
    //el valor dentro de el
    private val _score = MutableLiveData(0)
    val score : LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount : LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord : LiveData<Spannable> = Transformations.map(_currentScrambledWord){
        if (it == null){
            SpannableString("")
        }else {
            val scrambleWord = it.toString()
            val spannable : Spannable = SpannableString(scrambleWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambleWord).build(),
                0,
                scrambleWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }

    private var wordsList : MutableList<String> = mutableListOf()
    private lateinit var currentWord : String

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    fun nextWord() : Boolean{
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else false
    }

    fun reinitalizeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()
        /*
        el siguiente bloque de código es para asegurar que el shuffle()
        no retorne la palabra sin con el mismo orden que se dio (sin mezclar)
        el false de equals es para que no considera mayusculas y minisculas iguales
         */
        while (tempWord.toString().equals(currentWord, false)){
            tempWord.shuffle()
        }

        //asegurarse que no la palabra actual no fue ya mostrada
        if (wordsList.contains(currentWord)){
            getNextWord()
        } else{
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }
    fun isUserWordCorrect(playerWord : String) : Boolean{
        if(playerWord.equals(currentWord, true)){
            increaseScore()
            return true
        }
        return false
    }
    private fun increaseScore(){
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    /*override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }*/

}