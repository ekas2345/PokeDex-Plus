package com.ekas.pokedexplus

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Pokemon(
    @DocumentId
    val documentId: String = "",
    val id: Int = 0,
    val name: String = "",
    val type: List<String> = listOf(),
    val imageUrl: String = "",
    val stats: Map<String, Int> = mapOf(),
    val abilities: List<String> = listOf()
)

data class Favorite(
    @DocumentId
    val documentId: String = "",
    val uid: String = "",
    val pokemonId: String = "",
    val timestamp: Date = Date()
)

data class FavoriteDetail(
    val pokemon: Pokemon,
    val favoriteId: String
)