package com.suyong.kakaobot;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Pattern;

import xyz.iridiumion.iridiumhighlightingeditor.editor.HighlightingDefinition;
import xyz.iridiumion.iridiumhighlightingeditor.editor.IridiumHighlightingEditorJ;
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.HighlightingDefinitionLoader;

public class ScriptEditActivity extends AppCompatActivity {
    private String SCRIPT_PATH;
    private IridiumHighlightingEditorJ editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_edit);

        SCRIPT_PATH = new File(FileManager.KAKAOBOT_HOME, FileManager.SCRIPT_NAME).toString();

        editor = findViewById(R.id.editor);
        editor.setTabWidth(2);

        HighlightingDefinition definition = new HighlightingDefinitionLoader().selectDefinitionFromFileExtension("js");
        editor.loadHighlightingDefinition(definition);

        String source = FileManager.getInstance().readScript();
        editor.setText(source);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                String source = editor.getCleanText().toString();

                FileManager.getInstance().save(SCRIPT_PATH, source);

                Toast.makeText(this, getString(R.string.save_script), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
