@file:Suppress("unused")

package com.geno1024.cinterops.gmp

fun Long.toGMPInt() = GMPInt(this)

fun ULong.toGMPInt() = GMPInt(this)

fun String.toGMPInt() = GMPInt(this)


fun Long.toGMPFloat() = GMPFloat(this)

fun ULong.toGMPFloat() = GMPFloat(this)

fun Double.toGMPFloat() = GMPFloat(this)

fun String.toGMPFloat() = GMPFloat(this)


fun Pair<Long, ULong>.toGMPRational() = GMPRational(first, second)

fun Pair<ULong, ULong>.toGMPRational() = GMPRational(first, second)

fun String.toGMPRational() = GMPRational(this)
