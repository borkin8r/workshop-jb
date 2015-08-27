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
    public fun rangeTo(otherDate: MyDate): DateRange {
        return DateRange(this, otherDate)
    }
}

enum class TimeInterval {
    DAY
    WEEK
    YEAR
}

class DateRange(public override val start: MyDate, public override val end: MyDate): Iterator<MyDate>, Range<MyDate> {
    //for iterating with hasnext and next
    private var previousDate = MyDate(-1, -1, -1)

    public override fun contains(proposedDate: MyDate): Boolean {
        return if (proposedDate >= start && proposedDate <= end) true else false
    }

    public override fun hasNext(): Boolean {
        if (previousDate.dayOfMonth == -1) {
            previousDate = MyDate(start.year, start.month, start.dayOfMonth) //put date correcting code in here, next just returns date validated in hasNext
        } else {
            //increment day of month
            var nextDay = previousDate.dayOfMonth + 1
            var wasThirtyDayMonth = false
            val thirtyDayMonths = intArrayOf(4, 6, 9, 11)
            for (month in thirtyDayMonths.indices)
                if (this.start.month == month) {
                    nextDay %= 30
                    if (nextDay == 0) nextDay = 30 //correct for next day is last day of the month
                    wasThirtyDayMonth = true
                    break
                }

            if (!wasThirtyDayMonth && this.start.month != 2) {
                nextDay %= 31
                if (nextDay == 0) nextDay = 31 //correct for next day is last day of the month
            }


            //is leap year
            if (this.start.year % 4 == 0 && this.start.month == 2) {
                nextDay %= 29
                if (nextDay == 0) nextDay = 29 //correct for next day is last day of the month
            }
            else if (this.start.month == 2) {
                nextDay %= 28
                if (nextDay == 0) nextDay = 28 //correct for next day is last day of the month
            }

            var nextMonth = this.start.month
            var nextYear = this.start.year
            //check if month needs to be incremented
            if (nextDay == 1) {
                nextMonth = (nextMonth + 1) % 12
                if (nextMonth == 0) nextMonth = 12 //correct for when next month is dec
                //check for next year
                if (nextMonth == 1) nextYear += 1
            }

            previousDate = MyDate(nextYear, nextMonth, nextDay)
        }

        return if (this.previousDate <= this.end) true else false
    }

    public override fun next(): MyDate {
        //month: 9 4 6 11: 30 else 31 except feb 28/29 leap year 2016
        //check if first date
        if (previousDate == start) {
            previousDate = MyDate(previousDate.year, previousDate.month, previousDate.dayOfMonth)
            return start
        }

        return previousDate
    }
}
