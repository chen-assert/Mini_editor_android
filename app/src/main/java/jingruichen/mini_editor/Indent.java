package jingruichen.mini_editor;

import android.text.Editable;

import static jingruichen.mini_editor.miniEditor.editText;

public class Indent {
    private static int textChangeFlag = 0;
    private static int indent = 0;       //number of space in the front of  each line
    protected static void getIndent(MyEditText editText) {
        indent = 0;
        Editable text = editText.getText();
        //Toast.makeText(miniEditor.this, String.format("%c",editText.getText().charAt(editText.getSelectionStart()-1)), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < editText.getSelectionStart(); i++) {
            if (text.charAt(i) == '{') indent++;
            if (text.charAt(i) == '}') indent--;
            if (indent < 0) indent = 0;
        }
        return;
    }

    //change the current text to correct indentation
    protected static void indentation(MyEditText editText) {
        String newContent = editText.getText().toString().substring(0, editText.getSelectionStart());
        for (int i = 0; i < indent; i++) {
            newContent = newContent + "    ";
        }
        newContent = newContent + editText.getText().toString().substring(editText.getSelectionStart());
        int select = editText.getSelectionStart();
        editText.setText(newContent);
        editText.setSelection(select + indent * 4);
    }
    protected static void check(CharSequence s, int start, int before, int count){
        if (textChangeFlag == 1) return;
        textChangeFlag = 1;
        //Toast.makeText(miniEditor.this, String.format("%d %d %d",start,before,count), Toast.LENGTH_SHORT).show();
        if (count == 1) {
            //Toast.makeText(miniEditor.this, String.format("%c",s.charAt(start)), Toast.LENGTH_SHORT).show();
            char in = s.charAt(start);
            if (in == '}') {
                //delete two '\t'(if possible)
                if (editText.getText().subSequence(Math.max(editText.length() - 5, 0), editText.length()).toString().equals("    }")) {
                    int select = editText.getSelectionStart();
                    editText.setText((editText.getText().delete(editText.length() - 5, editText.length())).append('}'));
                    editText.setSelection(select - 4);
                }
            }
            if (in == '\n') {
                getIndent(editText);
                indentation(editText);

            }
        }
        textChangeFlag = 0;
    }
}
