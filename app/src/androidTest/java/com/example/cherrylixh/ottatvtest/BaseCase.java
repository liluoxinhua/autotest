package com.example.cherrylixh.ottatvtest;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.relevantcodes.extentreports.ExtentReports;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class BaseCase {
    private static int LAUNCH_TIMEOUT=7000;
    public static  String packageName="";
    public static String version="";
    public static HttpManager hm;
    public static PagesManager pm;
    private static boolean firstTest;
    public static Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
    public static UiDevice mDevice=UiDevice.getInstance(instrumentation);
    public static Context context= instrumentation.getContext();
    static LogcatHelper logcatHelper=new LogcatHelper(context);
    static String logdir="OttAtv";
    static ExtentReports extent;

    @BeforeClass
    public static void setup(){
        //测试项目开始前运行，如启动app
        packageName="com.pptv.tvsports";
        version="3.4.0";
        //开始抓取日志
        Logcats.getLog(false);
        //启动聚体育
        firstTest=true;
        if(firstTest) {
            startApp(mDevice, context, packageName);
        }
        sleep(30000);
        firstTest=false;
        hm=new HttpManager();
        pm=new PagesManager();
        //调起抓取log的接口，该线程必须在应用调起时候执行，因为如果sleep()线程会中断挂起，无法继续执行
       // logcatHelper.start();
        String sdPath= Environment.getExternalStorageDirectory().getAbsolutePath();
        String reportPath=sdPath+ File.separator+logdir+File.separator+"extent.html";
        Log.i("reportPath",reportPath);
       // extent=new ExtentReports(reportPath,true);
    }

    @AfterClass
    public static void teardown(){
        //测试项目结束后运行，如结束后返回到首页
        mDevice=UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        String cmd="am start -n "+packageName+"/com.pptv.tvsports.activity.HomeActivity";
        try {
            mDevice.executeShellCommand(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // logcatHelper.stop();
        Logcats.getLog(true);

     new AnlysysXml().xmlToExcel();
    }
//   @Rule
 // public ExtentUtils  eu=new ExtentUtils(extent);
    public static void startApp(UiDevice mDevice, Context context,String packageName) {
        if(mDevice!=null){
            Log.i("startAPP","get Device sucessfully!");
        }else{
            Log.i("startAPP","get Device failed!");
        }
        //启动聚体育app
        Log.i("startApp","启动聚体育");
        Intent intent=context.getPackageManager().getLaunchIntentForPackage(packageName);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.res(packageName+":id/tab_item_title")),LAUNCH_TIMEOUT);
        //判断是否启动成功
        if(mDevice.hasObject(By.res(packageName+":id/tv_dialog_text").textContains("内容加载失败"))){
            Log.v("startApp","聚体育加载失败....");
        }else{
            Log.v("startApp","聚体育加载成功，开始测试！");
        }
      //通过executeShellCommand启动聚体育
        String cmd="am start -n "+packageName+"/com.pptv.tvsports.activity.StartActivity";
        try {
            mDevice.executeShellCommand(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
