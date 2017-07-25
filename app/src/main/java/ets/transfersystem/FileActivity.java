package ets.transfersystem;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.io.Files;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import static ets.transfersystem.FolderObserver.getFilesList;

public class FileActivity extends AppCompatActivity {


    private String TAG = "FileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        String[] filesInJson = getFilesList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_list, filesInJson);

        ListView list = (ListView) findViewById(R.id.file_list);
        list.setAdapter(adapter);
    }


}
