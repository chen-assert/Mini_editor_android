package jingruichen.mini_editor;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jingruichen.mini_editor.miniEditor.editText;

/**
 * Created by yangzixuan on 26/04/2018.
 */

public class KeywordHighlighting {

    //a map to store keywords and the corresponding text color
    public static Map<String,String> keyWord = new HashMap<>();

    //store all the C keywords in the map
    public KeywordHighlighting(){
        keyWord.put("cin","purple");
        keyWord.put("cout","purple");
        keyWord.put("endl","purple");
        keyWord.put("printf","purple");
        keyWord.put("scanf","purple");
        keyWord.put("auto","blue");
        keyWord.put("break","red");
        keyWord.put("case","purple");
        keyWord.put("switch","purple");
        keyWord.put("char","blue");
        keyWord.put("const","blue");
        keyWord.put("continue","red");
        keyWord.put("default","purple");
        keyWord.put("do","pink");
        keyWord.put("double","blue");
        keyWord.put("else","pink");
        keyWord.put("if","pink");
        keyWord.put("enum","blue");
        keyWord.put("extern","blue");
        keyWord.put("float","blue");
        keyWord.put("for","pink");
        keyWord.put("goto","red");
        keyWord.put("int","blue");
        keyWord.put("long","blue");
        keyWord.put("register","blue");
        keyWord.put("return","red");
        keyWord.put("short","blue");
        keyWord.put("signed","blue");
        keyWord.put("sizeof","pink");
        keyWord.put("static","blue");
        keyWord.put("struct","blue");
        keyWord.put("typedef","blue");
        keyWord.put("union","blue");
        keyWord.put("unsigned","blue");
        keyWord.put("void","blue");
        keyWord.put("volatile","blue");
        keyWord.put("while","pink");


    }

    public void Highlight(){
        //if word is one of the keywords
        int select = editText.getSelectionStart();
        String text = editText.getText().toString();
        Scanner s = new Scanner(text);
        List<String> words = new ArrayList<>();
        while(s.hasNext()){
            String w = s.next();
            words.add(w);
        }
        SpannableString span = new SpannableString(text);
        for(int i=0;i<words.size();i++){
            String word = words.get(i);
            int begin = text.indexOf(word);
            while(begin < text.length()) {
                if (keyWord.containsKey(word)) {

                    String color = keyWord.get(word);

                    switch (color) {
                        case "red":
                            span.setSpan(new ForegroundColorSpan(Color.RED), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            break;
                        case "blue":
                            span.setSpan(new ForegroundColorSpan(Color.BLUE), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            break;
                        case "pink":
                            span.setSpan(new ForegroundColorSpan(Color.MAGENTA), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            break;
                        case "purple": 
                            span.setSpan(new ForegroundColorSpan(Color.parseColor("purple")), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            break;
                        case "greem":
                            span.setSpan(new ForegroundColorSpan(Color.GREEN), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                            break;
                        case "gray":
                            span.setSpan(new ForegroundColorSpan(Color.parseColor("grey")), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);

                    }

                }
                if ((word.charAt(0) == '"' && word.charAt(word.length() - 1) == '"') || word.endsWith("'")) {
                    span.setSpan(new ForegroundColorSpan(Color.GRAY), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                }

                if(isNum(word)){
                    span.setSpan(new ForegroundColorSpan(Color.parseColor("purple")), begin, begin + word.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                }

                if(word.charAt(0) == '#'){
                    span.setSpan(new ForegroundColorSpan(Color.parseColor("grey")),begin,begin+word.length(),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                }
                if(word.charAt(0) == '/' && word.charAt(1) == '/'){
                    span.setSpan(new ForegroundColorSpan(Color.GREEN),begin,begin+word.length(),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
                }

                begin = text.indexOf(word, begin + word.length());
                if (begin == -1) break;
            }
        }
        editText.setText(span);
        editText.setSelection(select);
    }

    private boolean isNum(String s){
        Pattern p = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        Matcher m = p.matcher(s);
        if(m.matches()) return true;
        return false;
    }

}
