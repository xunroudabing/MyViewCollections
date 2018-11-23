package com.xunroudabing.myviewscollections.views.hicon.subsegment;

import java.util.ArrayList;
import java.util.List;

import android.graphics.RectF;

public class SubSegmentInfo {
	public String name;
	/**
	 * 单周期 0-否 1-是
	 */
	public int cycleType;
	public int distance;
	public int forphase;
	public int revphase;
	public int forgreen;
	public int revgreen;
	public int forspeed;
	public int revspeed;
	public int x1;
	public int x2;
	public List<RectF> rect1;
	public List<RectF> rect2;
	public int formix;
	public int revmix;
	public SubSegmentInfo (){
		rect1 = new ArrayList<RectF>();
		rect2 = new ArrayList<RectF>();
	}
}
