package com.example.teseList;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description:
 * @date 2022/5/20 15:47
 */
@Slf4j
public class TestList {
    public static void main(String[] args) {

        LocalTime time0 = LocalTime.of(0, 0);
        LocalTime time1 = LocalTime.of(0, 30);
        LocalTime time2 = LocalTime.of(12, 0);
        LocalTime time3 = LocalTime.of(12, 30);
        LocalTime time4 = LocalTime.of(22, 0);
        LocalTime time5 = LocalTime.of(22, 30);
        LocalTime time6 = LocalTime.of(8, 45);
        LocalTime time7 = LocalTime.of(10, 20);

        ArrayList<LocalTime> target = new ArrayList<>(Arrays.asList(time0, time1, time2, time3));
//        ArrayList<LocalTime> list2 = new ArrayList<>(Arrays.asList(time0, time1, time2, time3));
        ArrayList<LocalTime> origin = new ArrayList<>(Arrays.asList(time4, time5, time6, time7));

        IntSummaryStatistics targetStatistics = target.stream().mapToInt((time) -> time.getHour() * (time.getMinute() + 24)).summaryStatistics();
        IntSummaryStatistics originStatistics = origin.stream().mapToInt((time) -> time.getHour() * (time.getMinute() + 24)).summaryStatistics();

        if (targetStatistics.getCount() == originStatistics.getCount()
                && targetStatistics.getSum() == originStatistics.getSum()
                && targetStatistics.getAverage() == originStatistics.getAverage()
        ) {
            log.info("equals");
        } else {
            log.info("not equals");
        }
        log.info("--------------------------------------------");

        log.info("count " + targetStatistics.getCount() + " : " + originStatistics.getCount());
        log.info("sum " + targetStatistics.getSum() + " : " + originStatistics.getSum());
        log.info("average " + targetStatistics.getAverage() + " : " + originStatistics.getAverage());
        log.info("min " + targetStatistics.getMin() + " : " + originStatistics.getMin());
        log.info("max " + targetStatistics.getMax() + " : " + originStatistics.getMax());

    }
}
