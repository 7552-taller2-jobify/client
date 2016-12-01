package ar.fi.uba.jobify.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ar.fi.uba.jobify.adapters.ChatAdapter;
import ar.fi.uba.jobify.domains.ChatMessage;
import fi.uba.ar.jobify.R;

public class ChatActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private FloatingActionButton sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public String myMail;
    public String friendMail;
    String chatNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chats");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        friendMail = "pedro@gmail.com";
        myMail = "facundosg@hotmail.com";
        //friendMail = "facundosg@hotmail.com";
        //myMail = "pedro@gmail.com";
        if (myMail.compareToIgnoreCase(friendMail) < 0){
            chatNode = myMail.replace('.','_') + "-" + friendMail.replace('.','_');
        }else {
            chatNode = friendMail.replace('.','_') + "-" + myMail.replace('.','_');
        }
        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (FloatingActionButton) findViewById(R.id.chatSendButton);

        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        companionLabel.setText(friendMail);

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String date = sdf.format( new Date());
                final ChatMessage chatMessage = new ChatMessage(date + myMail.replace('.','_'),true,messageText,myMail.replace('.','_'),date);

                if(myRef.child(chatNode) == null){
                    Map<String, String> chat = new HashMap<String, String>();
                    chat.put(chatNode, "");
                    myRef.setValue(chat);
                };

                myRef.child(chatNode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String key = myRef.child(chatNode).push().getKey();
                        Map<String, Object> chatMessageValues = chatMessage.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put( key, chatMessageValues);

                        myRef.child(chatNode).updateChildren(childUpdates);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                messageET.setText("");
                //displayMessage(chatMessage);
            }
        });
        ChildEventListener childEventListener = new ChildEventListener(){

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);

                if (chatMessage.userId.compareToIgnoreCase(myMail.replace('.','_')) == 0){
                    chatMessage.isMe = true;
                }else {
                    chatMessage.isMe = false;
                }
                displayMessage(chatMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.child(chatNode).addChildEventListener(childEventListener);
    }


    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }


}