package ecnu.uleda.model;

/**
 * Created by VinnyHu on 2017/3/13.
 */

public class Userorder {


    private String Time;
    private String Publisher;
    private String Picker;
    private int Money;
    private String Task;
    private String Time_last;

    public String getTime() {
        return Time;
    }

    public Userorder setTime(String time) {
        Time = time;
        return this;
    }

    public String getTime_last() {
        return Time_last;
    }

    public Userorder setTime_last(String time_last) {
        Time_last = time_last;
        return this;
    }

    public String getTask() {
        return Task;
    }

    public Userorder setTask(String task) {
        Task = task;
        return this;
    }

    public int getMoney() {
        return Money;
    }

    public Userorder setMoney(int money) {
        Money = money;
        return this;
    }

    public String getPicker() {
        return Picker;
    }

    public Userorder setPicker(String picker) {
        Picker = picker;
        return this;
    }

    public String getPublisher() {
        return Publisher;
    }

    public Userorder setPublisher(String publisher) {
        Publisher = publisher;
        return this;
    }



}
