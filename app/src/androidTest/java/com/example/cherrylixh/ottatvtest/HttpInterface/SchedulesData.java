package com.example.cherrylixh.ottatvtest.HttpInterface;

import com.example.cherrylixh.ottatvtest.BaseCase;
import com.example.cherrylixh.ottatvtest.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class SchedulesData extends BaseCase{
    //3.1版本的接口分析
    public String ScheduleListData;
    HttpUtil test = new HttpUtil();
    public ArrayList<String[]> schedulesInfo = new ArrayList<>();

    //获取焦点所在日期和当前日期的天数差
    public long getDtime(String eventDate) throws ParseException {
        SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
        long focusDate = f1.parse(eventDate).getTime();
        long cDate = f1.parse(f1.format(new Date())).getTime();
        long t = (focusDate - cDate) / (3600 * 24 * 1000);
        return t;
    }

    public String getScheduleListData(String eventDate) throws ParseException {
        //获取焦点所在日期去当前日期的天数差
        long dt = getDtime(eventDate);
        String scheduleListUrl = "";
        //渠道默认为非英超渠道
        String id = "77";
        //获取预加载的数据，预加载昨天，今天，明天，后天4天的数据，
        if (dt >= -1 && dt <= 2) {
            scheduleListUrl = "http://livecenter.cp61.ott.cibntv.net/api/v1/collection?id=" + id + "&format=json&competitionid=0&start=-1&end=2";
        }else {
            //获取所在焦点的赛程表接口数据，只取一天的数据
            scheduleListUrl = "http://livecenter.cp61.ott.cibntv.net/api/v1/collection?id=" + id + "&format=json&competitionid=0&start=" + dt + "&end=" + dt;
        }
        return test.doGet(scheduleListUrl);
    }

    //获取赛程表的时间，比赛名称，标题，解说，主队名称，客队名称，主队得分，客队得分，比赛状态
    public ArrayList<ArrayList<String>> getSchedulesInfo(String eventDate) throws JSONException, ParseException {
        ArrayList<ArrayList<String>> schedulesInfo = new ArrayList<>();
        String scheduleDatas = getScheduleListData(eventDate);
        JSONObject jsonObject = new JSONObject(scheduleDatas);
        JSONObject subJsonObject = jsonObject.getJSONObject("sections");
        JSONArray jsonArray=null;
        //根据焦点所在日期和接口返回日期对比，一致就取这天的数据
        Iterator eventDates = subJsonObject.keys();
        while (eventDates.hasNext()) {
            String dates = String.valueOf(eventDates.next());
            if(eventDate.equals(dates)){
                jsonArray=subJsonObject.getJSONArray(dates);
                break;
            }
        }
        if(jsonArray.length()==0){
            //当天没有数据
        }else {
            //不为空时，遍历所有数据
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=(JSONObject) jsonArray.get(i);
                ArrayList<String> tmpDatas=new ArrayList<>();
                //获取比赛时间，页面延迟5分钟
                String startTime=jsonObject1.getString("start_time");
                String newTime=getStartTime(startTime);
                String time=newTime.split(" ")[1].split(":")[0]+":"+newTime.split(" ")[1].split(":")[1];
                tmpDatas.add(time);
                //获取比赛的名称
                JSONObject catalogs=jsonObject1.getJSONObject("catalogs");
                JSONObject jsonObject2=catalogs.optJSONObject((String)catalogs.keys().next());
                String eventName=jsonObject2.getString("title");
                tmpDatas.add(eventName);
                String title=jsonObject1.getString("title");
                String homeTeamTitle=jsonObject1.getString("homeTeamTitle");
                String guestTeamTitle=jsonObject1.getString("guestTeamTitle");
                String score=jsonObject1.getString("score");
                String commentator=jsonObject1.getString("commentator");
                //获取比赛标题，如果有主队，客队名称则不显示标题
                if(homeTeamTitle.equals("") && guestTeamTitle.equals("")){
                    tmpDatas.add(title);
                }else {
                    tmpDatas.add(homeTeamTitle);
                    tmpDatas.add(guestTeamTitle);
                    //比分不为空时获取主队和客队得分
                    if(!score.equals("")){
                        String homeTeamScore=score.split("：")[0];
                        String guestTeamScore=score.split("：")[1];
                        tmpDatas.add(homeTeamScore);
                        tmpDatas.add(guestTeamScore);
                    }
                }
                //获取解说
                if(!commentator.equals("")){
                   tmpDatas.add( "解说："+commentator.replaceAll("  "," ").replaceAll(" "," / "));
                }
                schedulesInfo.add(tmpDatas);
            }
        }
        return schedulesInfo;
    }
