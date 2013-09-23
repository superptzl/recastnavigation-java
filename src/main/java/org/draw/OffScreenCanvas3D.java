package org.draw;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.ImageComponent;
import javax.media.j3d.ImageComponent2D;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author igozha
 * @since 22.09.13 15:31
 */
public class OffScreenCanvas3D extends Canvas3D
{

	OffScreenCanvas3D(GraphicsConfiguration graphicsConfiguration,
					  boolean offScreen)
	{

		super(graphicsConfiguration, offScreen);
	}

	private BufferedImage doRender(int width, int height)
	{

		BufferedImage bImage = new BufferedImage(width, height,
												 BufferedImage.TYPE_INT_RGB);

		ImageComponent2D buffer = new ImageComponent2D(
			ImageComponent.FORMAT_RGB, bImage);
		//buffer.setYUp(true);

		setOffScreenBuffer(buffer);
		renderOffScreenBuffer();
		waitForOffScreenRendering();
		bImage = getOffScreenBuffer().getImage();
		return bImage;
	}


}
