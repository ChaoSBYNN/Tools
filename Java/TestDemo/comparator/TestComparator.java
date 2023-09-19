package com.example.testOrder;


/**
 * @author Spike_Zhang
 * @version v4.1
 * @description: 高频告警地区排行排序器
 * @date 2021/7/30 14:56
 */
public class TestComparator implements java.util.Comparator<SpaceCount> {

    @Override
    public int compare(SpaceCount o1, SpaceCount o2) {
        if (o1.getCount().equals(o2.getCount())) {
            int result1 = o1.getId() < o2.getId() ? -1 : 1;
            Long.compare(o1.getId(), o2.getId());

            System.out.println(o1.getId() + " < " + o2.getId() + " result1 : " + (o1.getId() < o2.getId()));
            return result1;
        }
        int result2 = o1.getCount() < o2.getCount() ? 1 : -1;

        System.out.println("result2 : " + result2);
        return result2;
    }

}
