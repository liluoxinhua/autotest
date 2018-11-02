package com.example.cherrylixh.ottatvtest;

import android.os.Environment;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;



public class ExtentUtils extends TestWatcher{
    private static final String OUTPUT_FLODER= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"OttAtv/";
    private static final String FILE_NAME="index.html";
    private ExtentReports extent;
    public ExtentUtils(ExtentReports extent) {
        this.extent=extent;
    }
    @Override
    protected void succeeded(Description description){
        ExtentTest test=extent.startTest(description.getDisplayName(),"-");
        test.log(LogStatus.PASS,"-");
        flushedReports(extent,test);
    }

    @Override
    protected void failed(Throwable e,Description description){
        ExtentTest test=extent.startTest(description.getDisplayName(),"Test failed");
        //step log
        test.log(LogStatus.FAIL,e);
        flushedReports(extent,test);
    }
    private void flushedReports(ExtentReports extent, ExtentTest test) {
        //ending test
        extent.endTest(test);
        //writing everything to ducument
        extent.flush();
    }
    private void init(){
        File reportDir=new File(OUTPUT_FLODER);
        if(!reportDir.exists()&&!reportDir.isDirectory()){
            reportDir.mkdirs();
        }
//        ExtentHtmlReporter htmlReporter=new ExtentHtmlReporter(OUTPUT_FLODER+FILE_NAME);
//        htmlReporter.config().setDocumentTitle("api uiautomator test report");
//        htmlReporter.config().setReportName("API uiautomator test report");
//        htmlReporter.config().setChartVisibilityOnOpen(true);
//        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
//        //htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);
//        htmlReporter.config().setCSS(".node.level-1  ul{ display:none;} .node.level-1.active ul{display:block;}");
//        extent=new ExtentReports();
//        extent.attachReporter(htmlReporter);
//        extent.setReportUsesManualConfiguration(true);
    }
}
