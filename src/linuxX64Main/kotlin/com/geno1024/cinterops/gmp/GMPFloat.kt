@file:Suppress("unused")

package com.geno1024.cinterops.gmp

import cinterops.gmp.*
import kotlinx.cinterop.*

class GMPFloat
{
    var mpf: mpf_t

    companion object
    {
        inline fun initMpf(): mpf_t = nativeHeap.allocArray(sizeOf<__mpf_struct>())

        var default_precision: ULong
            get() = mpf_get_default_prec!!()
            set(value) = mpf_set_default_prec!!(value)

        inline fun unop(op: CPointer<CFunction<(mpf_ptr?, mpf_srcptr?) -> Unit>>?, op1: GMPFloat): GMPFloat
        {
            val result: mpf_t = initMpf()
            op!!(result, op1.mpf)
            return GMPFloat(result)
        }

        inline fun biopmm(op: CPointer<CFunction<(mpf_ptr?, mpf_srcptr?, mpf_srcptr?) -> Unit>>?, op1: GMPFloat, op2: GMPFloat): GMPFloat
        {
            val result: mpf_t = initMpf()
            op!!(result, op1.mpf, op2.mpf)
            return GMPFloat(result)
        }

        inline fun biopmu(op: CPointer<CFunction<(mpf_ptr?, mpf_srcptr?, ULong) -> Unit>>?, op1: GMPFloat, op2: ULong): GMPFloat
        {
            val result: mpf_t = initMpf()
            op!!(result, op1.mpf, op2)
            return GMPFloat(result)
        }

        inline fun biopum(op: CPointer<CFunction<(mpf_ptr?, ULong, mpf_srcptr?) -> Unit>>?, op1: ULong, op2: GMPFloat): GMPFloat
        {
            val result: mpf_t = initMpf()
            op!!(result, op1, op2.mpf)
            return GMPFloat(result)
        }

        fun abs(thiz: GMPFloat): GMPFloat = unop(mpf_abs, thiz)

        fun sqrt(thiz: GMPFloat): GMPFloat = unop(mpf_sqrt, thiz)

        fun sqrt(ulong: ULong): GMPFloat
        {
            val result: mpf_t = initMpf()
            mpf_sqrt_ui!!(result, ulong)
            return GMPFloat(result)
        }

        fun pow(thiz: GMPFloat, ulong: ULong): GMPFloat = biopmu(mpf_pow_ui, thiz, ulong)

        fun ceil(thiz: GMPFloat): GMPFloat = unop(mpf_ceil, thiz)

        fun floor(thiz: GMPFloat): GMPFloat = unop(mpf_floor, thiz)

        fun trunc(thiz: GMPFloat): GMPFloat = unop(mpf_trunc, thiz)

        fun isLong(thiz: GMPFloat): Boolean = mpf_integer_p!!(thiz.mpf) != 0
    }

    constructor(long: Long)
    {
        mpf = initMpf()
        mpf_init_set_si!!(mpf, long)
    }

    constructor(ulong: ULong)
    {
        mpf = initMpf()
        mpf_init_set_ui!!(mpf, ulong)
    }

    constructor(double: Double)
    {
        mpf = initMpf()
        mpf_init_set_d!!(mpf, double)
    }

    constructor(mpz: mpz_t)
    {
        mpf = initMpf()
        mpf_init!!(mpf)
        mpf_set_z!!(mpf, mpz)
    }

    constructor(gmpi: GMPInt) : this(gmpi.mpz)

    constructor(mpq: mpq_t)
    {
        mpf = initMpf()
        mpf_init!!(mpf)
        mpf_set_q!!(mpf, mpq)
    }

    constructor(gmpr: GMPRational) : this(gmpr.mpq)

    constructor(string: String, base: Int = 10)
    {
        mpf = initMpf()
        val stringPtr = nativeHeap.allocArray<ByteVar>(string.length).apply {
            string.mapIndexed { index, char ->
                this[index] = char.code.toByte()
            }
        }
        mpf_init_set_str!!(mpf, stringPtr, base)
        nativeHeap.free(stringPtr)
    }

    constructor(mpf: mpf_t)
    {
        this.mpf = mpf
    }

    fun getLong(): Long = mpf_get_si!!(mpf)

    fun getULong(): ULong = mpf_get_ui!!(mpf)

