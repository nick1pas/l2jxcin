/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.xcine.gameserver.model.quest;

import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;

import java.util.List;

import javolution.util.FastList;

import net.xcine.gameserver.model.actor.instance.L2PcInstance;

public class QuestStateManager
{
	public class ScheduleTimerTask implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				cleanUp();
				ThreadPoolManager(new ScheduleTimerTask(), 60000);
			}
			catch(Throwable t)
			{
				
			}
		}

		/**
		 * @param scheduleTimerTask
		 * @param i
		 */
		private void ThreadPoolManager(ScheduleTimerTask scheduleTimerTask, int i)
		{
			// TODO Auto-generated method stub
			
		}
	}

	private static QuestStateManager _instance;
	private List<QuestState> _questStates = new FastList<QuestState>();

	public QuestStateManager()
	{
		ThreadPoolManager(new ScheduleTimerTask(), 60000);
	}

	/**
	 * @param scheduleTimerTask
	 * @param i
	 */
	private void ThreadPoolManager(ScheduleTimerTask scheduleTimerTask, int i)
	{
		// TODO Auto-generated method stub
		
	}

	public static final QuestStateManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new QuestStateManager();
		}

		return _instance;
	}

	public void addQuestState(Quest quest, L2PcInstance player, State state, boolean completed)
	{
		QuestState qs = getQuestState(player);

		if(qs == null)
		{
			qs (quest, player, state, completed);
		}
	}

	/**
	 * @param quest
	 * @param player
	 * @param state
	 * @param completed
	 */
	private void qs(Quest quest, L2PcInstance player, State state, boolean completed)
	{
		// TODO Auto-generated method stub
		
	}

	public void cleanUp()
	{
		for(int i = getQuestStates().size() - 1; i >= 0; i--)
		{
			if(getQuestStates().get(i).getPlayer() == null)
			{
				removeQuestState(getQuestStates().get(i));
				getQuestStates().remove(i);
			}
		}
	}

	private void removeQuestState(QuestState qs)
	{
		qs = null;
	}

	public QuestState getQuestState(L2PcInstance player)
	{
		for(int i = 0; i < getQuestStates().size(); i++)
		{
			if(getQuestStates().get(i).getPlayer() != null && getQuestStates().get(i).getPlayer().getObjectId() == player.getObjectId())
			{
				return getQuestStates().get(i);
			}
		}
		return null;
	}

	public List<QuestState> getQuestStates()
	{
		if(_questStates == null)
		{
			_questStates = new FastList<QuestState>();
		}
		return _questStates;
	}
}