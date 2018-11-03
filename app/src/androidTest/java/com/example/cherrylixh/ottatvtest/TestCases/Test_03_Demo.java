package com.example.cherrylixh.ottatvtest.TestCases;

import android.support.test.runner.AndroidJUnit4;

import com.example.cherrylixh.ottatvtest.BaseCase;

import org.json.JSONException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_03_Demo extends BaseCase {
    @Test
    public void test_01() {
        boolean isnew = false;
        String sportid = "210329";
        //中超
        sleep(5000);
        try {
            new Test_10_CompetitionEventTest().Test_CompetitionEventTest_01(isnew,sportid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test_02() {
        boolean isnew = false;
        String sportid = "210329";
        //中超
        sleep(1000);
        new Test_10_CompetitionEventTest().Test_competitionEventTest_02(isnew,sportid);

    }
}
