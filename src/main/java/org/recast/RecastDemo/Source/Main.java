package org.recast.RecastDemo.Source;

import com.sun.j3d.utils.applet.MainFrame;
import org.recast.Recast.Source.BuildContextImpl;
import org.recast.RecastDemo.Include.Sample_SoloMesh;

import java.awt.*;

/**
 * @author igozha
 * @since 18.09.13 19:51
 */
public class Main
{
	public static void main(String[] args)
	{
		InputGeomImpl geom = new InputGeomImpl();
//		boolean loaded = geom.loadMesh("D:\\link\\study\\pathfinding\\test1\\recast\\RecastDemo\\Bin\\Meshes\\dungeon.obj");
//		boolean loaded = geom.loadMesh("D:\\work\\tests\\test1_v3\\recast\\RecastDemo\\Bin\\Meshes\\dungeon.obj");
//		boolean loaded = geom.loadMesh("D:\\link\\study\\pathfinding\\Recast_tests\\test1_v3_2\\recast\\RecastDemo\\Bin\\Meshes\\nav_test.obj");
		boolean loaded = geom.loadMesh("D:\\link\\study\\pathfinding\\Recast_tests\\test1_v3_2\\recast\\RecastDemo\\Bin\\Meshes\\dungeon.obj");
		System.out.println("loaded = " + loaded);

        Sample_SoloMesh sample = new Sample_SoloMeshImpl();
        sample.setContext(new BuildContextImpl());
        sample.handleMeshChanged(geom);
        sample.handleBuild();


//		Frame frame = new MainFrame(new DrawingApplet(geom), 800, 800);
//		new DrawingApplet(geom);
	}
}
