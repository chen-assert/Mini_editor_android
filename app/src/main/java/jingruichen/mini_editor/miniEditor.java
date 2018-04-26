package jingruichen.mini_editor;

import java.io.*;
import java.lang.String;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.net.Uri;
import android.net.Uri.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
    private static final int REC_REQUESTCODE = 0;
    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/save/");
    public static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    private Button button;
    private EditText editText;
    private File file;
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


        verifyStoragePermissions(this);
        initView();

    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.save);
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);

        words = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //输入回车时触发事件
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    indentation(editText.getText().toString());
                }
                return false;
            }
        });

    }

    //auto-indentation
    public void indentation(String content) {
        SpannableString span = new SpannableString(content);
        if (content.length() > 1 && content.charAt(content.length() - 1) == '}') {
            System.out.println("acsdfvbsdf");
            span.setSpan(new LeadingMarginSpan.Standard(36, 36), content.lastIndexOf("{"), content.indexOf("}"), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            editText.setText(span);
            editText.setSelection(content.length());
        } else if (content.length() > 1 && content.charAt(content.length() - 1) == ';') {
            if (content.contains("{")) {
                span.setSpan(new LeadingMarginSpan.Standard(36, 36), content.lastIndexOf("{"), content.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                editText.setText(span);
                editText.setSelection(content.length());
            }
        }
    }


    /**
     * Called when the user taps the OK button
     */
    public boolean writeTxtToFile(String strcontent,String filename) {

        try {
            if (!path.exists()) {
                path.mkdirs();
            }


            file = new File(path.getAbsolutePath(),filename);
            System.out.println(path.getAbsolutePath());
            if (!file.exists()) {
                file.createNewFile();
                return true;
            }
            else{
                Toast.makeText(miniEditor.this, "file already exist...", Toast.LENGTH_SHORT).show();
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(strcontent.getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter file name");
        builder.setIcon(R.drawable.ic_favorite_black_48dp);
        ConstraintLayout constraint = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialogs_savingfiles, null);
        builder.setView(constraint);
        final EditText edit = constraint.findViewById(R.id.name);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (writeTxtToFile(editText.getText().toString(),edit.getText().toString())) {
                    Toast.makeText(miniEditor.this, String.format("file saved in %s", path.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                }
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

            case R.id.action_settings:
                setColors(builder);
                break;

            case R.id.action_discard:
                Toast.makeText(miniEditor.this, "File deleted...", Toast.LENGTH_SHORT).show();
                deleteFile(file);
                Toast.makeText(miniEditor.this, "File deleted...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_browse:
                //show all saved files
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //now only can read .txt file
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REC_REQUESTCODE);
                break;

            case R.id.action_search_replace:
                find_and_replace(builder);
                break;


        }
        return super.onOptionsItemSelected(item);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "uri:" + data.getData().getPath(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(miniEditor.this, "Starting read file", Toast.LENGTH_SHORT).show();
        showInfo(data);
    }

    private void showInfo(Intent data) {
        //Toast.makeText(miniEditor.this, "Starting read file stage 2", Toast.LENGTH_SHORT).show();
        String str = null;
        try {
            InputStream is = getContentResolver().openInputStream(data.getData());
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            while ((str = reader.readLine()) != null) {
                editText.append(str);
                editText.append("\n");
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("1", Log.getStackTraceString(e));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
                Log.d("old&cur", String.format("%s:%s", old, cur));
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


    private void replace(String old, String cur) {
        if (words == null) {
            System.out.println("null");
            return;
        }

        String text = editText.getText().toString();
        text = text.replaceAll(old, cur);
        editText.setText(text);
    }



    //将SD卡文件删除
    public static void deleteFile(File file) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                }

                else if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
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
