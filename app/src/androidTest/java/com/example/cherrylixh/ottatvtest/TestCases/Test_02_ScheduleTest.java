package com.example.cherrylixh.ottatvtest.TestCases;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.example.cherrylixh.ottatvtest.BaseCase;
import com.example.cherrylixh.ottatvtest.Commentation;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.text.ParseException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_02_ScheduleTest extends BaseCase{
   // CommonFunction commonFunction=new CommonFunction();
    Commentation commentation=new Commentation();
    final String  TAG=this.getClass().getName();
    public ArrayList<ArrayList<String>> getTestSchedule(String eventDate) throws ParseException {
        ArrayList<ArrayList<String>> scheduleInfo=new ArrayList<>();
        try {
            scheduleInfo=hm.getSchedulesData().getSchedulesInfo(eventDate);
        } catch (JSONException e) {
            Log.i(TAG,"全部赛程接口获取失败......");
            e.printStackTrace();
        }
        return scheduleInfo;
    }
    @Test
    public  void  test_ScheduleTest_01() throws ParseException, JSONException, UiObjectNotFoundException {
        Log.v(TAG, "开始测试全部赛程页");
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Boolean eventIsOk = true;
        int errorId = 0;
       // 从首页点击全部赛程进入赛程页,当焦点不在赛程时需要点击两次
        try {
            UiObject2 enterSchedule = pm.getScheduleDataPage().getEnterSchedule();
            if (enterSchedule.isFocused()) {
                mDevice.pressEnter();
            } else {
                enterSchedule.clickAndWait(Until.newWindow(), 5000);
                mDevice.pressEnter();//点击一次不生效
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "全部赛程按钮没有找到.....");
            //commonFunction.TakeScreen(methodName);
            commentation.takeScreenShot(methodName);
            eventIsOk = false;
        }
        sleep(5000);
        //落焦在今天的赛程，向上移动到到第一个赛程
        for (int i = 0; i < 2; i++) {
            mDevice.pressDPadDown();
            sleep(500);
        }
        sleep(1000);
        try {
     //   for (int n = 0; n < 14; n++) {
                String eventDate = pm.getScheduleDataPage().getFocusDate();
                Log.i("eventdate",eventDate);
                //根据接口查看当天赛事数量
                ArrayList<ArrayList<String>> exceptData = getTestSchedule(eventDate);;
                ArrayList<ArrayList<String>> actualData = new ArrayList<>();
                mDevice.pressDPadRight();
                if (exceptData.size()==0) {
                    //如果赛事数量为空，则提示该赛程表无数据
                    Log.i("testSchedule", "今日暂无赛事");
                    try {
                        assertEquals(true, pm.getScheduleDataPage().getNotes().exists());
                    } catch (AssertionError e) {
                      //  commonFunction.TakeScreen(eventDate);
                        commentation.takeScreenShot(methodName);
                        eventIsOk = false;
                    }
                } else {
                    //如果赛事数量不为空，则从接口获取当天赛程表数据
                    if (exceptData.size() == 1) {
                        //获取单个赛事的数量
                        int exceptNum = exceptData.get(0).size();
                        //获取落焦所在的数据
                        ArrayList<String> focusContent =pm.getScheduleDataPage().getFocusDatas();
                        actualData.add(focusContent);
                    } else {
                        for (int i = 0; i < exceptData.size() - 1; i++) {
                            ArrayList<String> focusContent = new ArrayList<>();
                            //获取落焦赛事的数量
                            int exceptNum = exceptData.get(i).size();
                            //获取焦点所在行的数据
                            focusContent = pm.getScheduleDataPage().getFocusDatas();
                            Log.v("exceptdata", exceptData.get(i).toString());
                            Log.v("focusdata", focusContent.toString());
                            actualData.add(focusContent);
                            sleep(500);
                            mDevice.pressDPadDown();
                            sleep(500);
                        }
                        //判断是否是最后一个数据
                        int exceptNum = exceptData.get(exceptData.size() - 1).size();
                        ArrayList<String> lastFocusContent = pm.getScheduleDataPage().getFocusDatas();
                        Log.v("exceptdata", exceptData.get(exceptData.size() - 1).toString());
                        Log.v("focusdata", lastFocusContent.toString());
                        actualData.add(lastFocusContent);
                        sleep(500);
                       //断言接口和页面两个集合的数量是否相等
                        Log.i("excepSize", String.valueOf(exceptData.size()));
                        Log.i("actualSize", String.valueOf(actualData.size()));
                        assertEquals(eventDate+"赛程数据不正确",exceptData.size(),actualData.size());
                        //如果数量相等，在判断页面的数据是否全部在接口的数据集合中，如果全部在，则断言两个集合的元素是相等的
                        for(int j=0;j<actualData.size();j++){
                            try {
                                Log.v("exceptdata", exceptData.get(j).toString());
                                Log.v("focusdata", actualData.get(j).toString());
                                assertEquals("赛程表数据不正确" + eventDate + "第" + j + "行", true,exceptData.contains(actualData.get(j)));
                            } catch (AssertionError e) {
                                Log.e("赛程表数据不正确", eventDate + "第" + j + "行");
                              //  commonFunction.TakeScreen(eventDate + "i");
                                commentation.takeScreenShot(methodName);
                                eventIsOk = false;
                                errorId++;
                            }
                        }
                    }
                    //  sleep(500);

                }
            mDevice.pressDPadLeft();
            mDevice.pressDPadDown();
      //   }
        } catch (ParseException e) {
            Log.i(methodName, "全部赛事赛程页面加载失败...");
           // commonFunction.TakeScreen(methodName);
            commentation.takeScreenShot(methodName);
            eventIsOk = false;
       }
        assertEquals("全部赛程数据不正确,错误数:" + errorId, true, eventIsOk);

    }

}