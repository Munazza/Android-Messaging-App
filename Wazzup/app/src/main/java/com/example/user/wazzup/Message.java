package com.example.user.wazzup;

/**
 * Created by User on 14/07/2017.
 */

public class Message
{
    private String sender;
    private String content;

    public Message(String sender, String content){
        this.sender = sender;
        this.content = content;
    }

    public String getSender(){
        return sender;
    }
    public String getContent(){
        return content;
    }
}
