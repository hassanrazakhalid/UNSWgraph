package unsw.graphics.scene;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;

/**
 * A collection of useful math methods 
 *
 * @author malcolmr
 */
public class MathUtil {

    /**
     * Normalise an angle to the range [-180, 180)
     * 
     * @param angle 
     * @return
     */
    public static float normaliseAngle(float angle) {
        return ((angle + 180f) % 360f + 360f) % 360f - 180f;
    }

    /**
     * Clamp a value to the given range
     * 
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static Point2D normalize(Point2D pt) {
    	
//    	System.out.println("normalizing = " + pt.toString());
    	float denum = (float) (Math.sqrt(pt.getX() * pt.getX() + pt.getY() * pt.getY() ));
    	float x = pt.getX() / denum;
    	float y = pt.getY() / denum;
    	Point2D result = new Point2D(x, y);
//    	System.out.println("result Is = " + result.toString());
    	return result;
    	
    }
    
    public static float distance(Point2D pt1, Point2D pt2) {
    	float x_val = (float) Math.pow(pt2.getX() - pt1.getX(), 2);
    	float y_val = (float) Math.pow(pt2.getY() - pt1.getY(), 2);
    	return (float)Math.sqrt(x_val + y_val);
    }
    
    public static Vector3 crossProduct(Vector3 a, Vector3 b) {
    	
    	float val1 = a.getY() * b.getZ() - a.getZ() * b.getY();
    	float val2 = a.getZ() * b.getX() - a.getX() * b.getZ();
    	float val3 = a.getX() * b.getY() - a.getY() * b.getX();
    	return new Vector3(val1, val2, val3);
    }
       
}
