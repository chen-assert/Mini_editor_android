package jingruichen.mini_editor;

import java.io.*;
import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class miniEditor extends AppCompatActivity {
    protected static final int REC_REQUESTCODE = 0;
    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/save/");
    protected static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    protected Button button;
    protected static MyEditText editText;
    protected File file;
    protected List<String> words = new ArrayList<>();
    protected static final int REQUEST_EXTERNAL_STORAGE = 1;
    protected static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    public static void verifyStoragePermissions(AppCompatActivity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception editText) {
            editText.printStackTrace();
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
    private int textChangeFlag=0;
    //close listener when change text by code
    protected void initView() {
        editText = findViewById(R.id.editText);
        button = (Button) findViewById(R.id.save);
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            int indent = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(textChangeFlag==1)return;
                textChangeFlag=1;
                //Toast.makeText(miniEditor.this, String.format("%d %d %d",start,before,count), Toast.LENGTH_SHORT).show();
                if (count == 1) {
                    //Toast.makeText(miniEditor.this, String.format("%c",s.charAt(start)), Toast.LENGTH_SHORT).show();
                    char in = s.charAt(start);
                    if (in == '}') {
                        //delete two '\t'(if possible)
                        if (editText.getText().subSequence(Math.max(editText.length() - 5, 0), editText.length()).toString().equals("    }")) {
                            int select=editText.getSelectionStart();
                            editText.setText((editText.getText().delete(editText.length() - 5, editText.length())).append('}'));
                            editText.setSelection(select-4);
                        }
                    }
                    if (in == '\n') {
                        indent=getIndent(editText);
                        indentation(editText, indent);
                    }
                }
                textChangeFlag=0;
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    protected int getIndent(MyEditText editText) {
        int indent=0;
        Editable text = editText.getText();
        //Toast.makeText(miniEditor.this, String.format("%c",editText.getText().charAt(editText.getSelectionStart()-1)), Toast.LENGTH_SHORT).show();
        for(int i=0;i<editText.getSelectionStart();i++){
            if(text.charAt(i)=='{')indent++;
            if(text.charAt(i)=='}')indent--;
            if(indent<0)indent=0;
        }
        return indent;
    }

    public void indentation(MyEditText editText, int indent) {
        String newContent = editText.getText().toString().substring(0,editText.getSelectionStart());
        for (int i = 0; i < indent; i++) {
            newContent = newContent + "    ";
        }
        newContent=newContent+editText.getText().toString().substring(editText.getSelectionStart());
        int select=editText.getSelectionStart();
        editText.setText(newContent);
        editText.setSelection(select+indent*4);
    }


    /**
     * Called when the user taps the OK button
     */
    public boolean writeTxtToFile(String strcontent, String filename) {

        try {
            if (!path.exists()) {
                path.mkdirs();
            }


            file = new File(path.getAbsolutePath(), filename);
            System.out.println(path.getAbsolutePath());
            if (!file.exists()) {
                file.createNewFile();
                return true;
            } else {
                Toast.makeText(miniEditor.this, "file already exist...", Toast.LENGTH_SHORT).show();
            }
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(strcontent.getBytes());
            fos.close();

        } catch (Exception editText) {
            editText.printStackTrace();
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
                Scanner s = new Scanner(editText.getText().toString());
                keywordHighlighting kwh = new keywordHighlighting();
                while (s.hasNext()) {
                    String w = s.next();
                    System.out.println("w:" + w);
                    words.add(w);
                }
                if (edit.getText().toString().endsWith(".c")) kwh.Highlight(words);
                if (writeTxtToFile(editText.getText().toString(), edit.getText().toString())) {
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
                //now only can read .txt file?
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                this.startActivityForResult(intent, REC_REQUESTCODE);
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

    protected void showInfo(Intent data) {
        String str = null;
        try {
            InputStream is = this.getContentResolver().openInputStream(data.getData());
            InputStreamReader input = new InputStreamReader(is, "UTF-8");
            BufferedReader reader = new BufferedReader(input);
            editText.setText("");
            int flag = 0;
            while ((str = reader.readLine()) != null) {
                if (flag != 0) editText.append("\n");
                editText.append(str);
                flag = 1;
            }
        } catch (FileNotFoundException editText) {
            Log.e("1", Log.getStackTraceString(editText));
        } catch (IOException editText) {
            Log.e("1", Log.getStackTraceString(editText));
        }
    }

    protected void setColors(AlertDialog.Builder builder) {
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
        editText.setSelection(editText.getText().length());
        d.show();
    }

    protected void find_and_replace(AlertDialog.Builder builder) {
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


    protected void replace(String old, String cur) {
        if (words == null) {
            System.out.println("null");
            return;
        }

        String text = editText.getText().toString();
        text = text.replaceAll(old, cur);
        editText.setText(text);
        System.out.println(editText.getText().toString());
        editText.setSelection(text.length());
    }


    //将SD卡文件删除
    public static void deleteFile(File file) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
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
