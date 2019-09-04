package ru.home.bahr.cottage;

import android.content.Context;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingItem extends LinearLayout{
    private TextView title;                     //текс заголовка
    private EditText editText;                  //обьект EditText
    private static int id = 0;                  //новые id для обьектов

    public SettingItem(Context context){
        super(context);
        initComponent();
    }

    public SettingItem(Context context, String title){
        super(context);
        initComponent();
        setTitle(title);
    }

    public SettingItem(Context context, String title, String text){
        super(context);
        initComponent();
        setTitle(title);
        setText(text);
    }

    public SettingItem(Context context, String title, String text, String hint){
        super(context);
        initComponent();
        setTitle(title);
        setText(text);
        setHint(hint);
    }

    private void initComponent(){       //инициализация компонентов
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.setting_item, this);
        title = (TextView) findViewById(R.id.title);
        editText = (EditText) findViewById(R.id.editText);
        title.setId(id++);
        editText.setId(id++);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    public void setText(String text){
        editText.setText(text);
    }

    public void setHint(String hint){
        editText.setHint(hint);
    }

    public void setPassword(){      //не отображать вводимые символы
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setTransformationMethod(new PasswordTransformationMethod());
    }

    public String getText(){
        return editText.getText().toString();
    }

}
