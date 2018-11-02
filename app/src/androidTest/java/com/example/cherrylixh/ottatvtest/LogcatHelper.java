package com.example.cherrylixh.ottatvtest;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class LogcatHelper {
    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;
    private String fileName;

    /**
     *
     * 初始化目录
     *
     * */
    public void init(Context context) {
        Date dc=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        String fileName="OttAtv"+File.separator+format.format(dc);
        /*Environment类可以获取外部存储目录，getExternalStorageState()方法返回外部存储设备的当前状态
         getExternalStorageDirectory（）方法返回扩展存储区目录（SDCard）
         */
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator +fileName;
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            //context.getFileDir()获取内部存储路径，在/data/data/pakagename下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator +fileName;
        }
        Log.i("PATH_LOGCAT",PATH_LOGCAT);
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    public LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        Date dc=new Date();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        fileName=format.format(dc)+".log";
        Log.i("fileName",fileName);
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT,fileName);
        }
        mLogDumper.start();

    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir,String fileName) {
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, fileName));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
             cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
           // cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";
        }
        public void stopLogs() {
            mRunning = false;
        }
        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                while(mRunning) {
                    String line =mReader.readLine();
                    if (!mRunning) {
                        break;
                    }
                    if(line==null){
                        continue;
                    }else{
                        if (line.length() == 0) {
                            continue;
                        }
                        //获取当前时间
                        Date dc = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String ctime = format.format(dc);
                        if (out != null && line.contains(mPID)) {
                            out.write((ctime + line + "\n")
                                    .getBytes());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }
}
