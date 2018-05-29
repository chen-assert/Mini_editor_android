package jingruichen.mini_editor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
import java.util.LinkedList;

public class MyListActivity extends android.app.ListActivity {
    LinkedList<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.数据源
        data = miniEditor.list;
        //2.适配器
        @SuppressWarnings("unchecked")
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        //3.绑定
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Toast.makeText(MyListActivity.this,"点中了第"+id+"个,内容是"+ data[(int)id],Toast.LENGTH_SHORT).show();
        Intent intent =getIntent();
        intent.putExtra("path",data.get((int)id));
        setResult(0,intent);
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