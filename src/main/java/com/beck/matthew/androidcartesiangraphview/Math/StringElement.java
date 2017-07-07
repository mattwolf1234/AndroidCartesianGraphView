package com.beck.matthew.androidcartesiangraphview.Math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringElement {
	private Vector<Element> elements = new Vector<>(6,3);
	private double returnStuff = 0;

	public StringElement(Element item){
		elements.add(item);
	}
	/**public StringElement(Element[] items){
		for (Element item: items){
			elements.add(item);
		}
	}**/
	public StringElement(Element ... items){
		for (Element item: items){
			elements.add(item);
		}
		Collections.sort(elements);
		removeErrors();
	}
	public StringElement(String string){
		this(convertStringElement(string).toArray());
	}
	public StringElement(){
		
	}
	public double input(double x){
		returnStuff = 0;
		for (Element item: elements){
			returnStuff += item.input(x);
		}
		return returnStuff;
	}
	public String toString(){
		String build = "";
		for (Element item: elements){
			if (item.equals(elements.firstElement())){
				build += item.toString();
			}else{
                if (Math.signum(item.getCoefficient()) <= -1){
                    build += "-" + item.toString().substring(1);
                }else{
                    build += "+" + item.toString();
                }
			}
		}
		return build;
	}
	public void add(Element item){
		elements.add(item);
	}
    public void add(Element[] items){
        Collections.addAll(Arrays.asList(items));
    }
	public void delete(Element item){
		elements.remove(item);
	}
	public void delete(int n){
		elements.remove(n);
	}
	public Element getElement(int n){
		return elements.get(n);
	}
	public int length(){
		return elements.size();
	}
	public void sort(){
        Collections.sort(elements);
    }
    public boolean isEmpty(){
        return elements.isEmpty();
    }
    public Element lastElement(){
        return elements.lastElement();
    }
    public Element firstElement(){ return elements.firstElement();}
    public void cleanElements(){
        for (Element item: elements){
            if (item.getCoefficient() == 0 || item.getDegree() == 0){
                elements.remove(item);
            }
        }
    }
    public Element[] toArray(){
    	return elements.toArray(new Element[elements.size()]);
    }
    public void fixErrors(){
    	for (int n = 0; n < elements.size()-1; n++){
    		if (elements.get(n).getDegree() == elements.get(n+1).getDegree()){
    			if (!(elements.get(n+1) instanceof RealNumber)){
    				elements.remove(n);
    				n-=1;
    			}else if (elements.get(n) instanceof RealNumber){
    				elements.remove(n);
    				n-=1;
    			}
    		}
    	}
    }
    protected void removeErrors(){
    	for (int i = 0; i < elements.size(); i++){
    		if (elements.get(i).coefficient == 0 || (elements.get(i).degree == 0 && !(elements.get(i) instanceof RealNumber))){
    			elements.remove(i);
    			i--;
    		}
    	}
    }
    public int degree(){
        int degree = 0;
        for (Element item: elements){
            if (item.degree > degree){
                degree = item.degree;
            }
        }
        return degree;
    }
    public double leadingCoefficient(){
        int degree = degree();
        for (Element item: elements){
            if (item.degree == degree){
                return item.coefficient;
            }
        }
        return 0;
    }
    public Element nonX(){
        for (Element item: elements){
            if (item instanceof RealNumber){
                return item;
            }
        }
        return null;
    }
    public void addStepDownDegrees(){
        int degree = degree();
        int highestD = degree;
        for (int i = 0; degree != 1; degree--, i++){
            if (degree != highestD){
                if (elements.get(i).degree != degree){
                    elements.add(i,new Element(0,degree));
                }
            }
        }
    }
    public double largestNumber(){
    	double large = Math.abs(elements.firstElement().coefficient);
    	for (Element item: elements){
    		if (large < Math.abs(item.coefficient)){
    			large = Math.abs(item.coefficient);
    		}
    	}
    	return large;
    }
    public double smallestNumber(){
    	double small = elements.firstElement().coefficient;
    	for (Element item: elements){
    		if (small > item.coefficient){
    			small = item.coefficient;
    		}
    	}
    	return small;
    }

    private static StringElement convertStringElement(String given){
        ArrayList<Element> returnList = new ArrayList<>(10);

        char[] brokenString = given.toCharArray();
        String temp = "";
        for (int i = 0; i < brokenString.length; i++){
            if ((brokenString[i] == '+' || brokenString[i] == '-') && (i != 0 && brokenString[i-1] != '^')){// the and is the exceptions when I don't want them to count like
                //when it's the first thing or in a exponent
                returnList.add(identifyElement(temp));
                temp = String.valueOf(brokenString[i]);// set the sign for the next element
            }else if (!(String.valueOf(brokenString[i]).matches("\\d|\\.|x|\\^|[+|-]"))){// if it doesn't match then bad user input
                throw new NumberFormatException();
            }else{
                temp += String.valueOf(brokenString[i]);
            }
        }
        returnList.add(identifyElement(temp));

        return new StringElement(returnList.toArray(new Element[returnList.size()]));
    }
    private static Element identifyElement(String give){
        Element returnElement = new Element();
        Pattern pattern = Pattern.compile("^[+|-]?(\\d+(\\.)?\\d*)$");
        Matcher matcher = pattern.matcher(give);
        if (matcher.find()){// this first one will check if it's just a single number
            returnElement = new RealNumber(Double.valueOf(give.substring(matcher.start(), matcher.end())));
            return returnElement;
        } pattern = Pattern.compile("^[+|-]?\\d+(\\.)?\\d*x"); matcher = pattern.matcher(give); // seeing if there is a number in front of the x
        if (matcher.find()){
            returnElement.setCoefficient(Double.valueOf(give.substring(matcher.start(), matcher.end() - 1))); //the -1 is so that it doesnt include the x
            pattern = Pattern.compile(".+\\^[+|-]?\\d+"); matcher = pattern.matcher(give);// this gives the degree to the same element
            if (matcher.find()){
                int n = String.valueOf(returnElement.getCoefficient()).length();// n is for the .0 thing that happens with doubles
                returnElement.setDegree(Integer.valueOf(give.substring(matcher.start() + ((returnElement.getCoefficient()%1 == 0?n-2:n) + (give.matches("^[+].*")?3:2)))));
                // the last give matches thing is if a '+' is in front
            }
            return returnElement;
        } pattern = Pattern.compile("[+|-]?x\\^[+|-]?\\d+"); matcher = pattern.matcher(give);// this is only for when x is 1 or -1
        if (matcher.find()){
            returnElement.setCoefficient((give.matches("^[-].*")?-1*returnElement.getCoefficient():returnElement.getCoefficient()));
            returnElement.setDegree(Integer.valueOf(give.substring(matcher.start() + (give.matches("^[+|-].*")?3:2))));// give matches is same reason as before but needs a negative
            // the +2 is so the x and ^ will not be included
        }
        if (give.contains("-")){// this is for when it's just x or -x
        	returnElement.setCoefficient(-1);
        }
        return returnElement;
    }


}
