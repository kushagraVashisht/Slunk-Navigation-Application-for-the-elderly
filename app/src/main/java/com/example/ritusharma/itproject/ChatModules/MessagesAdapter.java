package com.example.ritusharma.itproject.ChatModules;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ritusharma.itproject.Auth_SignIn.UserObj;
import com.example.ritusharma.itproject.R;

import java.util.ArrayList;

public class MessagesAdapter<T> extends ArrayAdapter {

    private UserObj sender;
    private UserObj recvr;

    /**********************************************************************************************/

    /**
     * Constructor for Messages Adapter
     * 
     * @param context  - The Application Context
     * @param messages - The list of messages
     */
    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.item_message_received, messages);
    }

    /*********************************************************************************************/

    /**
     * Constructor for messages adapter
     * 
     * @param context  - The Application Context
     * @param messages - The list of messages in the adaptor
     * @param sender   - The Sender's Object
     * @param recvr    - The Receiver's Object
     */
    public MessagesAdapter(Context context, ArrayList<Message> messages, UserObj sender, UserObj recvr) {
        super(context, R.layout.item_message_received, messages);
        this.sender = sender;
        this.recvr = recvr;
    }

    /**********************************************************************************************/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Message message = (Message) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message_received, parent, false);
        }

        if (message.getSenderID().equals(sender.getUID())) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);

            // Get references to the views of item_message_received.xmltem.xml
            TextView messageText, messageUser, messageTime;
            messageText = (TextView) convertView.findViewById(R.id.sent_message_text);
            // messageUser = convertView.findViewById(R.id.received_message_user);
            messageTime = convertView.findViewById(R.id.sent_message_time);

            messageText.setText(message.getMessage());
            String name = sender.getFname();
            // messageUser.setText(name);
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimeSent()));

        } else {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent,
                    false);

            // Get references to the views of item_message_received.xmltem.xml
            TextView messageText, messageUser, messageTime;
            messageText = (TextView) convertView.findViewById(R.id.received_message_text);
            // messageUser = convertView.findViewById(R.id.received_message_user);
            messageTime = convertView.findViewById(R.id.received_message_time);

            messageText.setText(message.getMessage());
            String name = recvr.getFname();
            // messageUser.setText(name);
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getTimeSent()));

        }
        return convertView;
    }
}

/**********************************************************************************************/
