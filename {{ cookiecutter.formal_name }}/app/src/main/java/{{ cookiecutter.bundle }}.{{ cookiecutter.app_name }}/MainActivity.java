/*
 * Copyright (C) 2019 Paul PENY "pmp-p"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package {{ cookiecutter.bundle }}.{{ cookiecutter.module_name }};
// also {{ cookiecutter.bundle|replace('.', '/') }}/{{ cookiecutter.module_name }} in rmipython.c
// also {{ cookiecutter.bundle|replace('.', '_') }}_{{ cookiecutter.module_name }} in pythonsupport.c
// also  "{{ cookiecutter.bundle }}" for TAG


import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

//implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
//import androidx.constraintlayout.widget.ConstraintLayout ;
//import androidx.constraintlayout.widget.ConstraintSet;
import android.widget.RelativeLayout;


import android.util.Log;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;


import java.util.Map;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import android.view.SurfaceHolder;
//import android.view.SurfaceView;
import android.view.Surface;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;

class
Window extends android.view.SurfaceView implements SurfaceHolder.Callback
{
    public Window(Context context) {
        super(context);
        Log.d(MainActivity.TAG, "Window");
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
//DEPRECATED        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(MainActivity.TAG, "window.surfaceCreated()");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.d(
            MainActivity.TAG,
            "window.surfaceChanged->nativeSetSurface(surface) " +
            w + "x" + h);
        MainActivity.nativeSetSurface(holder.getSurface());
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(MainActivity.TAG, "window.surfaceDestroyed->nativeSetSurface(null)");
        MainActivity.nativeSetSurface(null);
    }

}


public class
MainActivity extends AppCompatActivity implements View.OnClickListener
//, SurfaceHolder.Callback
{
    public static final String PYTHON = "python3.8";
    public static final String TAG = "{{ cookiecutter.bundle }}";
    //g4 private static final String APPLICATION_ID = BuildConfig.APPLICATION_ID;
    private static final String onuithread = "Applications.dispatch('[\"onUiThread\", \"\"]')";

    private static String HOME;
    private static String APK;
    private static String LIB;

    public String py_jobs = "";

    Gson gson = new Gson();

    public Window sv;

    // Application Context and ui layout, everything android goes by there
    public static HPyContext __main__ = null;


    public static JavaSpace jspy = new JavaSpace();

    static {
        System.loadLibrary("rmipython");
    }

//============================================================
    public static native void nativeSetSurface(Surface jsurface);

    // things hard to do yet from Python or C

    // GLES surface
    private boolean hasGLES20() {
        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return info.reqGlEsVersion >= 0x20000;
    }

    public Window make_window() {
        if (hasGLES20() ){
            Log.i(MainActivity.TAG," == GL/ES 2.0 avail ==");
        } else {
            Log.i(MainActivity.TAG," == Fallback to GL/ES 1.0 ==");
        }
        Window sv = new Window(this);
        //sv.getHolder().addCallback(this);
        sv.setId(666);
        sv.setOnClickListener( this );
        return sv;
    }

    // input events

    @Override
    public void onClick(View v) {
      // default method for handling onClick Events..
        Log.v(MainActivity.TAG, "onClick :" + v.getId());
        App("onEvent", v.getId() );
    }

// ===========================================================

    public native String stringFromJNI();

    public static native String PyRun(String jstring_code);

    public static native void PyLoop();

    public static int jobs_processing = 0;
    public ArrayList<String> outq;
    public ArrayList<String> appq;

    private ArrayList get_array(String json) {
        return gson.fromJson(json , ArrayList.class );
    }


    public native void jnionCreate(
        String jstring_tag,
        String jstring_ver,
        String jstring_apk,
        String jstring_lib,
        String jstring_home);

    public native void VMstart();
    public native void VMresume();
    public native void VMpause();
    public native void VMstop();

    public void App(Object ...args){
            appq.add( gson.toJson(args)) ;
    }


    int hour = 0;
    int minute = 0;
    int second = 0;

    int steps = 0;


    android.widget.TextView tickView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(MainActivity.TAG, " ============== onCreate : Java begin ================");

        PackageManager pm = getPackageManager();

        try {
            // or is it Context.getApplicationInfo().sourceDir ?
            ApplicationInfo ai = pm.getApplicationInfo(getApplicationContext().getPackageName(), 0);
            APK = ai.publicSourceDir;
            LIB = ai.nativeLibraryDir;
            Log.v(MainActivity.TAG, "APK : "+ APK );
            java.io.File file =  new java.io.File( getApplicationContext().getFilesDir().getPath() );
            HOME = file.getParent() ;
            Log.v(MainActivity.TAG, "HOME : " + HOME  );
        } catch (Throwable x) {
            Log.e(MainActivity.TAG, "cannot locate APK");
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        android.widget.RelativeLayout ui;
        ui = (android.widget.RelativeLayout ) findViewById(R.id.Applications);

        tickView = new android.widget.TextView(this);
        ui.addView(tickView);



        outq  = new ArrayList<String>();
        appq  = new ArrayList<String>();

        if (ui!=null) {
            __main__ = jspy.new_context(TAG, this, ui);

            // a simple test for javaspace
            jspy.ffi_call("java.lang.System", null, "getProperty" , "java.specification.version");

        }
        Log.v(MainActivity.TAG, " ============== onCreate : Java end ================");
        jnionCreate(MainActivity.TAG, PYTHON, APK, LIB, HOME);
    }

    @Override
    public void onResume() {
        super.onResume();
        hour = minute = second = 0;
        ((android.widget.TextView)findViewById(R.id.hellojniMsg)).setText(stringFromJNI());
        Log.v(MainActivity.TAG, " ============== onResume : Java Begin ================");
        VMstart();
        Log.v(MainActivity.TAG, " ============== onResume : Java End ================");
    }

    @Override
    public void onPause () {
        super.onPause();
        VMstop();
    }

    /*
     * A function calling from JNI to update current timer
     */
    @Keep
    private void updateTimer() {
        //android.util.Log.i(MainActivity.TAG, "AIO PUMP BEGIN");


        if (outq.size()>0) {
            android.util.Log.i(MainActivity.TAG, "AIO java->python");

            while ( outq.size()>0 ) {
                String retjson = outq.remove(0).toString();
                MainActivity.this.PyRun("aio.step("+ retjson +")");
            }
        }

        if (appq.size()>0) {
            android.util.Log.i(MainActivity.TAG, "Events java->python");

            while ( appq.size()>0 ) {
                PyRun("Applications.dispatch("+appq.remove(0)+")");
            }
        }

        // if busy don't multiply pending runnables.

        if (jobs_processing>0) {
            android.util.Log.i(MainActivity.TAG, "AIO UI is busy");
            return ;
        }

        // not busy ? time to do ui stuff

        MainActivity.this.py_jobs = PyRun(onuithread);

        //MainActivity.this.py_jobs = App("onUiThread","");

        if (MainActivity.__main__ != null) {
            if (MainActivity.this.py_jobs.length()>4)
                jobs_processing = 1;
        }

        ++second;
        if(second >= 60) {
            ++minute;
            second -= 60;
            if(minute >= 60) {
                ++hour;
                minute -= 60;
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String ticks = "" + MainActivity.this.hour + ":" +
                        MainActivity.this.minute + ":" +
                        MainActivity.this.second;

                MainActivity.this.tickView.setText(ticks);

                // not ready yet or empty work queue
                if (jobs_processing==0)
                    return;

                // there are jobs, exec them in UI thread
                try {
                    android.util.Log.i(MainActivity.TAG, "JOBS : " + MainActivity.this.py_jobs);
                    for (String retjson: jspy.Calls(get_array(MainActivity.this.py_jobs)) ) {
                        outq.add( retjson );
                    }
                }
                catch (IllegalStateException e) {
                    android.util.Log.e(MainActivity.TAG, MainActivity.this.py_jobs);
                }

                // clear the work queue ( it serves also as busy marker for calling thread)
                MainActivity.this.py_jobs = "";
                jobs_processing = 0;

            }
        });

    }




}




































//
