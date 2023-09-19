/**
 * 
 */
package com.example.testFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapperConvert {
    private List<Node> mapperList;
    private Map<Integer, List<Node>> mapperMap = new HashMap<>();
    private static MapperConvert sMapperConvert;

    public static MapperConvert getInstance() {
        if (sMapperConvert == null) {
            sMapperConvert = new MapperConvert();
        }

        return sMapperConvert;
    }

    public MapperConvert() {
        this.init();
    }

    private void init() {
        this.init05();
        this.init03();
        this.init04();
        this.init02();
        this.init06();
    }

    private void init05() {
        List<Node> list = new ArrayList<>();
        Node node = new Node(-1.0F, 0.0F);
        list.add(node);
        node = new Node(0.072F, 0.123F);
        list.add(node);
        node = new Node(0.144F, 0.213F);
        list.add(node);
        node = new Node(0.216F, 0.302F);
        list.add(node);
        node = new Node(0.264F, 0.557F);
        list.add(node);
        node = new Node(0.342F, 0.87F);
        list.add(node);
        node = new Node(0.402F, 0.905F);
        list.add(node);
        node = new Node(0.45F, 0.93F);
        list.add(node);
        node = new Node(0.635F, 0.954F);
        list.add(node);
        node = new Node(0.818F, 0.969F);
        list.add(node);
        node = new Node(1.0F, 1.0F);
        list.add(node);
        this.mapperMap.put(100500512, list);
        this.mapperList = list;
    }

    private void init03() {
        List<Node> list = new ArrayList<>();
        Node node = new Node(-1.0F, 0.0F);
        list.add(node);
        node = new Node(0.132F, 0.123F);
        list.add(node);
        node = new Node(0.217F, 0.213F);
        list.add(node);
        node = new Node(0.295F, 0.302F);
        list.add(node);
        node = new Node(0.346F, 0.557F);
        list.add(node);
        node = new Node(0.426F, 0.87F);
        list.add(node);
        node = new Node(0.486F, 0.905F);
        list.add(node);
        node = new Node(0.512F, 0.93F);
        list.add(node);
        node = new Node(0.675F, 0.954F);
        list.add(node);
        node = new Node(0.818F, 0.969F);
        list.add(node);
        node = new Node(1.0F, 1.0F);
        list.add(node);
        this.mapperMap.put(100300128, list);
    }

    private void init04() {
        List<Node> list = new ArrayList<>();
        Node node = new Node(-1.0F, 0.0F);
        list.add(node);
        node = new Node(0.068F, 0.123F);
        list.add(node);
        node = new Node(0.155F, 0.213F);
        list.add(node);
        node = new Node(0.228F, 0.302F);
        list.add(node);
        node = new Node(0.276F, 0.557F);
        list.add(node);
        node = new Node(0.355F, 0.87F);
        list.add(node);
        node = new Node(0.413F, 0.905F);
        list.add(node);
        node = new Node(0.451F, 0.93F);
        list.add(node);
        node = new Node(0.638F, 0.954F);
        list.add(node);
        node = new Node(0.818F, 0.969F);
        list.add(node);
        node = new Node(1.0F, 1.0F);
        list.add(node);
        this.mapperMap.put(100400512, list);
    }

    private void init02() {
        List<Node> list = new ArrayList<>();
        Node node = new Node(-1.0F, 0.0F);
        list.add(node);
        node = new Node(0.108F, 0.123F);
        list.add(node);
        node = new Node(0.181F, 0.213F);
        list.add(node);
        node = new Node(0.252F, 0.302F);
        list.add(node);
        node = new Node(0.297F, 0.557F);
        list.add(node);
        node = new Node(0.372F, 0.87F);
        list.add(node);
        node = new Node(0.432F, 0.905F);
        list.add(node);
        node = new Node(0.467F, 0.93F);
        list.add(node);
        node = new Node(0.604F, 0.954F);
        list.add(node);
        node = new Node(0.818F, 0.969F);
        list.add(node);
        node = new Node(1.0F, 1.0F);
        list.add(node);
        this.mapperMap.put(100200512, list);
    }

    private void init06() {
        List<Node> list = new ArrayList<>();
        Node node = new Node(-1.0F, 0.0F);
        list.add(node);
        node = new Node(0.068F, 0.123F);
        list.add(node);
        node = new Node(0.142F, 0.213F);
        list.add(node);
        node = new Node(0.213F, 0.302F);
        list.add(node);
        node = new Node(0.261F, 0.557F);
        list.add(node);
        node = new Node(0.339F, 0.87F);
        list.add(node);
        node = new Node(0.398F, 0.905F);
        list.add(node);
        node = new Node(0.443F, 0.93F);
        list.add(node);
        node = new Node(0.63F, 0.954F);
        list.add(node);
        node = new Node(0.818F, 0.969F);
        list.add(node);
        node = new Node(1.0F, 1.0F);
        list.add(node);
        this.mapperMap.put(100600512, list);
    }

    public List<Node> getMapperList(int version) {
        List<Node> list = (List)this.mapperMap.get(version);
        return list == null ? this.mapperList : list;
    }

    public float scoreConvert(int version, float value) {
        List<Node> list = this.getMapperList(version);
        return this.scoreConvert(value, list);
    }

    public float scoreConvert(float value, List<Node> list) {
        return this.scoreConvert(value, list, list.size());
    }

    public float scoreConvert(float value, List<Node> list, int len) {
        float score = value;

        for(int i = 0; i < len - 1; ++i) {
            Node firstPoint = (Node)list.get(i);
            Node secondPoint = (Node)list.get(i + 1);
            if (value > firstPoint.baseValue && value <= secondPoint.baseValue) {
                score = this.convert(firstPoint, secondPoint, value);
                break;
            }
        }

        if ((double)score >= 1.0D) {
            score = 0.998F;
        }

        return score;
    }

    private float convert(Node pos0, Node pos1, float score) {
        float slope = 0.0F;
        float pos = 0.0F;
        if (pos0.baseValue != pos1.baseValue) {
            slope = (pos1.mapValue - pos0.mapValue) / (pos1.baseValue - pos0.baseValue);
            pos = pos0.mapValue - slope * pos0.baseValue;
        }

        return slope * score + pos;
    }

    public float convertOriginal(int version, float value) {
        List<Node> list = this.getMapperList(version);
        return this.convertOriginal(value, list);
    }

    public float convertOriginal(float value, List<Node> list) {
        float score = value;
        int len = list.size();

        for(int i = 0; i < len - 1; ++i) {
            Node firstPoint = (Node)list.get(i);
            Node secondPoint = (Node)list.get(i + 1);
            if (value > firstPoint.mapValue && value <= secondPoint.mapValue) {
                score = this.convertOriginal(firstPoint, secondPoint, value);
                break;
            }
        }

        if ((double)score >= 1.0D) {
            score = 0.999F;
        }

        return score;
    }

    private float convertOriginal(Node pos0, Node pos1, float score) {
        float slope = 1.0F;
        float pos = 0.0F;
        if (pos0.baseValue != pos1.baseValue) {
            slope = (pos1.mapValue - pos0.mapValue) / (pos1.baseValue - pos0.baseValue);
            pos = pos0.mapValue - slope * pos0.baseValue;
        }

        return (score - pos) / slope;
    }

    public class Node {
        float baseValue;
        float mapValue;

        public Node(float baseValue, float mapValue) {
            this.baseValue = baseValue;
            this.mapValue = mapValue;
        }

        public float getBaseValue() {
            return this.baseValue;
        }

        public float getMapValue() {
            return this.mapValue;
        }
    }
}
