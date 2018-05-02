package jingruichen.mini_editor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileInputStream;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.InputStreamReader;

public class IndexActivity extends AppCompatActivity {

    private List<Index> indexList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        String title = getIntent().getStringExtra("title");

        intiTitle();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.index_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        TitleAdapter adapter = new TitleAdapter(indexList);
        recyclerView.setAdapter(adapter);
    }

    public static void actionStart(Context context, String title){
        Intent intent = new Intent(context, IndexActivity.class);
        intent.putExtra("title", title);
    }





    private void intiTitle(){
        //input title from SD card -> (set click listener in titleAdapter ?)


        Index in = new Index("the rest of index is empty");
        indexList.add(in);
    }



}
