package com.suyong.kakaobot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;

import com.suyong.kakaobot.engine.ScriptEngine;

import java.io.File;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static DebugChatAdapter adapter;

    private LinearLayout logLayout;
    private LinearLayout debugLayout;
    private LinearLayout settingLayout;

    private ListView logList;

    private ListView debugChat;
    private EditText debugEdit;
    private EditText debugEditRoom;
    private EditText debugEditSender;
    private ImageButton debugSend;

    private Switch settingPower;
    private ImageButton settingReload;
    private ImageButton settingEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KakaoManager.getInstance().isForeground = true;
        FileManager.getInstance().init();
        KakaoManager.getInstance().start();
        Logger.getInstance().init();

        logLayout =  findViewById(R.id.main_log);
        debugLayout = findViewById(R.id.main_debug);
        settingLayout = findViewById(R.id.main_setting);

        initDebugLayout();
        initLogLayout();
        initSettingLayout();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private void initDebugLayout() {
        debugChat = findViewById(R.id.debug_list);
        debugEdit = findViewById(R.id.debug_chat_edit);
        debugEditRoom = findViewById(R.id.debug_chat_room);
        debugEditSender = findViewById(R.id.debug_chat_sender);
        debugSend = findViewById(R.id.debug_chat_send);

        adapter = new DebugChatAdapter();
        debugChat.setAdapter(adapter);

        debugSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!debugEdit.getText().toString().trim().equals("")) {
                    adapter.addPersonChat(debugEdit.getText().toString().trim());

                    KakaoManager.KakaoData data = new KakaoManager.KakaoData();
                    data.room = debugEditRoom.getText().toString();
                    data.sender = debugEditSender.getText().toString();
                    data.message = debugEdit.getText().toString();

                    KakaoManager.getInstance().addKakaoData(data);

                    debugEdit.setText("");
                }
            }
        });
    }

    private void initLogLayout() {
        logList = findViewById(R.id.log_list);

        final LogAdapter log = new LogAdapter();
        logList.setAdapter(log);
        log.notifyDataSetChanged();

        Logger.getInstance().setLogChangedListener(new Logger.OnLogChangedListener() {
            @Override
            public void onChanged(Logger.Type type, String str) {
                log.notifyDataSetChanged();
            }
        });
    }

    private void initSettingLayout() {
        settingPower = findViewById(R.id.setting_power_switch);
        settingReload = findViewById(R.id.setting_reload_script_button);
        settingEdit = findViewById(R.id.setting_edit_script_button);

        settingPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    KakaoManager.getInstance().start();
                } else {
                    KakaoManager.getInstance().stop();
                }
            }
        });
        settingReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KakaoManager.getInstance().reload();
            }
        });
        settingEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Intent intent = new Intent(Intent.ACTION_EDIT);
                Uri uri = Uri.parse("file://" + new File(FileManager.getInstance().KAKAOBOT_HOME, FileManager.getInstance().SCRIPT_NAME).getPath());
                intent.setDataAndType(uri, "text/plain");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_log:
                logLayout.setVisibility(VISIBLE);
                debugLayout.setVisibility(GONE);
                settingLayout.setVisibility(GONE);
                return true;
            case R.id.navigation_debug:
                logLayout.setVisibility(GONE);
                debugLayout.setVisibility(VISIBLE);
                settingLayout.setVisibility(GONE);
                return true;
            case R.id.navigation_setting:
                logLayout.setVisibility(GONE);
                debugLayout.setVisibility(GONE);
                settingLayout.setVisibility(VISIBLE);
                return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        KakaoManager.getInstance().isForeground = false;
    }
}
