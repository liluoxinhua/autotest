package com.example.cherrylixh.ottatvtest.HttpInterface;

import android.util.Log;

import com.example.cherrylixh.ottatvtest.BaseCase;
import com.example.cherrylixh.ottatvtest.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CompetitionInfo {
    public String CompetitionListData;
    HttpUtil test=new HttpUtil();
    public ArrayList<String[]> competitionIdAndName=new ArrayList<>();
    public CompetitionInfo(){
        CompetitionListData=getCompetitionListInfo();
    }
    //全部赛事接口
    public String getCompetitionListInfo(){
        String CompetitionListUrl="http://api.shp.cdn.cp61.ott.cibntv.net/v1/navigation/recommend/PP_COMPETITION_LIST?cid=230001&appversion="+BaseCase.version+"&appid=pptv.atv.sports&appplt=atv";
        return test.doGet(CompetitionListUrl);
    }
    //获取赛事id与名字的映射
    public ArrayList<String[]> getPageIdAndName() throws JSONException {
     ArrayList<String[]> competitionIdAndName=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(CompetitionListData);
        JSONObject subJsonObject=jsonObject.getJSONObject("data");
        JSONArray jsonArray=subJsonObject.getJSONArray("list_block_element");
        for(int i=0;i<jsonArray.length();i++){
         JSONObject subJsonObject2=(JSONObject)jsonArray.get(i);
         String[] tmpData=new String[2];
         //获取赛事名称
         tmpData[0]=subJsonObject2.getString("element_title");
         JSONObject subJsonObject3=(JSONObject)subJsonObject2.getJSONObject("link_package");
         JSONObject subJsonObject4=(JSONObject)subJsonObject3.getJSONObject("action_para");
         //获取赛事id
         tmpData[1]=subJsonObject4.getString("pptv_competition_id").trim();
         competitionIdAndName.add(tmpData);
        }
        return competitionIdAndName;
    }
    //赛事推荐页接口
    public String getCompetitionInfo(String competitionName) throws JSONException {
     ArrayList<String[]> pageIdAndName=getPageIdAndName();
     String pageId="" ;
     for (int i=0;i<pageIdAndName.size();i++){
         String[] tmpData=pageIdAndName.get(i);
         //如果推荐位的名称和传入的赛事名称一致，则取推荐位id即赛事id
         if(competitionName.equals(tmpData[0])){
             pageId= tmpData[1];
         }
     }
     Log.i("pageId",pageId);
     String competitionUrl="http://api.shp.cdn.cp61.ott.cibntv.net/v1/navigation/page/"+pageId+"?district=%E4%B8%8A%E6%B5%B7%E5%B8%82&cid=230001&appid=pptv.atv.sports&appversion=3.2.0&appplt=atv";
     return test.doGet(competitionUrl);
    }

    //获取赛事推荐位配置的赛事赛程id,及是否使用新老接口标志,
    public String[] getSportidAndIsNew(String competitionName) throws JSONException {
        //0 返回新旧标志，1返回赛事id
        String[] competitionId=new String[2];
        //默认1表示使用新接口
        String isNew="1";
        JSONObject jsonObject=new JSONObject(getCompetitionInfo(competitionName));
        JSONObject subJsonObject=jsonObject.getJSONObject("data");
        JSONArray jsonArray=subJsonObject.getJSONArray("list_navigation_screen");
        for(int i=0;i<jsonArray.length();i++) {
            JSONObject subJsonObject1 = (JSONObject) jsonArray.get(i);
            JSONArray blockArray=subJsonObject1.getJSONArray("list_navigation_block");
            for(int j=0;j<blockArray.length();j++) {
                JSONObject subJsonObject2 = (JSONObject) blockArray.get(j);
                String blockName = subJsonObject2.getString("block_name");
                JSONArray blockElementArray = subJsonObject2.getJSONArray("list_block_element");
                int num = blockElementArray.length();
                if (num > 0) {
                    JSONObject blockElementObject = (JSONObject) blockElementArray.get(0);
                    String element_title=blockElementObject.getString("element_title");
                    String element_sub_title=blockElementObject.getString("element_sub_title");
                    String content_type = blockElementObject.getString("content_type");
                    JSONObject linkPackageObject = blockElementObject.getJSONObject("link_package");
                    if (linkPackageObject.getJSONObject("action_para").equals("null")) {
                         //
                    } else {
                        JSONObject actionParaObject = linkPackageObject.getJSONObject("action_para");
                        //如果赛事类型是636代表是赛事赛程，element_sub_title表示具体赛事，获取接口中的competition_id和sport_id
                        if (content_type.equals("636") && element_sub_title.equals(competitionName)) {
                            String pptv_sports_id = actionParaObject.optString("pptv_sports_id");
                            String competition_id = actionParaObject.optString("competition_id");
                            //如果competitionid不为空则优先去copetition_id,表示使用新接口，否则使用老接口，使用sports_id
                            if (competition_id == null || competition_id.length() == 0) {
                                isNew = "0";
                                competitionId[0] = isNew;
                                competitionId[1] = pptv_sports_id;
                            } else {
                                competitionId[0] = isNew;
                                competitionId[1] = competition_id;
                            }
                        }
                    }
                }
            }
        }
        return competitionId;
    }
    //获取赛事赛程的当前赛季id，赛制id，和当前轮次id
    public String[] getEventIds(Boolean isNew,String sportId,Integer[] indexs) throws JSONException {
//        String[] competitionId=getCompetitionId(competitionName);
//        String isNEW=competitionId[0];
//        String competitionid=competitionId[1];
       // 0 seasonid,1 formatid,2 roundid
        String[] eventIds=new String[3];
        //如果isNew为false，则取pptv_sports_id用老接口，否则用新接口
        String competitionFormatsUrl="";
        if(isNew==false){
            //使用老接口
            Log.i("Test_competitionEventTest_01","使用老接口");
            competitionFormatsUrl="http://livecenter.cp61.ott.cibntv.net/api/v1/competition?competitionid="+sportId+"&seasonid=-1&platform=atv&user_level=0&ppi=&order=1&nowpage=0&appid=pptv.atv.sports&appver="+ BaseCase.version+"&format=json&appplt=atv";
            //获取当前赛季，赛制下的所有轮次id
            eventIds=getOldCompetitionIds(competitionFormatsUrl,indexs);
        }else{
            //使用新接口
            Log.i("Test_competitionEventTest_01","使用新接口");
            competitionFormatsUrl="http://api.epglive.cdn.cp61.ott.cibntv.net/api/sport/competition/"+sportId+"?appversion="+ BaseCase.version+"&appid=pptv.atv.sports&appplt=atv&format=json&cid=230001&version=1.0";
            //获取当前赛季，赛制下的所有轮次id
            eventIds=getNewCompetitionIds(competitionFormatsUrl,indexs);
        }
      return eventIds;
    }
//获取当前轮次的所有赛事数据，包括：开始时间，主队名称，客队名称，主队得分，客队得分，解说
    public ArrayList<ArrayList<String>> getCompetitionsData(boolean isNew,String sportId, String[] ids) throws JSONException {
        ArrayList<ArrayList<String>> competitionData=new ArrayList<>();
//        String[] competitionId=getCompetitionId(competitionName);
//        String isNew=competitionId[0];
//        String competitionid=competitionId[1];
        String competitonEventUrl="";
        Log.i("competitionId",sportId);
        Log.i("赛季id：",ids[0]);
        Log.i("赛制id：",ids[1]);
        Log.i("轮次id：",ids[2]);
        //如果isNew是false表示使用老接口
        if(isNew==false){
            competitonEventUrl="http://livecenter.cp61.ott.cibntv.net/api/v1/competition?competitionid="+sportId+"&seasonid="+ids[0]+"&formatid="+ids[1]+"&roundid="+ids[2]+"&appid=pptv.atv.sports&appver="+BaseCase.version+"&format=json&appplt=atv";
            JSONObject competitionEventData=new JSONObject(test.doGet(competitonEventUrl));
            JSONArray jsonArray=competitionEventData.getJSONArray("list");
            for(int i=0;i<jsonArray.length();i++){
                ArrayList<String> eventData=new ArrayList<>();
                JSONObject jsonObject=(JSONObject) jsonArray.get(i);
                //获取开始时间
                String starttime=jsonObject.getString("starttime");
                //使用老接口，前端开始时间要延迟5分钟
                String startTime=getStartTime(starttime);
                String date=startTime.split(" ")[0].split("-")[1]+"月"+startTime.split(" ")[0].split("-")[2]+"日";
                String time=startTime.split(" ")[1].split(":")[0]+":"+startTime.split(" ")[1].split(":")[1];
                String newTime=date+" "+time;
                //获取主队名称
                String homeTeamTitle=jsonObject.getString("homeTeamTitle");
                //获取客队名称
                String guestTeamTitle=jsonObject.getString("guestTeamTitle");
                //获取比赛得分
                String scroe=jsonObject.getString("score");
                JSONArray jsonArray1=jsonObject.getJSONArray("live");
                //有流时获取解说，多路解说多条赛事
                if(jsonArray1.length()>0) {
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        ArrayList<String> tmpData=new ArrayList<>();
                        JSONObject jsonObject1 = (JSONObject) jsonArray1.get(j);
                        String commentator =jsonObject1.getString("commentator");
                        tmpData.add(newTime);
                        tmpData.add(homeTeamTitle);
                        tmpData.add(guestTeamTitle);
                        if(!scroe.equals("")){
                            //获取主队得分
                            tmpData.add(scroe.split("：")[0]);
                            //获取客队得分
                            tmpData.add(scroe.split("：")[1]);
                        }
                        if(!commentator.equals("")){
                            commentator= "解说："+commentator;
                            tmpData.add(commentator);
                        }
                        competitionData.add(tmpData);
                    }
                }else {
                    //没有流时没有解说
                    eventData.add(newTime);
                    eventData.add(homeTeamTitle);
                    eventData.add(guestTeamTitle);
                    if(!scroe.equals("")){
                        //获取主队得分
                        eventData.add(scroe.split("：")[0]);
                        //获取客队得分
                        eventData.add(scroe.split("：")[1]);
                    }
                    competitionData.add(eventData);
                }
            }

        }else{
            //使用新赛事赛程接口
            competitonEventUrl="http://api.epglive.cdn.cp61.ott.cibntv.net/api/sport/competition/"+sportId+"/events?seasonId="+ids[0]+"&formatId="+ids[1]+"&roundId="+ids[2]+"&appversion="+ BaseCase.version+"&appid=pptv.atv.sports&appplt=atv&format=json&cid=230001&version=1.0";
            JSONObject competitionEventData=new JSONObject(test.doGet(competitonEventUrl));
            JSONObject jsonObject=competitionEventData.getJSONObject("data");
            JSONArray jsonArray=jsonObject.getJSONArray("round_events");
            for(int i=0;i<jsonArray.length();i++){
                ArrayList<String> tmpData=new ArrayList<>();
                JSONObject jsonObject1=(JSONObject) jsonArray.get(i);
                JSONArray commentsArray=jsonObject1.getJSONArray("comments");
                String startTime=jsonObject1.getString("start_time");
                String startDate=startTime.split(" ")[0].split("-")[1]+"月"+startTime.split(" ")[0].split("-")[2]+"日";
                String starttime=startTime.split(" ")[1].split(":")[0]+":"+startTime.split(" ")[1].split(":")[1];
                tmpData.add(startDate+" "+starttime);
                tmpData.add(jsonObject1.getString("home_team_name"));
                tmpData.add(jsonObject1.getString("guest_team_name"));
                String homeScore=jsonObject1.getString("home_score");
                String guestScore=jsonObject1.getString("guest_score");
                if(homeScore!="null" && guestScore!="null"){
                    tmpData.add(homeScore);
                    tmpData.add(guestScore);
                }
                //获取解说
                String commentator=getCommentator(commentsArray);
                if(!commentator.equals("")) {
                    tmpData.add(commentator);
                }
                competitionData.add(tmpData);
            }
        }
        return competitionData;
    }
