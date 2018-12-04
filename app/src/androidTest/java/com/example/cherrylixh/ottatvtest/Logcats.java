package com.example.cherrylixh.ottatvtest;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class Logcats extends BaseCase{
    public static String getLogPath(){
        String path;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            path=Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            path=context.getFilesDir().getAbsolutePath();
        }
        Date dc=new Date();
       // SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        path=path+File.separator+logdir+File.separator;
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        Log.i("logpath:",path);
        return path;
    }

    public static void getLog(boolean isStop) {
        ArrayList<String> commandLine=new ArrayList<>();
        //通过cmd直接执行命令，将日志文件输出到本地文件中
        String logpath=getLogPath()+"logtest.txt";
        commandLine.add("logcat");
        commandLine.add("-d");//使用该参数可以让logcat获取日志完毕后终止进程
        commandLine.add("-v");
        commandLine.add("threadtime");
        commandLine.add("-f");//如果使用commandLine.add(">");是不会写入文件，使用-f可以写入文件
        commandLine.add(logpath);
        Process process = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            //创建执行cmd命令的进程
            process=Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //输出正确流
        try {
           //创建输入流
            inputStream=process.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream),1024);
        String line=bufferedReader.readLine();
        while (line!=null){
            //使用isstop参数控制日志手动停止
            if(isStop){
                break;
            }
        }} catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //关闭日志输出流
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //输出错误流
            InputStream errorInput=process.getErrorStream();
            BufferedReader errorReader=new BufferedReader(new InputStreamReader(errorInput));
            String eline="";
        try {
            while ((eline=errorReader.readLine())!=null){
                if(isStop){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                errorReader.close();
                errorInput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
