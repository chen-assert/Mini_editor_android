package jingruichen.mini_editor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.LinkedList;

public class list_activity extends ListActivity {
    LinkedList<String> data = miniEditor.list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //1.数据源
        //2.适配器
        @SuppressWarnings("unchecked")
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
        //3.绑定
        setListAdapter(arrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Toast.makeText(list_activity.this,"点中了第"+id+"个,内容是"+ data[(int)id],Toast.LENGTH_SHORT).show();
        Intent intent =getIntent();
        intent.putExtra("path",data.get((int)id));
        setResult(0,intent);
        finish();
    }
}