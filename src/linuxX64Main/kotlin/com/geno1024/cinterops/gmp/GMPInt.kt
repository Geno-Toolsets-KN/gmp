@file:Suppress("unused")

package com.geno1024.cinterops.gmp

import cinterops.gmp.*
import kotlinx.cinterop.*

class GMPInt
{
    var mpz: mpz_t

    companion object
    {
        inline fun initMpz(): mpz_t = nativeHeap.allocArray(sizeOf<__mpz_struct>())

        inline fun unop(op: CPointer<CFunction<(mpz_ptr?, mpz_srcptr?) -> Unit>>?, op1: GMPInt): GMPInt
        {
            val result: mpz_t = initMpz()
            op!!(result, op1.mpz)
            return GMPInt(result)
        }

        inline fun biopmm(op: CPointer<CFunction<(mpz_ptr?, mpz_srcptr?, mpz_srcptr?) -> Unit>>?, op1: GMPInt, op2: GMPInt): GMPInt
        {
            val result: mpz_t = initMpz()
            op!!(result, op1.mpz, op2.mpz)
            return GMPInt(result)
        }

        inline fun biopmu(op: CPointer<CFunction<(mpz_ptr?, mpz_srcptr?, ULong) -> Unit>>?, op1: GMPInt, op2: ULong): GMPInt
        {
            val result: mpz_t = initMpz()
            op!!(result, op1.mpz, op2)
            return GMPInt(result)
        }

        inline fun biopms(op: CPointer<CFunction<(mpz_ptr?, mpz_srcptr?, Long) -> Unit>>?, op1: GMPInt, op2: Long): GMPInt
        {
            val result: mpz_t = initMpz()
            op!!(result, op1.mpz, op2)
            return GMPInt(result)
        }

        inline fun biopum(op: CPointer<CFunction<(mpz_ptr?, ULong, mpz_srcptr?) -> Unit>>?, op1: ULong, op2: GMPInt): GMPInt
        {
            val result: mpz_t = initMpz()
            op!!(result, op1, op2.mpz)
            return GMPInt(result)
        }

        inline fun biopsm(op: CPointer<CFunction<(mpz_ptr?, Long, mpz_srcptr?) -> Unit>>?, op1: Long, op2: GMPInt): GMPInt
        {
            val result: mpz_t = initMpz()
            op!!(result, op1, op2.mpz)
            return GMPInt(result)
        }

        fun swap(thiz: GMPInt, that: GMPInt) = mpz_swap!!(thiz.mpz, that.mpz)

        fun abs(thiz: GMPInt) = unop(mpz_abs, thiz)

        fun gcd(thiz: GMPInt, that: GMPInt): GMPInt = biopmm(mpz_gcd, thiz, that)

        fun gcd(thiz: GMPInt, ulong: ULong): ULong = mpz_gcd_ui!!(null, thiz.mpz, ulong)

        fun lcm(thiz: GMPInt, that: GMPInt): GMPInt = biopmm(mpz_lcm, thiz, that)

        fun lcm(thiz: GMPInt, ulong: ULong): GMPInt = biopmu(mpz_lcm_ui, thiz, ulong)
    }

    constructor()
    {
        mpz = initMpz()
        mpz_init!!(mpz)
    }

    constructor(long: Long)
    {
        mpz = initMpz()
        mpz_init_set_si!!(mpz, long)
    }

    constructor(ulong: ULong)
    {
        mpz = initMpz()
        mpz_init_set_ui!!(mpz, ulong)
    }

    constructor(double: Double)
    {
        mpz = initMpz()
        mpz_init_set_d!!(mpz, double)
    }

    constructor(string: String, base: Int = 10)
    {
        mpz = initMpz()
        val stringPtr = nativeHeap.allocArray<ByteVar>(string.length).apply {
            string.mapIndexed { index, char ->
                this[index] = char.code.toByte()
            }
        }
        mpz_init_set_str!!(mpz, stringPtr, base)
        nativeHeap.free(stringPtr)
    }

    constructor(mpz: mpz_t)
    {
        this.mpz = mpz
    }

    constructor(mpq: mpq_t)
    {
        mpz = initMpz()
        mpz_init!!(mpz)
        mpz_set_q!!(mpz, mpq)
    }

