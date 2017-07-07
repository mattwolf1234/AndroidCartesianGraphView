package com.beck.matthew.androidcartesiangraphview.Math;

@SuppressWarnings("serial")
public class RealNumber extends Element{
	public RealNumber(){
		coefficient = 1;
		degree = 1;
	}
	public RealNumber(double value){
		coefficient = value;
		degree = 1;
	}
	public String getText(){
		return coefficient +"";
	}
	public double input(double x){
		return coefficient;
	}
	public double input(){
		return coefficient;
	}
	public void setDegree(int degree){
	}
}
