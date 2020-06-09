package kr.ac.jbnu.se.mobile.oneulro;

import java.util.Date;

public class Item {
    private String id;
    private String text;
    private String code;
    private String status;
   // private String group;


    public Item() {
        this.id = "";
        this.text = "";
        this.code = "";
        this.status = "";

    }

    public Item(String id, String text, String code, String status){
        this.id = id;
        this.text = text;
        this.code = code;
        this.status = status;
       // this.group = group;
    }

    public String getId(){return id;}
    public String getText(){return text;}
    public String getCode(){return code;}
    public String getStatus(){return status;}
  //  public String getGroup(){return group;}

    public void setId(String id){this.id = id;}
    public void setText(String text){this.text = text;}
    public void setCode(String code){this.code = code;}
    public void setStatus(String status){this.status = status;}
   // public void setGroup(String group){this.group = group;}

}