    constructor(gmpr: GMPRational) : this(gmpr.mpq)

    constructor(mpf: mpf_t)
    {
        mpz = initMpz()
        mpz_init!!(mpz)
        mpz_set_f!!(mpz, mpf)
    }

    constructor(gmpf: GMPFloat) : this(gmpf.mpf)

    fun getLong(): Long = mpz_get_si!!(mpz)

    fun getULong(): ULong = mpz_get_ui!!(mpz)

    fun getDouble(): Double = mpz_get_d!!(mpz)

    operator fun plus(that: GMPInt): GMPInt = biopmm(mpz_add, this, that)

    operator fun plus(ulong: ULong): GMPInt = biopmu(mpz_add_ui, this, ulong)

    operator fun ULong.plus(that: GMPInt): GMPInt = biopmu(mpz_add_ui, that, this)

    operator fun plusAssign(that: GMPInt): Unit = mpz_add!!(this.mpz, this.mpz, that.mpz)

    operator fun plusAssign(ulong: ULong): Unit = mpz_add_ui!!(this.mpz, this.mpz, ulong)

    operator fun minus(that: GMPInt): GMPInt = biopmm(mpz_sub, this, that)

    operator fun minus(ulong: ULong): GMPInt = biopmu(mpz_sub_ui, this, ulong)

    operator fun ULong.minus(that: GMPInt): GMPInt = biopum(mpz_ui_sub, this, that)

    operator fun minusAssign(that: GMPInt): Unit = mpz_sub!!(this.mpz, this.mpz, that.mpz)

    operator fun minusAssign(ulong: ULong): Unit = mpz_sub_ui!!(this.mpz, this.mpz, ulong)

    operator fun unaryMinus(): GMPInt = unop(mpz_neg, this)

    operator fun times(that: GMPInt): GMPInt = biopmm(mpz_mul, this, that)

    operator fun times(int: Long): GMPInt = biopms(mpz_mul_si, this, int)

    operator fun times(ulong: ULong): GMPInt = biopmu(mpz_mul_ui, this, ulong)

    operator fun ULong.times(that: GMPInt): GMPInt = biopmu(mpz_mul_ui, that, this)

    operator fun timesAssign(that: GMPInt): Unit = mpz_mul!!(this.mpz, this.mpz, that.mpz)

    operator fun timesAssign(int: Long): Unit = mpz_mul_si!!(this.mpz, this.mpz, int)

    operator fun timesAssign(ulong: ULong): Unit = mpz_mul_ui!!(this.mpz, this.mpz, ulong)

    operator fun div(that: GMPInt): GMPInt = biopmm(mpz_div, this, that)

    //    operator fun div(ulong: ULong): GMPInt = biopmu(mpz_div_ui, this, ulong)
    //
    //    operator fun ULong.div(that: GMPInt) = biopmu(mpz_div_ui, that, this)

    operator fun divAssign(that: GMPInt): Unit = mpz_div!!(this.mpz, this.mpz, that.mpz)

    operator fun rem(that: GMPInt): GMPInt = biopmm(mpz_mod, this, that)

    operator fun compareTo(that: GMPInt): Int = mpz_cmp!!(this.mpz, that.mpz)

    infix fun and(that: GMPInt): GMPInt = biopmm(mpz_and, this, that)

    infix fun or(that: GMPInt): GMPInt = biopmm(mpz_ior, this, that)

    infix fun xor(that: GMPInt): GMPInt = biopmm(mpz_xor, this, that)

    override operator fun equals(other: Any?): Boolean = when(other)
    {
        is GMPInt -> mpz_cmp!!(this.mpz, other.mpz) == 0
        is Double -> mpz_cmp_d!!(this.mpz, other) == 0
        is Long -> _mpz_cmp_si!!(this.mpz, other) == 0
        is ULong -> _mpz_cmp_ui!!(this.mpz, other) == 0
        else -> false
    }

    override fun toString(): String = toString(10)

    fun toString(base: Int): String = mpz_get_str!!(null, base, mpz)!!.toKString()

    override fun hashCode(): Int
    {
        return mpz.hashCode()
    }

    fun finalize() = mpz_clear!!(this.mpz)
}
