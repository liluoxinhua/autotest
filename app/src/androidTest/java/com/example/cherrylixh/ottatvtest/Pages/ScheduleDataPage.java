
package com.example.cherrylixh.ottatvtest.Pages;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.example.cherrylixh.ottatvtest.BaseCase;
import com.example.cherrylixh.ottatvtest.uiHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDataPage extends BaseCase {
    //在首页获取进入全部赛程的入口按钮
    public UiObject2 getEnterSchedule(){
      return uiHelper.getUiObject2ByResourceID(packageName+":id/all_schedule_item");
    }
    //获取焦点所在的赛程的日期
    public String getFocusDate() throws ParseException {
        UiObject focusBg=uiHelper.getUiObjectByResourceID(packageName+":id/base_item_focus_bg");
        String dateText="";
        try {
            UiObject dateContent=focusBg.getFromParent(new UiSelector().resourceId(packageName+":id/base_item_txt"));
            dateText=dateContent.getText().trim();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        SimpleDateFormat f1=new SimpleDateFormat("yyyyMMdd");
        //如果落焦在今天，将今天转化为日期字符串
        if (dateText.equals("今天")){
            dateText=f1.format(new Date());
        }else{
            int year= Calendar.getInstance().get(Calendar.YEAR);
            dateText=dateText.split(" ")[0].replaceAll("月","-").replaceAll("日","");
            String month=dateText.split("-")[0];
            if(month.length()==1){
                month="0"+month;
            }
            String day=dateText.split("-")[1];
            dateText=f1.format(f1.parse(year+month+day));
        }
        return dateText;
    }
    //如果所在焦点的日期没有数据，则获取提示
   public UiObject getNotes(){
        return uiHelper.getuiObjectByText("今日暂无赛事");
    }

    //所在焦点的日期有数据，则获取：比赛时间，比赛名称，标题,主队名称，客队名称，主队得分，客队得分,解说
    public ArrayList<String> getFocusDatas(){
        ArrayList<String> focusContent=new ArrayList<>();
        UiObject2 focus=uiHelper.getUiObject2ByResourceID(packageName+":id/focus_view");
        UiObject2 containerObject=null;
        try {
            containerObject=focus.getParent().findObject(By.res(packageName+":id/competition_container"));
            //获取焦点所在赛事的比赛时间，3.2之后的版本去除延迟5分钟，和接口保持一致；3.2之前的版本前端延迟5分钟
            focusContent.add(containerObject.findObject(By.res(packageName + ":id/tv_time")).getText());
            //获取焦点所在赛事的比赛名称
            focusContent.add(containerObject.findObject(By.res(packageName+":id/tv_category")).getText());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }catch (StaleObjectException e){
            containerObject=focus.getParent();
        }
        try{
            //获取焦点所在赛事的标题
            focusContent.add(containerObject.findObject(By.res(packageName + ":id/tv_gametitle")).getText());
        } catch (NullPointerException e) {
            //do nothing
        }
        try{
            //获取焦点所在赛事的主队名称
            focusContent.add(containerObject.findObject(By.res(packageName + ":id/tv_hometeam_name")).getText());
            //获取焦点所在赛事的客队名称
            focusContent.add(containerObject.findObject(By.res(packageName+":id/tv_gustteam_name")).getText());
        } catch (NullPointerException e) {
            //do nothing
        }
        try{
            //获取焦点所在赛事的主队比分
            focusContent.add(containerObject.findObject(By.res(packageName + ":id/tv_hometeam_score")).getText());
            //获取焦点所在赛事的客队比分
            focusContent.add(containerObject.findObject(By.res(packageName+":id/tv_guestteam_score")).getText());
        } catch (NullPointerException e) {
            //do nothing
        }
        try{
            //获取焦点所在赛事的解说名字
            focusContent.add(containerObject.findObject(By.res(packageName + ":id/tv_commentator_textView")).getText());
        } catch (NullPointerException e) {
            //do nothing
        }
        try{
            //获取焦点所在赛事的解说名字1
            focusContent.add(containerObject.findObject(By.res(packageName + ":id/tv_commentator_textView1")).getText());
        } catch (NullPointerException e) {
            //do nothing
        }
        return focusContent;
    }

}

