package com.lyeeedar.DungeonGeneration.Data

import com.badlogic.gdx.utils.ObjectMap
import com.exp4j.Helpers.EquationHelper
import com.lyeeedar.Enums
import com.lyeeedar.Util.Array2D
import com.lyeeedar.Util.Point
import java.util.*

/**
 * Created by Philip on 08-Apr-16.
 */

class SymbolicRoom()
{
	lateinit var contents: Array2D<Symbol>
	val width: Int
		get() = contents.xSize
	val height: Int
		get() = contents.ySize

	var x: Int = 0
	var y: Int = 0

	var placement: Enums.Direction = Enums.Direction.CENTER

	// ----------------------------------------------------------------------
	private fun addDoor(pos: Int, space: Int, dir: Enums.Direction, ran: Random, symbolMap: ObjectMap<Char, Symbol>)
	{
		val offset = if (space > 1) ran.nextInt(space - 1) else 0

		if (dir === Enums.Direction.WEST)
		{
			doors.add(RoomDoor(Enums.Direction.WEST, Point(0, pos + offset)))
		}
		else if (dir === Enums.Direction.EAST)
		{
			doors.add(RoomDoor(Enums.Direction.EAST, Point(width - 1, pos + offset)))
		}
		else if (dir === Enums.Direction.NORTH)
		{
			doors.add(RoomDoor(Enums.Direction.NORTH, Point(pos + offset, 0)))
		}
		else if (dir === Enums.Direction.SOUTH)
		{
			doors.add(RoomDoor(Enums.Direction.SOUTH, Point(pos + offset, height - 1)))
		}
	}

	// ----------------------------------------------------------------------
	private fun processSide(dir: Enums.Direction, ran: Random, symbolMap: ObjectMap<Char, Symbol>)
	{
		val range = if (dir === Enums.Direction.WEST || dir === Enums.Direction.EAST) height else width

		var blockStart = -1
		for (pos in 1..range - 1 - 1)
		{
			var x = 0
			var y = 0

			if (dir === Enums.Direction.WEST)
			{
				x = 0
				y = pos
			} else if (dir === Enums.Direction.EAST)
			{
				x = width - 1
				y = pos
			} else if (dir === Enums.Direction.NORTH)
			{
				x = pos
				y = 0
			} else
			{
				x = pos
				y = height - 1
			}

			if (blockStart >= 0)
			{
				if (!contents[x, y].getPassable(Enums.SpaceSlot.ENTITY, null))
				{
					addDoor(blockStart, pos - blockStart, dir, ran, symbolMap)
					blockStart = -1
				}
			} else
			{
				if (contents[x,y].getPassable(Enums.SpaceSlot.ENTITY, null))
				{
					blockStart = pos
				}
			}
		}

		if (blockStart >= 0)
		{
			val pos = range - 1
			addDoor(blockStart, pos - blockStart, dir, ran, symbolMap)
		}
	}

	// ----------------------------------------------------------------------
	fun findDoors(ran: Random, symbolMap: ObjectMap<Char, Symbol>)
	{
		// Sides
		//  1
		// 0 2
		//  3

		// Side 0
		processSide(Enums.Direction.WEST, ran, symbolMap)

		// Side 2
		processSide(Enums.Direction.EAST, ran, symbolMap)

		// Side 1
		processSide(Enums.Direction.NORTH, ran, symbolMap)

		// Side 3
		processSide(Enums.Direction.SOUTH, ran, symbolMap)
	}

	// ----------------------------------------------------------------------
	fun fill(ran: Random, data: SymbolicRoomData)
	{
		placement = data.placement

		x = data.x
		y = data.y

		data.ran = ran
		val w = data.widthVal
		val h = data.heightVal

		contents = Array2D<Symbol>(w, h) { x, y -> data.symbolMap['#'].copy() }

		if (data.generator != null)
		{
			// generate the room
		}
		else
		{
			for (x in 0..w-1)
			{
				for (y in 0..h-1)
				{
					contents[x, y] = data.symbolMap[data.contents[x, y]].copy()
				}
			}
		}
	}
}