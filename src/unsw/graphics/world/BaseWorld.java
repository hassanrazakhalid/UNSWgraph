package unsw.graphics.world;

import com.jogamp.opengl.GL3;

public abstract class BaseWorld {

	/**
	 * Override this method if there is some thing need to be done only one
	 * @param gl
	 */
	public abstract void initGL(GL3 gl);
	
}
