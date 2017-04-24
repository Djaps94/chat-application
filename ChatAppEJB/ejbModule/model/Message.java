package model;

import java.util.Date;

public class Message {
    
    private User from;
    private User to;
    private Date date;
    private String subject;
    private String content;
    
    public Message() { }
    
    public Message(User from, User to, Date date, String subject, String content){
        this.from    = from;
        this.to      = to;
        this.date    = date;
        this.subject = subject;
        this.content = content;
    }
    
    public User getFrom() {
        return from;
    }
    public void setFrom(User from) {
        this.from = from;
    }
    public User getTo() {
        return to;
    }
    public void setTo(User to) {
        this.to = to;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    

}
