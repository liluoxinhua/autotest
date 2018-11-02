package com.example.cherrylixh.ottatvtest.TestCases;

import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.example.cherrylixh.ottatvtest.BaseCase;
import com.example.cherrylixh.ottatvtest.Commentation;
import org.json.JSONException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;


//@RunWith(AndroidJUnit4.class)
////@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_10_CompetitionEventTest extends BaseCase {
  //  CommonFunction commonFunction=new CommonFunction();
    Commentation commentation=new Commentation();
    final String  TAG=this.getClass().getName();
  //  @Test //进入赛事赛程页测试当前轮次的赛事数据是否正确
  // 需要传入参数：isNew,true表示使用新接口，false使用老接口，sportid，推荐位配置的赛事id，competition_id不为空去该值，否则取pptv_sports_id，如亚冠：isNew=false,sportId=210299
    public void Test_CompetitionEventTest_01(boolean isNew,String sportId) throws JSONException {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        Log.i(methodName, "开始测试.....");
        //点击赛事赛程入口进入赛事赛程页
        UiObject2 scheduleItem=null;
        try {
           scheduleItem = pm.getCompetitionEventPage().getAllSheduleItem();
        }catch (NullPointerException e){
            commentation.takeScreenShot(methodName);
            Log.i(TAG,"赛事赛程按钮找不到.....");
        }
        if(scheduleItem.isFocused()){
            mDevice.pressEnter();
        }else {
            scheduleItem.clickAndWait(Until.newWindow(), 2000);
            mDevice.pressEnter();
        }
        sleep(2000);
        //进入赛事赛程页，获取赛事名称
        String competitionName="";
        try {
            competitionName=pm.getCompetitionEventPage().getItemName();
        }catch (UiObjectNotFoundException e){
            commentation.takeScreenShot(methodName);
            Log.i(TAG,"赛事赛程页面加载失败......");
        }
        Log.i("获取赛事名称", competitionName);
        //获取赛事当前轮次的赛季id，赛制id，轮次id
        String[] ids = new String[3];
        try {
            ids=hm.getcompetitionEventData().getIds(isNew,sportId);
        }catch (JSONException e){
            Log.i(TAG,competitionName+"赛事赛程接口获取失败.....");
            e.printStackTrace();
        }
        //获取赛事赛程页当前轮次的接口数据
        ArrayList<ArrayList<String>> exceptEventData = new ArrayList<>();
        try {
            exceptEventData = hm.getcompetitionEventData().getCompetitionsData(isNew, sportId, ids);
        } catch (JSONException e) {
            Log.i(TAG, sportId + "赛事赛程接口获取失败......");
            e.printStackTrace();
        }
        int errorId = 0;
        Boolean eventIsOk = true;
        if (exceptEventData.size() == 0) {
            //如果当前轮次没有数据，进行判断和断言
            Log.i(TAG, sportId + "当前轮次无数据");
            try {
                assertEquals(true, pm.getCompetitionEventPage().getNotes().exists());
            } catch (AssertionError e1) {
                commentation.takeScreenShot(sportId + methodName);
                eventIsOk = false;
            }
        } else {
            mDevice.pressDPadRight();
            //有数据时，获取落焦所在行的数据与实际接口返回数据断言
            for (int i = 0; i < exceptEventData.size() - 1; i++) {
                ArrayList<String> exceptDta = exceptEventData.get(i);
                ArrayList<String> focusData = pm.getCompetitionEventPage().getFocusContent();
                Log.i("exceptData", exceptDta.toString());
                Log.i("focusData", focusData.toString());
                try {
                    assertEquals(sportId + "赛事数据不正确" + "第" + i + "行", exceptDta, focusData);
                } catch (AssertionError e2) {
                    commentation.takeScreenShot(sportId + methodName + i);
                    eventIsOk = false;
                    errorId++;
                }
                mDevice.pressDPadDown();
                sleep(500);
            }
            //判断赛事当前轮次数量与接口数量一致
            ArrayList<String> exceptLastData = exceptEventData.get(exceptEventData.size() - 1);
            ArrayList<String> focusLastData = pm.getCompetitionEventPage().getFocusContent();
            try {
                assertEquals(sportId + "赛事数量与实际不一致", exceptLastData, focusLastData);
            } catch (AssertionError e3) {
                commentation.takeScreenShot(sportId + methodName);
                eventIsOk = false;
                errorId++;
            }
        }
        assertEquals("赛事赛程数据不正确,错误数" + errorId, true, eventIsOk);
    }
  //  @Test //在赛事赛程页面，按菜单键随机选取赛季，赛制，轮次，测试数据是否正确,
  // 需要传入参数：isNew,true表示使用新接口，false使用老接口，sportid，推荐位配置的赛事id，competition_id不为空去该值，否则取pptv_sports_id，如亚冠：isNew=false,sportId=210299，中超=210329
    public void Test_competitionEventTest_02(boolean isNew,String sportId){
        //测试前提是在赛赛赛程页面
        String methodName=Thread.currentThread().getStackTrace()[2].getMethodName();
        Log.i(methodName,"开始测试.....");
        //按菜单键，随机选择一个赛季和赛制，选择随机选择一个轮次的数据是否正确
        mDevice.pressKeyCode(82);
        sleep(3000);
        Integer[] indexs=pm.getCompetitionEventPage().getIndexs();
        Log.i("indexs", String.valueOf(indexs[0]));
        Log.i("indexs", String.valueOf(indexs[1]));
        Log.i("indexs", String.valueOf(indexs[2]));
        //获取赛事名称
        String competitionName="";
        try {
            competitionName=pm.getCompetitionEventPage().getItemName();
        }catch (UiObjectNotFoundException e){
            commentation.takeScreenShot(TAG);
            Log.i(TAG,"赛事赛程页面加载失败......");
        }
        //获取菜单随机帅选后的赛季id，赛制id，轮次id
        String[] competitionIds=new String[3];
        try {
            competitionIds=hm.getcompetitionEventData().getEventIds(isNew,sportId,indexs);
        } catch (JSONException e) {
            Log.i(TAG,competitionName+"赛事赛程接口获取失败.....");
            e.printStackTrace();
        }
        //获取赛事赛程页当前轮次的接口数据
        ArrayList<ArrayList<String>> exceptEventData = new ArrayList<>();
        try {
            exceptEventData = hm.getcompetitionEventData().getCompetitionsData(isNew, sportId, competitionIds);
        } catch (JSONException e) {
            Log.i(TAG, sportId + "赛事赛程接口获取失败......");
            e.printStackTrace();
        }
        int errorId = 0;
        Boolean eventIsOk = true;
        if (exceptEventData.size() == 0) {
            //如果当前轮次没有数据，进行判断和断言
            Log.i(TAG, sportId + "当前轮次无数据");
            try {
                assertEquals(true, pm.getCompetitionEventPage().getNotes().exists());
            } catch (AssertionError e1) {
                commentation.takeScreenShot(sportId + methodName);
                eventIsOk = false;
            }
        } else {
            mDevice.pressDPadRight();
            sleep(500);
            //有数据时，获取落焦所在行的数据与实际接口返回数据断言
            for (int i = 0; i < exceptEventData.size() - 1; i++) {
                ArrayList<String> exceptDta = exceptEventData.get(i);
                ArrayList<String> focusData = pm.getCompetitionEventPage().getFocusContent();
                Log.i("exceptData", exceptDta.toString());
                Log.i("focusData", focusData.toString());
                try {
                    assertEquals(sportId + "赛事数据不正确" + "第" + i + "行", exceptDta, focusData);
                } catch (AssertionError e2) {
                    commentation.takeScreenShot(sportId + methodName + i);
                    eventIsOk = false;
                    errorId++;
                }
                mDevice.pressDPadDown();
                sleep(300);
            }
            //判断赛事当前轮次数量与接口数量一致
            ArrayList<String> exceptLastData = exceptEventData.get(exceptEventData.size() - 1);
            ArrayList<String> focusLastData = pm.getCompetitionEventPage().getFocusContent();
            try {
                assertEquals(sportId + "赛事数量与实际不一致", exceptLastData, focusLastData);
            } catch (AssertionError e3) {
                commentation.takeScreenShot(sportId + methodName);
                eventIsOk = false;
                errorId++;

            }
        }
        assertEquals("赛事赛程数据不正确,错误数" + errorId, true, eventIsOk);
    }
}
