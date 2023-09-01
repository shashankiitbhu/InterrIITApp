package com.example.interriitapp.Models

import com.google.gson.annotations.SerializedName


data class Recipe(
    @SerializedName("recipe") val recipe: RecipeDetails
)

data class RecipeDetails(
    @SerializedName("uri") val uri: String?,
    @SerializedName("label") val label: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("source") val source: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("yield") val yield: Double?,
    @SerializedName("dietLabels") val dietLabels: List<String>?,
    @SerializedName("healthLabels") val healthLabels: List<String>?,
    @SerializedName("cautions") val cautions: List<String>?,
    @SerializedName("ingredientLines") val ingredientLines: List<String>?,
    @SerializedName("ingredients") val ingredients: List<Ingredient>?,
    @SerializedName("calories") val calories: Double?,
    @SerializedName("co2EmissionsClass") val co2EmissionsClass: String?,
    @SerializedName("totalWeight") val totalWeight: Double?,
    @SerializedName("totalTime") val totalTime: Double?,
    @SerializedName("cuisineType") val cuisineType: List<String>?,
    @SerializedName("mealType") val mealType: List<String>?
)

data class Ingredient(
    @SerializedName("text") val text: String?,
    @SerializedName("quantity") val quantity: Double?,
    @SerializedName("measure") val measure: String?,
    @SerializedName("food") val food: String?,
    @SerializedName("weight") val weight: Double?,
    @SerializedName("foodCategory") val foodCategory: String?,
    @SerializedName("foodId") val foodId: String?,
    @SerializedName("image") val image: String?
)
