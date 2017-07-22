package ets.transfersystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Pierre-Luc on 2017-07-20.
 */

public class OrderContacts {

    private Activity activity;
    private Context packageContext;
    private Class<?> className;
    private Contact[] contacts;
    private String orderBy;

    public OrderContacts(Activity activity, Context packageContext, Class<?> className, Contact[] contacts, String orderBy) {
        Log.d("ButtonClick", "ORDER BY " + orderBy);
        this.activity = activity;
        this.packageContext = packageContext;
        this.className = className;
        this.contacts = contacts;
        this.orderBy = orderBy;
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
}
