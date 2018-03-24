package jingruichen.mini_editor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.lang.String;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;

public class miniEditor extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    private Button button;
    private EditText editText;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    //需要为SD卡的写入申请动态权限
    public static void verifyStoragePermissions(AppCompatActivity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("miniEditor");
        setSupportActionBar(myToolbar);
        myToolbar.setLogo(R.drawable.myico);


        verifyStoragePermissions(this);
        initView();

    }

    private void initView(){
        editText = (EditText)findViewById(R.id.editText);
        button = (Button)findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeTxtToFile(editText.getText().toString().trim());
                Toast.makeText(miniEditor.this,"file saved",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Called when the user taps the save button */
    public void writeTxtToFile(String strcontent) {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/save/");

        try {
            if(!path.exists()) {
                path.mkdirs();
            }
            File file = new File(path.getAbsolutePath(),"save.txt");
            if(!file.createNewFile()) {
                Toast.makeText(miniEditor.this,"file already exist...",Toast.LENGTH_SHORT).show();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strcontent.getBytes());
            raf.close();

        }
        catch (Exception e) {
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
