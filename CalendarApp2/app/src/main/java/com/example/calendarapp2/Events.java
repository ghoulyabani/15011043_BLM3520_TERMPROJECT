package com.example.calendarapp2;

public class Events {
    String name, detail, startTime, finishTime, remindTimer, repeat, date, month, year, adress;

    public Events(String name, String detail, String startTime, String finishTime, String remindTimer, String repeat, String date, String month, String year, String adress) {
        this.name = name;
        this.detail = detail;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.remindTimer = remindTimer;
        this.repeat = repeat;
        this.date = date;
        this.month = month;
        this.year = year;
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public String getRemindTimer() {
        return remindTimer;
    }

    public String getRepeat() {
        return repeat;
    }

    public String Date() {
        return date;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public String getAdress() {
        return adress;
    }
}
