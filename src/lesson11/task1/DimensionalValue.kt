@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import java.lang.IllegalArgumentException

/**
 * Класс "Величина с размерностью".
 *
 * Предназначен для представления величин вроде "6 метров" или "3 килограмма"
 * Общая сложность задания - средняя, общая ценность в баллах -- 18
 * Величины с размерностью можно складывать, вычитать, делить, менять им знак.
 * Их также можно умножать и делить на число.
 *
 * В конструктор передаётся вещественное значение и строковая размерность.
 * Строковая размерность может:
 * - либо строго соответствовать одной из abbreviation класса Dimension (m, g)
 * - либо соответствовать одной из приставок, к которой приписана сама размерность (Km, Kg, mm, mg)
 * - во всех остальных случаях следует бросить IllegalArgumentException
 */
class DimensionalValue(value: Double, dimension: String) : Comparable<DimensionalValue> {
    /**
     * Величина с БАЗОВОЙ размерностью (например для 1.0Kg следует вернуть результат в граммах -- 1000.0)
     */
    var value: Double = value
        get() = field * 1.0

    init {
        if (dimension.length > 1) {
            var str = dimension.substring(0, 2)
            val enumV = DimensionPrefix.values()
            for (i in enumV) {
                if (i.abbreviation == str)
                    this.value = value * i.multiplier
            }
            if (this.value == value) {
                str = dimension.substring(0, 1)
                for (i in enumV) {
                    if (i.abbreviation == str)
                        this.value = value * i.multiplier
                }
            }
        } else this.value = value
    }

    /**
     * БАЗОВАЯ размерность (опять-таки для 1.0Kg следует вернуть GRAM)
     */
    var dimension: Dimension = Dimension.INDEFINITED
        get() = Dimension.valueOf(field.name)

    init {
        if (dimension.length > 1) {
            val enumV = Dimension.values()
            for (i in enumV) {
                var str = dimension
                    .substring(dimension.length - 2, dimension.length)
                if (i.abbreviation == str)
                    this.dimension = Dimension.valueOf(i.name)
                else {
                    str =
                        dimension
                            .substring(dimension.length - 1, dimension.length)
                    if (i.abbreviation == str) this.dimension = Dimension.valueOf(i.name)
                }
            }
        } else {
            val str = dimension
                .substring(dimension.length - 1, dimension.length)
            val enumV = Dimension.values()
            for (i in enumV) {
                if (i.abbreviation == str)
                    this.dimension = Dimension.valueOf(i.name)
            }
        }
    }

    /**
     * Конструктор из строки. Формат строки: значение пробел размерность (1 Kg, 3 mm, 100 g и так далее).
     */

    constructor(s: String) : this(value = stringNumber(s), dimension = stringWord(s))

    companion object {
        fun stringNumber(s: String): Double {
            val reg = Regex("""\d+""").findAll(s)
            return if (reg.map { it.groupValues }.count() == 1) {
                if (s[0] == '-') {
                    -reg.map { it.value }.joinToString().toDouble()
                } else reg.map { it.value }.joinToString().toDouble()
            } else -1.0

        }

        fun stringWord(s: String): String {
            val res = s.replace(Regex("""\d+ """), "")
            return res.trim()
        }
    }

    /**
     * Сложение с другой величиной. Если базовая размерность разная, бросить IllegalArgumentException
     * (нельзя складывать метры и килограммы)
     */
    operator fun plus(other: DimensionalValue): DimensionalValue {
        if (dimension == other.dimension) {
            val plusV = value + other.value
            return DimensionalValue(plusV, dimension.abbreviation)
        } else throw IllegalArgumentException()
    }

    /**
     * Смена знака величины
     */
    operator fun unaryMinus(): DimensionalValue = DimensionalValue(-1.0 * value, dimension.abbreviation)


    /**
     * Вычитание другой величины. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun minus(other: DimensionalValue): DimensionalValue {
        if (dimension == other.dimension) {
            val minusV = value - other.value
            return DimensionalValue(minusV, dimension.abbreviation)
        } else throw IllegalArgumentException()
    }

    /**
     * Умножение на число
     */
    operator fun times(other: Double): DimensionalValue = DimensionalValue(value * other, dimension.abbreviation)

    /**
     * Деление на число
     */
    operator fun div(other: Double): DimensionalValue = DimensionalValue(value / other, dimension.abbreviation)

    /**
     * Деление на другую величину. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun div(other: DimensionalValue): Double {
        if (dimension == other.dimension) {
            return value / other.value
        } else throw IllegalArgumentException()
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean =
        other is DimensionalValue && value == other.value && dimension == other.dimension

    /**
     * Сравнение на больше/меньше. Если базовая размерность разная, бросить IllegalArgumentException
     */
    override fun compareTo(other: DimensionalValue): Int {
        if (other.dimension == dimension) {
            val diff = value - other.value
            if (diff > 0)
                return 1
            if (diff < 0)
                return -1
            return 0
        } else throw IllegalArgumentException()
    }

    override fun hashCode(): Int = dimension.hashCode() + value.hashCode()
}

/**
 * Размерность. В этот класс можно добавлять новые варианты (секунды, амперы, прочие), но нельзя убирать
 */
enum class Dimension(val abbreviation: String) {
    INDEFINITED("indef"),
    METER("m"),
    GRAM("g"),
    HERTZ("Hz"),
    JOULE("J"),
    CANDELA("cd"),
    COULOMB("C");
}

/**
 * Приставка размерности. Опять-таки можно добавить новые варианты (деци-, санти-, мега-, ...), но нельзя убирать
 */
enum class DimensionPrefix(val abbreviation: String, val multiplier: Double) {
    KILO("K", 1000.0),
    MILLI("m", 0.001),
    MICRO("mc", 0.000001),
    MEGA("M", 1000000.0),
    NANO("n", 0.1E-8);
}