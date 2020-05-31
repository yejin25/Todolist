package kr.ac.jbnu.se.mobile.oneulro;

public class Group {
    private String title;
    private String text;
    private int color;

    public Group(String title, String text) {
        this.title = title;
        this.text = text;
        //this.color = color;

    }

    public String getTitle(){return title;}
    public String getText(){return text;}
    public int getColor(){return color;}

    public void setTitle(String title){this.title = title;}
    public void setText(String text){this.text = text;}
    public void setColor(int color){this.color = color;}
}
