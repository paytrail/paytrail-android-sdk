package fi.paytrail.paymentsdk.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import fi.paytrail.paymentsdk.R

// TODO: Remove unused fonts from definition here & corresponding files from res
val poppinsFontFamily = FontFamily(
    listOf(

        Font(R.font.poppins_extralight, FontWeight.ExtraLight),
        Font(R.font.poppins_light, FontWeight.Light),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_semibold, FontWeight.SemiBold),
        Font(R.font.poppins_bold, FontWeight.Bold),
        Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
        Font(R.font.poppins_black, FontWeight.Black),

        Font(R.font.poppins_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(R.font.poppins_light_italic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.poppins_medium_italic, FontWeight.Medium, FontStyle.Italic),
        Font(R.font.poppins_regular_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.poppins_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
        Font(R.font.poppins_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.poppins_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(R.font.poppins_black_italic, FontWeight.Black, FontStyle.Italic),
    ),
)
