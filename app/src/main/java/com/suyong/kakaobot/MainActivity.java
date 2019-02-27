package com.suyong.kakaobot;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import com.suyong.kakaobot.engine.ScriptEngine;

import org.mozilla.javascript.tools.jsc.Main;

import java.io.File;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_READ = 0;
    private static final int PERMISSION_WRITE = 1;
    private static final int PERMISSION_INTERNET = 2;
    private static final int PERMISSION_ALL = 3;
    private static final int PERMISSION_OVERLAY = 4;

    public static DebugChatAdapter adapter;
    public static Context context;

    private FrameLayout logLayout;
    private LinearLayout debugLayout;
    private ScrollView settingLayout;

    private ListView logList;
    private FloatingActionButton deleteLog;
    private FloatingActionButton goDown;

    private ListView debugChat;
    private EditText debugEdit;
    private EditText debugEditRoom;
    private EditText debugEditSender;
    private ImageButton debugSend;

    public static Switch settingPower;
    private LinearLayout settingPermission;
    private LinearLayout settingReload;
    private LinearLayout settingChange;
    private LinearLayout settingEdit;
    private LinearLayout settingHelp;
    private LinearLayout settingCafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        KakaoManager.getInstance().isForeground = true;
        KakaoManager.getInstance().setContext(this);
        FileManager.getInstance().init();
        Logger.getInstance().init();

        logLayout =  findViewById(R.id.main_log);
        debugLayout = findViewById(R.id.main_debug);
        settingLayout = findViewById(R.id.main_setting);

        initDebugLayout();
        initLogLayout();
        initSettingLayout();

        checkPermission();

        KakaoTalkListener.changeListener();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        KakaoManager.getInstance().isForeground = true;
    }

    private void checkPermission() {
        String[] permissions = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(permissions)) {
                requestPermissions(permissions, PERMISSION_ALL);
            }
        }
    }

    public boolean hasPermissions(String[] permissions) {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) == PERMISSION_DENIED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initDebugLayout() {
        debugChat = findViewById(R.id.debug_list);
        debugEdit = findViewById(R.id.debug_chat_edit);
        debugEditRoom = findViewById(R.id.debug_chat_room);
        debugEditSender = findViewById(R.id.debug_chat_sender);
        debugSend = findViewById(R.id.debug_chat_send);

        adapter = new DebugChatAdapter();
        adapter.setOnChatListener(new DebugChatAdapter.OnChatListener() {
            @Override
            public void onChatted(DebugChatAdapter.ChatData data) {
                if(debugChat.getFirstVisiblePosition() > adapter.getCount() - 5)
                    debugChat.setSelection(adapter.getCount() - 1);
            }
        });

        debugChat.setAdapter(adapter);

        debugSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!debugEdit.getText().toString().trim().equals("") && KakaoManager.getInstance().isRunning()) {
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
        deleteLog = findViewById(R.id.delete_log);
        goDown = findViewById(R.id.go_down);

        final LogAdapter log = new LogAdapter();
        logList.setAdapter(log);
        log.notifyDataSetChanged();

        Logger.getInstance().setLogChangedListener(new Logger.OnLogChangedListener() {
            @Override
            public void onChanged(Logger.Log l) {
                log.notifyDataSetChanged();

                if(logList.getFirstVisiblePosition() > log.getCount() - 5)
                    logList.setSelection(log.getCount() - 1);
            }
        });

        final Context ctx = this;
        deleteLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileManager.getInstance().delete(FileManager.LOG);
                Toast.makeText(ctx, getString(R.string.delete_log), Toast.LENGTH_SHORT).show();
                Logger.getInstance().deleteLog();

                log.notifyDataSetChanged();
            }
        });
        goDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logList.setSelection(log.getCount() - 1);
            }
        });
    }

    private void initSettingLayout() {
        settingPower = findViewById(R.id.setting_power_switch);
        settingPermission = findViewById(R.id.setting_give_permission_layout);
        settingReload = findViewById(R.id.setting_reload_script_layout);
        settingChange = findViewById(R.id.setting_change_listener_layout);
        settingEdit = findViewById(R.id.setting_edit_script_layout);
        settingHelp = findViewById(R.id.setting_etc_help_layout);
        settingCafe = findViewById(R.id.setting_etc_cafe_layout);

        settingPower.setChecked(KakaoManager.getInstance().isRunning());
        settingPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("power", KakaoManager.getInstance().isRunning() + "");
                if(b) {
                    KakaoManager.getInstance().start();
                } else {
                    KakaoManager.getInstance().stop();
                }
            }
        });
        settingPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
        settingReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(KakaoManager.getInstance().isRunning()) {
                    KakaoManager.getInstance().reload();

                    Logger.Log log = new Logger.Log();
                    log.type = Logger.Type.APP;
                    log.title = "Restart";
                    log.index = "";

                    Logger.getInstance().add(log);
                }
            }
        });
        settingChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.context).create();
                dialog.setTitle(getString(R.string.change_listener));

                final EditText editText = new EditText(MainActivity.context);
                editText.setHint(getString(R.string.write_package));

                try {
                    editText.setText(FileManager.getInstance().readData(FileManager.KAKAOBOT_DATA, "listener_package").toString());
                } catch(NullPointerException e) {
                    editText.setText("");
                }

                dialog.setView(editText);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FileManager.getInstance().saveData(FileManager.KAKAOBOT_DATA, "listener_package", editText.getText());
                        KakaoTalkListener.changeListener();
                        Toast.makeText(MainActivity.context, getString(R.string.changed), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.context, getString(R.string.canceled), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            }
        });
        settingEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScriptEditActivity.class));
            }
        });
        settingHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Su-Yong/NewKakaoBot/blob/master/API.md"));
                startActivity(intent);
            }
        });
        settingCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafe.naver.com/nameyee"));
                startActivity(intent);
            }
        });
    }

    public static void UIThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, 0);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if (grantResults[0] == PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, PERMISSION_READ);

                }
                break;
            case PERMISSION_READ:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {

                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,}, PERMISSION_READ);

                }
                break;
            case PERMISSION_WRITE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    // TODO
                } else {
                    // TODO
                }
                break;
            case PERMISSION_INTERNET:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    // TODO
                } else {
                    // TODO
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        KakaoManager.getInstance().isForeground = false;
    }
}
