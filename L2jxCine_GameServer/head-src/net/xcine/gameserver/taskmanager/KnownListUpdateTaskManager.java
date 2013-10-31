package net.xcine.gameserver.taskmanager;

import java.util.logging.Logger;

import net.xcine.Config;
import net.xcine.gameserver.model.L2Character;
import net.xcine.gameserver.model.L2Object;
import net.xcine.gameserver.model.L2Playable;
import net.xcine.gameserver.model.L2World;
import net.xcine.gameserver.model.L2WorldRegion;
import net.xcine.gameserver.thread.ThreadPoolManager;

public class KnownListUpdateTaskManager
{
	protected static final Logger _log = Logger.getLogger(DecayTaskManager.class.getName());

	private static KnownListUpdateTaskManager _instance;

	public KnownListUpdateTaskManager()
	{
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new KnownListUpdate(), 1000, 750);
	}

	public static KnownListUpdateTaskManager getInstance()
	{
		if(_instance == null)
		{
			_instance = new KnownListUpdateTaskManager();
		}

		return _instance;
	}

	private class KnownListUpdate implements Runnable
	{
		boolean toggle = false;
		boolean fullUpdate = true;

		protected KnownListUpdate()
		{
		// Do nothing
		}

		@Override
		public void run()
		{
			try
			{
				for(L2WorldRegion regions[] : L2World.getInstance().getAllWorldRegions())
				{
					for(L2WorldRegion r : regions) // go through all world regions
					{
						if(r.isActive()) // and check only if the region is active
						{
							updateRegion(r, fullUpdate, toggle);
						}
					}
				}
				if(toggle)
				{
					toggle = false;
				}
				else
				{
					toggle = true;
				}
				if(fullUpdate)
				{
					fullUpdate = false;
				}

			}
			catch(Throwable e)
			{
				if(Config.ENABLE_ALL_EXCEPTIONS)
					e.printStackTrace();
				
				_log.warning(e.toString());
			}
		}
	}

	public void updateRegion(L2WorldRegion region, boolean fullUpdate, boolean forgetObjects)
	{
		for(L2Object object : region.getVisibleObjects()) // and for all members in region
		{
			if(!object.isVisible())
			{
				continue; // skip dying objects
			}
			if(forgetObjects)
			{
				object.getKnownList().forgetObjects(); //TODO
				continue;
			}
			if(object instanceof L2Playable /*|| (false && object instanceof L2GuardInstance)*/|| fullUpdate)
			{
				for(L2WorldRegion regi : region.getSurroundingRegions()) // offer members of this and surrounding regions
				{
					for(L2Object _object : regi.getVisibleObjects())
					{
						if(_object != object)
						{
							object.getKnownList().addKnownObject(_object);
						}
					}
				}
			}
			else if(object instanceof L2Character)
			{
				for(L2WorldRegion regi : region.getSurroundingRegions()) // offer members of this and surrounding regions
				{
					if(regi.isActive())
					{
						for(L2Object _object : regi.getVisibleObjects())
						{
							if(_object != object)
							{
								object.getKnownList().addKnownObject(_object);
							}
						}
					}
				}
			}

		}
	}
}
