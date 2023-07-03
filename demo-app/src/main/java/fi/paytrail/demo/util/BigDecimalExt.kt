package fi.paytrail.demo.util

import java.math.BigDecimal

operator fun BigDecimal.times(other: Int): BigDecimal = this * other.toBigDecimal()
operator fun Int.times(other: BigDecimal): BigDecimal = this.toBigDecimal() * other

operator fun BigDecimal.times(other: Long): BigDecimal = this * other.toBigDecimal()
operator fun Long.times(other: BigDecimal): BigDecimal = this.toBigDecimal() * other
