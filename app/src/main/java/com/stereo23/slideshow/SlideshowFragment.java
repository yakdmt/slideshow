package com.stereo23.slideshow;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Username on 05.09.2014.
 */
public class SlideshowFragment extends Fragment {

    Uri[] mUrls;
    int currentImage = -1;
    int BITMAP_SIZE = 1280;
    Timer timer;
    int stopHour =-1;
    int stopMinute =-1;
    Calendar calendar;
    boolean isScheduledStart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle!=null){
            isScheduledStart = bundle.getBoolean("schedule",false);
        }
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
    }
    @Override
    public void onResume() {
        super.onResume();
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        String path = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("path", "null");

        if (path!=null && !path.equals("null")){
            File images = new File (path);
            Log.d("abc","IMAGES PATH IS "+images.getAbsolutePath());
            File[] imagelist = images.listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name){
                    return ((name.endsWith(".jpg"))||(name.endsWith(".png")));
                }
            });
            Log.d("abc","imagelist length is "+imagelist.length);
            if (imagelist.length>0){
                String[] mFiles = new String[imagelist.length];
                for(int i= 0 ; i< imagelist.length; i++){
                    mFiles[i] = imagelist[i].getAbsolutePath();
                }
                Log.d("abc","mFiles length is "+mFiles.length);
                mUrls = new Uri[mFiles.length];
                for(int i=0; i < mFiles.length; i++){
                    mUrls[i] = Uri.parse(mFiles[i]);
                }
                ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);

                int interval = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("interval", 0);
                stopHour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_hour", 0);
                stopMinute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_minute", 0);
                if (interval != 0) {
                    timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            calendar = Calendar.getInstance();
                            Log.d("abc","getFragmentManager().getBackStackEntryCount()"+getFragmentManager().getBackStackEntryCount());
                            if (calendar.get(Calendar.HOUR_OF_DAY)==stopHour && calendar.get(Calendar.MINUTE)==stopMinute && isScheduledStart){
                                Log.d("abc","getFragmentManager().getBackStackEntryCount()"+getFragmentManager().getBackStackEntryCount());
                                for (int i = 1; i<getFragmentManager().getBackStackEntryCount(); i++){
                                    getFragmentManager().popBackStack();
                                }
                                timer.cancel();
                                isScheduledStart=false;
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(startMain);
                            }
                            if (currentImage+1 < mUrls.length){
                                currentImage++;
                            }
                            else {
                                currentImage = 0;

                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    changeImage(mUrls[currentImage]);
                                }
                            });
                        }
                    }, 0, interval*1000);
                }
            } else {
                Toast.makeText(getActivity(), "Folder is empty!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "Choose folder in settings!", Toast.LENGTH_LONG).show();
        }


    }
    private void changeImage(Uri uri){
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        try {
            if (imageView != null) imageView.setImageBitmap(getBitmap(Uri.parse("file://" + uri)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Bitmap getBitmap(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getActivity().getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > BITMAP_SIZE) ? (originalSize / BITMAP_SIZE) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = false;
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = getActivity().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }
    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }
    @Override
     public void onPause() {
        super.onPause();
        if (timer!=null) timer.cancel();
    }
}
