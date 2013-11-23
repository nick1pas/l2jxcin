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
package net.xcine.gameserver.taskmanager.tasks;

import java.util.Collection;
import java.util.logging.Logger;

import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.network.serverpackets.UserInfo;
import net.xcine.gameserver.taskmanager.TaskManager;
import net.xcine.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.xcine.gameserver.taskmanager.models.Task;
import net.xcine.gameserver.taskmanager.models.TaskTypes;

/**
 * @author Layane
 */
public class TaskRecom extends Task
{
	private static final Logger _log = Logger.getLogger(TaskRecom.class.getName());
	private static final String NAME = "sp_recommendations";
	
	/*
	 * (non-Javadoc)
	 * @see net.xcine.gameserver.taskmanager.Task#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.xcine.gameserver.taskmanager.Task#onTimeElapsed(net.xcine.gameserver.taskmanager.TaskManager.ExecutedTask)
	 */
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();
		
		for (L2PcInstance player : pls)
		{
			player.restartRecom();
			player.sendPacket(new UserInfo(player));
		}
		_log.config("Recommendation Global Task: launched.");
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}