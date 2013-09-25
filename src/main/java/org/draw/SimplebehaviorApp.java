package org.draw;

import java.applet.Applet;

import java.awt.BorderLayout;

import java.awt.Frame;

import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.applet.MainFrame;

import com.sun.j3d.utils.geometry.ColorCube;

import com.sun.j3d.utils.universe.*;

import javax.media.j3d.*;

import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.keyboard.*;

import java.awt.event.*;

import java.util.Enumeration;

//   SimplebehaviorApp renders a single ColorCube

//   that rotates when any key is pressed.

public class SimplebehaviorApp extends Applet
{

	private SimpleUniverse u;
	private ViewingPlatform ourView;

	Shape3D createLand()
	{
		int size = 24;
		LineArray landGeom = new LineArray(size, GeometryArray.COORDINATES | GeometryArray.COLOR_3);

		landGeom.setCoordinate(0, new Point3f(-1, -1, 4));
		landGeom.setCoordinate(1, new Point3f(-1, 1, 4));

		landGeom.setCoordinate(2, new Point3f(-1, -1, 2));
		landGeom.setCoordinate(3, new Point3f(-1, 1, 2));

		landGeom.setCoordinate(4, new Point3f(1, -1, 2));
		landGeom.setCoordinate(5, new Point3f(1, 1, 2));

		landGeom.setCoordinate(20, new Point3f(1, -1, 4));
		landGeom.setCoordinate(21, new Point3f(1, 1, 4));

		landGeom.setCoordinate(6, new Point3f(-1, 1, 4));
		landGeom.setCoordinate(7, new Point3f(1, 1, 4));

		landGeom.setCoordinate(8, new Point3f(-1, 1, 2));
		landGeom.setCoordinate(9, new Point3f(1, 1, 2));

		landGeom.setCoordinate(10, new Point3f(-1, 1, 4));
		landGeom.setCoordinate(11, new Point3f(-1, 1, 2));

		landGeom.setCoordinate(22, new Point3f(1, 1, 4));
		landGeom.setCoordinate(23, new Point3f(1, 1, 2));

		landGeom.setCoordinate(12, new Point3f(-1, -1, 4));
		landGeom.setCoordinate(13, new Point3f(1, -1, 4));

		landGeom.setCoordinate(14, new Point3f(-1, -1, 2));
		landGeom.setCoordinate(15, new Point3f(1, -1, 2));

		landGeom.setCoordinate(16, new Point3f(-1, -1, 4));
		landGeom.setCoordinate(17, new Point3f(-1, -1, 2));

		landGeom.setCoordinate(18, new Point3f(1, -1, 4));
		landGeom.setCoordinate(19, new Point3f(1, -1, 2));

		Color3f c = new Color3f(0.1f, 0.8f, 0.1f);
		Color3f c2 = new Color3f(1, 0, 0);

		for (int i = 0; i < size; i++) landGeom.setColor(i, c);

		return new Shape3D(landGeom);
	}

	public BranchGroup createSceneGraph(SimpleUniverse su)
	{
		BranchGroup objRoot = new BranchGroup();
		objRoot.addChild(createLand());

		objRoot.compile();
		return objRoot;

	} // end of CreateSceneGraph method of SimplebehaviorApp

	public SimplebehaviorApp()
	{
		setLayout(new BorderLayout());

		GraphicsConfiguration config =
			SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		add("Center", canvas3D);

		// SimpleUniverse is a Convenience Utility class
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
		this.u = simpleU;
		ourView = u.getViewingPlatform();

		// This will move the ViewPlatform back a bit so the
		Transform3D locator = new Transform3D();
		locator.setTranslation(new Vector3f(0, 3f, -3f));
		locator.lookAt(new Point3d(0d, 3d, -6d), new Point3d(0d, 0d, 5d), new Vector3d(0d, 1d, 0d));
		locator.invert();
		this.ourView.getViewPlatformTransform().setTransform(locator);
		BranchGroup scene = createSceneGraph(this.u);
		simpleU.addBranchGraph(scene);

	} // end of SimplebehaviorApp (constructor)

	public static void main(String[] args)
	{
		Frame frame = new MainFrame(new SimplebehaviorApp(), 800, 800);
	} // end of main (method of SimplebehaviorApp)

} // end of class SimplebehaviorApp
