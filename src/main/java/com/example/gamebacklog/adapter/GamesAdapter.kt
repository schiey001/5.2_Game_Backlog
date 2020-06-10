package com.example.gamebacklog.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gamebacklog.R
import com.example.gamebacklog.model.Game

class GamesAdapter(private val context: Context, private val games: ArrayList<Game>, private val clickListener: (Game) -> Unit) : RecyclerView.Adapter<GamesAdapter.ViewHolder?>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(game: Game, clickListener: (Game) -> Unit) {

            val gameTitle: TextView = itemView.findViewById(R.id.GameTitle)
            val gamePlatform: TextView = itemView.findViewById(R.id.GamePlatform)
            val date: TextView = itemView.findViewById(R.id.tvDate)

            val dateString = "Release: " + game.day + " " + game.month + " " + game.year
            Log.i("Logging", dateString)
            gameTitle.text = game.gameTitle
            gamePlatform.text = game.gamePlatform
            date.text = "Release: " + game.day + " " + game.month + " " + game.year

            itemView.setOnClickListener { clickListener(game) }
        }
    }

    /**
     * Creates and returns a ViewHolder object.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.single_game, parent, false)
        return ViewHolder(view)
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return games.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(games[position], clickListener)
    }

}