package edu.msudenver.cs3013.myfinalproject.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


/**
 * Returns a color associated with a Pokemon type.
 *
 * @param type Pokemon type name used to determine the display color
 * @return Color mapped to the specified Pokémon type
 */
fun typeColor(type: String): Color {
    return when (type) {
        "fire"     -> Color(0xFFE62829)
        "water"    -> Color(0xFF2980EF)
        "grass"    -> Color(0xFF3FA129)
        "electric" -> Color(0xFFFAC000)
        "psychic"  -> Color(0xFFEF4179)
        "ice"      -> Color(0xFF3DCEF3)
        "dragon"   -> Color(0xFF5060E1)
        "dark"     -> Color(0xFF624D4E)
        "fairy"    -> Color(0xFFEF70EF)
        "fighting" -> Color(0xFFFF8000)
        "poison"   -> Color(0xFF9141CB)
        "ground"   -> Color(0xFF915121)
        "rock"     -> Color(0xFFAFA981)
        "bug"      -> Color(0xFF91A119)
        "ghost"    -> Color(0xFF704170)
        "steel"    -> Color(0xFF60A1B8)
        "flying"   -> Color(0xFF81B9EF)
        "normal"   -> Color(0xFF9FA19F)
        else       -> Color.Gray
    }
}