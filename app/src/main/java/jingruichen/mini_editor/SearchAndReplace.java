package jingruichen.mini_editor;

import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by yangzixuan on 2018/5/4.
 */

public class SearchAndReplace {
    protected static void find_and_replace(ConstraintLayout constraint,AlertDialog.Builder builder) {
        builder.setTitle("Search and Replace");
        builder.setIcon(R.drawable.options);


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
            }
        });

        builder.setCancelable(true);
        AlertDialog d2 = builder.create();
        d2.show();
    }
    protected static void replace(String old, String cur) {
        if (miniEditor.words == null) {
            System.out.println("null");
            return;
        }

        String text = miniEditor.editText.getText().toString();
        text = text.replaceAll(old, cur);
        miniEditor.editText.setText(text);
        System.out.println(miniEditor.editText.getText().toString());
        miniEditor.editText.setSelection(text.length());
    }
}
