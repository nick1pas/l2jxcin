package quests.Q333_HuntOfTheBlackLion;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q333_HuntOfTheBlackLion extends Quest
{
    private static final String qn = "Q333_HuntOfTheBlackLion";
    
    // NPCs
    private static final int SOPHYA = 30735;
    private static final int REDFOOT = 30736;
    private static final int RUPIO = 30471;
    private static final int UNDRIAS = 30130;
    private static final int LOCKIRIN = 30531;
    private static final int MORGAN = 30737;
    
    // Needs for start
    private static final int BLACK_LION_MARK = 1369;
    
    // Quest items
    private static final int LIONS_CLAW = 3675;
    private static final int LIONS_EYE = 3676;
    private static final int GUILD_COIN = 3677;
    private static final int UNDEAD_ASH = 3848;
    private static final int BLOODY_AXE_INSIGNIAS = 3849;
    private static final int DELU_FANG = 3850;
    private static final int STAKATO_TALONS = 3851;
    private static final int SOPHIAS_LETTER1 = 3671;
    private static final int SOPHIAS_LETTER2 = 3672;
    private static final int SOPHIAS_LETTER3 = 3673;
    private static final int SOPHIAS_LETTER4 = 3674;
    
    private static final int CARGO_BOX = 3440;
    private static final int GLUDIO_APPLE = 3444;
    private static final int CORN_MEAL = 3445;
    private static final int WOLF_PELTS = 3446;
    private static final int MOONSTONE = 3447;
    private static final int GLUDIO_WEETS_FLOWER = 3448;
    private static final int SPIDERSILK_ROPE = 3449;
    private static final int ALEXANDRIT = 3450;
    private static final int SILVER_TEA = 3451;
    private static final int GOLEM_PART = 3452;
    private static final int FIRE_EMERALD = 3453;
    private static final int SILK_FROCK = 3454;
    private static final int PORCELAN_URN = 3455;
    private static final int IMPERIAL_DIAMOND = 3456;
    private static final int STATUE_SHILIEN_HEAD = 3457;
    private static final int STATUE_SHILIEN_TORSO = 3458;
    private static final int STATUE_SHILIEN_ARM = 3459;
    private static final int STATUE_SHILIEN_LEG = 3460;
    private static final int COMPLETE_STATUE = 3461;
    private static final int FRAGMENT_ANCIENT_TABLE1 = 3462;
    private static final int FRAGMENT_ANCIENT_TABLE2 = 3463;
    private static final int FRAGMENT_ANCIENT_TABLE3 = 3464;
    private static final int FRAGMENT_ANCIENT_TABLE4 = 3465;
    private static final int COMPLETE_TABLET = 3466;
    
    // Neutral items
    private static final int ADENA = 57;
    private static final int SWIFT_ATTACK_POTION = 735;
    private static final int SCROLL_OF_ESCAPE = 736;
    private static final int HEALING_POTION = 1061;
    private static final int SOULSHOT_D = 1463;
    private static final int SPIRITSHOT_D = 2510;
    
    private static final int[] SOPHIAS_LETTERS = 
    {
        SOPHIAS_LETTER1,
        SOPHIAS_LETTER2,
        SOPHIAS_LETTER3,
        SOPHIAS_LETTER4
    };
    
    private static final int[] STATUE_LIST = 
    {
        STATUE_SHILIEN_HEAD,
        STATUE_SHILIEN_TORSO,
        STATUE_SHILIEN_ARM,
        STATUE_SHILIEN_LEG
    };
    
    private static final int[] TABLET_LIST = 
    {
        FRAGMENT_ANCIENT_TABLE1,
        FRAGMENT_ANCIENT_TABLE2,
        FRAGMENT_ANCIENT_TABLE3,
        FRAGMENT_ANCIENT_TABLE4
    };
    
    // Part #1 - Execution Ground
    private static final int[][] DROPLIST_PART1 = 
    {
        {20160, 67, 29, UNDEAD_ASH}, // Neer Crawler
        {20171, 76, 31, UNDEAD_ASH}, // Specter
        {20197, 89, 25, UNDEAD_ASH}, // Sorrow Maiden
        {20198, 60, 35, UNDEAD_ASH}, // Neer Ghoul Berserker
        {20200, 60, 28, UNDEAD_ASH}, // Strain
        {20201, 70, 29, UNDEAD_ASH}, // Ghoul
    };
    
    // Part #2 - Partisan Hideaway
    private static final int[][] DROPLIST_PART2 = 
    {
        {20207, 69, 29, BLOODY_AXE_INSIGNIAS}, // Ol Mahum Guerilla
        {20208, 67, 32, BLOODY_AXE_INSIGNIAS}, // Ol Mahum Raider
        {20209, 62, 33, BLOODY_AXE_INSIGNIAS}, // Ol Mahum Marksman
        {20210, 78, 23, BLOODY_AXE_INSIGNIAS}, // Ol Mahum Sergeant
        {20211, 71, 22, BLOODY_AXE_INSIGNIAS} // Ol Mahum Captain
    };
    
    // Part #3 - Near Giran Town
    private static final int[][] DROPLIST_PART3 = 
    {
        {20251, 70, 30, DELU_FANG}, // Delu Lizardman
        {20252, 67, 28, DELU_FANG}, // Delu Lizardman Scout
        {20253, 65, 26, DELU_FANG} // Delu Lizardman Warrior 
    };
    
    // Part #4 - Cruma Area
    private static final int[][] DROPLIST_PART4 = 
    {
        {20157, 66, 32, STAKATO_TALONS}, // Marsh Stakato
        {20230, 68, 26, STAKATO_TALONS}, // Marsh Stakato Worker
        {20232, 67, 28, STAKATO_TALONS}, // Marsh Stakato Soldier
        {20234, 69, 32, STAKATO_TALONS} // Marsh Stakato Drone
    };
    
    public Q333_HuntOfTheBlackLion()
    {
        super(333, qn, "Hunt Of The Black Lion");
        
        questItemIds = new int[]
        {
            LIONS_CLAW, 
            LIONS_EYE, 
            GUILD_COIN, 
            UNDEAD_ASH, 
            BLOODY_AXE_INSIGNIAS, 
            DELU_FANG, 
            STAKATO_TALONS, 
            SOPHIAS_LETTER1, 
            SOPHIAS_LETTER2, 
            SOPHIAS_LETTER3, 
            SOPHIAS_LETTER4
        };
        
        addStartNpc(SOPHYA);
        addTalkId(SOPHYA, REDFOOT, RUPIO, UNDRIAS, LOCKIRIN, MORGAN);
        
        for (int[] i : DROPLIST_PART1)
            addKillId(i[0]);
        
        for (int[] i : DROPLIST_PART2)
            addKillId(i[0]);
        
        for (int[] i : DROPLIST_PART3)
            addKillId(i[0]);
        
        for (int[] i : DROPLIST_PART4)
            addKillId(i[0]);
    }
    
    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        String htmltext = event;
        QuestState st = player.getQuestState(qn);
        
        if (st == null)
            return htmltext;
        
        if (event.equals("start"))
        {
            htmltext = "30735-01.htm";
            st.setState(STATE_STARTED);
            st.set("cond", "1");
            st.set("part", "0");
            st.set("text", "0");
            st.playSound(QuestState.SOUND_ACCEPT);
        }
        else if (event.equals("p1_t"))
        {
            st.set("part", "1");
            st.giveItems(SOPHIAS_LETTER1, 1);
            htmltext = "30735-02.htm";
        }
        else if (event.equals("p2_t"))
        {
            st.set("part", "2");
            st.giveItems(SOPHIAS_LETTER2, 1);
            htmltext = "30735-03.htm";
        }
        else if (event.equals("p3_t"))
        {
            st.set("part", "3");
            st.giveItems(SOPHIAS_LETTER3, 1);
            htmltext = "30735-04.htm";
        }
        else if (event.equals("p4_t"))
        {
            st.set("part", "4");
            st.giveItems(SOPHIAS_LETTER4, 1);
            htmltext = "30735-05.htm";
        }
        else if (event.equals("continue"))
        {
            int claws = st.getQuestItemsCount(LIONS_CLAW);
            int eyes = st.getQuestItemsCount(LIONS_EYE);
            int rand = Rnd.get(100);
            
            if (claws > 9)
            {
                st.takeItems(LIONS_CLAW, 10);
                
                if (eyes < 5)
                {
                    st.giveItems(LIONS_EYE, 1);
                    
                    if (rand < 25)
                        st.giveItems(HEALING_POTION, 20);
                    else if (rand < 50)
                    {
                        if (player.isMageClass())
                            st.giveItems(SPIRITSHOT_D, 50);
                        else
                            st.giveItems(SOULSHOT_D, 100);
                    }
                    else if (rand < 75)
                        st.giveItems(SCROLL_OF_ESCAPE, 20);
                    else
                        st.giveItems(SWIFT_ATTACK_POTION, 3);
                }
                else if (eyes < 9)
                {
                    st.giveItems(LIONS_EYE, 1);
                    
                    if (rand < 25)
                        st.giveItems(HEALING_POTION, 25);
                    else if (rand < 50)
                    {
                        if (player.isMageClass())
                            st.giveItems(SPIRITSHOT_D, 100);
                        else
                            st.giveItems(SOULSHOT_D, 200);
                    }
                    else if (rand < 75)
                        st.giveItems(SCROLL_OF_ESCAPE, 20);
                    else
                        st.giveItems(SWIFT_ATTACK_POTION, 3);
                }
                else if (eyes > 8)
                {
                    st.takeItems(LIONS_EYE, 8);
                    
                    if (rand < 25)
                        st.giveItems(HEALING_POTION, 50);
                    else if (rand < 50)
                    {
                        if (player.isMageClass())
                            st.giveItems(SPIRITSHOT_D, 200);
                        else
                            st.giveItems(SOULSHOT_D, 400);
                    }
                    else if (rand < 75)
                        st.giveItems(SCROLL_OF_ESCAPE, 30);
                    else
                        st.giveItems(SWIFT_ATTACK_POTION, 4);
                }
                
                htmltext = "30735-06.htm";
            }
            else
                htmltext = "30735-start.htm";
        }
        else if (event.equals("leave"))
        {
            st.takeItems(SOPHIAS_LETTERS[st.getInt("part") - 1], 1);
            
            st.set("part", "0");
            htmltext = "30735-07.htm";
        }
        else if (event.equals("exit"))
        {
            htmltext = "30735-exit.htm";
            st.exitQuest(true);
        }
        else if (event.equals("f_info"))
        {
            int text = st.getInt("text");
            
            if (text < 4)
            {
                st.set("text", String.valueOf(text + 1));
                htmltext = "30736-" + Rnd.get(1, 19) + "-text.htm";
            }
            else
                htmltext = "30736-01.htm";
        }
        else if (event.equals("f_give"))
        {
            if (st.hasQuestItems(CARGO_BOX))
            {
                if (st.getQuestItemsCount(ADENA) >= 650)
                {
                    st.takeItems(CARGO_BOX, 1);
                    st.takeItems(ADENA, 650);
                    
                    int rand = Rnd.get(1, 162);
                    
                    if (rand < 21)
                    {
                        st.giveItems(GLUDIO_APPLE, 1);
                        htmltext = "30736-02.htm";
                    }
                    else if (rand < 41)
                    {
                        st.giveItems(CORN_MEAL, 1);
                        htmltext = "30736-03.htm";
                    }
                    else if (rand < 61)
                    {
                        st.giveItems(WOLF_PELTS, 1);
                        htmltext = "30736-04.htm";
                    }
                    else if (rand < 74)
                    {
                        st.giveItems(MOONSTONE, 1);
                        htmltext = "30736-05.htm";
                    }
                    else if (rand < 86)
                    {
                        st.giveItems(GLUDIO_WEETS_FLOWER, 1);
                        htmltext = "30736-06.htm";
                    }
                    else if (rand < 98)
                    {
                        st.giveItems(SPIDERSILK_ROPE, 1);
                        htmltext = "30736-07.htm";
                    }
                    else if (rand < 99)
                    {
                        st.giveItems(ALEXANDRIT, 1);
                        htmltext = "30736-08.htm";	
                    }
                    else if (rand < 109)
                    {
                        st.giveItems(SILVER_TEA, 1);
                        htmltext = "30736-09.htm";	
                    }
                    else if (rand < 119)
                    {
                        st.giveItems(GOLEM_PART, 1);
                        htmltext = "30736-10.htm";	
                    }
                    else if (rand < 123)
                    {
                        st.giveItems(FIRE_EMERALD, 1);
                        htmltext = "30736-11.htm";	
                    }
                    else if (rand < 127)
                    {
                        st.giveItems(SILK_FROCK, 1);
                        htmltext = "30736-12.htm";	
                    }
                    else if (rand < 131)
                    {
                        st.giveItems(PORCELAN_URN, 1);
                        htmltext = "30736-13.htm";
                    }
                    else if (rand < 132)
                    {
                        st.giveItems(IMPERIAL_DIAMOND, 1);
                        htmltext = "30736-14.htm";
                    }
                    else if (rand < 147)
                    {
                        st.giveItems(STATUE_LIST[Rnd.get(STATUE_LIST.length)], 1);
                        htmltext = "30736-15.htm";
                    }
                    else if (rand <= 162)
                    {
                        st.giveItems(TABLET_LIST[Rnd.get(TABLET_LIST.length)], 1);
                        htmltext = "30736-16.htm";
                    }
                }
                else
                    htmltext = "30736-no_adena.htm";
            }
            else
                htmltext = "30736-no_box.htm";
        }
        else if (event.equals("r_give_statue") || event.equals("r_give_tablet"))
        {
            int[] list = STATUE_LIST;
            int reward = COMPLETE_STATUE;
            String piece = "30471-01.htm";
            String broken = "30471-02.htm";
            String complete = "30471-03.htm";
            
            if (event.equals("r_give_tablet"))
            {
                list = TABLET_LIST;
                reward = COMPLETE_TABLET;
                piece = "30471-04.htm";
                broken = "30471-05.htm";
                complete = "30471-06.htm";
            }
            
            int count = 0;
            
            for (int i : list)
                if (st.hasQuestItems(i))
                    count += 1;
            
            if (count > 3)
            {
                for (int i : list)
                    st.takeItems(i, 1);
                
                if (Rnd.nextBoolean())
                {
                    st.giveItems(reward, 1);
                    htmltext = complete;
                }
                else
                    htmltext = broken;
            }
            else if (count < 4 && count > 0)
                htmltext = piece;
            else
                htmltext = "30471-07.htm";
        }
        else if (event.equals("u_give"))
        {
            if (st.hasQuestItems(COMPLETE_STATUE))
            {
                st.takeItems(COMPLETE_STATUE, 1);
                st.rewardItems(ADENA, 30000);
                htmltext = "30130-01.htm";
            }
            else
                htmltext = "30130-02.htm";
        }
        else if (event.equals("l_give"))
        {
            if (st.hasQuestItems(COMPLETE_TABLET))
            {
                st.takeItems(COMPLETE_TABLET, 1);
                st.rewardItems(ADENA, 30000);
                htmltext = "30531-01.htm";
            }
            else
                htmltext = "30531-02.htm";
        }
        else if (event.equals("m_give"))
        {
            if (st.hasQuestItems(CARGO_BOX))
            {
                int coins = (st.getQuestItemsCount(GUILD_COIN) >= 40 && st.getQuestItemsCount(GUILD_COIN) <= 80) ? 2 : 1;
                
                st.giveItems(GUILD_COIN, 1);
                st.rewardItems(ADENA, coins * 100);
                
                htmltext = "30737-0" + Rnd.get(1, 3) + ".htm";
            }
            else
                htmltext = "30737-04.htm";
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
                if (st.hasQuestItems(BLACK_LION_MARK))
                    htmltext = (player.getLevel() > 24) ? "30735-17.htm" : "30735-18.htm";
                else
                    htmltext = "30735-19.htm";
                break;
                
            case STATE_STARTED:
                int part = st.getInt("part");
                
                switch(npc.getNpcId())
                {
                    case SOPHYA:
                        int _item = 0;
                        
                        if (part == 1)
                            _item = UNDEAD_ASH;
                        else if (part == 2)
                            _item = BLOODY_AXE_INSIGNIAS;
                        else if (part == 3)
                            _item = DELU_FANG;
                        else if (part == 4)
                            _item = STAKATO_TALONS;
                        else
                            return "30735-20.htm";
                        
                        int _count = st.getQuestItemsCount(_item);
                        int _boxes = st.getQuestItemsCount(CARGO_BOX);
                        
                        if (_boxes > 0 && _count > 0)
                        {
                            giveRewards(st, _item, _count);
                            htmltext = "30735-21.htm";
                        }
                        else if (_boxes > 0)
                            htmltext = "30735-22.htm";
                        else if (_count > 0)
                        {
                            giveRewards(st, _item, _count);
                            htmltext = "30735-23.htm";
                        }
                        else
                            htmltext = "30735-24.htm";
                        break;
                    
                    case REDFOOT:
                        htmltext = (st.hasQuestItems(CARGO_BOX)) ? "30736-20-text.htm" : "30736-21-text.htm";
                        break;
                        
                    case RUPIO:
                        for (int i : STATUE_LIST)
                            if (st.hasQuestItems(i))
                                return "30471-08.htm";
                        
                        for (int i : TABLET_LIST)
                            if (st.hasQuestItems(i))
                                return "30471-08.htm";
                        
                        htmltext = "30471-07.htm";
                        break;
                        
                    case UNDRIAS:
                        if (st.hasQuestItems(COMPLETE_STATUE))
                            return htmltext = "30130-04.htm";
                        
                        for (int i : STATUE_LIST)
                            if (st.hasQuestItems(i))
                                return "30130-05.htm";
                        
                        htmltext = "30130-02.htm";
                        break;
                        
                    case LOCKIRIN:
                        if (st.hasQuestItems(COMPLETE_TABLET))
                            return htmltext = "30531-04.htm";
                        
                        for (int i : TABLET_LIST)
                            if (st.hasQuestItems(i))
                                return "30531-05.htm";
                        
                        htmltext = "30531-06.htm";
                        break;
                        
                    case MORGAN:
                        htmltext = (st.hasQuestItems(CARGO_BOX)) ? "30737-06.htm" : "30737-07.htm";
                        break;	
                }
                break;
        }
        
        return htmltext;
    }
    
    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        QuestState st = checkPlayerState(player, npc, STATE_STARTED);
        if (st == null)
            return null;
        
        int npcId = npc.getNpcId();
        int part = st.getInt("part");
        
        switch (part)
        {
            case 1:
                dropItem(st, npcId, DROPLIST_PART1);
                break;
                
            case 2:
                dropItem(st, npcId, DROPLIST_PART2);
                break;
                
            case 3:
                dropItem(st, npcId, DROPLIST_PART3);
                break;
                
            case 4:
                dropItem(st, npcId, DROPLIST_PART4);
                break;
        }
        
        return null;
    }
    
    public static void giveRewards(QuestState st, int item, int count)
    {
        st.takeItems(item, count);
        st.rewardItems(ADENA, 35 * count);
        
        if (count < 20)
            return;
        else if (count < 50)
            st.giveItems(LIONS_CLAW, 1);
        else if (count < 100)
            st.giveItems(LIONS_CLAW, 2);
        else if (count >= 100 )
            st.giveItems(LIONS_CLAW, 3);	
    }
    
    public static void dropItem(QuestState st, int npc, int[][] droplist)
    {
        for (int[] i : droplist)
        {
            if (npc == i[0])
            {
                st.dropItems(CARGO_BOX, 1, 0, i[2] * 10000);
                st.dropItems(i[3], 1, 0, i[1] * 10000);
                break;
            }
        }
    }
    
    public static void main(String[] args)
    {
        new Q333_HuntOfTheBlackLion();
    }
}