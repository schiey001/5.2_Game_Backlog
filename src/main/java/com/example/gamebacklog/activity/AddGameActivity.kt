package com.example.gamebacklog.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamebacklog.R
import com.example.gamebacklog.database.GameRepository
import com.example.gamebacklog.model.Game
import kotlinx.android.synthetic.main.activity_games.*
import kotlinx.android.synthetic.main.content_add_game.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val EXTRA_GAME = "EXTRA_GAME"

class AddGameActivity : AppCompatActivity() {

    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var gameRepository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_game)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        gameRepository = GameRepository(this)
        initViews()
    }

    private fun initViews() {
        fab.setOnClickListener { onSaveClick() }
    }

    private fun onSaveClick() {
        val gameTitle = etGameTitle.text.toString()
        val gamePlatform = etGamePlatform.text.toString()
        val day = etDay.text.toString()
        val month = etMonth.text.toString()
        val year = etYear.text.toString()

        if (etGameTitle.text.toString().isNotBlank() && etGamePlatform.text.toString().isNotBlank()) {
            mainScope.launch {
                val game = Game(gameTitle = gameTitle, gamePlatform = gamePlatform, day = day, month = month, year = year)
                withContext(Dispatchers.IO) {
                    gameRepository.insertGame(game)
                }
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_GAME, game)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        } else {
            Toast.makeText(this,"All fields need to be filled in correctly!"
                , Toast.LENGTH_SHORT).show()
        }
    }
}