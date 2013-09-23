package org.draw;

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import org.recast.RecastDemo.Source.InputGeomImpl;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.applet.Applet;
import java.awt.*;

/**
 * @author igozha
 * @since 22.09.13 15:20
 */
public class DrawingApplet extends Applet
{
	SimpleUniverse simpleU;
	protected float eyeOffset = 0.03F;
//		InputGeomImpl geom;

	public BranchGroup createSceneGraph()
	{

		//		BranchGroup objRoot = new BranchGroup();
		//
		//		Transform3D myTransform3D = new Transform3D();
		//		TransformGroup objTrans = new TransformGroup(myTransform3D);
		//
		//		Appearance polygon1Appearance = new Appearance();
		//		QuadArray polygon1 = new QuadArray(4, QuadArray.COORDINATES);
		//		polygon1.setCoordinate(0, new Point3f(0f, 0f, 0f));
		//		polygon1.setCoordinate(1, new Point3f(2f, 0f, 0f));
		//		polygon1.setCoordinate(2, new Point3f(2f, 3f, 0f));
		//		polygon1.setCoordinate(3, new Point3f(0f, 3f, 0f));
		//		objTrans.addChild(new Shape3D(polygon1, polygon1Appearance));
		//
		//
		//		MouseRotate behavior = new MouseRotate();
		//					behavior.setTransformGroup(objTrans);
		//					objTrans.addChild(behavior);
		//					// Create the translate behavior node
		//					MouseTranslate behavior3 = new MouseTranslate();
		//					behavior3.setTransformGroup(objTrans);
		//					objTrans.addChild(behavior3);
		//
		//		BoundingSphere bounds =
		//						new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		//
		//					behavior3.setSchedulingBounds(bounds);

		BranchGroup objRoot = new BranchGroup();
		try
		{
			Transform3D myTransform3D = new Transform3D();
			myTransform3D.setTranslation(new Vector3f(+0.0f, -0.15f, -3.6f));
			TransformGroup objTrans = new TransformGroup(myTransform3D);
			objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			Transform3D t = new Transform3D();
			TransformGroup tg = new TransformGroup(t);
			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
			tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			objTrans.addChild(tg);
			ObjectFile f = new ObjectFile();
			f.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
			System.out.println("About to load");

			Transform3D myTrans = new Transform3D();
			myTrans.setTranslation(new Vector3f(eyeOffset, -eyeOffset, 0F));
			TransformGroup mytg = new TransformGroup(myTrans);
			//mytg.addChild(s.getSceneGroup());
			tg.addChild(mytg);
			Transform3D myTrans2 = new Transform3D();
			myTrans2.setTranslation(new Vector3f(-eyeOffset, +eyeOffset, 0F));
			TransformGroup mytg2 = new TransformGroup(myTrans2);
			//mytg2.addChild(s.getSceneGroup());

			Appearance polygon1Appearance = new Appearance();

			//					for (int i = 0; i < geom.m_mesh.m_vertCount; i++) {
			//						geom.m_mesh.m_verts[i * 3 + 0]
			//						geom.m_mesh.m_verts[i * 3 + 1]
			//						geom.m_mesh.m_verts[i * 3 + 2]
			Sphere sphere = new Sphere(0.05f);
			//						TriangleArray polygon1 = new TriangleArray(3, TriangleArray.COORDINATES);
			//													polygon1.setCoordinate(0, new Point3f(geom.m_mesh.m_verts[i * 3 + 0], geom.m_mesh.m_verts[i * 3 + 1], geom.m_mesh.m_verts[i * 3 + 2]));
			//													polygon1.setCoordinate(0, new Point3f(geom.m_mesh.m_verts[i * 3 + 0], geom.m_mesh.m_verts[i * 3 + 1]+1f, geom.m_mesh.m_verts[i * 3 + 2]+0f));
			//													polygon1.setCoordinate(0, new Point3f(geom.m_mesh.m_verts[i * 3 + 0], geom.m_mesh.m_verts[i * 3 + 1]+0f, geom.m_mesh.m_verts[i * 3 + 2]+1f));
			//													polygon1.setCoordinate(1, new Point3f(2f, 0f, 0f));
			//													polygon1.setCoordinate(2, new Point3f(2f, 3f, 0f));
			//							polygon1.setCoordinate(3, new Point3f(0f, 3f, 0f));
			//						mytg.addChild(new Shape3D(polygon1, polygon1Appearance));
			mytg.addChild(sphere);
			//					}

			tg.addChild(mytg2);
			System.out.println("Finished Loading");
			BoundingSphere bounds =
				new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
			Color3f light1Color = new Color3f(.9f, 0.9f, 0.9f);
			Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
			DirectionalLight light1
				= new DirectionalLight(light1Color, light1Direction);
			light1.setInfluencingBounds(bounds);
			objTrans.addChild(light1);
			// Set up the ambient light
			Color3f ambientColor = new Color3f(1.0f, .4f, 0.3f);
			AmbientLight ambientLightNode = new AmbientLight(ambientColor);
			ambientLightNode.setInfluencingBounds(bounds);
			objTrans.addChild(ambientLightNode);

			MouseRotate behavior = new MouseRotate();
			behavior.setTransformGroup(tg);
			objTrans.addChild(behavior);
			MouseWheelZoom zoom = new MouseWheelZoom();
			zoom.setTransformGroup(tg);
			objTrans.addChild(zoom);
			zoom.setSchedulingBounds(bounds);
			// Create the translate behavior node
			MouseTranslate behavior3 = new MouseTranslate();
			behavior3.setTransformGroup(tg);
			objTrans.addChild(behavior3);
			behavior3.setSchedulingBounds(bounds);

			KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(tg);
			keyNavBeh.setSchedulingBounds(new BoundingSphere(
				new Point3d(), 1000.0));
			objTrans.addChild(keyNavBeh);

			behavior.setSchedulingBounds(bounds);
			objRoot.addChild(objTrans);
		}
		catch (Throwable t)
		{
			System.out.println("Error: " + t);
		}
		return objRoot;


	}
//
//		public DrawingApplet(InputGeomImpl geom)
//		{
//			this.geom = geom;
//		}

	public void init()
	{

		setLayout(new BorderLayout());

		Canvas3D c = new Canvas3D(SimpleUniverse.getPreferredConfiguration());

		add("Center", c);

		BranchGroup scene = createSceneGraph();

		simpleU = new SimpleUniverse(c);

		TransformGroup tg = simpleU.getViewingPlatform().getViewPlatformTransform();

		Transform3D t3d = new Transform3D();
		t3d.setTranslation(new Vector3f(0f, 0f, 10f));
		tg.setTransform(t3d);

		scene.compile();

		simpleU.addBranchGraph(scene);

	}

	public void destroy()
	{
		simpleU.removeAllLocales();
	}

	//
	public static void main(String[] args)
	{

		Frame frame = new MainFrame(new DrawingApplet(), 800, 800);

	}

}
