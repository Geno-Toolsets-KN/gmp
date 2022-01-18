@file:Suppress("unused")

package com.geno1024.cinterops.gmp

import cinterops.gmp.*
import kotlinx.cinterop.*

class GMPRational
{
    var mpq: mpq_t

    companion object
    {
        inline fun initMpq(): mpq_t = nativeHeap.allocArray(sizeOf<__mpq_struct>())

        inline fun unop(op: CPointer<CFunction<(mpq_ptr?, mpq_srcptr?) -> Unit>>?, op1: GMPRational): GMPRational
        {
            val result: mpq_t = initMpq()
            op!!(result, op1.mpq)
            return GMPRational(result)
        }

        inline fun biop(op: CPointer<CFunction<(mpq_ptr?, mpq_srcptr?, mpq_srcptr?) -> Unit>>?, op1: GMPRational, op2: GMPRational): GMPRational
        {
            val result: mpq_t = initMpq()
            op!!(result, op1.mpq, op2.mpq)
            return GMPRational(result)
        }

        fun swap(thiz: GMPRational, that: GMPRational) = mpq_swap!!(thiz.mpq, that.mpq)

        fun abs(thiz: GMPRational) = unop(mpq_abs, thiz)
    }

    constructor(numerator: Long, denominator: ULong)
    {
        mpq = initMpq()
        mpq_init!!(mpq)
        mpq_set_si!!(mpq, numerator, denominator)
        mpq_canonicalize!!(mpq)
    }

    constructor(numerator: ULong, denominator: ULong)
    {
        mpq = initMpq()
        mpq_init!!(mpq)
        mpq_set_ui!!(mpq, numerator, denominator)
        mpq_canonicalize!!(mpq)
    }

    constructor(numerator: GMPInt, denominator: GMPInt)
    {
        mpq = initMpq()
        mpq_init!!(mpq)
        setNumerator(numerator)
        setDenominator(denominator)
        mpq_canonicalize!!(mpq)
    }

    constructor(fraction: Pair<Long, ULong>) : this(fraction.first, fraction.second)

    constructor(fraction: Pair<ULong, ULong>) : this(fraction.first, fraction.second)

    constructor(string: String, base: Int = 10)
    {
        mpq = initMpq()
        val stringPtr = nativeHeap.allocArray<ByteVar>(string.length).apply {
            string.mapIndexed { index, char ->
                this[index] = char.code.toByte()
            }
        }
        mpq_init!!(mpq)
        mpq_set_str!!(mpq, stringPtr, base)
        mpq_canonicalize!!(mpq)
        nativeHeap.free(stringPtr)
    }

    constructor(mpq: mpq_t)
    {
        this.mpq = mpq
    }

    constructor(mpz: mpz_t)
    {
        mpq = initMpq()
        mpq_init!!(mpq)
        mpq_set_z!!(mpq, mpz)
    }

    constructor(mpf: mpf_t)
    {
        mpq = initMpq()
        mpq_init!!(mpq)
        mpq_set_f!!(mpq, mpf)
    }

    constructor(double: Double)
    {
        mpq = initMpq()
        mpq_init!!(mpq)
        mpq_set_d!!(mpq, double)
    }

    fun getDouble(): Double = mpq_get_d!!(mpq)

    operator fun plus(that: GMPRational): GMPRational = biop(mpq_add, this, that)

    operator fun minus(that: GMPRational): GMPRational = biop(mpq_sub, this, that)

    operator fun unaryMinus(): GMPRational = unop(mpq_neg, this)

    operator fun times(that: GMPRational): GMPRational = biop(mpq_mul, this, that)

    operator fun div(that: GMPRational): GMPRational = biop(mpq_div, this, that)

    fun inv(): GMPRational = unop(mpq_inv, this)

    fun getNumerator(): GMPInt
    {
        val result: mpz_t = GMPInt.initMpz()
        mpq_get_num!!(result, mpq)
        return GMPInt(result)
    }

    fun getDenominator(): GMPInt
    {
        val result: mpz_t = GMPInt.initMpz()
        mpq_get_den!!(result, mpq)
        return GMPInt(result)
    }

    fun setNumerator(gmpi: GMPInt): Unit = mpq_set_num!!(this.mpq, gmpi.mpz)

    fun setDenominator(gmpi: GMPInt): Unit = mpq_set_den!!(this.mpq, gmpi.mpz)

    operator fun compareTo(that: GMPRational): Int = mpq_cmp!!(this.mpq, that.mpq)

    override fun equals(other: Any?): Boolean = when(other)
    {
        is GMPRational -> mpq_equal!!(this.mpq, other.mpq) != 0
        is GMPInt -> mpq_cmp_z!!(this.mpq, other.mpz) == 0
        is Pair<*, *> ->
            if (other.first is Long && other.second is ULong)
                _mpq_cmp_si!!(this.mpq, other.first as Long, other.second as ULong) == 0
            else if (other.first is ULong && other.second is ULong)
                _mpq_cmp_ui!!(this.mpq, other.first as ULong, other.second as ULong) == 0
            else false
        else -> false
    }

    override fun toString(): String = toString(10)

    fun toString(base: Int): String = mpq_get_str!!(null, base, mpq)!!.toKString()

    override fun hashCode(): Int
    {
        return mpq.hashCode()
    }

    fun finalize() = mpq_clear!!(this.mpq)
}
