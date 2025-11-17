package com.ekas.pokedexplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PokemonAdapter(
    private var pokemonList: List<Pokemon>,
    private val clickListener: (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.text_view_pokemon_name)
        val typeTextView: TextView = itemView.findViewById(R.id.text_view_pokemon_types)
        val imageView: ImageView = itemView.findViewById(R.id.image_view_pokemon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon_card, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position]

        holder.nameTextView.text = pokemon.name
        holder.typeTextView.text = pokemon.type.joinToString(" / ")

        Glide.with(holder.itemView.context)
            .load(pokemon.imageUrl)
            .placeholder(R.drawable.ic_pokedex_logo)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            clickListener(pokemon)
        }
    }

    override fun getItemCount(): Int = pokemonList.size

    fun updateList(newList: List<Pokemon>) {
        pokemonList = newList
        notifyDataSetChanged()
    }
}