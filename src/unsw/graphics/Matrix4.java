/**
 * 
 */
package unsw.graphics;

import java.util.Arrays;

import unsw.graphics.geometry.Point3D;

/**
 * A 4x4 matrix.
 * 
 * This class is immutable.
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Matrix4 {
    // Matrix is stored in column-major order to match OpenGL
    private float[] values;
    
    /**
     * Construct a matrix from the given flat array of values.
     * @param values The values for the matrix in column-major order.
     */
    public Matrix4(float[] values) {
        if (values.length != 16)
            throw new IllegalArgumentException("Matrix4 constructor passed an array of length " + values.length);
        this.values = Arrays.copyOf(values, 16);
    }
    
    /**
     * Create an identity matrix.
     */
    public static Matrix4 identity() {
        float[] values = new float[] {
            1, 0, 0, 0, // i
            0, 1, 0, 0, // j
            0, 0, 1, 0, // k
            0, 0, 0, 1  // phi
        };
        return new Matrix4(values);
    }
    
    /**
     * Create a translation matrix to the given point.
     * @param x The x coordinate of the new origin
     * @param y The y coordinate of the new origin
     * @return
     */
    public static Matrix4 translation(float x, float y, float z) {
        float[] values = new float[] {
            1, 0, 0, 0, // i
            0, 1, 0, 0, // j
            0, 0, 1, 0, // k
            x, y, z, 1  // phi
        };
        return new Matrix4(values);
    }
    
    /**
     * Create a translation matrix to the given point.
     * @param x The x coordinate of the new origin
     * @param y The y coordinate of the new origin
     * @return
     */
    public static Matrix4 translation(Point3D point) {
        return translation(point.getX(), point.getY(), point.getZ());
    }
    
    /**
     * Create a rotation matrix for rotation around the x-axis.
     * @param degrees Rotate around the axis this many degrees
     * @return
     */
    public static Matrix4 rotationX(float degrees) {
        double radians = Math.toRadians(degrees);
        float[] values = new float[] {
            1, 0, 0, 0,                                                  // i
            0, (float) Math.cos(radians), (float) Math.sin(radians), 0,  // j
            0, (float) -Math.sin(radians), (float) Math.cos(radians), 0, // k
            0, 0, 0, 1                                                   // phi
        };
        return new Matrix4(values);
    }
    
    
    /**
     * Create a rotation matrix for rotation around the y-axis.
     * @param degrees Rotate around the axis this many degrees
     * @return
     */
    public static Matrix4 rotationY(float degrees) {
        double radians = Math.toRadians(degrees);
        float[] values = new float[] {
            (float) Math.cos(radians), 0, (float) -Math.sin(radians), 0, // i
            0, 1, 0, 0,                                                  // j
            (float) Math.sin(radians), 0, (float) Math.cos(radians), 0,  // k
            0, 0, 0, 1                                                   // phi
        };
        return new Matrix4(values);
    }
    
    
    /**
     * Create a rotation matrix for rotation around the x-axis.
     * @param degrees Rotate around the axis this many degrees
     * @return
     */
    public static Matrix4 rotationZ(float degrees) {
        double radians = Math.toRadians(degrees);
        float[] values = new float[] {
            (float) Math.cos(radians), (float) Math.sin(radians), 0, 0,  // i
            (float) -Math.sin(radians), (float) Math.cos(radians), 0, 0, // j
            0, 0, 1, 0,                                                  // k
            0, 0, 0, 1                                                   // phi
        };
        return new Matrix4(values);
    }
    
    /**
     * Create a scale matrix.
     * @param x Scale the i-axis by this much
     * @param y Scale the j-axis by this much
     * @return
     */
    public static Matrix4 scale(float x, float y, float z) {
        float[] values = new float[] {
            x, 0, 0, 0, // i
            0, y, 0, 0, // j
            0, 0, z, 0, // k
            0, 0, 0, 1  // phi
        };
        return new Matrix4(values);
    }
    
    @Override
    public String toString() {
        String str = "";
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                str += values[x*4 + y] + " ";
            }
            str += "\n";
        }
        return str;
    }
    
    /**
     * Multiply this matrix by the given matrix.
     * @param mat
     * @return
     */
    public Matrix4 multiply(Matrix4 mat) {
        float[] r = new float[16];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                r[x*4 + y] = getRow(y).dotp(mat.getColumn(x));
            }
        }
        return new Matrix4(r);
    }
    
    /**
     * Multiply this matrix by the given (column) vector.
     * @param mat
     * @return
     */
    public Vector4 multiply(Vector4 v) {
        float[] r = new float[4];
        for (int y = 0; y < 4; y++) {
            r[y] = getRow(y).dotp(v);
        }
        return new Vector4(r);
    }
    
    private Vector4 getColumn(int x) {
        float[] vec = new float[4];
        for (int y = 0; y < 4; y++) {
            vec[y] = values[x*4 + y];
        }
        return new Vector4(vec);
    }
    
    private Vector4 getRow(int y) {
        float[] vec = new float[4];
        for (int x = 0; x < 4; x++) {
            vec[x] = values[x*4 + y];
        }
        return new Vector4(vec);
    }

    /**
     * Get the values stored in this matrix in column-major order.
     * @return
     */
    public float[] getValues() {
        return Arrays.copyOf(values, 16);
    }

}