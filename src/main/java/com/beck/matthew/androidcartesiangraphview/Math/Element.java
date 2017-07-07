package com.beck.matthew.androidcartesiangraphview.Math;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Element implements Comparable<Element>,Serializable {// For exponent do alt 0178 ²
	protected double coefficient;
	protected int degree;
	public Element(){
		 coefficient = 1;
		 degree = 1;
	}
	public Element(double coefficient){
		this.coefficient = coefficient;
		degree = 1;
	}
	public Element(double coefficient, int degree){
		this.coefficient = coefficient;
		this.degree = degree;
	}
	public Element(Element copy){
		coefficient = copy.getCoefficient();
		degree = copy.getDegree();
	}
	public double getCoefficient(){
		return coefficient;
	}
	public void setCoefficient(double coefficient){
		this.coefficient = coefficient;
	}
	public int getDegree(){
		return degree;
	}
	public void setDegree(int degree){
		this.degree = degree;
	}
	public double input(double x){
		return coefficient * (Math.pow(x, degree));
	}
	public String getText(){
		if (coefficient == 1){
			if (degree == 1){
				return "x";
			}else{
				return "x" + getExponentText(degree);
			}
		}else{
			if (degree == 1){
				return coefficient + "x";
			}
			return coefficient + "x" + getExponentText(degree);
		}
	}
	public String toString(){
		return getText();
	}
	@Override
	public int compareTo(Element arg) {
		if (this instanceof RealNumber){
			if (arg instanceof RealNumber){
				if (arg.getCoefficient() == coefficient){
					return 0;
				}else if (arg.getCoefficient() < coefficient){
					return -1;
				} return 1;
			}else{
				return 1;
			}
			
		}else if (arg instanceof RealNumber){
			if (this instanceof RealNumber){
				if (arg.getCoefficient() == coefficient){
					return 0;
				}else if (arg.getCoefficient() < coefficient){
					return -1;
				} return 1;
			}else{
				return -1;
			}
		}
		if (arg.getDegree() == degree){
			if (arg.getCoefficient() > coefficient){
				return 1;
			}else if (arg.getCoefficient() == coefficient){
				return 0;
			}
			return -1;
		}else if (arg.getDegree() < degree){
			return -1;
		}else{
			return 1;
		}
	}
	protected static String getExponentText(int num){
		char[] holdNumbers = Integer.valueOf(num).toString().toCharArray();
		int n = 0;
		String returnFinal = "";
		if (num >= 0){
			while (true){
				if (holdNumbers.length <= n){
					return returnFinal;
				}
				if (holdNumbers[n] == '0'){
					returnFinal = returnFinal + "\u2070";
				}else if (holdNumbers[n] == '1'){
					returnFinal = returnFinal + "\u00B9";
				}else if (holdNumbers[n] == '2'){
					returnFinal = returnFinal + "\u00B2";
				}else if (holdNumbers[n] == '3'){
					returnFinal = returnFinal + "\u00B3";
				}else if (holdNumbers[n] == '4'){
					returnFinal = returnFinal + "\u2074";
				}else if (holdNumbers[n] == '5'){
					returnFinal = returnFinal + "\u2075";
				}else if (holdNumbers[n] == '6'){
					returnFinal = returnFinal + "\u2076";
				}else if (holdNumbers[n] == '7'){
					returnFinal = returnFinal + "\u2077";
				}else if (holdNumbers[n] == '8'){
					returnFinal = returnFinal + "\u2078";
				}else if (holdNumbers[n] == '9'){
					returnFinal = returnFinal + "\u2079";
				}else{
					return returnFinal;
				}
				n++;
			}
		}else{
			return "^" + num;
		}
	}
	public int length(){
		return getText().length();
	}
}
