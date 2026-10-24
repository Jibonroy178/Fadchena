package com.example.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fadchena.data.model.RiskLevel

data class RiskVisual(
    val bg: Color,
    val fg: Color,
    val icon: ImageVector
)

fun getRiskVisual(riskLevel: RiskLevel): RiskVisual {
    return when (riskLevel) {
        RiskLevel.HIGH_RISK -> RiskVisual(RiskHighRedBg, RiskHighRed, Icons.Default.Error)
        RiskLevel.SUSPICIOUS -> RiskVisual(RiskSuspiciousOrangeBg, RiskSuspiciousOrange, Icons.Default.Warning)
        RiskLevel.INSUFFICIENT_EVIDENCE -> RiskVisual(RiskInsufficientAmberBg, RiskInsufficientAmber, Icons.AutoMirrored.Filled.Help)
        RiskLevel.LOWER_RISK_SIGNS -> RiskVisual(RiskLowTealBg, RiskLowTeal, Icons.Default.CheckCircle)
    }
}
