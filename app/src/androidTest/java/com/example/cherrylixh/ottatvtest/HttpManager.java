package com.example.cherrylixh.ottatvtest;

import com.example.cherrylixh.ottatvtest.HttpInterface.CompetitionInfo;
import com.example.cherrylixh.ottatvtest.HttpInterface.SchedulesData;
import com.example.cherrylixh.ottatvtest.Pages.ScheduleDataPage;

public class HttpManager {
    public SchedulesData schedulesData;
    public CompetitionInfo competitionInfo;
    public SchedulesData getSchedulesData() {
        if(schedulesData==null){
            schedulesData=new SchedulesData();
        }
        return schedulesData;
    }

    public CompetitionInfo getcompetitionEventData() {
        if(competitionInfo==null){
            competitionInfo=new CompetitionInfo();

        }
        return competitionInfo;
    }
}
