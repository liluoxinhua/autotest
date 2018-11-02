package com.example.cherrylixh.ottatvtest.Pages;

import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.example.cherrylixh.ottatvtest.BaseCase;
import com.example.cherrylixh.ottatvtest.uiHelper;

import java.util.ArrayList;
import java.util.Random;

public class CompetitionEventPage extends BaseCase {
    //获取赛事赛程的入口按钮
    public UiObject2 getAllSheduleItem() throws NullPointerException{
      UiObject2 text= uiHelper.getUiObeject2ByResourceIdAndTextContains(packageName+":id/item_schedule_entrance","赛程表");
      UiObject2 uiObject1=text.getParent().getParent().getParent();
      return uiObject1;
    }
    //获取赛事名称
    public String getItemName() throws UiObjectNotFoundException{
        String specificItemText="";
        try {
            specificItemText= uiHelper.getUiObjectByResourceID(packageName+":id/specific_item_title").getText();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        return specificItemText;
    }

    //如果所在焦点的日期没有数据，则获取提示
    public UiObject getNotes(){
        return uiHelper.getUiObjectByTextContains("当前无内容");
    }

    //获取落焦所在的行的数据：比赛开始时间，主队名称，客队名称，主队得分，客队得分，解说等
    public ArrayList<String> getFocusContent(){
        UiObject focusView=uiHelper.getUiObjectByResourceID(packageName+":id/focus_view");
        UiSelector tvTime=new UiSelector().resourceId(packageName+":id/tv_time");
        UiSelector homeTeatName=new UiSelector().resourceId(packageName+":id/tv_hometeam_name");
        UiSelector guestTeamName=new UiSelector().resourceId(packageName+":id/tv_gustteam_name");
        UiSelector homeTeamScore=new UiSelector().resourceId(packageName+":id/tv_hometeam_score");
        UiSelector guestTeamScore=new UiSelector().resourceId(packageName+":id/tv_guestteam_score");
        UiSelector commentator=new UiSelector().resourceId(packageName+":id/tv_commentator_textView");
        ArrayList<String> focusData=new ArrayList<>();
        UiObject container = null;
        try {
            container=focusView.getFromParent(new UiSelector().resourceId(packageName+":id/competition_container"));
            //获取比赛开始时间
            focusData.add(container.getChild(tvTime).getText());
            //获取主队名称
            focusData.add(container.getChild(homeTeatName).getText());
            //获取客队名称
            focusData.add(container.getChild(guestTeamName).getText());
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        try{
            //获取主队得分
            focusData.add(container.getChild(homeTeamScore).getText());
            //获取客队得分
            focusData.add(container.getChild(guestTeamScore).getText());
        }catch (UiObjectNotFoundException e){
            //do nothing
        }
        try{
            //获取解说，多个解说时去掉"/"
            focusData.add(container.getChild(commentator).getText().replace(" / "," "));
        }catch (UiObjectNotFoundException e){
            //do nothing
        }
        return focusData;
    }

    //获取刷选菜单，随机选择一个赛季序号，赛制序号
      public Integer[] getIndexs(){
        UiSelector fifterContainer=new UiSelector().resourceId(packageName+":id/filter_container");
        //获取赛季的父类
        UiSelector container1=fifterContainer.childSelector(new UiSelector().className("android.widget.LinearLayout").index(0)).childSelector(new UiSelector().resourceId(packageName+":id/filter_part_content"));
        //获取赛制的父类
        UiSelector container2=fifterContainer.childSelector(new UiSelector().className("android.widget.LinearLayout").index(1)).childSelector(new UiSelector().resourceId(packageName+":id/filter_part_content"));
        UiObject uiObject1=new UiObject(container1);
        UiObject uiObject2=new UiObject(container2);
        Random random=new Random();
        Integer[] indexs=new Integer[3];
          try {
              int num1=uiObject1.getChildCount();
              Log.i("num1", String.valueOf(num1));
              //获取一个随机的赛季index
              int seasonIndex=random.nextInt(num1);
              indexs[0]=seasonIndex;
              Log.i("seasonIndex", String.valueOf(seasonIndex));
              UiObject subUiobject1=new UiObject(container1.childSelector(new UiSelector().className("android.widget.FrameLayout").index(seasonIndex)));
              subUiobject1.click();
              sleep(2000);
              int num2=uiObject2.getChildCount();
              Log.i("num2", String.valueOf(num2));
              //获取一个随机的赛制index
              int formatIndex=random.nextInt(num2);
              indexs[1]=formatIndex;
              Log.i("formatIndex", String.valueOf(formatIndex));
              UiObject subUiobject2=new UiObject(container2.childSelector(new UiSelector().className("android.widget.FrameLayout").index(formatIndex)));
              subUiobject2.click();
              sleep(5000);
              //获取随机一个轮次
              //获取轮次的父类
              UiSelector container3=fifterContainer.childSelector(new UiSelector().className("android.widget.LinearLayout").index(2)).childSelector(new UiSelector().resourceId(packageName+":id/filter_part_content"));
              UiObject uiObject3=new UiObject(container3);
              int num3=uiObject3.getChildCount();
              int roundIndex=random.nextInt(num3);
              indexs[2]=roundIndex;
              Log.i("roundIndex", String.valueOf(roundIndex));
              UiObject subUiObject3=new UiObject(container3.childSelector(new UiSelector().className("android.widget.FrameLayout").index(roundIndex)));
             //页面翻屏时很难定位，通过按键下移方法定位
              if(num3>=8){
                  new UiObject(container3.childSelector(new UiSelector().className("android.widget.FrameLayout").index(0))).click();
                  mDevice.pressEnter();
                 sleep(500);
                 for(int i=0;i<roundIndex;i++){
                     mDevice.pressDPadDown();
                     sleep(500);
                 }
              }else {
                  subUiObject3.clickAndWaitForNewWindow(2000);
                  mDevice.pressEnter();
              }
              sleep(5000);
          } catch (UiObjectNotFoundException e) {
              e.printStackTrace();
          }
          return indexs;
      }
}
