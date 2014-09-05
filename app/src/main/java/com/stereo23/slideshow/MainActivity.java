package com.stereo23.slideshow;

import android.app.Activity;
import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import com.stereo23.slideshow.utilities.DirectoryChooserDialog;
import com.stereo23.slideshow.utilities.TimePickerFragment;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

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
            Button startTimeButton = (Button) getActivity().findViewById(R.id.startTimeButton);
            startTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(getFragmentManager(), "timePicker");
                }
            });
            Button stopTimeButton = (Button) getActivity().findViewById(R.id.stopTimeButton);
            stopTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new TimePickerFragment();
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

        }
    }

}
