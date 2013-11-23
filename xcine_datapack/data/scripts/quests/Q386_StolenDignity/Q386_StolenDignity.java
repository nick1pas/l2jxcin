package quests.Q386_StolenDignity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q386_StolenDignity extends Quest
{
    private static final String qn = "Q386_StolenDignity";
    
    // NPCs
    private static final int ROMP = 30843;
    
    // Quest items
    private static final int STOLEN_INFINIUM_ORE = 6363;
    
    private static final Map<Integer, Integer> DROP_CHANCE = new HashMap<>();
    {
        DROP_CHANCE.put(20670, 14);
        DROP_CHANCE.put(20671, 14);
        DROP_CHANCE.put(20954, 11);
        DROP_CHANCE.put(20956, 13);
        DROP_CHANCE.put(20958, 13);
        DROP_CHANCE.put(20959, 13);
        DROP_CHANCE.put(20960, 11);
        DROP_CHANCE.put(20964, 13);
        DROP_CHANCE.put(20967, 18);
        DROP_CHANCE.put(20969, 19);
        DROP_CHANCE.put(20970, 18);
        DROP_CHANCE.put(20971, 18);
        DROP_CHANCE.put(20974, 28);
        DROP_CHANCE.put(20975, 28);
        DROP_CHANCE.put(21001, 14);
        DROP_CHANCE.put(21003, 18);
        DROP_CHANCE.put(21005, 14);
        DROP_CHANCE.put(21020, 16);
        DROP_CHANCE.put(21021, 15);
        DROP_CHANCE.put(21089, 13);
        DROP_CHANCE.put(21108, 19);
        DROP_CHANCE.put(21110, 18);
        DROP_CHANCE.put(21113, 25);
        DROP_CHANCE.put(21114, 23);
        DROP_CHANCE.put(21116, 25);
    };
    
    private static final int[][] LINES = 
    {
        // Horizontal
        {0, 1, 2},
        {3, 4, 5},
        {6, 7, 8},
        // Vertical
        {0, 3, 6},
        {1, 4, 7},
        {2, 5, 8},
        // Diagonal
        {0, 4, 8},
        {2, 4, 6}
    };
    
    private static final String[] TEMPLATE = 
    {
        "%msg%<br><br>%choices%<br><br>%board%",
        "%msg%<br><br>%board%",
        "For your information, below is your current selection.<br><table border=\"1\" border color=\"white\" width=100><tr><td align=\"center\">%cell1%</td><td align=\"center\">%cell2%</td><td align=\"center\">%cell3%</td></tr><tr><td align=\"center\">%cell4%</td><td align=\"center\">%cell5%</td><td align=\"center\">%cell6%</td></tr><tr><td align=\"center\">%cell7%</td><td align=\"center\">%cell8%</td><td align=\"center\">%cell9%</td></tr></table>",
        "<a action=\"bypass -h Quest Q386_StolenDignity choice-%n%\">%n%</a>&nbsp;&nbsp;&nbsp;&nbsp;"
    };
    
    protected final static String[] MESSAGE = 
    {
        "You have already selected that number. Choose your %choicenum% number again.",
        "I've arranged 9 numbers on the panel.<br>Now, select your %choicenum% number.",
        "Now, choose your %choicenum% number.",
        "You are spectacularly unlucky! The red-colored numbers on the panel below are the ones you chose. As you can see, they didn't create even a single line. Did you know that it is harder not to create a single line than creating all 3 lines?",
        "You've created 3 lines! The red colored numbers on the bingo panel below are the numbers you chose. Congratulations!",
        "Hmm... You didn't make 3 lines. Why don't you try again? The red-colored numbers on the panel are the ones you chose."
    };
    
    protected final static String[] NUMBERS = 
    {
        "first", 
        "second", 
        "third", 
        "fourth", 
        "fifth", 
        "final"
    };
    
    private final List<Integer> guesses = new ArrayList<>();
    private final Map<Integer, List<Integer>> GAMES = new HashMap<>();
    
    // L2OFF Interlude rewards
    private final static int[][] REWARDS = 
    {
        {5529, 10, 4}, // dragon_slayer_edge
        {5532, 10, 4}, // meteor_shower_head
        {5533, 10, 4}, // elysian_head
        {5534, 10, 4}, // soul_bow_shaft
        {5535, 10, 4}, // carnium_bow_shaft
        {5536, 10, 4}, // bloody_orchid_head
        {5537, 10, 4}, // soul_separator_head
        {5538, 10, 4}, // dragon_grinder_edge
        {5539, 10, 4}, // blood_tornado_edge
        {5541, 10, 4}, // tallum_glaive_edge
        {5542, 10, 4}, // halbard_edge
        {5543, 10, 4}, // dasparion_s_staff_head
        {5544, 10, 4}, // worldtree_s_branch_head
        {5545, 10, 4}, // dark_legion_s_edge_edge
        {5546, 10, 4}, // sword_of_miracle_edge
        {5547, 10, 4}, // elemental_sword_edge
        {5548, 10, 4} // tallum_blade_edge
    };
    
    public Q386_StolenDignity()
    {
        super(386, qn, "Stolen Dignity");
        
        questItemIds = new int[]
        {
            STOLEN_INFINIUM_ORE
        };
        
        addStartNpc(ROMP);
        addTalkId(ROMP);
        
        for (Integer i : DROP_CHANCE.keySet())
            addKillId(i);
    }
    
    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        String htmltext = event;
        QuestState st = player.getQuestState(qn);
        if (st == null)
            return htmltext;
        
        if (event.equalsIgnoreCase("30843-05.htm"))
        {
            st.set("cond", "1");
            st.setState(STATE_STARTED);
            st.playSound(QuestState.SOUND_ACCEPT);
        }
        else if(event.equals("game"))
        {
            if (st.getQuestItemsCount(STOLEN_INFINIUM_ORE) < 100)
            {
                htmltext = "30843-07.htm";
                return htmltext;
            }
            
            st.takeItems(STOLEN_INFINIUM_ORE, 100);
            
            int _objectId = st.getPlayer().getObjectId();
            
            if (GAMES.containsKey(_objectId))
            {
                GAMES.remove(_objectId);
                guesses.clear();
            }
            
            GAMES.put(_objectId, getNewBoard());
            
            htmltext = getHtmlText("30843-02.htm").replace("%content%", getDialog(player, ""));
        }
        else if (event.startsWith("choice-"))
        {
            int _objectId = st.getPlayer().getObjectId();
            
            if (!GAMES.containsKey(_objectId))
                return null;
            
            htmltext = getHtmlText("30843-02.htm").replace("%content%", Select(player, event.replaceFirst("choice-", "")));
        }
        else if (event.equalsIgnoreCase("30843-08.htm"))
        {
            st.playSound(QuestState.SOUND_FINISH);
            st.exitQuest(true);
        }
        
        return htmltext;
    }
    
    @Override
    public String onTalk(L2Npc npc, L2PcInstance player)
    {
        String htmltext = getNoQuestMsg();
        QuestState st = player.getQuestState(qn);
        if (st == null)
            return htmltext;
        
        switch (st.getState())
        {
            case STATE_CREATED:
                if (player.getLevel() < 58)
                    htmltext = "30843-04.htm";
                else
                    htmltext = "30843-01.htm";
                break;
                
            case STATE_STARTED:
                if (st.getQuestItemsCount(STOLEN_INFINIUM_ORE) >= 100)
                    htmltext = "30843-07.htm";
                else
                    htmltext = "30843-06.htm";
                break;
        }
        
        return htmltext;
    }
    
    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        QuestState st = checkPlayerCondition(player, npc, "cond", "1");
        if (st == null)
            return null;
        
        st.dropItems(STOLEN_INFINIUM_ORE, 1, 0, (DROP_CHANCE.get(npc.getNpcId()) * 10000));
        
        return null;
    }
    
    public String Select(L2PcInstance player, String s)
    {
        try
        {
            return Select(player, Integer.valueOf(s));
        }
        catch(Exception E)
        {
            return null;
        }
    }
    
    public String Select(L2PcInstance player, int choice)
    {
        if (choice < 1 || choice > 9)
            return null;
        
        if (guesses.contains(choice))
            return getDialog(player, MESSAGE[0]);
        
        guesses.add(choice);
        
        if (guesses.size() == 6)
            return getFinal(player);
        
        return getDialog(player, "");
    }
    
    public String getFinal(L2PcInstance player)
    {
        QuestState st = player.getQuestState(qn);
        
        String result = TEMPLATE[1].replaceFirst("%board%", getBoard(player));
        
        int _lines = checkLines(player);
        int[] _item = REWARDS[Rnd.get(REWARDS.length)];
        
        if (_lines == 3)
        {
            st.rewardItems(_item[0], _item[2]);
            result = result.replaceFirst("%msg%", MESSAGE[4]);
        }
        else if (_lines == 0)
        {
            st.rewardItems(_item[0], _item[1]);
            result = result.replaceFirst("%msg%", MESSAGE[3]);
        }
        else
            result = result.replaceFirst("%msg%", MESSAGE[5]);
        
        guesses.clear();
        GAMES.remove(player.getObjectId());
        
        return result;
    }
    
    public String getBoard(L2PcInstance player)
    {
        if (guesses.size() == 0)
            return "";
        
        List<Integer> _board = GAMES.get(player.getObjectId());
        String result = TEMPLATE[2];
        
        for (int i = 1; i <= 9; i++)
        {
            String cell = "%cell" + String.valueOf(i) + "%";
            int num = _board.get(i - 1);
            
            if (guesses.contains(num))
                result = result.replaceFirst(cell, "<font color=\"" + (guesses.size() == 6 ? "ff0000" : "ffff00") + "\">" + String.valueOf(num) + "</font>");
            else
                result = result.replaceFirst(cell, "?");
        }
        return result;
    }
    
    public int checkLines(L2PcInstance player)
    {
        int _lines  = 0;
        List<Integer> _board = GAMES.get(player.getObjectId());
        
        
        for (int[] _line : LINES)
            if (guesses.contains(_board.get(_line[0])) && guesses.contains(_board.get(_line[1])) && guesses.contains(_board.get(_line[2])))
                _lines += 1;
        
        return _lines;
    }
    
    public String getDialog(L2PcInstance player, String _msg)
    {
        StringBuilder choices = new StringBuilder();
        String result = TEMPLATE[0];
        
        if (guesses.size() == 0)
            result = result.replaceFirst("%msg%", MESSAGE[1]);
        else
            result = result.replaceFirst("%msg%", _msg.equalsIgnoreCase("") ? MESSAGE[2] : _msg);
        
        result = result.replaceFirst("%choicenum%", NUMBERS[guesses.size()]);
        
        for (int i = 1; i <= 9; i++)
            if (!guesses.contains(i))
                choices.append(TEMPLATE[3].replaceAll("%n%", String.valueOf(i)));
        
        result = result.replaceFirst("%choices%", choices.toString());
        result = result.replaceFirst("%board%", getBoard(player));
        
        return result;
    }
    
    public List<Integer> getNewBoard()
    {
        List<Integer> _board = new ArrayList<>();
        
        while(_board.size() < 9)
        {
            int num = Rnd.get(1, 9);
            
            if (!_board.contains(num))
                _board.add(num);
        }
        
        return _board;
    }
    
    public static void main(String[] args)
    {
        new Q386_StolenDignity();
    }
}