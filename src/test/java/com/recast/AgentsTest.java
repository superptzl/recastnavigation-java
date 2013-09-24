package com.recast;

import org.junit.Test;
import org.recast.DetourCrowd.Source.dtCrowdImpl;
import org.recast.Recast.Source.BuildContextImpl;
import org.recast.RecastDemo.Include.CrowdTool;
import org.recast.RecastDemo.Include.Sample_SoloMesh;
import org.recast.RecastDemo.Source.CrowdToolImpl;
import org.recast.RecastDemo.Source.InputGeomImpl;
import org.recast.RecastDemo.Source.Sample_SoloMeshImpl;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class AgentsTest {
    @Test
    public void testAddAgent() throws URISyntaxException {
        InputGeomImpl geom = new InputGeomImpl();
        URL source = RecastTest.class.getClassLoader().getResource("mesh/dungeon.obj");
        File sourceFile = new File(source.toURI());
        boolean loaded = geom.loadMesh(sourceFile);
        System.out.println("loaded = " + loaded);

        Sample_SoloMesh sample = new Sample_SoloMeshImpl();
        sample.setContext(new BuildContextImpl());
        sample.handleMeshChanged(geom);
        sample.handleBuild();

        CrowdToolImpl crowdTool = new CrowdToolImpl();
		sample.m_tool = crowdTool;
        sample.m_tool.init(sample);
        sample.m_crowd = new dtCrowdImpl();
        sample.m_crowd.init(10, 1, sample.getNavMesh());

        crowdTool.m_mode = CrowdTool.ToolMode.TOOLMODE_CREATE;

		sample.handleClick(new float[]{76.728340f,84.241943f,24.204132f}, new float[]{38.056499f, 9.9981785f, 1.7914636f}, false);

        crowdTool.m_mode = CrowdTool.ToolMode.TOOLMODE_MOVE_TARGET;
        sample.handleClick(new float[]{76.728340f,84.241943f,24.204132f}, new float[]{38.056499f, 9.9981785f, 1.7914636f}, false);
        sample.handleStep();
    }
}
