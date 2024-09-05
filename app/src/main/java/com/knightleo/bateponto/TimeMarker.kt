package com.knightleo.bateponto

import com.knightleo.bateponto.Date.Companion.asDate
import com.knightleo.bateponto.DateAndTime.Companion.asDateAndTime
import com.knightleo.bateponto.Time.Companion.asTime
import com.knightleo.bateponto.Time.Companion.formattedAsTime
import io.github.aakira.napier.Napier
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val FILE_NAME: String = "times"
private const val NEW_DAY_HEADER_PREFIX: String = "---"

typealias BreakPoint = Pair<Date, List<Time>>
typealias BreakPoints = List<BreakPoint>
private typealias MutableBreakPoints = MutableList<Pair<Date, MutableList<Time>>>

private fun makeHeader(date: Date): String =
    "$NEW_DAY_HEADER_PREFIX ${date.formatted} $NEW_DAY_HEADER_PREFIX"

private fun readHeader(header: String): Date {
    val l = header
        .removePrefix("$NEW_DAY_HEADER_PREFIX ")
        .removeSuffix(" $NEW_DAY_HEADER_PREFIX")
        .split("/")
    return Date(
        year = l[2],
        month = l[1],
        day = l[0]
    )
}

data class Date(
    val year: String,
    val month: String,
    val day: String,
) {
    val formatted: String
        get() = "$day/$month/$year"
    companion object {
        fun String.asDate(): Date {
            val date = split("_")
            return Date(
                date[0], date[1], date[2]
            )
        }
    }
}

data class Time(
    val hour: String,
    val minute: String,
    val second: String
) {
    val formatted: String
        get() = "$hour:$minute"
    companion object {
        fun String.asTime(): Time {
            val time = split("_")
            return Time(
                time[0], time[1], time[2]
            )
        }
        fun String.formattedAsTime(): Time {
            val time = split(":")
            return Time(
                time[0], time[1], "00"
            )
        }
    }
}

data class DateAndTime(
    val date: Date,
    val time: Time
) {
    companion object {
        fun String.asDateAndTime(): DateAndTime {
            val l = split("-")
            return DateAndTime(
                date = l[0].asDate(),
                time = l[1].asTime()
            )
        }
    }
}

class TimeMarker(file: File) {
    private val dateFormat = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss", Locale.getDefault())
    private val file: File = file
        get() {
            if(!field.exists()) {
                Napier.d("Creating file at ${field.absolutePath}")
                field.createNewFile()
            }
            return field
        }
    fun reset() {
        file.writeText("")
    }
    fun readPreviousEntries(): BreakPoints {
        val list: MutableBreakPoints = mutableListOf()
        file.reader().apply {
            forEachLine {
                if(it.startsWith(NEW_DAY_HEADER_PREFIX)) {
                    list.add(readHeader(it) to mutableListOf())
                } else {
                    list.last().second.add(it.formattedAsTime())
                }
            }
            close()
        }
        Napier.d("Found logs: $list")
        return list
    }
    fun saveCurrentTime() {
        val currentTime = dateFormat.format(Calendar.getInstance().time).asDateAndTime()
        var lastSavedDate: Date? = null
        file.reader().apply {
            forEachLine {
                if(it.startsWith(NEW_DAY_HEADER_PREFIX)) {
                    lastSavedDate = readHeader(it)
                }
            }
            close()
        }
        FileOutputStream(file, true).run {
            if(lastSavedDate == null) {
                val t = makeHeader(currentTime.date)
                Napier.d("Appending\n$t to file")
                write(t.toByteArray())
            } else if(lastSavedDate != currentTime.date) {
                val t = "\n${makeHeader(currentTime.date)}"
                Napier.d("Appending $t to file")
                write(t.toByteArray())
            }
            this.flush()
            val f = currentTime.time.formatted
            Napier.d("Appending $f to file")
            write("\n$f".toByteArray())
            close()
        }
    }
}
