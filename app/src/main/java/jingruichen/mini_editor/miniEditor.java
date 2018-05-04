package jingruichen.mini_editor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.reflect.Method;

public class miniEditor extends AppCompatActivity {
    protected static final int REC_REQUESTCODE = 0;
    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/save/");
    protected static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    protected Button button;
    protected static MyEditText editText;
    protected File file;
    protected static final List<String> words = new ArrayList<>();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("miniEditor");
        myToolbar.setTitleTextColor(0xFF8b4513);
        myToolbar.setBackgroundColor(0xFFd2b48c);
        setSupportActionBar(myToolbar);


        verifyStoragePermissions(this);

        initView();

        Button buttoni = findViewById(R.id.index);
        buttoni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(miniEditor.this, IndexActivity.class);
                //startActivity(intent);
            }
        });

    }



    //close listener when change text by code
    protected void initView() {
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.save);
        button.setBackgroundColor(0xFFFFF5EE);
        button.setTextColor(0xFFA52A2A);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


        editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                jingruichen.mini_editor.indent.check(s,start,before,count);
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
                settings(builder);
                break;

            case R.id.action_browse:
                //show all saved files
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                this.startActivityForResult(intent, REC_REQUESTCODE);
                break;

            case R.id.action_search_replace:
                ConstraintLayout constraint = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialogs, null);
                SearchAndReplace.find_and_replace(constraint,builder);
                break;

            case R.id.action_email:
                //send the files by email
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"voltage111@sina.com"});
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SUBJECT");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "Sending file from miniEditor...");
                intent.setType("text/html");
                startActivity(Intent.createChooser(intent, "Please choose your email client"));
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

    //general settings
    protected void settings(AlertDialog.Builder builder) {
        builder.setTitle("Setting options");
        builder.setIcon(R.drawable.ic_settings_black_24dp);
        final AlertDialog.Builder color_dialog = new AlertDialog.Builder(this);
        final AlertDialog.Builder highlight_dialog = new AlertDialog.Builder(this);

        builder.setNeutralButton("Color", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setColors(color_dialog);
            }
        });


        builder.setNegativeButton("Others", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                 setHighlight_status(highlight_dialog);
            }
        });

        builder.setCancelable(true);
        AlertDialog d = builder.create();
        d.show();
    }

    protected void setHighlight_status(AlertDialog.Builder builder) {
        builder.setTitle("Turn on KeywordHighlighting?");
        builder.setIcon(R.drawable.options);
        builder.setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Scanner s = new Scanner(editText.getText().toString());
                final keywordHighlighting kwh = new keywordHighlighting();
                while (s.hasNext()) {
                    String w = s.next();
                    System.out.println("w:" + w);
                    words.add(w);
                }
                kwh.Highlight(words);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.charAt(start) == '\n') kwh.Highlight(words);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }

        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                return;
            }
        });
        builder.setCancelable(true);
        AlertDialog d = builder.create();
        editText.setSelection(editText.getText().length());
        d.show();
    }


    protected void setColors(AlertDialog.Builder builder) {
        builder.setTitle("Select text color");
        builder.setIcon(R.drawable.color);

        //set color red
        builder.setNeutralButton("red", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String text = editText.getText().toString().trim();
                final SpannableString span = new SpannableString(text);
                span.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                editText.setText(span);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(start < s.length() && s.charAt(start) == '\n'){
                            span.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            editText.setText(span);
                            editText.setSelection(text.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                editText.setSelection(text.length());
            }
        });


        //set default color
        builder.setNegativeButton("default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                final String text = editText.getText().toString().trim();
                final SpannableString span = new SpannableString(text);
                span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                editText.setText(span);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(start < s.length() && s.charAt(start) == '\n'){
                            span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            editText.setText(span);
                            editText.setSelection(text.length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                editText.setSelection(text.length());
            }
        });

        builder.setCancelable(true);
        AlertDialog d = builder.create();
        editText.setSelection(editText.getText().length());
        d.show();
    }




    @Override
    //show icons in the menu
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }
}
