package com.stereo23.slideshow;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.stereo23.slideshow.utilities.Alarm;
import com.stereo23.slideshow.utilities.AutoStart;
import com.stereo23.slideshow.utilities.DirectoryChooserDialog;
import com.stereo23.slideshow.utilities.PowerConnected;
import com.stereo23.slideshow.utilities.TimePickerFragment;

/**
 * Created by Username on 08.09.2014.
 */
public class SettingsFragment extends PreferenceFragment {
    static int startHour = -1;
    static int startMinute = -1;
    static int stopHour;
    static int stopMinute;
    String m_chosenDir = "";
    boolean m_newFolderEnabled = false;
    private SharedPreferences.OnSharedPreferenceChangeListener onPrefChangeListener;
    Alarm alarm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarm = new Alarm();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences_screen);
        final Preference folderPref = (Preference) findPreference("path");
        folderPref.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("path", "null"));
        folderPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // Create DirectoryChooserDialog and register a callback
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(getActivity(),
                                new DirectoryChooserDialog.ChosenDirectoryListener() {
                                    @Override
                                    public void onChosenDir(String chosenDir) {
                                        m_chosenDir = chosenDir;
                                        Toast.makeText(
                                                getActivity(), "Chosen directory: " +
                                                        chosenDir, Toast.LENGTH_LONG).show();
                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("path", m_chosenDir).commit();
                                        folderPref.setSummary(m_chosenDir);
                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = !m_newFolderEnabled;
                return true;
            }
        });
        final CheckBoxPreference scheduleStartTimePref = (CheckBoxPreference) findPreference("schedule_start");
        final Preference startTimePref = (Preference) findPreference("start_time");
        final Preference stopTimePref = (Preference) findPreference("stop_time");
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("schedule_start", false)) {
            scheduleStartTimePref.setChecked(true);
            startTimePref.setEnabled(true);
            stopTimePref.setEnabled(true);
        }
        updateStartTime();
        updateStopTime();



        scheduleStartTimePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    startTimePref.setEnabled(true);
                    stopTimePref.setEnabled(true);
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("schedule_start", true).commit();
                } else {
                    startTimePref.setEnabled(false);
                    stopTimePref.setEnabled(false);
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean("schedule_start", false).commit();
                }
                return true;
            }
        });
        startTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Bundle bundle = new Bundle();
                bundle.putInt("pickerType", 0);
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");
                return false;
            }
        });
        stopTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Bundle bundle = new Bundle();
                bundle.putInt("pickerType", 1);
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");
                return false;
            }
        });
        onPrefChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key)
            {
                if (key.equals("start_hour")||key.equals("start_minute")){
                    updateStartTime();
                    if (alarm!=null) alarm.cancelAlarm(getActivity());
                    alarm.setAlarm(getActivity(),startHour,startMinute);
                }
                if (key.equals("stop_hour")||key.equals("stop_minute")){
                    updateStopTime();
                }
                if (key.equals("schedule_start")){
                    if (alarm!=null) alarm.cancelAlarm(getActivity());
                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("schedule_start", false)) {
                        if (startHour!=-1&&startMinute!=-1){
                            alarm.setAlarm(getActivity(),startHour,startMinute);
                        }else{
                            Toast.makeText(getActivity(), "Choose start time first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                if (key.equals("start_after_reboot")){
                    ComponentName receiver = new ComponentName(getActivity(), AutoStart.class);
                    PackageManager pm = getActivity().getPackageManager();
                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("start_after_reboot", false)){
                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                    }else{
                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }
                }
                if (key.equals("start_after_reboot")){
                    ComponentName receiver = new ComponentName(getActivity(), AutoStart.class);
                    PackageManager pm = getActivity().getPackageManager();
                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("start_after_reboot", false)){
                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                    }else{
                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }
                }
                if (key.equals("start_when_power_connected")){
                    ComponentName receiver = new ComponentName(getActivity(), PowerConnected.class);
                    PackageManager pm = getActivity().getPackageManager();
                    if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("start_when_power_connected", false)){
                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                    }else{
                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                    }
                }

            }
        };
    }
    public void updateStartTime(){
        Preference startTimePref = (Preference) findPreference("start_time");
        startHour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("start_hour", -1);
        startMinute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("start_minute", -1);
        if (startHour != -1 && startMinute != -1) {
            StringBuilder stringBuilder =  new StringBuilder();
            if (startHour<10) stringBuilder.append("0");
            stringBuilder.append(startHour).append(":");
            if (startMinute<10) stringBuilder.append("0");
            stringBuilder.append(startMinute);
            startTimePref.setSummary(stringBuilder.toString());
        }
    }
    public void updateStopTime(){
        Preference stopTimePref = (Preference) findPreference("stop_time");
        stopHour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_hour", -1);
        stopMinute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_minute", -1);
        if (stopHour != -1 && stopMinute != -1) {
            StringBuilder stringBuilder =  new StringBuilder();
            if (stopHour<10) stringBuilder.append("0");
            stringBuilder.append(stopHour).append(":");
            if (stopMinute<10) stringBuilder.append("0");
            stringBuilder.append(stopMinute);
            stopTimePref.setSummary(stringBuilder.toString());
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(onPrefChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(onPrefChangeListener);
    }


        /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();

        Button chooseButton = (Button) getActivity().findViewById(R.id.chooseButton);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            private String m_chosenDir = "";
            private boolean m_newFolderEnabled = true;

            @Override
            public void onClick(View v) {
                // Create DirectoryChooserDialog and register a callback
                DirectoryChooserDialog directoryChooserDialog =
                        new DirectoryChooserDialog(getActivity(),
                                new DirectoryChooserDialog.ChosenDirectoryListener() {
                                    @Override
                                    public void onChosenDir(String chosenDir) {
                                        m_chosenDir = chosenDir;
                                        Toast.makeText(
                                                getActivity(), "Chosen directory: " +
                                                        chosenDir, Toast.LENGTH_LONG).show();
                                        PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("path", m_chosenDir).commit();
                                        TextView folderTv = (TextView) getActivity().findViewById(R.id.folderName);
                                        folderTv.setText(m_chosenDir);
                                    }
                                });
                // Toggle new folder button enabling
                directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
                // Load directory chooser dialog for initial 'm_chosenDir' directory.
                // The registered callback will be called upon final directory selection.
                directoryChooserDialog.chooseDirectory(m_chosenDir);
                m_newFolderEnabled = ! m_newFolderEnabled;
            }
        });
        String folderPath = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("path", "null");
        if (folderPath != null) {
            TextView folderTv = (TextView) getActivity().findViewById(R.id.folderName);
            folderTv.setText(folderPath);
        }
        startHour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("start_hour", -1);
        startMinute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("start_minute", -1);
        if (startHour != -1 && startMinute != -1) {
            TextView startTimeTv = (TextView) getActivity().findViewById(R.id.startTimeTextView);
            startTimeTv.setText(startHour+":"+startMinute);
        }
        stopHour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_hour", -1);
        stopMinute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_minute", -1);
        if (stopHour != -1 && stopMinute != -1) {
            TextView stopTimeTv = (TextView) getActivity().findViewById(R.id.stopTimeTextView);
            stopTimeTv.setText(stopHour+":"+stopMinute);
        }
        final Alarm alarm = new Alarm();
        Button startTimeButton = (Button) getActivity().findViewById(R.id.startTimeButton);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("pickerType", 0);
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        Button stopTimeButton = (Button) getActivity().findViewById(R.id.stopTimeButton);
        stopTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("pickerType", 1);
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
        Button goButton = (Button) getActivity().findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new SlideshowFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        Button saveButton = (Button) getActivity().findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startHour > -1 && startMinute > -1){
                    if (stopHour > -1 && stopMinute > -1){
                        alarm.setAlarm(getActivity(), startHour, startMinute);
                    }

                }


            }
        });
        SeekBar seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);
        int interval = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("interval", -1);
        if (interval != -1) {
            TextView currentInterval = (TextView) getActivity().findViewById(R.id.currentInterval);
            currentInterval.setText(interval + " sec.");
            seekBar.setProgress(interval-1);
        }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                TextView currentInterval = (TextView) getActivity().findViewById(R.id.currentInterval);
                currentInterval.setText(progress+1 +" sec.");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("interval", progressChanged+1).commit();
            }
        });
    }*/
}
