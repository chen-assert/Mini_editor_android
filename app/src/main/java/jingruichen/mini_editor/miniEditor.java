package jingruichen.mini_editor;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.lang.String;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.util.Log;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.constraint.ConstraintLayout;

public class miniEditor extends AppCompatActivity {
    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/save/");
    public static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    private Button button;
    private EditText editText;
    private File file;
    private String filename;
    private List<String> List_of_file;
    private List<String> words;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    //需要为SD卡的写入申请动态权限
    public static void verifyStoragePermissions(AppCompatActivity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
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
        myToolbar.setTitleTextColor(Color.MAGENTA);
        myToolbar.setBackgroundColor(Color.CYAN);
        setSupportActionBar(myToolbar);
        //myToolbar.setLogo(R.drawable.snowflake);


        verifyStoragePermissions(this);
        initView();

    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.save);
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);

        words = new ArrayList<String>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
                writeTxtToFile(editText.getText().toString());
                Toast.makeText(miniEditor.this, String.format("file saved in %s", path.getAbsolutePath()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Called when the user taps the save button
     */
    public void writeTxtToFile(String strcontent) {
        List_of_file = new ArrayList<String>();

        try {
            if (!path.exists()) {
                path.mkdirs();
            }

            Scanner s = new Scanner(editText.getText().toString().trim());
            String word;

            while(s.hasNext()){
                word = s.next();
                System.out.println(word);
                words.add(word);
            }



            file = new File(path.getAbsolutePath(),filename);
            System.out.println(path.getAbsolutePath());
            if(!file.createNewFile()) {
                Toast.makeText(miniEditor.this,"file already exist...",Toast.LENGTH_SHORT).show();
                return;
            }
            List_of_file.add(filename);
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strcontent.getBytes());
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter file name");
        builder.setIcon(R.drawable.ic_favorite_black_48dp);
        final EditText edit = new EditText(this);
        builder.setView(edit);
        filename = edit.getText().toString().trim();
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(miniEditor.this, "you cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true);
        AlertDialog d = builder.create();
        d.show();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case R.id.action_favorite:
                Toast.makeText(miniEditor.this, "Added to favorite", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_settings:
                setColors(builder);
                break;

            case R.id.action_discard:
                Toast.makeText(miniEditor.this, "File deleted...", Toast.LENGTH_SHORT).show();
                deleteFile(file);
                Toast.makeText(miniEditor.this,"File deleted...",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_browse:
                //show all saved files
                display(builder);

                break;

            case R.id.action_search_replace:
                find_and_replace(builder);
                break;


        }
        return super.onOptionsItemSelected(item);

    }

    private void setColors(AlertDialog.Builder builder) {
        builder.setTitle("Select text color");
        builder.setIcon(R.drawable.color);

        //set color red
        builder.setNeutralButton("red", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = editText.getText().toString().trim();
                SpannableString span = new SpannableString(text);
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                editText.setText(span);
            }
        });


        //set default color
        builder.setNegativeButton("default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                String text = editText.getText().toString().trim();
                SpannableString span = new SpannableString(text);
                span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                editText.setText(span);
            }
        });
        builder.setCancelable(true);
        AlertDialog d = builder.create();
        d.show();
    }

    private void find_and_replace(AlertDialog.Builder builder) {
        builder.setTitle("Search and Replace");
        builder.setIcon(R.drawable.options);

        ConstraintLayout constraint = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialogs, null);
        builder.setView(constraint);
        final EditText edit1 = constraint.findViewById(R.id.old);
        final EditText edit2 = constraint.findViewById(R.id.current);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String old = edit1.getText().toString();
                final String cur = edit2.getText().toString();
                Log.d("old&cur", String.format("%s:%s",old,cur));
                replace(old, cur);
            }
        });


        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(miniEditor.this, "you cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setCancelable(true);
        AlertDialog d2 = builder.create();
        d2.show();
    }


    private void replace(String old,String cur){
        if(words == null){
            System.out.println("null");
            return;
        }

        String text = editText.getText().toString();
        text = text.replaceAll(old,cur);
        editText.setText(text);
    }



    private void display(AlertDialog.Builder builder){
        builder.setTitle("All files");
        if(List_of_file == null){
            Toast.makeText(miniEditor.this,"No files",Toast.LENGTH_SHORT).show();
        }

        builder.setNegativeButton("cancel", null);

        builder.setCancelable(true);
        AlertDialog d2 = builder.create();
        d2.show();
    }

    //将SD卡文件删除
    public static void  deleteFile(File file) {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }
                // 如果它是一个目录
                else if (file.isDirectory()) {
                    // 声明目录下所有的文件 files[];
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 为ActionBar扩展菜单项
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }
}
