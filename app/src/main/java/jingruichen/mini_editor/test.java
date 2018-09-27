package jingruichen.mini_editor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class test extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn1 = (Button) this.findViewById(R.id.button1);
        btn1.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {

            }
        });
        Button btn2 = (Button) this.findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {

            }
        });
        Button btn3 = (Button) this.findViewById(R.id.button3);
        EditText t1 = (EditText)this.findViewById(R.id.editText2);
        EditText t2 = (EditText)this.findViewById(R.id.editText3);
        EditText t3 = (EditText)this.findViewById(R.id.editText4);
        EditText t4 = (EditText)this.findViewById(R.id.editText5);
        EditText t5 = (EditText)this.findViewById(R.id.editText6);
        EditText t6 = (EditText)this.findViewById(R.id.editText7);

        btn3.setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                /*
                t1.clearComposingText();
                t2.clearComposingText();
                t3.clearComposingText();
                t4.clearComposingText();
                t5.clearComposingText();
                t6.clearComposingText();
                */
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

