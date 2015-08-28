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

    public fun plus(interval: TimeInterval): MyDate {

        if ( interval == TimeInterval.DAY ) {
            //TODO assumes 30 day month, doesnt handle leap year
            //fastforward by 1 day
            val nextDay = if ( ( this.dayOfMonth + 1 ) % 30 == 0 ) 30 else ( this.dayOfMonth + 1 ) % 30

            if (nextDay == 1) {
                val nextMonth = if ( ( this.month + 1 ) % 12 == 0 ) 12 else this.month + 1

                if ( nextMonth == 1) {
                    val nextYear = this.year + 1

                    return MyDate(nextYear, nextMonth, nextDay)
                }

                return MyDate(this.year, nextMonth, nextDay)
            }

            return MyDate(this.year, this.month, nextDay)
        } else if ( interval == TimeInterval.WEEK ) {
            //TODO assumes 30 day month, doesn't handle leap year
            //fastforward by 7 days
            val nextDay = if ( ( this.dayOfMonth + 7 ) % 30 == 0 ) 30 else ( this.dayOfMonth + 7 ) % 30

            if (nextDay in 1..7) {
                val nextMonth = if ( ( this.month + 1 ) % 12 == 0 ) 12 else this.month + 1

                if ( nextMonth == 1) {
                    val nextYear = this.year + 1

                    return MyDate(nextYear, nextMonth, nextDay)
                }

                return MyDate(this.year, nextMonth, nextDay)
            }

            return MyDate(this.year, this.month, nextDay)
        } else {
            //is time interval year
            //fastforward by 1 year
            return MyDate(this.year + 1, this.month, this.dayOfMonth) //TODO handle leap year
        }

    }

    public fun plus(repeatedInterval: RepeatedTimeInterval): MyDate {
        //TODO assumes 30 day month, doesnt handle leap year

        if ( repeatedInterval.ti == TimeInterval.DAY ) {
            //fastforward by ri.n days
            val monthsElapsed = ( repeatedInterval.n + this.dayOfMonth ) / 30
            val yearsElapsed = ( repeatedInterval.n + this.dayOfMonth ) / 365
            val nextDay = if ( ( this.dayOfMonth + repeatedInterval.n ) % 30 == 0 ) 30 else ( this.dayOfMonth + repeatedInterval.n ) % 30

            if ( monthsElapsed > 0 ) {
                val nextMonth = if ( ( this.month + monthsElapsed ) % 12 == 0 ) 12 else this.month + monthsElapsed

                if ( yearsElapsed > 0 ) {
                    val nextYear = this.year + yearsElapsed

                    return MyDate(nextYear, nextMonth, nextDay)
                }

                return MyDate(this.year, nextMonth, nextDay)
            }

            return MyDate(this.year, this.month, nextDay)
        } else if ( repeatedInterval.ti == TimeInterval.WEEK ) {
            //fastforward by ri.n * 7 days
            val monthsElapsed = ( ( repeatedInterval.n * 7 ) + this.dayOfMonth ) / 30
            val yearsElapsed = ( ( repeatedInterval.n * 7 ) + this.dayOfMonth ) / 365
            val nextDay = if ( ( this.dayOfMonth + ( repeatedInterval.n * 7)  ) % 30 == 0 ) 30 else ( this.dayOfMonth + ( repeatedInterval.n * 7 ) ) % 30

            if ( monthsElapsed > 0 ) {
                val nextMonth = if ( ( this.month + monthsElapsed ) % 12 == 0 ) 12 else this.month + monthsElapsed

                if ( yearsElapsed > 0 ) {
                    val nextYear = this.year + yearsElapsed

                    return MyDate(nextYear, nextMonth, nextDay)
                }

                return MyDate(this.year, nextMonth, nextDay)
            }

            return MyDate(this.year, this.month, nextDay)
        } else {
            //time interval is a year
            //fastforward by ri.n * 1 year
            return MyDate(this.year + repeatedInterval.n, this.month, this.dayOfMonth) //TODO handle leap year
        }

    }
}

class RepeatedTimeInterval(val ti: TimeInterval, val n: Int = 1)

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR,
    public fun times(multiplier: Int): RepeatedTimeInterval {
         return RepeatedTimeInterval(this, multiplier)
    }
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
