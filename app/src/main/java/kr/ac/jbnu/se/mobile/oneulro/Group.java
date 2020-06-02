package kr.ac.jbnu.se.mobile.oneulro;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Group {
    private String title;
    private String text;
    private int color;
    private Date makeDate;

    public Group(String title, String text, Date makeDate, int color) {
        this.title = title;
        this.text = text;
        this.makeDate=makeDate;
        this.color = color;

    }

    public String getTitle(){return title;}
    public String getText(){return text;}
    public int getColor(){return color;}
    public String getDate()
    {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("EE d MMM yyyy");
        String MySDate = newDateFormat.format(this.makeDate);
        return MySDate;
    }
    public String getTime()
    {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("HH:mm");
        String MySDate = newDateFormat.format(this.makeDate);
        return MySDate;
    }
    public String getMonth()
    {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd/MM");
        String MySDate = newDateFormat.format(this.makeDate);
        return MySDate;
    }
    public String getYear()
    {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy");
        String MySDate = newDateFormat.format(this.makeDate);
        return MySDate;
    }

    public void setTitle(String title){this.title = title;}
    public void setText(String text){this.text = text;}
    public void setColor(int color){this.color = color;}
}
