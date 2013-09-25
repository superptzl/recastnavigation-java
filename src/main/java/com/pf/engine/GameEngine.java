package com.pf.engine;

import com.pf.domain.Obstacle;
import com.pf.domain.Unit;
import com.pf.util.Point;

import java.util.Collection;

/**
 * @author igozha
 * @since 07.09.13 08:41
 */
public interface GameEngine
{
	void addObstacle(Obstacle box);

	void startGame();

	void tick();

	void addUnit(Unit unit);

	Collection<Unit> getUnits();

	void moveUnitTo(int unitId, Point destination);
}
