/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.xcine.gameserver.event;

/**
 * @author Rizel
 *
 */
public class EventTeam implements Comparable<EventTeam>
{
	private int score;

	private String name;

	private int[] nameColor;

	private int[] startPos;

	private int id;

	public EventTeam(int id, String name, int[] color, int[] startPos)
	{
		this.id = id;
		score = 0;
		this.name = name;
		nameColor = color;
		this.startPos = startPos;
	}

	@Override
	public int compareTo(EventTeam second)
	{
		if (getScore() > second.getScore())
			return 1;
		if (getScore() < second.getScore())
			return -1;
		if (getScore() == second.getScore())
			return 0;

		return 0;
	}

	protected String getHexaColor()
	{
		String hexa;
		Integer i1 = nameColor[0];
		Integer i2 = nameColor[1];
		Integer i3 = nameColor[2];
		hexa = "" + (i1 > 15 ? Integer.toHexString(i1) : "0" + Integer.toHexString(i1)) + (i2 > 15 ? Integer.toHexString(i2) : "0" + Integer.toHexString(i2)) + (i3 > 15 ? Integer.toHexString(i3) : "0" + Integer.toHexString(i3));
		return hexa;
	}

	protected int getId()
	{
		return id;
	}

	protected String getName()
	{
		return name;
	}

	protected int getScore()
	{
		return score;
	}

	protected int[] getTeamColor()
	{
		return nameColor;
	}

	protected int[] getTeamPos()
	{
		return startPos;
	}

	protected void increaseScore()
	{
		score++;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */

	protected void increaseScore(int ammount)
	{
		score += ammount;
	}

	protected void setScore(int ammount)
	{
		score = ammount;
	}
}
