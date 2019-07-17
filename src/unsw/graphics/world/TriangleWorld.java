package unsw.graphics.world;

import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.Point3D;

public class TriangleWorld {

	Line3D line1;
	Line3D line2;
	Line3D line3;
	
	public TriangleWorld(Line3D line1, Line3D line2, Line3D line3) {
		// TODO Auto-generated constructor stub
		this.line1 = line1;
		this.line2 = line2;
		this.line3 = line3;
	}
	
	public boolean isPointInTriangle(Point3D pt) {
    	
    	//Line equation
		boolean isOk = false;
    	if (isPointOnLine(pt, line1)) {
    		isOk = true;
    	}
    	else if (isPointOnLine(pt, line2)) {
    		isOk = true;
    	}
    	else if (isPointOnLine(pt, line3)) {
    		isOk = true;
    	}
    	else {
    		isOk = false;
    	}
    	return isOk;
    }
	
	private boolean isPointOnLine(Point3D pt, Line3D line) {
    	
    	float m = (line.getEnd().getX() - line.getStart().getX()) / (line.getEnd().getZ() - line.getStart().getZ()); // finding slope
    	float b = line.getStart().getX() - m * line.getStart().getZ(); // equation of line
    	
    	float x = pt.getX();
    	float y = pt.getZ();
    	return y == m * x + b;
    }
	
}
