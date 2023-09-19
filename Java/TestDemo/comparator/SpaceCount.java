package com.example.testOrder;

/**
 * @author Spike_Zhang
 * @version 1.0
 * @description: TODO
 * @date 2021/8/12 14:29
 */
public class SpaceCount {

    public SpaceCount(String name, Long id, Integer count) {
        this.name = name;
        this.id = id;
        this.count = count;
    }

    private String name;
    private Long id;
    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SpaceCount{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", count=" + count +
                "}\n";
    }
}
