package quests.Q224_TestOfSagittarius;

import net.xcine.gameserver.model.actor.L2Npc;
import net.xcine.gameserver.model.actor.instance.L2PcInstance;
import net.xcine.gameserver.model.base.ClassId;
import net.xcine.gameserver.model.itemcontainer.Inventory;
import net.xcine.gameserver.model.quest.Quest;
import net.xcine.gameserver.model.quest.QuestState;
import net.xcine.util.Rnd;

public class Q224_TestOfSagittarius extends Quest
{
    private static final String qn = "Q224_TestOfSagittarius";
    
    // Quest items
    private static final int MARK_OF_SAGITTARIUS = 3293;
    private static final int BERNARDS_INTRODUCTION = 3294;
    private static final int LETTER_OF_HAMIL_1 = 3295;
    private static final int LETTER_OF_HAMIL_2 = 3296;
    private static final int LETTER_OF_HAMIL_3 = 3297;
    private static final int HUNTERS_RUNE1 = 3298;
    private static final int HUNTERS_RUNE2 = 3299;
    private static final int TALISMAN_OF_KADESH = 3300;
    private static final int TALISMAN_OF_SNAKE = 3301;
    private static final int MITHRIL_CLIP = 3302;
    private static final int STAKATO_CHITIN = 3303;
    private static final int REINFORCED_BOWSTRING = 3304;
    private static final int MANASHENS_HORN = 3305;
    private static final int BLOOD_OF_LIZARDMAN = 3306;
    private static final int CRESCENT_MOON_BOW = 3028;
    
    // Neutral items
    private static final int WOODEN_ARROW = 17;
    
    // NPCs
    private static final int BERNARD = 30702;
    private static final int HAMIL = 30626;
    private static final int TANFORD = 30653;
    private static final int VOKIAN = 30514;
    private static final int GAUEN = 30717;
    
    // MOBs
    private static final int EVIL_SPIRIT_KADESH = 27090;
    
    private static final int[] ITEMS = 
    {
        MANASHENS_HORN,
        MITHRIL_CLIP,
        REINFORCED_BOWSTRING,
        STAKATO_CHITIN
    };
    
    // Droplist (NpcID, Current Condition, MaxCount, DropChance, ItemID)
    private static final int[][] DROPLIST = 
    {
        // Cond 3
        {20079, 3},
        {20080, 3},
        {20081, 3},
        {20082, 3},
        {20084, 3},
        {20086, 3},
        {20089, 3},
        {20090, 3},
        // Cond 6
        {20269, 6},
        {20270, 6},
        // Cond 10
        {20230, 10, STAKATO_CHITIN},
        {20233, 10, REINFORCED_BOWSTRING},
        {20551, 10, MITHRIL_CLIP},
        {20563, 10, MANASHENS_HORN},
        // Cond 13
        {20577, 13},
        {20578, 13},
        {20579, 13},
        {20580, 13},
        {20581, 13},
        {20582, 13},
        {27090, 13}
    };
    
