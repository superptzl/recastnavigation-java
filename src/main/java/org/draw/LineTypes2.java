
package org.draw;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

/**
 * @author igozha
 * @since 22.09.13 15:28
 */
public class LineTypes2 extends Applet
{

	SimpleUniverse u;

	boolean isApplication;

	Canvas3D canvas;

	View view;

	/* image capture */
	OffScreenCanvas3D offScreenCanvas;

	float offScreenScale = 1.0f;

	String snapImageString = "Snap Image";

	// GUI elements
	JTabbedPane tabbedPane;

	// Temporaries that are reused
	Transform3D tmpTrans = new Transform3D();

	Vector3f tmpVector = new Vector3f();

	AxisAngle4f tmpAxisAngle = new AxisAngle4f();

	// colors for use in the cones
	Color3f red = new Color3f(1.0f, 0.0f, 0.0f);

	Color3f black = new Color3f(0.0f, 0.0f, 0.0f);

	Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

	// geometric constants
	Point3f origin = new Point3f();

	Vector3f yAxis = new Vector3f(0.0f, 1.0f, 0.0f);

	// NumberFormat to print out floats with only two digits
	NumberFormat nf;

	// Returns the TransformGroup we will be editing to change the tranform
	// on the lines
	Shape3D createLineTypes()
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
//		return lineGroup;

	}

	BranchGroup createSceneGraph()
	{
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create a TransformGroup to scale the scene down by 3.5x
		// TODO: move view platform instead of scene using orbit behavior
		TransformGroup objScale = new TransformGroup();
		Transform3D scaleTrans = new Transform3D();
		//scaleTrans.set(1 / 3.5f); // scale down by 3.5x
		objScale.setTransform(scaleTrans);
		objRoot.addChild(objScale);

		// Create a TransformGroup and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// the mouse behaviors code can modify it at runtime. Add it to the
		// root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);

		// Add the primitives to the scene
		objTrans.addChild(createLineTypes());

		BoundingSphere bounds = new BoundingSphere(new Point3d(), 100.0);
		Background bg = new Background(new Color3f(0, 0, 0));
		bg.setApplicationBounds(bounds);
		objTrans.addChild(bg);

		// set up the mouse rotation behavior
		MouseRotate mr = new MouseRotate();
		mr.setTransformGroup(objTrans);
		mr.setSchedulingBounds(bounds);
		mr.setFactor(0.007);
		objTrans.addChild(mr);

		// Set up the ambient light
		Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLightNode);

		// Set up the directional lights
		Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f light1Direction = new Vector3f(0.0f, -0.2f, -1.0f);

		DirectionalLight light1 = new DirectionalLight(light1Color,
													   light1Direction);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);

		return objRoot;
	}

	public LineTypes2()
	{
		this(false);
	}

	public LineTypes2(boolean isApplication)
	{
		this.isApplication = isApplication;
	}

	public void init()
	{

		// set up a NumFormat object to print out float with only 3 fraction
		// digits
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);

		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse
			.getPreferredConfiguration();

		canvas = new Canvas3D(config);

		add("Center", canvas);

		// Create a simple scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph();
		u = new SimpleUniverse(canvas);
//		this.u = simpleU;
		ViewingPlatform ourView = u.getViewingPlatform();
		Transform3D locator = new Transform3D();
				locator.setTranslation(new Vector3f(0, 3f, -3f));
				locator.lookAt(new Point3d(0d, 0.1d, 0d), new Point3d(0.1d, 0d, 0d), new Vector3d(0d, 0d, 0.1d));
//				locator.invert();
				ourView.getViewPlatformTransform().setTransform(locator);

		if (isApplication)
		{
			offScreenCanvas = new OffScreenCanvas3D(config, true);
			// set the size of the off-screen canvas based on a scale
			// of the on-screen size
			Screen3D sOn = canvas.getScreen3D();
			Screen3D sOff = offScreenCanvas.getScreen3D();
			Dimension dim = sOn.getSize();
			dim.width *= 1.0f;
			dim.height *= 1.0f;
			sOff.setSize(dim);
			sOff.setPhysicalScreenWidth(sOn.getPhysicalScreenWidth()
											* offScreenScale);
			sOff.setPhysicalScreenHeight(sOn.getPhysicalScreenHeight()
											 * offScreenScale);

			// attach the offscreen canvas to the view
			u.getViewer().getView().addCanvas3D(offScreenCanvas);
		}

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);

		view = u.getViewer().getView();

		add("South", guiPanel());
	}

	// create a panel with a tabbed pane holding each of the edit panels
	JPanel guiPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));

		if (isApplication)
		{
			JButton snapButton = new JButton(snapImageString);
			snapButton.setActionCommand(snapImageString);
			snapButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.out.println("something");
				}
			});
			panel.add(snapButton);
		}

		return panel;
	}



	public void destroy()
	{
		u.removeAllLocales();
	}

	// The following allows LineTypes to be run as an application
	// as well as an applet
	//
	public static void main(String[] args)
	{
		new MainFrame(new LineTypes2(true), 600, 600);
	}
}

