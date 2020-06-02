package kr.ac.jbnu.se.mobile.oneulro;

public class Item {
    private String text;
    private Status status;
    private String group;

    public enum Status {TODO, DONE}

    public Item(String text, Status status, String group) {
        this.text = text;
        this.status = status;
        this.group = group;
    }

    public String getText(){return text;}
    public Status getStatus(){return status;}
    public String getGroup(){return group;}

    public void setText(String text){this.text = text;}
    public void setStatus(Status status){this.status = status;}
    public void setGroup(String group){this.group = group;}

}
