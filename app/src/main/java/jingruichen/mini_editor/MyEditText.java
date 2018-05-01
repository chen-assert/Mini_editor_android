package jingruichen.mini_editor;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.support.v7.widget.AppCompatEditText;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Canvas;
import android.content.Context;

/**
 * Created by yangzixuan on 2018/4/29.
 */

public class MyEditText extends AppCompatEditText {
    public MyEditText(Context context, AttributeSet as){
        super(context,as);
        setFocusable(true);
        setGravity(Gravity.TOP);
        setPadding(75,0,0,0);
    }

    protected void onDraw(final Canvas canvas){
        if(getText().toString().length() != 0){
            float y = 0;
            Paint p = new Paint();
            p.setColor(Color.GRAY);
            p.setTextSize(40);
            for(int i=0;i<getLineCount();i++){
                y = (i+1)*getLineHeight() - (getLineHeight()/4);
                canvas.drawText(String.valueOf(i+1),0,y,p);
                canvas.save();
            }
            
        }
        super.onDraw(canvas);

    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(null);
    }
}

