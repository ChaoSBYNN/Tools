package com.chaos_zhang.main;

import java.util.HashMap;

public class TestMap {

	public void calLazaraillo(){
		HashMap<String,String> laiziMap = new HashMap<String,String>();
		
		laiziMap.put("1", "a");
		laiziMap.put("2", "b");
		laiziMap.put("3", "c");
		laiziMap.put("4", "b");
		
		String[] plist = {"a","b","c","d"};
		
		for(int i = 1;i<=4;i++){
            String laiziPlayer = laiziMap.get((i+""));
            if(null == laiziPlayer){
                return;
            }
            //CHAOS_ZHANG ��ǰ��ӵ��� ����ͬһ��Ҵ�������ǰ��ӵĴ���
            int base = getBase(i,laiziMap,laiziPlayer);
            //CHAOS_ZHANG ��ǰ����Ӵ������ �׷� (Ĭ��һ����� base = 0)
            int laiziScore = 1 << base;
            //CHAOS_ZHANG ��ĳ����ҵ�ǰ��Ӵ�����ܽ���
            int count_win = 0;

            for (String player : plist) {
                //CHAOS_ZHANG Ʈ���3��1 �����Ҳ���
                if(laiziPlayer.equals(player)){
                    continue;
                }
                //CHAOS_ZHANG ����ܿ۷�ͳ��
                int laizi_lose = 1;
                //CHAOS_ZHANG ��ǰ��ӵ��� �������С�ڸ����
                int obase = getBase(i,laiziMap,player);

                //CHAOS_ZHANG ����������Ҳ�������� ������ӷ��� �⳥ (������˳��С�ڸ�˳��)
                boolean b1 = laiziMap.containsValue(player);
                boolean b2 = obase>0;

                if(b1&&b2){
                    laizi_lose = laiziScore * (1 << obase);
                }else{
                    laizi_lose = laiziScore;//CHAOS_ZHANG �������
                }

                count_win+=laizi_lose;//CHAOS_ZHANG �÷��ۼ�

                System.out.println("Player : "+player+(0 - (laizi_lose)));

            }

            for(String lzplayer : plist){//CHAOS_ZHANG �����Ʈ��� �ܽ��� ���ҵ�ǰƮ������
                if(laiziPlayer.equals(lzplayer)){

                  System.out.println("lzplayer : " + lzplayer+count_win);
                    break;
                }
            }
        }
		
	}
	
    //CHAOS_ZHANG ��ȡ ��ǰ��ҵ�ǰ��ӵ���
    public int getBase(int laiziValue,HashMap<String,String> laiziMap,String playerId){
        //CHAOS_ZHANG ��ǰ��ӵ���
        int base = 0;

        for(int j=1;j<laiziValue;j++){
            //CHAOS_ZHANG �ڴ�Ʈ���֮ǰͬһ��� Ʈ����Ӵ���
            if(playerId.equals(laiziMap.get((j+"")))){
            	base++;
            }
        }
        return base;
    }
	
	public static void main(String[] args) {
		
		TestMap tm = new TestMap();
		
		tm.calLazaraillo();
		
	}
	
}
