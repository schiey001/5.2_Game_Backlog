package com.example.gamebacklog.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gamebacklog.R
import com.example.gamebacklog.adapter.GamesAdapter
import com.example.gamebacklog.database.GameRepository
import com.example.gamebacklog.model.Game
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_games.*
import kotlinx.android.synthetic.main.games_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ADD_GAME_REQUEST_CODE = 100

class GamesActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private val games = arrayListOf<Game>()
    private val gamesAdapter = GamesAdapter(this, games) { gameItem : Game -> quizItemClicked(gameItem) }
    private lateinit var gameRepository: GameRepository
    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("Logging", "Test 1")
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Game Backlog"
        setContentView(R.layout.activity_games)
        gameRepository = GameRepository(this)
        initView()
        fab.setOnClickListener {
            startAddActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_clear_history -> {
                mainScope.launch {
                    gameRepository.deleteAllGames()
                    games.clear()
                    gamesAdapter.notifyDataSetChanged()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startAddActivity() {
        val intent = Intent(this, AddGameActivity::class.java)
        startActivityForResult(intent, ADD_GAME_REQUEST_CODE)
    }

    private fun initView() {
        Log.i("Logging", "Inside InitView")
        GameRecyclerView.layoutManager = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        GameRecyclerView.adapter = gamesAdapter
        getGamesFromDatabase()
        createItemTouchHelper().attachToRecyclerView(GameRecyclerView)
        Log.i("Logging", games.toString())
    }

    private fun getGamesFromDatabase() {
        mainScope.launch {
            val games = withContext(Dispatchers.IO) {
                gameRepository.getAllGames()
            }
            this@GamesActivity.games.clear()
            this@GamesActivity.games.addAll(games)
            gamesAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Create a touch helper to recognize when a user swipes an item from a recycler view.
     * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
     * and uses callbacks to signal when a user is performing these actions.
     */
    private fun createItemTouchHelper(): ItemTouchHelper {

        // Callback which is used to create the ItemTouch helper. Only enables left swipe.
        // Use ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) to also enable right swipe.
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // Enables or Disables the ability to move items up and down.
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // Callback triggered when a user swiped an item.
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val tmp = games[position]
                games.removeAt(position)
                mainScope.launch {
                    gameRepository.deleteGame(tmp)
                }
                gamesAdapter.notifyDataSetChanged()

                Snackbar
                    .make(GameRecyclerView, "Subscription Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        games.add(position, tmp)
                        mainScope.launch {
                            gameRepository.insertGame(tmp)
                        }
                        gamesAdapter.notifyDataSetChanged()
                    }
                    .show()
            }
        }
        return ItemTouchHelper(callback)
    }

    private fun quizItemClicked(gameItem : Game) {
        Log.i("Logging", "Test")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_GAME_REQUEST_CODE -> {
                    val game = data!!.getParcelableExtra<Game>(EXTRA_GAME)
                    games.add(game)
                    gamesAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}