    public Q224_TestOfSagittarius()
    {
        super(224, qn, "Test Of Sagittarius");
        
        questItemIds = new int[]
        {
            BERNARDS_INTRODUCTION,
            LETTER_OF_HAMIL_1,
            LETTER_OF_HAMIL_2,
            LETTER_OF_HAMIL_3,
            HUNTERS_RUNE1,
            HUNTERS_RUNE2,
            TALISMAN_OF_KADESH,
            TALISMAN_OF_SNAKE,
            MITHRIL_CLIP,
            STAKATO_CHITIN,
            REINFORCED_BOWSTRING,
            MANASHENS_HORN,
            BLOOD_OF_LIZARDMAN,
            CRESCENT_MOON_BOW
        };
        
        addStartNpc(BERNARD);
        
        addTalkId(BERNARD, HAMIL, TANFORD, VOKIAN, GAUEN);
        
        for (int[] i : DROPLIST)
            addKillId(i[0]);
    }
    
    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
    {
        String htmltext = event;
        QuestState st = player.getQuestState(qn);
        if (st == null)
            return htmltext;
        
        if (event.equalsIgnoreCase("30702-04.htm"))
        {
            st.setState(STATE_STARTED);
            st.set("cond", "1");
            st.playSound(QuestState.SOUND_ACCEPT);
            st.giveItems(BERNARDS_INTRODUCTION, 1);
        }
        else if (event.equalsIgnoreCase("30626-03.htm"))
        {
            st.set("cond", "2");
            st.playSound(QuestState.SOUND_MIDDLE);
            st.takeItems(BERNARDS_INTRODUCTION, 1);
            st.giveItems(LETTER_OF_HAMIL_1, 1);
        }
        else if (event.equalsIgnoreCase("30653-02.htm"))
        {
            st.set("cond", "3");
            st.playSound(QuestState.SOUND_MIDDLE);
            st.takeItems(LETTER_OF_HAMIL_1, 1);
        }
        else if (event.equalsIgnoreCase("30626-07.htm"))
        {
            st.set("cond", "5");
            st.playSound(QuestState.SOUND_MIDDLE);
            st.takeItems(HUNTERS_RUNE1, st.getQuestItemsCount(HUNTERS_RUNE1));
            st.giveItems(LETTER_OF_HAMIL_2, 1);
        }
        else if (event.equalsIgnoreCase("30514-02.htm"))
        {
            st.set("cond", "6");
            st.playSound(QuestState.SOUND_MIDDLE);
            st.takeItems(LETTER_OF_HAMIL_2, 1);
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
                if (st.hasQuestItems(MARK_OF_SAGITTARIUS))
                    htmltext = getAlreadyCompletedMsg();
                else if (checkCondition(player))
                {
                    if (player.getLevel() > 38)
                        htmltext = "30702-03.htm";
                    else
                    {
                        htmltext = "30702-01.htm";
                        st.exitQuest(true);
                    }
                }
                else
                    htmltext = "30702-02.htm";
                break;
                
            case STATE_STARTED:
                int cond = st.getInt("cond");
                switch (npc.getNpcId())
                {
                    case BERNARD:
                        if (st.hasQuestItems(BERNARDS_INTRODUCTION))
                            htmltext = "30702-05.htm";
                        break;
                        
                    case HAMIL:
                        if (cond == 1 && st.hasQuestItems(BERNARDS_INTRODUCTION))
                            htmltext = "30626-01.htm";
                        else if (cond == 2 && st.hasQuestItems(LETTER_OF_HAMIL_1))
                            htmltext = "30626-04.htm";
                        else if (cond == 4 && st.getQuestItemsCount(HUNTERS_RUNE1) == 10)
                            htmltext = "30626-05.htm";
                        else if (cond == 5 && st.hasQuestItems(LETTER_OF_HAMIL_2))
                            htmltext = "30626-08.htm";
                        else if (cond == 8)
                        {
                            st.set("cond", "9");
                            st.playSound(QuestState.SOUND_MIDDLE);
                            st.giveItems(LETTER_OF_HAMIL_3, 1);
                            htmltext = "30626-09.htm";
                        }
                        else if (cond == 9 && st.hasQuestItems(LETTER_OF_HAMIL_3))
                            htmltext = "30626-10.htm";
                        else if (cond == 12 && st.hasQuestItems(CRESCENT_MOON_BOW))
                        {
                            st.set("cond", "13");
                            st.playSound(QuestState.SOUND_MIDDLE);
                            htmltext = "30626-11.htm";
                        }
                        else if (cond == 13)
                            htmltext = "30626-12.htm";
                        else if (cond == 14 && st.hasQuestItems(TALISMAN_OF_KADESH))
                        {
                            htmltext = "30626-13.htm";
                            st.takeItems(CRESCENT_MOON_BOW, 1);
                            st.takeItems(TALISMAN_OF_KADESH, 1);
                            st.giveItems(MARK_OF_SAGITTARIUS, 1);
                            st.rewardExpAndSp(79832, 3750);
                            st.playSound(QuestState.SOUND_FINISH);
                            st.exitQuest(true);
                        }
                        break;
                        
                    case TANFORD:
                        if (cond == 2 && st.hasQuestItems(LETTER_OF_HAMIL_1))
                            htmltext = "30653-01.htm";
                        else if (cond == 3)
                            htmltext = "30653-03.htm";
                        break;
                        
                    case VOKIAN:
                        if (cond == 5 && st.hasQuestItems(LETTER_OF_HAMIL_2))
                            htmltext = "30514-01.htm";
                        else if (cond == 6)
                            htmltext = "30514-03.htm";
                        else if (cond == 7 && st.hasQuestItems(TALISMAN_OF_SNAKE))
                        {
                            st.set("cond", "8");
                            st.playSound(QuestState.SOUND_MIDDLE);
                            st.takeItems(TALISMAN_OF_SNAKE, 1);
                            htmltext = "30514-04.htm";
                        }
                        else if (cond == 8)
                            htmltext = "30514-05.htm";
                        break;
                        
                    case GAUEN:
                        if (cond == 9 && st.hasQuestItems(LETTER_OF_HAMIL_3))
                        {
                            st.set("cond", "10");
                            st.playSound(QuestState.SOUND_MIDDLE);
                            st.takeItems(LETTER_OF_HAMIL_3, 1);
                            htmltext = "30717-01.htm";
                        }
                        else if (cond == 10)
                            htmltext = "30717-03.htm";
                        else if (cond == 11 && checkItems(st))
                        {
                            st.set("cond", "12");
                            st.playSound(QuestState.SOUND_MIDDLE);
                            
                            for (int i : ITEMS)
                                st.takeItems(i, 1);
                            
                            st.giveItems(CRESCENT_MOON_BOW, 1);
                            st.giveItems(WOODEN_ARROW, 10);
                            
                            htmltext = "30717-02.htm";
                        }
                        else if (cond == 12)
                            htmltext = "30717-04.htm";
                        break;
                }
                break;
        }
        
        return htmltext;
    }
    
