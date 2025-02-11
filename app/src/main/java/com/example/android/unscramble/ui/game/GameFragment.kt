/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {



    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment
    //by viewModel es para inicializar el GameViewModel
    private val viewModel: GameViewModel by viewModels()

    /*
        by viewModels()
        use the property delegate approach and delegate the responsibility of the viewModel object to
        a separate class called viewModels. That means when you access the viewModel object,
        it is handled internally by the delegate class, viewModels.
        The delegate class creates the viewModel object for you on the first access, and retains its value
        through configuration changes and returns the value when requested.

     */

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        //binding = GameFragmentBinding.inflate(inflater, container, false)
        //usando dataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)


        Log.d("GameFragment", "GameFragment created/re-created!")

        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        /*
        The LiveData is lifecycle-aware observable, so you have to pass the lifecycle owner to the layout.
        In the GameFragment, inside the onViewCreated()method, below the initialization of the binding variables,
         add the following code.
         */

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
        // Update the UI
        //updateNextWordOnScreen()
        //binding.score.text = getString(R.string.score, 0)
        /* todos los vieModel. (binding) son eliminados xq se utilizo dataBinding
            y el enlace se hace en el layout
            viewModel.score.observe(viewLifecycleOwner){ newScore ->
            binding.score.text = getString(R.string.score, newScore)

        }*/

        //binding.wordCount.text = getString( R.string.word_count, 0, MAX_NO_OF_WORDS)
        /*viewModel.currentWordCount.observe(viewLifecycleOwner){newWordCount ->
            binding.wordCount.text = getString( R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
        }*/

        // Observe the currentScrambledWord LiveData.
        /*viewModel.currentScrambledWord.observe(viewLifecycleOwner,
                { newWord ->
                 binding.textViewUnscrambledWord.text = newWord
                })*/

    }

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()
        if(viewModel.isUserWordCorrect(playerWord)){
           setErrorTextField(false)
            if (!viewModel.nextWord()){
                showFinalScoreDialog()
                //updateNextWordOnScreen()
            }
        }else{
            setErrorTextField(true)
        }

    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()){
            setErrorTextField(false)
            //updateNextWordOnScreen()
        } else {
            showFinalScoreDialog()
        }

    }


    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitalizeData()
        setErrorTextField(false)
        //updateNextWordOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
     * Displays the next scrambled word on screen.
    private fun updateNextWordOnScreen() {
        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
    }
    * se usa live data por lo tanto no se necesita mas actualizar
    * la ui manualmente
     */
   /* override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }*/

    private fun showFinalScoreDialog(){
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.congratulations))
                .setMessage(getString(R.string.you_scored, viewModel.score.value))
                .setCancelable(false)
                //. When the last argument being passed in is a function,
                // you could place the lambda expression outside the parentheses.
                // This is known as trailing lambda syntax.
                .setNegativeButton(getString(R.string.exit)){_, _  ->
                    exitGame()
                }
                .setPositiveButton(getString(R.string.play_again)){_, _ ->
                    restartGame()
                }
                .show()

    }
}
