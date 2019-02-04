package com.example.ritusharma.itproject.Auth_SignIn;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ritusharma.itproject.Auth_SignIn.UserObj;
import com.example.ritusharma.itproject.R;
import com.github.library.bubbleview.BubbleTextView;

import java.util.ArrayList;

public class UserObjAdapter<T> extends ArrayAdapter {

    /**
     * Constructor for Messages Adapter
     * 
     * @param context
     * @param users
     */
    public UserObjAdapter(Context context, ArrayList<UserObj> users) {
        super(context, R.layout.layout_list_user, users);
    }

    /*********************************************************************************************/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        UserObj user = (UserObj) getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_list_user, parent, false);
        }

        // Get references to the views of layout_list_user.xmltem.xml
        TextView userName;
        userName = convertView.findViewById(R.id.user_name);

        userName.setText(user.getFname() + " " + user.getLname());

        return convertView;
    }
}

/*********************************************************************************************/
