package com.christaustin.habittrainer

/**
 * Data Class to store Habit info
 */

data class Habit(val title: String, val description: String, val image: Int)

fun getSampleHabits(): List<Habit> {
    return listOf(
            Habit("Go for a walk",
                    "A nice walk in the sun gets you ready for the day",
                    R.drawable.walk),

            Habit ("Drink water",
                    "A refreshing glass of water gets you hydrated",
                    R.drawable.water)

    )
}