package com.example.zbj.rxbindingdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * RxView.clicks(eventButton)返回一个Observable的对象。
 * 1.首先RxBinding是对Android View事件的扩展，它使得你可以对View事件使用RxJava的各种操作。

 2.提供了与RxJava一致的回调，使得代码简洁明了。尤其是页面中充斥着大量的监听事件，各种各样的匿名内部类时。

 3.几乎支持我们常用的所有控件及事件。（v4、v7、design、recyclerview等）另外每个库还有对应的Kotlin支持库。
 * <p>
 * observer的三种写法
 */

public class MainActivity extends AppCompatActivity {
    private EditText et1, et2;
    TextView TvShow;
    Button btPreventDouble, btLongPress, btLogin;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et1 = (EditText) findViewById(R.id.rxbinding_et_usual_approach);
        et2 = (EditText) findViewById(R.id.rxbinding_et_reactive_approach);
        TvShow = (TextView) findViewById(R.id.rxbinding_tv_show);
        btPreventDouble = (Button) findViewById(R.id.bt_prevent_double);
        btLongPress = (Button) findViewById(R.id.bt_long_press);
        btLogin = (Button) findViewById(R.id.login);
        checkBox = (CheckBox) findViewById(R.id.text_login);

        //RxView监听editText变化
        textChangeListener();
        textChangeListenerLambda();

        //Rxview延时请求，应用于搜索关键词联想
        textDelayListener();

        //RxView防止双击,防抖
        doubleClickListener();

        //RxView长按监听
        longPressListener();

        //RxView登录验证
        testLoginListener();


    }


    private void testLoginListener() {
        RxCompoundButton.checkedChanges(checkBox)
                .subscribe(this::textLogin);
    }

    private void textLogin(Boolean flag) {
        btLogin.setEnabled(flag);
        btLogin.setBackgroundResource(flag ? android.R.color.holo_blue_dark : android.R.color.darker_gray);
    }


    private void longPressListener() {
        RxView.longClicks(btLongPress)
                .subscribe(this::longPressMethod);
    }

    private void longPressMethod(Void v) {
        Toast.makeText(this, "长按", Toast.LENGTH_SHORT).show();
    }

    private void textDelayListener() {
        RxTextView.textChanges(et1)
                .debounce(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::tvSetText);
    }

    private void tvSetText(CharSequence s) {
        TvShow.setText(s);
    }

    private void doubleClickListener() {
        //throttleFirst取时间间隔内第一次点击事件
        RxView.clicks(btPreventDouble)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Toast.makeText(MainActivity.this, "点击", Toast.LENGTH_SHORT).show();
                        Log.i("tag", "点击");
                    }
                });
    }

    private void textChangeListenerLambda() {
        RxTextView.textChanges(et2).subscribe(
                TvShow::setText);
    }

    private void textChangeListener() {
        RxTextView.textChanges(et2).subscribe(
                new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        TvShow.setText(charSequence);
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
