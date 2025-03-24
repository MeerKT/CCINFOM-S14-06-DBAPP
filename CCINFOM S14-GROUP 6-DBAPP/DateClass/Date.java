package DateClass;

public class Date {
    private int day;
    private int month;
    private int year;
    
    public Date(int day, int month, int year) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid date provided.");
        } 

        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("Invalid date provided.");
        } 
        
        this.day = day;
        this.month = month;
        this.year = year;

        }
    
    public int getDay() {
        return day;
    }
    
    public int getMonth() {
        return month;
    }
    
    public int getYear() {
        return year;
    }
    
}
