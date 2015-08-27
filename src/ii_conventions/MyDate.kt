package ii_conventions

data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    public override fun compareTo(m: MyDate): Int {

        if (this.year < m.year)
            return -1
        else if (this.year > m.year)
            return 1
        else if (this.month < m.month)
            return -1
        else if (this.month > m.month)
            return 1
        else if (this.dayOfMonth < m.dayOfMonth)
            return -1
        else if (this.dayOfMonth > m.dayOfMonth)
            return 1
        else
            return 0
    }
}

enum class TimeInterval {
    DAY
    WEEK
    YEAR
}

class DateRange(public val start: MyDate, public val end: MyDate)