//将字符串日期转化为时间戳，加上5分钟，在转化为字符串日期得到新的时间
    private String getStartTime(String startTime) {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newTime1 = null;
        try {
            Long newTime=format.parse(startTime).getTime()+300000;
            newTime1=format.format(new Date(newTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newTime1;
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

    //获取老赛事赛程接口的赛季id，赛制id，轮次id，0 seasonid,1 formatid,2 roundid
    private String[] getOldCompetitionIds(String competitionFormatsUrl, Integer[] indexs) throws JSONException {
        JSONObject oldCompetitionInfo=new JSONObject(test.doGet(competitionFormatsUrl));
        JSONObject rootObject=oldCompetitionInfo.getJSONObject("root");
        JSONObject cataObject=rootObject.getJSONObject("cata");
        JSONArray jsonArray=cataObject.getJSONArray("competition");
        JSONArray seasonsArray=((JSONObject)jsonArray.get(0)).getJSONArray("season");
        //0 seasonIndex，1formatIndex，2 roundIndex
        int seasonIndex=indexs[0];
        int formatIndex=indexs[1];
        int roundIndex=indexs[2];
        String[] ids=new String[3];
        System.out.println("seasonindex="+seasonIndex+",formatindex="+formatIndex);
        for(int i=0;i<seasonsArray.length();i++){
            JSONObject seasonObject=(JSONObject)seasonsArray.get(i);
            if(i==seasonIndex){
            String seasonid=seasonObject.getString("seasonid");
            JSONArray formatsArray=seasonObject.getJSONArray("format");
            for(int j=0;j<formatsArray.length();j++) {
                JSONObject formatObject = (JSONObject) formatsArray.get(j);
                if (j == formatIndex) {
                    String formatid = formatObject.getString("formatid");
                    JSONArray roundsArray = formatObject.getJSONArray("round");
                    for (int k = 0; k < roundsArray.length(); k++) {
                        JSONObject roundObject = (JSONObject) roundsArray.get(k);
                        if(k==roundIndex) {
                            String roundid = roundObject.getString("roundid");
                            ids[0] = seasonid;
                            ids[1] = formatid;
                            ids[2] = roundid;
                        }
                    }
                }
            }
            }
        }
        return ids;
    }
    //获取新赛事赛程接口的赛季id，赛制id，轮次id
    private String[] getNewCompetitionIds(String competitionFormatsUrl, Integer[] indexs) throws JSONException {
        JSONObject newCompetitionInfo=new JSONObject(test.doGet(competitionFormatsUrl));
        JSONObject jsonObject=newCompetitionInfo.getJSONObject("data");
        JSONObject jsonObject1=jsonObject.getJSONObject("competition");
        JSONArray jsonArray=jsonObject1.getJSONArray("season");
        //0 seasonIndex，1formatIndex，2 roundIndex
        int seasonIndex=indexs[0];
        int formatIndex=indexs[1];
        int roundIndex=indexs[2];
        String[] ids=new String[3];
       for(int i=0;i<jsonArray.length();i++){
            JSONObject seasonObject=jsonArray.getJSONObject(i);
           if(i==seasonIndex){
            String seasonid=seasonObject.getString("season_id");
            JSONArray formatsArray=seasonObject.getJSONArray("format");
           for(int j=0;j<formatsArray.length();j++) {
               JSONObject formatObject =formatsArray.getJSONObject(j);
               if (j == formatIndex) {
                    String formatid = formatObject.getString("format_id");
                    JSONArray roundsArray = formatObject.getJSONArray("round");
                    for (int k = 0; k < roundsArray.length(); k++) {
                        JSONObject roundObject = (JSONObject) roundsArray.get(k);
                        if(k==roundIndex){
                            String roundid=roundObject.getString("round_id");
                            ids[0]=seasonid;
                            ids[1]=formatid;
                            ids[2] =roundid ;
                        }
                    }
                }
            }
            }
       }
        return ids;
    }

    //获取当前轮次的赛季id，赛制id，轮次id
public String[] getIds(boolean isNew,String sportId) throws JSONException {
//        String[] competitionIdAndIsnew=getCompetitionId(competitionName);
//        String isNew=competitionIdAndIsnew[0];
//        String competitionid=competitionIdAndIsnew[1];
        String competitionFormatsUrl="";
        String[] ids=new String[3];
        if(isNew==false){
            //使用老接口
            Log.i("Test_competitionEventTest_01","使用老接口");
            competitionFormatsUrl="http://livecenter.cp61.ott.cibntv.net/api/v1/competition?competitionid="+sportId+"&seasonid=-1&platform=atv&user_level=0&ppi=&order=1&nowpage=0&appid=pptv.atv.sports&appver="+ BaseCase.version+"&format=json&appplt=atv";
            //获取当前赛季id，赛制id，当前轮次id
            JSONObject oldCompetitionInfo=new JSONObject(test.doGet(competitionFormatsUrl));
            JSONObject rootObject=oldCompetitionInfo.getJSONObject("root");
            JSONObject cataObject=rootObject.getJSONObject("cata");
            JSONArray jsonArray=cataObject.getJSONArray("competition");
            JSONArray seasonsArray=((JSONObject)jsonArray.get(0)).getJSONArray("season");
            Res:for(int i=0;i<seasonsArray.length();i++){
                JSONObject seasonObject=(JSONObject)seasonsArray.get(i);
                    String seasonid=seasonObject.getString("seasonid");
                    JSONArray formatsArray=seasonObject.getJSONArray("format");
                    for(int j=0;j<formatsArray.length();j++) {
                        JSONObject formatObject = (JSONObject) formatsArray.get(j);
                            String formatid = formatObject.getString("formatid");
                            JSONArray roundsArray = formatObject.getJSONArray("round");
                            for (int k = 0; k < roundsArray.length(); k++) {
                                JSONObject roundObject = (JSONObject) roundsArray.get(k);
                                String roundid= roundObject.getString("roundid");
                                String nowRound=roundObject.getString("nowRound");
                                if(nowRound.equals("1")){
                                 ids[0]=seasonid;
                                 ids[1]=formatid;
                                 ids[2]=roundid;
                                break Res;//获取第一个nowround为1的id，后台接口可能返回多个nowround均为1
                                }
                            }
                        }
                    }
        }else{
            //使用新接口
            Log.i("Test_competitionEventTest_01","使用新接口");
            competitionFormatsUrl="http://api.epglive.cdn.cp61.ott.cibntv.net/api/sport/competition/"+sportId+"?appversion="+ BaseCase.version+"&appid=pptv.atv.sports&appplt=atv&format=json&cid=230001&version=1.0";
            //获取当前赛季id，赛制id，当前轮次id
            JSONObject newCompetitionInfo=new JSONObject(test.doGet(competitionFormatsUrl));
            JSONObject jsonObject=newCompetitionInfo.getJSONObject("data");
            JSONObject jsonObject1=jsonObject.getJSONObject("competition");
            JSONArray jsonArray=jsonObject1.getJSONArray("season");
           Res: for(int i=0;i<jsonArray.length();i++){
                JSONObject seasonObject=jsonArray.getJSONObject(i);
                    String seasonid=seasonObject.getString("season_id");
                    JSONArray formatsArray=seasonObject.getJSONArray("format");
                    for(int j=0;j<formatsArray.length();j++) {
                        JSONObject formatObject =formatsArray.getJSONObject(j);
                            String formatid = formatObject.getString("format_id");
                            JSONArray roundsArray = formatObject.getJSONArray("round");
                            for (int k = 0; k < roundsArray.length(); k++) {
                                JSONObject roundObject = (JSONObject) roundsArray.get(k);
                                String roundid = roundObject.getString("round_id");
                                String nowRound=roundObject.getString("now_round");
                                if(nowRound.equals("1")){
                                    ids[0]=seasonid;
                                    ids[1]=formatid;
                                    ids[2]=roundid;
                                    break Res;//获取第一个nowround为1的id，后台接口可能返回多个nowround均为1
                                }
                            }
                        }
                    }
        }
        return ids;
}
}
