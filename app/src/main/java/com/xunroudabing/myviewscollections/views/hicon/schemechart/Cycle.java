package com.xunroudabing.myviewscollections.views.hicon.schemechart;

import java.util.List;

public class Cycle implements Comparable<Cycle> {
	public Integer index;
	public List<Phase> list;
	public List<PhaseData> data;
	@Override
	public int compareTo(Cycle another) {
		// TODO Auto-generated method stub
		return index.compareTo(another.index);
	}
}
