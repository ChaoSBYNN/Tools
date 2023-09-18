package com.example.race;

import java.util.Scanner;

public class SingleCycle {
	static int teamNum;
	static int[] teams;
	private static Scanner scanner;
	public static void main(String[] args) {
		//通过命令行参数获取球队数量
		scanner = new Scanner(System.in);
		teamNum= scanner.nextInt();
//		if(teamNum<=0 || teamNum%2!=0){
//			System.out.println("输入错误，程序退出！");
//			System.exit(0);
//		}
		//创建球队和初始化球队
		teams=new int[teamNum];
		for(int i=0;i<teams.length;i++){
			teams[i]=i+1;
		}
		//打印对阵形式和轮循
		for(int i=1;i<=teamNum;i++){
			System.out.println("第" + i + "局的对阵：");
			printTeams();
			round();
			
		}
	}
	//打印对阵形式
	public static void printTeams(){
		for(int j=0;j<teams.length/2;j++){
			System.out.print(teams[j]+"-"+teams[teams.length-1-j]+"  ");
		}
		System.out.println();
	}
	//球队单循环轮训
	public static void round(){
		int temp=teams[teams.length-1];
		for(int i=teams.length-1;i>=1;i--){
			teams[i]=teams[i-1];
		}
		teams[0]=temp;
	}
}
