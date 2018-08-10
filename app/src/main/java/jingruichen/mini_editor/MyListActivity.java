package jingruichen.mini_editor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.LinkedList;

public class MyListActivity extends ListActivity {
    LinkedList<String> data;
    boolean delete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.data source
        data = miniEditor.list;
        //2.adapter
        @SuppressWarnings("unchecked")
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        //3.bind
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(delete == false) {
            Intent intent = getIntent();
            intent.putExtra("path", data.get((int) id));
            setResult(0, intent);
        }
        else{
            Toast.makeText(this,"file "+data.get((int)id)+" is deleted",Toast.LENGTH_SHORT).show();
            data.remove((int)id);
        }
        finish();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_actions, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:{
                delete = true;
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, data);
                setListAdapter(arrayAdapter);
                if (data instanceof Serializable) {
                    PresistenceSave.putBean(this, "123", data);
                }
                break;
            }
            case R.id.action_clear:{
                data.clear();
                if (data instanceof Serializable) {
                    PresistenceSave.putBean(this, "123", data);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
                setListAdapter(arrayAdapter);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}