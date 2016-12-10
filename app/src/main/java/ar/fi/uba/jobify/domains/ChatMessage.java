package ar.fi.uba.jobify.domains;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Shadow on 20/11/2016.
 */
@IgnoreExtraProperties
public class ChatMessage {
    public String id;
    public boolean isMe;
    public String message;
    public String userId;
    public String dateTime;
    public ChatMessage(){

    }
    public ChatMessage(String id, boolean isMe, String message, String userId, String dateTime){
        this.id = id;
        this.isMe = isMe;
        this.message = message;
        this.userId = userId;
        this.dateTime = dateTime;

    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("isMe", isMe);
        result.put("message", message);
        result.put("userId", userId);
        result.put("dateTime", dateTime);

        return result;
    }
}
