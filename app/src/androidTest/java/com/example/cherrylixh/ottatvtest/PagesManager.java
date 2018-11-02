package com.example.cherrylixh.ottatvtest;

import com.example.cherrylixh.ottatvtest.Pages.CompetitionEventPage;
import com.example.cherrylixh.ottatvtest.Pages.ScheduleDataPage;

public class PagesManager {
    public ScheduleDataPage scheduleDataPage;
    public CompetitionEventPage competitionEventPage;
    public ScheduleDataPage getScheduleDataPage() {
        if(scheduleDataPage==null){
            scheduleDataPage=new ScheduleDataPage();
        }
        return scheduleDataPage;
    }

    public CompetitionEventPage getCompetitionEventPage() {
        if(competitionEventPage==null){
            competitionEventPage=new CompetitionEventPage();
        }
        return competitionEventPage;
    }
}
