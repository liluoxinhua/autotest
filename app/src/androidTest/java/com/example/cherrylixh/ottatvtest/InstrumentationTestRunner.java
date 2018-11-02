package com.example.cherrylixh.ottatvtest;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class InstrumentationTestRunner extends android.support.test.runner.AndroidJUnitRunner {
    private Writer mWriter;
    private XmlSerializer mTestSuiteSerializer;
    private Long mTestStarted;
    private static final String XML_FILE="Test.xml";
    private static final int REPORT_VALUE_RESULT_START=1;
    private static final int REPORT_VALUE_PRESULT_ERROR=-1;
    private static final int REPORT_VALUE_RESULT_FAILURE=-2;
    private static final int REPORT_VALUE_RESULT_OK=0;
    @Override
    public void onStart(){
        try {
        File fileRobo=new File(getTestResultDir(getTargetContext()));
        if(!fileRobo.exists()){
            fileRobo.mkdir();
        }
        if(isSDCardAvaliable()){
            File resultFile=new File(getTestResultDir(getTargetContext()),XML_FILE);
            startJunitOutput(new FileWriter(resultFile));
            } else{
            startJunitOutput(new FileWriter(new File(getTargetContext().getFilesDir(),XML_FILE)));
        }
        }catch (IOException e) {
            e.printStackTrace();
        }
        super.onStart();
    }
//判断SDcord是否存在
    private boolean isSDCardAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    /*
     * 获取测试结果报告文件所在的路径
     * @param context 被测工程的context
     * @return  返回测试结果报告文件所在的路径
     */
    private String getTestResultDir(Context context) {
        String packageName="/OttAtv";
        String filepath=context.getCacheDir().getPath()+packageName;
        if(Build.VERSION.SDK_INT<8){
            if(isSDCardAvaliable()){
                filepath=Environment.getExternalStorageDirectory().getAbsolutePath()+packageName;
            }
        }else{
            if(isSDCardAvaliable()){
                filepath=Environment.getExternalStorageDirectory().getAbsolutePath()+packageName;
            }
        }
        return filepath;
    }

    private void startJunitOutput(Writer writer) {
        try {
        mWriter=writer;
        mTestSuiteSerializer=newSerializer(this.mWriter);
        mTestSuiteSerializer.startDocument(null,null);
      //  mTestSuiteSerializer.startTag(null,"testsuites");
        mTestSuiteSerializer.startTag(null,"testsuite");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private XmlSerializer newSerializer(Writer mWriter) {
        XmlSerializer serializer = null;
        try {
            XmlPullParserFactory pf = XmlPullParserFactory.newInstance();
            serializer = pf.newSerializer();
            serializer.setOutput(mWriter);
          //  serializer.setOutput(mWriter,"UTF-8");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializer;
    }
    @Override
    public void sendStatus(int resultCode,Bundle results){
        super.sendStatus(resultCode,results);
        switch(resultCode){
            case REPORT_VALUE_PRESULT_ERROR:
            case REPORT_VALUE_RESULT_FAILURE:
            case REPORT_VALUE_RESULT_OK:
                recordTestResult(resultCode,results);
                break;
            case REPORT_VALUE_RESULT_START:
                recordTestStart(results);
            default:
                break;
        }

}

    private void recordTestStart(Bundle results) {
        mTestStarted=System.currentTimeMillis();
    }

    private void recordTestResult(int resultCode, Bundle results) {
        float time=(System.currentTimeMillis()-mTestStarted)/1000.0F;
        String className=results.getString("class");
        String testMethod=results.getString("test");
        String stack=results.getString("stack");
        int current=results.getInt("current");
        int total=results.getInt("numtests");
        try {
            mTestSuiteSerializer.startTag(null,"testcase");
            mTestSuiteSerializer.attribute(null,"ID",current+"");
            mTestSuiteSerializer.attribute(null,"classname",className);
            mTestSuiteSerializer.attribute(null,"casename",testMethod);
            mTestSuiteSerializer.attribute(null,"time",String.format("%.3f",time));
            if(resultCode!=REPORT_VALUE_RESULT_OK){
                mTestSuiteSerializer.startTag(null,"failure");
                if(stack!=null){
                    String reson=stack.substring(0,stack.indexOf("\n"));
                    String message="";
                    int index=reson.indexOf(":");
                    if(index>-1){
                        message=reson.substring(0);
                        //message=stack;
                        reson=reson.substring(0,index);
                    }
                    mTestSuiteSerializer.attribute(null,"message",message);
                    mTestSuiteSerializer.attribute(null,"type",reson);
                    mTestSuiteSerializer.text(stack);
                   // mTestSuiteSerializer.attribute(null,"stack",stack);
                }
                mTestSuiteSerializer.endTag(null,"failure");
            }else{
                mTestSuiteSerializer.startTag(null,"result");
                mTestSuiteSerializer.attribute(null,"message","pass");
                mTestSuiteSerializer.text("pass");
                mTestSuiteSerializer.endTag(null,"result");
            }
            mTestSuiteSerializer.endTag(null,"testcase");
            if(current==total){
                mTestSuiteSerializer.startTag(null,"system-out");
                mTestSuiteSerializer.endTag(null,"system-out");
                mTestSuiteSerializer.startTag(null,"system-err");
                mTestSuiteSerializer.endTag(null,"system-err");
                mTestSuiteSerializer.endTag(null,"testsuite");
                mTestSuiteSerializer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void finish(int resultCode,Bundle results){
        endTestSuites();
        super.finish(resultCode,results);
}

    private void endTestSuites() {
        try {
           // mTestSuiteSerializer.endTag(null,"testsuites");
            mTestSuiteSerializer.endDocument();
            mTestSuiteSerializer.flush();
            mWriter.flush();
            mWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
