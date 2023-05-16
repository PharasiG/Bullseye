package com.example.bullseye

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build.VERSION_CODES.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bullseye.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.random.Random
import com.example.bullseye.R.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var seekValue = 50
    private var targetValue = generateTargetValue()
    private var score = 0
    private var totalScore = 0
    private var round = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        restartGame()

        binding.startOver?.setOnClickListener {
            restartGame()
        }

        binding.infoButton?.setOnClickListener {
            navigateToABoutPage()
        }

        binding.hitMeButton.setOnClickListener {
            Log.i(TAG, "Hit me button was clicked")
            calcScore()
            showResult()
            binding.scoreCount?.text = totalScore.toString()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                seekValue = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
    }

    private fun getDifference() = abs(targetValue - seekValue)

    private fun generateTargetValue() = Random.nextInt(1, 100)

    private fun calcScore() {
        score = 100 - getDifference()
        val bonus = when (score) {
            100 -> 100
            99 -> 51
            98 -> 52
            else -> 0
        }
        score += bonus
        totalScore += score
    }

    private fun showResult() {
        val dialogTitle = getDialogTitle()
        val dialogMessage = getString(string.result_dialog_message, targetValue, seekValue, score)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.setPositiveButton(getString(string.result_dialog_close_text)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()

        round++
        binding.roundCount?.text = round.toString()

        targetValue = generateTargetValue()
        binding.targetTextView.text = targetValue.toString()
    }

    private fun getDialogTitle(): String {
        val difference = getDifference()
        val ans = when {
            difference == 0 -> getString(string.alertPerfect)
            difference <= 5 -> getString(string.alertAlmost)
            difference <= 15 -> getString(string.alertDecent)
            else -> getString(string.alertSeriously)
        }

        return ans
    }

    private fun restartGame() {
        targetValue = generateTargetValue()
        score = 0
        totalScore = 0
        round = 0
        seekValue = 50

        binding.targetTextView.text = targetValue.toString()
        binding.roundCount?.text = round.toString()
        binding.seekBar.progress = 50
        binding.scoreCount?.text = totalScore.toString()
    }

    private fun navigateToABoutPage() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }
}