//开始时间延迟5分钟
//将字符串日期转化为时间戳，加上5分钟，在转化为字符串日期得到新的时间
private String getStartTime(String startTime) {
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String newTime1 = null;
    int time1= Integer.parseInt(startTime.split(" ")[1].split(":")[0]);
    int time2= Integer.parseInt(startTime.split(" ")[1].split(":")[1]);
    long newTime;
    try {
        if(time1==23 && time2>=55){
            newTime=format.parse(startTime).getTime()+240000;
        }else{
           newTime=format.parse(startTime).getTime()+300000;}
        newTime1=format.format(new Date(newTime));
    } catch (ParseException e) {
        e.printStackTrace();
    }
    return newTime1;
}
////对List进行排序，得到新的值
//public static void getListSort(ArrayList<ArrayList<String>> arr){
//    Comparator<ArrayList<String>> comparator=new Comparator<ArrayList<String>>(){
//        @Override
//        public int compare(ArrayList<String> arr1, ArrayList<String> arr2) {
//            if(arr1.get(0).compareTo(arr2.get(0))<0){
//                return -1;
//            }else if(arr1.get(0).compareTo(arr2.get(0))>0){
//                return 1;
//            }else{
//                if(arr1.get(1).compareTo(arr2.get(1))<0){
//                    return -1;
//                }else if(arr1.get(1).compareTo(arr2.get(1))>0){
//                    return 1;
//                }else{
//                    if(arr1.get(2).compareTo(arr2.get(2))<0){
//                        return -1;
//                    }else if(arr1.get(2).compareTo(arr2.get(2))>0){
//                        return 1;
//                    }else {
//                        if (arr1.get(arr1.size() - 1).compareTo(arr2.get(arr2.size() - 1)) < 0) {
//                            return -1;
//                        } else {
//                            return 1;
//                        }
//                    }
//                }
//            }
//        }
//    };
//    Collections.sort(arr, comparator);
//}
    //3.2版本的接口分析
    /*
    public String ScheduleListData;
    HttpUtil test=new HttpUtil();
    public ArrayList<String[]> schedulesInfo=new ArrayList<>();
//    public SchedulesData(){
//        ScheduleListData=getScheduleListData(String eventDate);
//    }

    public String getScheduleListData(String eventDate) throws ParseException {
        //获取焦点所在日期去当前日期的天数差
        long dt=getDtime(eventDate);
        String scheduleListUrl="";
        //获取预加载的数据，预加载昨天，今天，明天，后天4天的数据
        if(dt>=-1 && dt<=2){
            scheduleListUrl="http://api.epglive.cdn.cp61.ott.cibntv.net/api/sport/competition/schedule?start=-1&end=2&pay=0&cid=230001&appid=pptv.atv.sports&appplt=atv&appversion=3.2.0&format=json&version=1.0";
        }else {
            //获取所在焦点的赛程表接口数据，只取一天的数据
            scheduleListUrl = "http://api.epglive.cdn.cp61.ott.cibntv.net/api/sport/competition/schedule?start=" + dt + "&end=" + dt + "&pay=0&cid=230001&appid=pptv.atv.sports&appplt=atv&appversion=3.2.0&format=json&version=1.0";
        }
        return test.doGet(scheduleListUrl);
    }
     //获取焦点所在日期和当前日期的天数差
    public long getDtime(String eventDate) throws ParseException {
        SimpleDateFormat f1=new SimpleDateFormat("yyyy-MM-dd");
        long focusDate=f1.parse(eventDate).getTime();
        long cDate=f1.parse(f1.format(new Date())).getTime();
        long t=(focusDate-cDate)/(3600*24*1000);
        return t;
    }

    //获取当天的赛事数量
    public  String getEventNum(String eventDate) throws JSONException, ParseException {
       //ArrayList<String[]> dataAndNum=new ArrayList<>();
       String scheduleDatas=getScheduleListData(eventDate);
       JSONObject jsonObject=new JSONObject(scheduleDatas);
       JSONObject subJsonObject=jsonObject.getJSONObject("data");
       JSONArray jsonArray=subJsonObject.getJSONArray("day_events");
        String[] eventsData=new String[2];
      //  String eventNum=((JSONObject)jsonArray.get(0)).getString("total_num");
        String eventNum="null";
        //根据焦点所在的日期和接口中获取的日期做对比，如果一致就取这天的赛事数量
       for(int i=0;i<jsonArray.length();i++){
           JSONObject dayObject=(JSONObject)jsonArray.get(i);
           if(dayObject.getString("date").equals(eventDate));
           eventNum=dayObject.getString("total_num");
       }
       return eventNum;
    }
    //获取赛程表的时间，比赛名称，标题，解说，主队名称，客队名称，主队得分，客队得分，比赛状态
    public ArrayList<ArrayList<String>> getSchedulesInfo(String eventDate) throws JSONException, ParseException {
       ArrayList<ArrayList<String>> schedulesInfo=new ArrayList<>();
        String scheduleDatas=getScheduleListData(eventDate);
        JSONObject jsonObject=new JSONObject(scheduleDatas);
        JSONObject subJsonObject=jsonObject.getJSONObject("data");
        JSONArray jsonArray=subJsonObject.getJSONArray("day_events");
      //  JSONObject dayObject=(JSONObject)jsonArray.get(0);
     //   JSONArray eventsArray = dayObject.getJSONArray("events");
        JSONArray eventsArray=null;
        //根据焦点所在日期和接口返回日期对比，一致就取这天的数据
        for(int i = 0; i<jsonArray.length(); i++) {
            JSONObject dayObject = (JSONObject) jsonArray.get(i);
            String dayDate = dayObject.getString("date");
            if (dayDate.equals(eventDate)) {
                eventsArray = dayObject.getJSONArray("events");
            }
        }
        //获取当天接口返回的数据
         if (eventsArray.length()==0){
               //没有数据
           }
           else{
            //不为空时，遍历所有数据
               for (int j=0;j<eventsArray.length();j++){
                   JSONObject eventJsonObject=(JSONObject)eventsArray.get(j);
                   JSONArray commentsArray=eventJsonObject.getJSONArray("comments");
                   ArrayList<String> tmpDatas=new ArrayList<>();
                   //比赛时间
                   tmpDatas.add(eventJsonObject.getString("time"));
                   //赛事名称
                   tmpDatas.add(eventJsonObject.getString("competition_name"));
                   //获取主队名称
                   String home_team_name=eventJsonObject.getString("home_team_name");
                   //获取客队名称
                   String guest_team_name=eventJsonObject.getString("guest_team_name");
                   //获取主队得分
                   String home_scroe=eventJsonObject.getString("home_score");
                   //获取客队得分
                   String guest_scroe=eventJsonObject.getString("guest_score");
                   //赛事标题,如果有主队和客队名称，则标题不显示
                   if(home_team_name.isEmpty() && guest_team_name.isEmpty()){
                       tmpDatas.add(eventJsonObject.getString("title"));
                   }else {
                       tmpDatas.add(home_team_name);
                       tmpDatas.add(guest_team_name);
                      if(!home_scroe.equals("null")){
                          tmpDatas.add(home_scroe);
                          tmpDatas.add(guest_scroe);
                      }
                   }
                   //获取解说
                 String commentator=getCommentator(commentsArray);
                   if(!commentator.equals("")) {
                       tmpDatas.add(commentator);
                   }
//                   if(commentator.length()>0) {
//                       tmpDatas.add(commentator);
//                   }
                   //获取开始的时间戳
                  // Long startTimeLong=eventJsonObject.getLong("start_time_long");
                   //获取结束的时间戳
                  // Long endTimeLong=eventJsonObject.getLong("end_time_long");
                   //获取状态
                 //  int vodStatus=eventJsonObject.getInt("vod_status");
                   //通过开始时间戳，结束时间戳，状态来判断比赛状态
                  // tmpDatas[8]=getStatus(startTimeLong,endTimeLong,vodStatus);
                   schedulesInfo.add(tmpDatas);
                  // Log.v("event",Arrays.toString(tmpDatas));
               }
           }

        return schedulesInfo;
    }

    //通过开始时间戳，结束时间戳，状态来判断比赛状态
    public String getStatus(Long startTimeLong, Long endTimeLong, int vodStatus) {
        String status="";
        Long currentTime=System.currentTimeMillis();
        if(currentTime>endTimeLong){
            //赛事已结束，根据vod_status的返回值判断状态，vod_status：0 无点播 1 录播  2 集锦
            if(vodStatus==0){
                status="";
            }
            else if(vodStatus==1){
                status="录播";
            }else if(vodStatus==2){
                status="集锦";
            }else{
                status="";
            }
        }
        else if(currentTime>=startTimeLong && currentTime<=endTimeLong){
            //当前赛事正在直播中,显示状态：比赛中
            status="比赛中";
        }
        else if(currentTime<startTimeLong){
            //当前赛事还未开始，显示状态：前瞻
            status="前瞻";
        }
        return status;
    }

    //根据comments中含有流的多少判断是多路流还是一路流，显示解说
    public String getCommentator(JSONArray commentsArray) throws JSONException {
        String commentatorName="";
      if(commentsArray.length()==1){
          //只有一路流
          JSONObject commentsObject=(JSONObject)commentsArray.get(0);
          String commentator=commentsObject.getString("commentator");
          if(commentator.equals("")){
              //如果没有解说，默认显示官方解说
              commentatorName="官方解说";
          }
          else{
              commentatorName="解说："+commentator;
          }
      }else if(commentsArray.length()>1)
        {//有多路流
          commentatorName="多路解说";
      }
      return commentatorName;
    }
    */
}
