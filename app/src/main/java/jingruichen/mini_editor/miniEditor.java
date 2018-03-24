package jingruichen.mini_editor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.lang.String;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.util.Log;

public class miniEditor extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    String filePath = Environment.getExternalStorageDirectory()+"/save";
    String fileName = "save.txt";
    private Button button;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("miniEditor");
        setSupportActionBar(myToolbar);
        myToolbar.setLogo(R.drawable.myico);


        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);

        initView();

    }

    private void initView(){
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeTxtToFile(editText.getText().toString().trim(),filePath,fileName);
            }
        });
    }

    /** Called when the user taps the save button */
    public void writeTxtToFile(String strcontent,String filePath,String fileName) {
        makeFilePath(filePath,fileName);
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_favorite:
                Toast.makeText(miniEditor.this,"Added to favorite",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                //something to be added...
                break;
            case R.id.action_discard:
                Toast.makeText(miniEditor.this,"File deleted...",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }
}
