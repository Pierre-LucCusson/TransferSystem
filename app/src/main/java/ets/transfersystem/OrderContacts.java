package ets.transfersystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Pierre-Luc on 2017-07-20.
 */

public class OrderContacts implements Comparable<Contact> {

    private Activity activity;
    private Context packageContext;
    private Class<?> className;
    private Contact[] contacts;
    private String orderBy;

    public OrderContacts(Activity activity, Context packageContext, Class<?> className, Contact[] contacts) {
        this.activity = activity;
        this.packageContext = packageContext;
        this.className = className;
        this.contacts = contacts;
        orderBy = activity.getIntent().getStringExtra("EXTRA_ORDER_BY");
        Log.d("ButtonClick", "ORDER BY " + orderBy);
    }

    public void setOrderButtons() {

        //Distance button
        final Button orderByDistanceButton = (Button) activity.findViewById(R.id.distanceButton);
        orderByDistanceButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                orderByDistance(view);
            }
        } );

        //Name button
        final Button orderByNameButton = (Button) activity.findViewById(R.id.nameButton);
        orderByNameButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                orderByName(view);
            }
        } );

        //Last Login button
        final Button orderByLastLoginButton = (Button) activity.findViewById(R.id.lastLoginButton);
        orderByLastLoginButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                orderByLastLogin(view);
            }
        } );
    }



    private void orderByDistance(View view) {
        Log.d("ButtonClick", "Distance button was click");
        startActivityAndOrderBy("distance");
    }

    private void orderByName(View view) {
        Log.d("ButtonClick", "Name button was click");
        startActivityAndOrderBy("name");
    }

    private void orderByLastLogin(View view) {
        Log.d("ButtonClick", "Last Login button was click");
        startActivityAndOrderBy("lastlogin");
    }

    private void startActivityAndOrderBy(String orderBy) {
        Intent intent = new Intent(packageContext, className);
        intent.putExtra("EXTRA_ORDER_BY", orderBy);
        intent.putExtra("EXTRA_CURRENT_FRIEND_ID", activity.getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_ID"));
        intent.putExtra("EXTRA_CURRENT_FRIEND_IP", activity.getIntent().getStringExtra("EXTRA_CURRENT_FRIEND_IP"));
        activity.startActivity(intent);
    }

    public String[] getContactsByOrderInJson() {

        if (orderBy != null) {
            if (orderBy.equals("distance")) {
                Arrays.sort(contacts, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        return Double.valueOf(c1.getDistance()).compareTo(Double.valueOf(c2.getDistance()));
                    }
                });
            } else if (orderBy.equals("name")) {
                Arrays.sort(contacts, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        return c1.getId().compareTo(c2.getId());
                    }
                });
            } else if (orderBy.equals("lastlogin")) {
                Arrays.sort(contacts, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        return Long.valueOf(c1.getLastLogin()).compareTo(Long.valueOf(c2.getLastLogin()));
                    }
                });
            }
        }

        String[] contactsInJson = new String[contacts.length];
        for (int i = 0; i < contacts.length; i++) {
            contactsInJson[i] = new Gson().toJson(contacts[i]);
        }
        return contactsInJson;
    }

    @Override
    public int compareTo(Contact compareContact) {
        return compareTo(compareContact);
    }
}
