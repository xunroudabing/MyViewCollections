package com.xunroudabing.myviewscollections.views.hicon.schemechart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.util.Log;

public class Phase implements Comparable<Phase> {
	static final String TAG = Phase.class.getSimpleName();
	/**
	 * 名称 如P1 P2 P3...
	 */
	public String Name;
	/**
	 * 最大绿灯时间
	 */
	public int MaxGreen1;
	public int YellowTime;
	public int MinGreen;
	public int PhaseNo;
	public int ChannelNo;
	public int RedTime;
	public int SpecialPhase;
	public int Direction;
	public int PhaseType;
	
	public Integer getIndex(){
		try {
			String temp = Name.substring(1);
			return Integer.parseInt(temp);
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.toString());
		}
		return -1;
	}
	//根据Name升序排列，如：P1 P2 P3 P5
	@Override
	public int compareTo(Phase another) {
		// TODO Auto-generated method stub
		return getIndex().compareTo(another.getIndex());
	}
	public static Phase fromJson(JSONObject object){
		
		try {
			Phase phase = new Phase();
			phase.MaxGreen1 = object.getInt("MaxGreen1");
			phase.YellowTime = object.getInt("YellowTime");
			phase.MinGreen = object.getInt("MinGreen");
			phase.PhaseNo = object.getInt("PhaseNo");
			phase.ChannelNo = object.getInt("ChannelNo");
			phase.RedTime = object.getInt("RedTime");
			phase.SpecialPhase = object.getInt("SpecialPhase");
			phase.Direction = object.getInt("Direction");
			phase.PhaseType = object.getInt("PhaseType");
			return phase;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.toString());
		}
		return null;
	}

	public static void main(String[] args){
		Phase p1 = new Phase();
		p1.Name = "P1";
		Phase p2 = new Phase();
		p2.Name = "P2";
		Phase p3 = new Phase();
		p3.Name = "P3";
		
		List<Phase> list = new ArrayList<Phase>();
		list.add(p2);
		list.add(p1);
		list.add(p3);
		Collections.sort(list);
		for(Phase p:list){
			System.out.println(p.Name);
		}
	}
}
