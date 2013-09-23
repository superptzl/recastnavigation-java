package com.recast;

import org.junit.Test;
import org.recast.Recast.Source.BuildContextImpl;
import org.recast.RecastDemo.Include.Sample_SoloMesh;
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


    }
}
