package com.example.tomer.baslo.firebasechat;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Tomer on 13/11/2017.
 */

@IgnoreExtraProperties
public class Chat {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public Chat() {
    }

    public Chat(String sender, String receiver, String senderUid, String receiverUid, String message, long timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }
}
