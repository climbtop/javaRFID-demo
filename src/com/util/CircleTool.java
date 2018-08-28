package com.util;

public class CircleTool {
	private int scope;
	private int index;
	
	public CircleTool(){
		this(1);
	}
	
	public CircleTool(int scope){
		if(scope<1) scope = 1;
		this.scope = scope;
		this.index = scope-1;
	}
	
	public int next(){
		index = ++index%scope;
		return index;
	}
	
	public int index(){
		return index;
	}
	
}
