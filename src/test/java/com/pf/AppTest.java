package com.pf;

import com.pf.domain.Obstacle;
import com.pf.domain.Unit;
import com.pf.engine.GameEngine;
import com.pf.util.Point;
import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppTest
{
	List<Obstacle> obstacles = new ArrayList<>();
	List<Unit> gameUnits = new ArrayList<>();
	Point spawnPoint;

	public GameEngine getGameEngine()
	{
		return null;//new StraightedgeGameEngine();
	}

	@Before
	public void setup()
	{
		int offest = 100;
		int size = 500;
		//add middle box
		int boxSize = 40;
		obstacles.add(new Obstacle(new Point(offest + size / 2 - boxSize, offest + size / 2 - boxSize), new Point(offest + size / 2 + boxSize, offest + size / 2 + boxSize)));

		//add bounds
		obstacles.add(new Obstacle(new Point(offest - 10, offest - 10), new Point(offest + 0, offest + 10 + size)));
		obstacles.add(new Obstacle(new Point(offest - 10, offest - 10), new Point(offest + 10 + size, offest + 0)));
		obstacles.add(new Obstacle(new Point(offest - 10, offest + size), new Point(offest + 10 + size, offest + 10 + size)));
		obstacles.add(new Obstacle(new Point(offest + size, offest - 10), new Point(offest + 10 + size, offest + 10 + size)));

		spawnPoint = new Point(offest + 10, offest + 10);
		//create 30 units
		final int unitsCount = 10;
		for (int i = 0; i < unitsCount; i++)
		{
			Unit unit = new Unit();
			unit.id = i;
			unit.position = spawnPoint;
			gameUnits.add(unit);
		}
	}

	@Test
	public void testPathFinding()
	{
		GameEngine engine = getGameEngine();

		for (Obstacle obstacle : obstacles)
		{
			engine.addObstacle(obstacle);
		}

		for (Unit unit : gameUnits)
		{
			engine.addUnit(unit);
		}

		//let your engine to start/initialize the game
		engine.startGame();

		tickGame(engine);

		Collection<Unit> units = engine.getUnits();
		for (Unit unit : units)
		{
			assert unit.position.distanceTo(spawnPoint) < 5 * 4;
		}

		//move all units to other position
		Point destination = new Point(100 + 490, 100 + 490);
		for (Unit unit : units)
		{
			engine.moveUnitTo(unit.id, destination);
		}

		//give them time to go
		int acceptedTime = 150;
		for (int i = 0; i < acceptedTime; i++)
		{
			tickGame(engine);
		}

		//check destination
		units = engine.getUnits();
		for (Unit unit : units)
		{
//			assert unit.position.distanceTo(destination) < 5 * 4;
		}
		while (true) {
			try
			{
				Thread.sleep(1000l);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	private void ensureUnitsNotInObstacle(GameEngine engine)
	{
		Collection<Unit> units = engine.getUnits();
		for (Unit unit : units)
		{
			for (Obstacle obstacle : obstacles)
			{
				Circle unitSpace = new Circle(unit.position, unit.radius);
				assert !unitSpace.intersects(obstacle);
			}
		}
	}

	private void ensureUnitsDoNotIntersect(GameEngine engine)
	{
		Collection<Unit> units = engine.getUnits();
		List<Circle> unitSpaces = new ArrayList<>();
		for (Unit unit : units)
		{
			Circle unitSpace = new Circle(unit.position, unit.radius);
			for (Circle existingSpace : unitSpaces)
			{
				boolean notIntersects = !existingSpace.intersects(unitSpace);
				assert notIntersects;
			}
			unitSpaces.add(unitSpace);
		}
	}

	public void tickGame(GameEngine engine)
	{
		long start = System.currentTimeMillis();
		engine.tick();
		long end = System.currentTimeMillis();
		assert (end - start) < 100;

		//ensure units do not intersect with each other
//		ensureUnitsDoNotIntersect(engine);
//		ensureUnitsNotInObstacle(engine);
	}

	@Test
	public void testIntersectCircle()
	{
		Circle first = new Circle(new Point(0, 0), 5);
		Circle second = new Circle(new Point(5, 5), 1);
		assert !first.intersects(second);

		Circle third = new Circle(new Point(5, 5), 3);
		assert first.intersects(third);
	}

	private static class Circle
	{
		private Circle(Point center, double radius)
		{
			this.center = center;
			this.radius = radius;
		}

		public boolean intersects(LineSegment line)
		{
			return new Line2D.Double(line.start.x, line.start.y, line.end.x, line.end.y).ptSegDist(center.x, center.y) <= radius;
		}

		public boolean intersects(Obstacle obstacle)
		{
			return obstacle.containsPoint(center) ||
				intersects(new LineSegment(obstacle.connerOne, new Point(obstacle.connerOne.x, obstacle.connerTwo.y))) ||
				intersects(new LineSegment(obstacle.connerOne, new Point(obstacle.connerTwo.x, obstacle.connerOne.y))) ||
				intersects(new LineSegment(obstacle.connerTwo, new Point(obstacle.connerTwo.x, obstacle.connerOne.y))) ||
				intersects(new LineSegment(obstacle.connerTwo, new Point(obstacle.connerOne.x, obstacle.connerTwo.y)));
		}

		public boolean intersects(Circle other)
		{
			double dx = center.x - other.center.x;
			double dy = center.y - other.center.y;

			double d = Math.sqrt(dx * dx + dy * dy);

			double r = radius + other.radius;

			return d <= r;
		}

		public Point center;
		public double radius;
	}

	private static class LineSegment
	{
		private LineSegment(Point start, Point end)
		{
			this.start = start;
			this.end = end;
		}

		public Point start;
		public Point end;
	}
}
