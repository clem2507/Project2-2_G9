package domains.Calendar;

public class Date {
    int year;
    int month;
    int day;

    /**
     * Constructor to create a date class
     * @param year
     * @param month
     * @param day
     */
    public Date(int year, int month, int day)
    {
        this.year=year;
        this.month = month;
        this.day = day;
    }

    /**
     * Return years
      * @return
     */
    public int getYear() {
        return year;
    }

    /**
     * Return month
     * @return
     */
    public int getMonth() {
        return month;
    }

    /**
     * Return day
     * @return
     */
    public int getDay() {
        return day;
    }

    /**
     * Return a string of date
     * @return
     */
    @Override
    public String toString() {
        return year +
                "/" + month +
                "/" + day;
    }
}
