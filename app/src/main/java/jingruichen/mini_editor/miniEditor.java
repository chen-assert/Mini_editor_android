package jingruichen.mini_editor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import java.util.HashMap;
import java.util.Map;
import android.net.Uri;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.Canvas;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static jingruichen.mini_editor.presistenceSave.getBean;

public class miniEditor extends AppCompatActivity {
    protected static final int FILE_REQUESTCODE = 0;
    protected static final int LIST_REQUESTCODE = 1;
    File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/save/");
    protected static final String EXTRA_MESSAGE = "jingruichen.mini_editor.Message";
    protected Button button;
    protected static MyEditText editText;
    protected File file;
    protected static LinkedList<String> list;
    protected static final List<String> words = new ArrayList();
    protected static final int REQUEST_EXTERNAL_STORAGE = 1;
    protected static Intent intent;
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


        list = (LinkedList<String>) getBean(this, "123");

        verifyStoragePermissions(this);

        initView();


    }


    //close listener when change text by code
    protected void initView() {
        editText = findViewById(R.id.editText);

        editText.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                jingruichen.mini_editor.Indent.check(s, start, before, count);
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
            Toast.makeText(miniEditor.this, "file saved in" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
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
            case R.id.action_save:
                showDialog();
                break;
            case R.id.action_favorite: {
                if (file == null) {
                    Toast.makeText(miniEditor.this, "You need to save file first", Toast.LENGTH_SHORT).show();
                } else {
                    if (list == null) list = new LinkedList<>();
                    list.addLast(Uri.fromFile(file).toString());
                }
                if (list instanceof Serializable) {
                    presistenceSave.putBean(this, "123", list);
                }
                break;
            }
            case R.id.action_settings:
                settings(builder);
                break;

            case R.id.action_search_replace:
                ConstraintLayout constraint = (ConstraintLayout) getLayoutInflater().inflate(R.layout.dialogs, null);
                SearchAndReplace.find_and_replace(constraint, builder);
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
            case R.id.action_browse:
                //show all saved files
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, FILE_REQUESTCODE);
                break;
            case R.id.action_list:
                if (list == null) list = new LinkedList<>();
                Intent intent2 = new Intent(this, list_activity.class);
                startActivityForResult(intent2, LIST_REQUESTCODE);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    //used for callback to display text
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            Toast.makeText(miniEditor.this, "Not select file!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == FILE_REQUESTCODE) {
            Toast.makeText(this, "uri:" + data.getData().getPath(), Toast.LENGTH_SHORT).show();
            showText(data.getData());
        } else if (requestCode == LIST_REQUESTCODE) {
            Toast.makeText(this, "uri:" + data.getExtras().get("path"), Toast.LENGTH_SHORT).show();
            showText(Uri.parse((String)data.getExtras().get("path")));
        }
    }

    protected void showText(Uri uri) {
        String str = null;
        try {
            InputStream is = this.getContentResolver().openInputStream(uri);
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
                final KeywordHighlighting kwh = new KeywordHighlighting();
                kwh.Highlight();
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (start < s.length() && s.charAt(start) == '\n') kwh.Highlight();
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
                        if (start < s.length() && s.charAt(start) == '\n') {
                            SpannableString span2 = new SpannableString(s);
                            span2.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            editText.setText(span2);
                            editText.setSelection(editText.getText().toString().length());
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
                        if (start < s.length() && s.charAt(start) == '\n') {
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
