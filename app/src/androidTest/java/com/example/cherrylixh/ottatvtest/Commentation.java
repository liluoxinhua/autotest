package com.example.cherrylixh.ottatvtest;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Commentation {
    public void takeScreenShot(String fileName){
        File file=new File(getPath()+"/"+fileName+".jpg");
        UiDevice mDevice=UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Log.i("takeScreenShot","fileName:"+fileName);
        mDevice.takeScreenshot(file,1.0f,20);
        mDevice.waitForIdle();
    }

    private String getPath() {
        File file=new File("/data/");
        if(file.exists()){
            file=new File("/data/OttAtv");
        }else {
            file=new File("/mnt/shell/emulated/0/OttAtv");
        }
        if(!file.exists()){
            file.mkdir();
            Log.i("getpath","mkdir");
        }
        Log.i("getPath",file.getPath());
        return file.getPath();
    }

    public File createFile(String path, String fileName){
        File file=new File(path,fileName);
        try{
         if(file.exists()){
             file.delete();
         }
         file.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
           return null;
        }
        return file;
    }
  public void logToSdcard(){
      File file=new File("/data/OttAtv/l/log");
      try {
          Runtime.getRuntime().exec("logcat -f"+file.getPath());
      } catch (IOException e) {
          e.printStackTrace();
      }
  }
  public void pullToLocal(String logpath){
        if(!new File(logpath).exists()){
            Log.i("pullToLocal","logpath is not exists");
        }else {
            ArrayList<String> commandLine = new ArrayList<>();
            //通过cmd直接执行命令，将日志文件输出到本地文件中
            commandLine.add("logcat");
            commandLine.add("-d");//使用该参数可以让logcat获取日志完毕后终止进程
            commandLine.add("-v");
            commandLine.add("threadtime");
            commandLine.add("-f");//如果使用commandLine.add(">");是不会写入文件，使用-f可以写入文件
            commandLine.add(logpath);
        }
  }
}
