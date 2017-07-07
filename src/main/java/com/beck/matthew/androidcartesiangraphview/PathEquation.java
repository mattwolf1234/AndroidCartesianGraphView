package com.beck.matthew.androidcartesiangraphview;


import android.graphics.Canvas;
import android.graphics.Paint;

import com.beck.matthew.androidcartesiangraphview.Math.Element;
import com.beck.matthew.androidcartesiangraphview.Math.StringElement;

public class PathEquation {

    private StringElement stringElement;
    private float[] yValues;
    private float x;
    private float x2;
    private float y;
    private float y2;

    public PathEquation(){
        stringElement = new StringElement();
        yValues = new float[CartesianGraphView.MAX_GRID_LINES+1];
    }
    public PathEquation(StringElement stringElement){
        yValues = new float[CartesianGraphView.MAX_GRID_LINES+1];
        this.stringElement = stringElement;
    }
    public PathEquation(Element ... args){
        yValues = new float[CartesianGraphView.MAX_GRID_LINES];
        this.stringElement = new StringElement(args);
    }

    public StringElement getStringElement(){
        return stringElement;
    }
    public void setStringElement(StringElement stringElement){
        this.stringElement = stringElement;
    }

    public void drawEquations(float xStart, float xEnd, float unit, float scale, float centerX, float centerY, Canvas canvas, Paint paint){
        xStart *= unit;// adjusting the grid line value position to the correct one
        xEnd *= unit;

        float maxPointsDraw = (xEnd - xStart)/unit;// gets how many lines are present

        scale = scale / unit;

        float n = 0;
        for (int i = 0; i <= maxPointsDraw; i++, n+=unit){
            yValues[i] = (float) stringElement.input((xStart + (i*unit)));// calculating the y values based on the x values

        }

        for (int i = 1; i <= maxPointsDraw; i++){
            if (i == 1){// this runs only for the first draw

                x = (xStart * scale) + centerX;
                y = (yValues[0] * -scale) + centerY;
                x2 = (((xStart+(i*unit)) * scale)  + centerX);
                y2 = (yValues[i] * -scale) + centerY;

                if (inBounds(x,y,x2,y2, canvas)) {
                    canvas.drawLine(x, y, x2, y2, paint);
                }

            }else{

                x = (((xStart+((i*unit)-unit)) * scale)  + centerX);
                y = (yValues[i-1] * -scale) + centerY;
                x2 = (((xStart+(i*unit)) * scale)  + centerX);
                y2 = (yValues[i] * -scale) + centerY;

                if (inBounds(x,y,x2,y2, canvas)) {// this checks if the coordinates are in bounds
                    // this checking is necessary to improve performance
                    canvas.drawLine(x, y, x2, y2, paint);
                }

            }
        }
    }

    private boolean inBounds(float x, float y, float x2, float y2,Canvas canvas){

        if (!canvas.getClipBounds().contains((int) x, (int) y)){
            if (!canvas.getClipBounds().contains((int) x2, (int) y2)) {
                return false;
            }
        }

        return true;
    }
}
