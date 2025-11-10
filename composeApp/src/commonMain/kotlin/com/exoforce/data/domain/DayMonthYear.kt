
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = DayMonthYearSerializer::class)
data class DayMonthYear(val date: LocalDate) {



    override fun toString(): String = date.toString() // Returns YYYY-MM-DD

    fun isZero(): Boolean = date.year == 1970 && date.monthNumber == 1 && date.dayOfMonth == 1

    fun addDays(days: Int): DayMonthYear {
        val period = DatePeriod(days = days)
        return DayMonthYear(date.plus(period))
    }

    fun addMonths(months: Int): DayMonthYear {
        val period = DatePeriod(months = months)
        return DayMonthYear(date.plus(period))
    }

    fun addYears(years: Int): DayMonthYear {
        val period = DatePeriod(years = years)
        return DayMonthYear(date.plus(period))
    }

    companion object {
        fun today(): DayMonthYear {
            val now = Clock.System.now()
            return DayMonthYear(now.toLocalDateTime(TimeZone.currentSystemDefault()).date)
        }

        fun from(year: Int, month: Int, day: Int): DayMonthYear =
            DayMonthYear(LocalDate(year, month, day))
    }
}

object DayMonthYearSerializer : KSerializer<DayMonthYear> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("DayMonthYear", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DayMonthYear) {
        encoder.encodeString(value.date.toString()) // Outputs YYYY-MM-DD
    }

    override fun deserialize(decoder: Decoder): DayMonthYear {
        val dateString = decoder.decodeString()

        // Handle empty string
        if (dateString.isEmpty()) {
            return DayMonthYear(LocalDate(1970, 1, 1))
        }

        return try {
            DayMonthYear(LocalDate.parse(dateString))
        } catch (e: Exception) {
            throw SerializationException("Unable to parse date: $dateString", e)
        }
    }
}