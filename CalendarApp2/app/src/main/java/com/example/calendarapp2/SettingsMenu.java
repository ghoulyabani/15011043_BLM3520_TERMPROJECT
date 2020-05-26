package com.example.calendarapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsMenu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);

        final Spinner settingSpinner = findViewById(R.id.spnSettingsFreq);
        final EditText edtDefaultReminder = findViewById(R.id.edtReminderSetting);
        final RadioButton rbtnMinute = findViewById(R.id.settingsMinute);
        final RadioButton rbtnHour = findViewById(R.id.settingsHour);
        final RadioButton rbtnDay = findViewById(R.id.settingsDay);
        final Switch swtchUI = findViewById(R.id.swtUIMode);

        String selectedSpinnerItem = sharedPreferences.getString("spinner", "Asla");
        if (selectedSpinnerItem.equals("Asla")){
            settingSpinner.setSelection(0);
        }
        else if (selectedSpinnerItem.equals("Günlük")){
            settingSpinner.setSelection(1);
        }
        else if (selectedSpinnerItem.equals("Haftalık")){
            settingSpinner.setSelection(2);
        }
        else if (selectedSpinnerItem.equals("Aylık")){
            settingSpinner.setSelection(3);
        }
        else if (selectedSpinnerItem.equals("Yıllık")){
            settingSpinner.setSelection(4);
        }

        edtDefaultReminder.setText(sharedPreferences.getString("reminder", "30"));
        final String selectedRaido = sharedPreferences.getString("radioButton", "Dakika");
        if (selectedRaido.equals("Dakika")){
            rbtnMinute.setChecked(true);
        }
        else if (selectedRaido.equals("Saat")){
            rbtnHour.setChecked(true);
        }
        else if (selectedRaido.equals("Gün")){
            rbtnDay.setChecked(true);
        }

        String uiMode = sharedPreferences.getString("ui", "Light");
        if (uiMode.equals("Light")){
            swtchUI.setChecked(false);
            swtchUI.setText("Aydınlık Mod");
        }
        else if (uiMode.equals("Dark")){
            swtchUI.setChecked(true);
            swtchUI.setText("Karanlık Mod");
        }

        swtchUI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (swtchUI.isChecked()){
                    swtchUI.setText("Karanlık Mod");
                }
                else if(!swtchUI.isChecked()){
                    swtchUI.setText("Aydınlık Mod");
                }
            }
        });


        Button btnKaydet = findViewById(R.id.btnSaveSettings);
        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeNumbers = edtDefaultReminder.getText().toString();
                String timeInterval = null;
                if (rbtnMinute.isChecked()){
                    timeInterval = "Dakika";
                }
                else if (rbtnHour.isChecked()){
                    timeInterval = "Saat";
                }
                else if (rbtnDay.isChecked()){
                    timeInterval = "Gün";
                }
                String selectedRepeat = settingSpinner.getSelectedItem().toString();
                String uiMode = null;
                if (swtchUI.isChecked()){
                    uiMode = "Dark";
                }
                else if(!swtchUI.isChecked()){
                    uiMode = "Light";
                }
                editor.putString("spinner", selectedRepeat);
                editor.putString("reminder", timeNumbers);
                editor.putString("radioButton", timeInterval);
                editor.putString("ui", uiMode);
                editor.apply();
                finish();
            }
        });

    }
}
