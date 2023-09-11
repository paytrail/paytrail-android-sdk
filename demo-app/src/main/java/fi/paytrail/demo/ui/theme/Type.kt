package fi.paytrail.demo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.unit.sp
import fi.paytrail.paymentsdk.theme.Poppins

val PaytrailTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 16.sp,
    ),

    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = W700,
        fontSize = 24.sp,
        lineHeight = 36.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = W700,
        fontSize = 20.sp,
        lineHeight = 30.sp,
    ),

    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = W500,
        fontSize = 16.sp,
        lineHeight = 24.sp,

    ),

    /* Other default text styles to override
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
     */
)