    @Override
    public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
    {
        QuestState st = player.getQuestState(qn);
        
        int cond = st.getInt("cond");
        int npcId = npc.getNpcId();
        
        if (cond == 3)
        {
            for (int[] i : DROPLIST)
            {
                if (i[0] == npcId && cond == i[1])
                {
                    if (st.dropItems(HUNTERS_RUNE1, 1, 10, 500000))
                        st.set("cond", "4");
                    
                    break;
                }
            }
        }
        else if (cond == 6)
        {
            for (int[] i : DROPLIST)
            {
                if (i[0] == npcId && cond == i[1])
                {
                    if (st.dropItems(HUNTERS_RUNE2, 1, 10, 500000))
                    {
                        st.takeItems(HUNTERS_RUNE2, 10);
                        st.giveItems(TALISMAN_OF_SNAKE, 1);
                        st.set("cond", "7");
                    }
                    
                    break;
                }
            }
        }
        else if (cond == 10)
        {
            for (int[] i : DROPLIST)
                if (i[0] == npcId && cond == i[1])
                    if (!st.hasQuestItems(i[2]))
                        st.dropItems(i[2], 1, 0, 100000);
            
            if (checkItems(st))
            {
                st.set("cond", "11");
                st.playSound(QuestState.SOUND_MIDDLE);
            }
        }
        else if (cond == 13)
        {
            if (npcId == EVIL_SPIRIT_KADESH && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == CRESCENT_MOON_BOW)
            {
                st.takeItems(BLOOD_OF_LIZARDMAN, st.getQuestItemsCount(BLOOD_OF_LIZARDMAN));
                
                if (st.dropItemsAlways(TALISMAN_OF_KADESH, 1, 1))
                    st.set("cond", "14");
            }
            else
            {
                for (int[] i : DROPLIST)
                    if (i[0] == npcId && cond == i[1])
                        st.dropItemsAlways(BLOOD_OF_LIZARDMAN, 1, 0);
                
                if (st.getQuestItemsCount(BLOOD_OF_LIZARDMAN) > 100)
                    if (Rnd.get(100) < (st.getQuestItemsCount(BLOOD_OF_LIZARDMAN) - 100))
                        addSpawn(EVIL_SPIRIT_KADESH, player, true, 300000, true);
            }
        }
        
        return null;
    }
    
    public static boolean checkCondition(L2PcInstance player)
    {
        return (player.getClassId() == ClassId.rogue || player.getClassId() == ClassId.elvenScout || player.getClassId() == ClassId.assassin);
    }
    
    public static boolean checkItems(QuestState st)
    {
        int count = 0;
        
        for (int i : ITEMS)
            if (st.hasQuestItems(i))
                count += 1;
        
        if (count == 4)
            return true;
        
        return false;
    }
    
    public static void main(String[] args)
    {
        new Q224_TestOfSagittarius();
    }
}