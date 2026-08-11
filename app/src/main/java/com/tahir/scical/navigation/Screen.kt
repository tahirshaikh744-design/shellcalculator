package com.tahir.scical.navigation

import kotlinx.serialization.Serializable

/**
 * Sealed class representing all navigation destinations in the app
 */
sealed interface Screen {
    @Serializable
    object Main : Screen
    
    @Serializable
    object Calculator : Screen
    
    @Serializable
    object Converter : Screen
    
    @Serializable
    object Finance : Screen
    
    @Serializable
    object Smart : Screen
}

/**
 * Individual calculator screens for detailed views
 */
sealed interface CalculatorScreen {
    @Serializable
    data object Age : CalculatorScreen
    
    @Serializable
    data object Area : CalculatorScreen
    
    @Serializable
    data object Bmi : CalculatorScreen
    
    @Serializable
    data object Data : CalculatorScreen
    
    @Serializable
    data object Date : CalculatorScreen
    
    @Serializable
    data object Discount : CalculatorScreen
    
    @Serializable
    data object Length : CalculatorScreen
    
    @Serializable
    data object Mass : CalculatorScreen
    
    @Serializable
    data object Numeral : CalculatorScreen
    
    @Serializable
    data object Speed : CalculatorScreen
    
    @Serializable
    data object Temperature : CalculatorScreen
    
    @Serializable
    data object Time : CalculatorScreen
    
    @Serializable
    data object Volume : CalculatorScreen
    
    @Serializable
    data object Angle : CalculatorScreen
    
    @Serializable
    data object Energy : CalculatorScreen
    
    @Serializable
    data object Force : CalculatorScreen
    
    @Serializable
    data object FuelEfficiency : CalculatorScreen
    
    @Serializable
    data object Power : CalculatorScreen
    
    @Serializable
    data object Pressure : CalculatorScreen
    
    @Serializable
    data object Percentage : CalculatorScreen
    
    @Serializable
    data object UnitPrice : CalculatorScreen
    
    @Serializable
    data object Tip : CalculatorScreen
    
    @Serializable
    data object Torque : CalculatorScreen
    
    @Serializable
    data object Frequency : CalculatorScreen
    
    @Serializable
    data object VolumeFlow : CalculatorScreen
    
    @Serializable
    data object Cooking : CalculatorScreen
    
    @Serializable
    data object TransferRate : CalculatorScreen
    
    @Serializable
    data object Acceleration : CalculatorScreen
    
    @Serializable
    data object Sound : CalculatorScreen
    
    @Serializable
    data object ElectricCurrent : CalculatorScreen
    
    @Serializable
    data object Voltage : CalculatorScreen
    
    @Serializable
    data object Resistance : CalculatorScreen
    
    @Serializable
    data object Density : CalculatorScreen
    
    @Serializable
    data object Illuminance : CalculatorScreen
    
    @Serializable
    data object Radiation : CalculatorScreen
    
    @Serializable
    data object ElectricCharge : CalculatorScreen
    
    @Serializable
    data object Inductance : CalculatorScreen
}

/**
 * Finance calculator screens
 */
sealed interface FinanceScreen {
    @Serializable
    data object Gst : FinanceScreen
    
    @Serializable
    data object Currency : FinanceScreen
    
    @Serializable
    data object Investment : FinanceScreen
    
    @Serializable
    data object Loan : FinanceScreen
    
    @Serializable
    data object Inflation : FinanceScreen
    
    @Serializable
    data object FD : FinanceScreen
    
    @Serializable
    data object RD : FinanceScreen
    
    @Serializable
    data object Tax : FinanceScreen
}

/**
 * Smart feature screens
 */
sealed interface SmartScreen {
    @Serializable
    data object WorldClock : SmartScreen
    
    @Serializable
    data object Constants : SmartScreen
    
    @Serializable
    data object Capitals : SmartScreen
}