    fun getDouble(): Double = mpf_get_d!!(mpf)

    fun getPrecision(): ULong = mpf_get_prec!!(mpf)

    fun setPrecision(ulong: ULong): Unit = mpf_set_prec!!(mpf, ulong)

    fun setPrecisionFast(ulong: ULong): Unit = mpf_set_prec_raw!!(mpf, ulong)

    operator fun plus(that: GMPFloat): GMPFloat = biopmm(mpf_add, this, that)

    operator fun plus(ulong: ULong): GMPFloat = biopmu(mpf_add_ui, this, ulong)

    operator fun ULong.plus(that: GMPFloat): GMPFloat = biopmu(mpf_add_ui, that, this)

    operator fun plusAssign(that: GMPFloat): Unit = mpf_add!!(this.mpf, this.mpf, that.mpf)

    operator fun plusAssign(ulong: ULong): Unit = mpf_add_ui!!(this.mpf, this.mpf, ulong)

    operator fun minus(that: GMPFloat): GMPFloat = biopmm(mpf_sub, this, that)

    operator fun minus(ulong: ULong): GMPFloat = biopmu(mpf_sub_ui, this, ulong)

    operator fun ULong.minus(that: GMPFloat): GMPFloat = biopum(mpf_ui_sub, this, that)

    operator fun minusAssign(that: GMPFloat): Unit = mpf_sub!!(this.mpf, this.mpf, that.mpf)

    operator fun minusAssign(ulong: ULong): Unit = mpf_sub_ui!!(this.mpf, this.mpf, ulong)

    operator fun unaryMinus(): GMPFloat = unop(mpf_neg, this)

    operator fun times(that: GMPFloat): GMPFloat = biopmm(mpf_mul, this, that)

    operator fun times(ulong: ULong): GMPFloat = biopmu(mpf_mul_ui, this, ulong)

    operator fun ULong.times(that: GMPFloat): GMPFloat = biopmu(mpf_mul_ui, that, this)

    operator fun timesAssign(that: GMPFloat): Unit = mpf_mul!!(this.mpf, this.mpf, that.mpf)

    operator fun timesAssign(ulong: ULong): Unit = mpf_mul_ui!!(this.mpf, this.mpf, ulong)

    operator fun div(that: GMPFloat): GMPFloat = biopmm(mpf_div, this, that)

    operator fun div(ulong: ULong): GMPFloat = biopmu(mpf_div_ui, this, ulong)

    operator fun ULong.div(that: GMPFloat): GMPFloat = biopmu(mpf_div_ui, that, this)

    operator fun compareTo(that: GMPFloat): Int = mpf_cmp!!(this.mpf, that.mpf)

    operator fun compareTo(mpz: mpz_t): Int = mpf_cmp_z!!(this.mpf, mpz)

    operator fun compareTo(gmpi: GMPInt): Int = mpf_cmp_z!!(this.mpf, gmpi.mpz)

    operator fun compareTo(double: Double): Int = mpf_cmp_d!!(this.mpf, double)

    operator fun compareTo(ulong: ULong): Int = mpf_cmp_ui!!(this.mpf, ulong)

    operator fun compareTo(long: Long): Int = mpf_cmp_si!!(this.mpf, long)

    override operator fun equals(other: Any?): Boolean = when(other)
    {
        is GMPFloat -> compareTo(other) == 0
        is GMPInt -> compareTo(other) == 0
        is Double -> compareTo(other) == 0
        is ULong -> compareTo(other) == 0
        is Long -> compareTo(other) == 0
        else -> false
    }

    override fun toString(): String = toString(10)

    fun toString(base: Int): String
    {
        // TODO
        /*
        mpf_get_str (digit_ptr, exp, base, n_digits, a) -- Convert the floating
   polong number A to a base BASE number and store N_DIGITS raw digits at
   DIGIT_PTR, and the base BASE exponent in the word polonged to by EXP.  For
   example, the number 3.1416 would be returned as "31416" in DIGIT_PTR and
   1 in EXP.
         */

        val expPtr = nativeHeap.alloc<mp_exp_tVar>()
        val result = mpf_get_str!!(null, expPtr.ptr, base, 0UL, mpf)
        return result!!.toKString()
    }

    override fun hashCode(): Int
    {
        return mpf.hashCode()
    }

    fun finalize(): Unit = mpf_clear!!(mpf)
}
