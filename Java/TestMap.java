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
            //CHAOS_ZHANG 当前癞子底数 查找同一玩家打出该癞子前癞子的次数
            int base = getBase(i,laiziMap,laiziPlayer);
            //CHAOS_ZHANG 当前张癞子打出奖励 底番 (默认一次癞子 base = 0)
            int laiziScore = 1 << base;
            //CHAOS_ZHANG 计某个玩家当前癞子打出后总奖励
            int count_win = 0;

            for (String player : plist) {
                //CHAOS_ZHANG 飘癞子3赔1 癞子玩家不赔
                if(laiziPlayer.equals(player)){
                    continue;
                }
                //CHAOS_ZHANG 玩家总扣分统计
                int laizi_lose = 1;
                //CHAOS_ZHANG 当前癞子底数 其他玩家小于该癞子
                int obase = getBase(i,laiziMap,player);

                //CHAOS_ZHANG 如果其他玩家也打出过癞子 计算癞子翻倍 赔偿 (打出癞子顺序小于该顺序)
                boolean b1 = laiziMap.containsValue(player);
                boolean b2 = obase>0;

                if(b1&&b2){
                    laizi_lose = laiziScore * (1 << obase);
                }else{
                    laizi_lose = laiziScore;//CHAOS_ZHANG 正常算分
                }

                count_win+=laizi_lose;//CHAOS_ZHANG 得分累加

                System.out.println("Player : "+player+(0 - (laizi_lose)));

            }

            for(String lzplayer : plist){//CHAOS_ZHANG 最后算飘癞子 总奖励 查找当前飘癞子玩家
                if(laiziPlayer.equals(lzplayer)){

                  System.out.println("lzplayer : " + lzplayer+count_win);
                    break;
                }
            }
        }
		
	}
	
    //CHAOS_ZHANG 获取 当前玩家当前癞子底数
    public int getBase(int laiziValue,HashMap<String,String> laiziMap,String playerId){
        //CHAOS_ZHANG 当前癞子底数
        int base = 0;

        for(int j=1;j<laiziValue;j++){
            //CHAOS_ZHANG 在此飘癞子之前同一玩家 飘过癞子次数
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
