package com.recast;

import com.sun.j3d.utils.applet.MainFrame;
import org.draw.DrawingApplet;
import org.junit.Test;
import org.recast.Recast.Source.BuildContextImpl;
import org.recast.RecastDemo.Include.Sample_SoloMesh;
import org.recast.RecastDemo.Source.InputGeomImpl;
import org.recast.RecastDemo.Source.Sample_SoloMeshImpl;

import java.io.File;
import java.net.URL;

/**
 * @author igozha
 * @since 22.09.13 15:14
 */
public class RecastDrawTest
{
	@Test
	public void doTest() throws Exception
	{
		InputGeomImpl geom = new InputGeomImpl();
		URL source = RecastTest.class.getClassLoader().getResource("mesh/dungeon.obj");
		File sourceFile = new File(source.toURI());
		boolean loaded = geom.loadMesh(sourceFile);
		System.out.println("loaded = " + loaded);

		Sample_SoloMesh sample = new Sample_SoloMeshImpl();
		sample.setContext(new BuildContextImpl());
		sample.handleMeshChanged(geom);
		sample.handleBuild();
		assert sample.m_pmesh.nverts == 223;
		assert sample.m_pmesh.npolys == 120;

		DrawingApplet applet = new DrawingApplet();
		sample.handleRender(applet);
		MainFrame mf = new MainFrame(applet, 800, 800);
	}
}
