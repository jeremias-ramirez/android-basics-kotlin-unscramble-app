package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel(){



    private var _score = 0
    val score : Int
        get() = _score

    private var _currentWordCount = 0
    val currentWordCount : Int
        get() = _currentWordCount

    private lateinit var _currentScrambledWord : String
    val currentScrambledWord : String
        get() = _currentScrambledWord

    private var wordsList : MutableList<String> = mutableListOf()
    private lateinit var currentWord : String

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    fun nextWord() : Boolean{
        return if (_currentWordCount < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else false
    }

    fun reinitalizeData(){
        _score = 0
        _currentWordCount = 0
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
            _currentScrambledWord = String(tempWord)
            ++_currentWordCount
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
        _score += SCORE_INCREASE
    }
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

}