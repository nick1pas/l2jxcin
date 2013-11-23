DROP TABLE IF EXISTS raidboss_spawnlist;

CREATE TABLE raidboss_spawnlist (
  boss_id INT NOT NULL default 0,
  loc_x INT NOT NULL default 0,
  loc_y INT NOT NULL default 0,
  loc_z INT NOT NULL default 0,
  heading INT NOT NULL default 0,
  respawn_min_delay INT(11) NOT NULL default '43200',
  respawn_max_delay INT(11) NOT NULL default '129600',
  respawn_time BIGINT NOT NULL default 0,
  currentHp decimal(8,0) default NULL,
  currentMp decimal(8,0) default NULL,
  PRIMARY KEY (boss_id,loc_x,loc_y,loc_z)
);

INSERT INTO `raidboss_spawnlist` VALUES 
(25001,-54416,146480,-2887,0,43200,129600,0,95986,545), -- Greyclaw Kutus (23)
(25004,-94208,100240,-3520,0,43200,129600,0,168366,763), -- Turek Mercenary Captain (30)
(25007,124240,75376,-2800,0,43200,129600,0,331522,1062), -- Retreat Spider Cletu (42)
(25010,113920,52960,-3735,0,43200,129600,0,624464,2039), -- Furious Thieles (55)
(25013,169744,11920,-2732,0,43200,129600,0,507285,1722), -- Ghost Of Peasant Leader (50)
(25016,76787,245775,-10376,0,43200,129600,0,188376,2368), -- The 3rd Underwater Guardian (60)
(25019,7376,169376,-3600,0,43200,129600,0,206185,606), -- Pan Dryad (25)
(25020,90384,125568,-2128,0,43200,129600,0,156584,893), -- Breka Warlock Pastu (34)
(25023,27280,101744,-3696,0,43200,129600,0,208019,1415), -- Stakato Queen Zyrnna (34)
(25026,92976,7920,-3914,0,43200,129600,0,352421,1660), -- Katu Van Leader Atui (49)
(25029,54941,206705,-3728,0,43200,129600,0,156190,1911), -- Atraiban (53)
(25032,88532,245798,-10376,0,43200,129600,0,229722,2707), -- Eva's Guardian Millenu (58)
(25035,180968,12035,-2720,0,43200,129600,0,888658,3058), -- Shilen's Messenger Cabrio (70)
(25038,-57360,186272,-4967,0,43200,129600,0,116581,699), -- Tirak (28)
(25041,10416,126880,-3676,0,43200,129600,0,165289,927), -- Remmel (35)
(25044,107792,27728,-3488,0,43200,129600,0,319791,1296), -- Barion (47)
(25047,116352,27648,-3319,0,43200,129600,0,352421,1660), -- Karte (49)
(25050,125520,27216,-3632,0,43200,129600,0,771340,2039), -- Verfa (51)
(25051,117760,-9072,-3264,0,43200,129600,0,818959,2707), -- Rahha (65)
(25054,113432,16403,3960,0,43200,129600,0,945900,3420), -- Kernon (75)
(25057,107056,168176,-3456,0,43200,129600,0,288415,2235), -- Biconne Of Blue Sky (45)
(25060,-60428,188264,-4512,0,43200,129600,0,99367,575), -- Unrequited Kael (24)
(25063,-91024,116304,-3466,0,43200,129600,0,330579,927), -- Chertuba Of Great Soul (35)
(25064,92528,84752,-3703,0,43200,129600,0,218810,1120), -- Wizard Of Storm Teruk (40)
(25067,94992,-23168,-2176,0,43200,129600,0,554640,1598), -- Captain Of Red Flag Shaka (52)
(25070,125600,50100,-3600,0,43200,129600,0,451391,2039), -- Enchanted Forest Watcher Ruell (55)
(25073,143265,110044,-3944,0,43200,129600,0,875948,2987), -- Bloody Priest Rudelto (69)
(25076,-60976,127552,-2960,0,43200,129600,0,103092,606), -- Princess Molrang (25)
(25079,53712,102656,-1072,0,43200,129600,0,168366,763), -- Cat's Eye Bandit (30)
(25082,88512,140576,-3483,0,43200,129600,0,206753,1062), -- Leader Of Cat Gang (39)
(25085,66944,67504,-3704,0,43200,129600,0,371721,1355), -- Timak Orc Chief Ranger (44)
(25088,90848,16368,-5296,0,43200,129600,0,702418,2039), -- Crazy Mechanic Golem (43)
(25089,165424,93776,-2992,0,43200,129600,0,512194,2301), -- Soulless Wild Boar (59)
(25092,116151,16227,1944,0,43200,129600,0,888658,3058), -- Korim (70)
(25095,-37856,198128,-2672,0,43200,129600,0,121941,731), -- Elf Renoa (29)
(25098,123536,133504,-3584,0,43200,129600,0,330579,927), -- Sejarr's Servitor (35)
(25099,64048,16048,-3536,0,43200,129600,0,273375,1355), -- Rotten Tree Repiro (44)
(25102,113840,84256,-2480,0,43200,129600,0,576831,1722), -- Shacram (45)
(25103,135872,94592,-3735,0,43200,129600,0,451391,2039), -- Sorcerer Isirr (55)
(25106,173880,-11412,-2880,0,43200,129600,0,526218,2570), -- Ghost Of The Well Lidia (60)
(25109,152660,110387,-5520,0,43200,129600,0,935092,3347), -- Antharas Priest Cloe (74)
(25112,116128,139392,-3640,0,43200,129600,0,127782,763), -- Agent Of Beres, Meana (30)
(25115,94000,197500,-3300,0,43200,129600,0,294846,1120), -- Icarus Sample 1 (40)
(25118,50896,146576,-3645,0,43200,129600,0,330579,1415), -- Guilotine, Warden Of The Execution Grounds (35)
(25119,121872,64032,-3536,0,43200,129600,0,507285,1722), -- Messenger Of Fairy Queen Berun (50)
(25122,86300,-8200,-3000,0,43200,129600,0,467209,2235), -- Refugee Hopeful Leo (56)
(25125,170656,85184,-2000,0,43200,129600,0,1637918,2707), -- Fierce Tiger King Angel (65)
(25126,116263,15916,6992,0,43200,129600,0,1974940,3718), -- Longhorn Golkonda (79)
(25127,-47552,219232,-2413,0,43200,129600,0,198734,763), -- Langk Matriarch Rashkos (24)
(25128,17696,179056,-3520,0,43200,129600,0,148507,860), -- Vuku Grand Seer Gharmash (33)
(25131,75488,-9360,-2720,0,43200,129600,0,369009,1415), -- Carnage Lord Gato (50)
(25134,87536,75872,-3591,0,43200,129600,0,218810,1722), -- Leto Chief Talkin (40)
(25137,125280,102576,-3305,0,43200,129600,0,451391,2039), -- Beleth's Seer Sephia (55) 
(25140,191975,56959,-7616,0,43200,129600,0,818959,2707), -- Hekaton Prime (65)
(25143,113102,16002,6992,0,43200,129600,0,977229,3718), -- Fire Of Wrath Shuriel (78)
(25146,-13056,215680,-3760,0,43200,129600,0,90169,485), -- Serpent Demon Bifrons (21)
(25149,-12656,138176,-3584,0,43200,129600,0,103092,606), -- Zombie Lord Crowl (25)
(25152,43872,123968,-2928,0,43200,129600,0,165289,927), -- Flame Lord Shadar (35)
(25155,73520,66912,-3728,0,43200,129600,0,294846,1120), -- Shaman King Selu (40)
(25158,77104,5408,-3088,0,43200,129600,0,920790,1722), -- King Tarlk (48)
(25159,124984,43200,-3625,0,43200,129600,0,435256,1975), -- Paniel The Unicorn (54)
(25162,194107,53884,-4368,0,43200,129600,0,1461912,2368), -- Giant Marpanak (60)
(25163,130500,59098,3584,0,43200,129600,0,888658,3058), -- Roaring Skylancer (70)
(25166,-21800,152000,-2900,0,43200,129600,0,134813,606), -- Ikuntai (25)
(25169,-54464,170288,-3136,0,43200,129600,0,336732,763), -- Ragraman (30)
(25170,26064,121808,-3738,0,43200,129600,0,195371,1028), -- Lizardmen Leader Hellion (38)
(25173,75968,110784,-2512,0,43200,129600,0,288415,1415), -- Tiger King Karuta (45)
(25176,92544,115232,-3200,0,43200,129600,0,451391,2039), -- Black Lily (55)
(25179,181814,52379,-4344,0,43200,129600,0,526218,2368), -- Guardian Of The Statue Of Giant Karum (60)
(25182,41966,215417,-3728,0,43200,129600,0,512194,2707), -- Demon Kurikups (59)
(25185,88123,166312,-3412,0,43200,129600,0,165289,927), -- Tasaba Patriarch Hellena (35)
(25188,88256,176208,-3488,0,43200,129600,0,255564,763), -- Apepi (30)
(25189,68832,203024,-3547,0,43200,129600,0,156584,893), -- Cronos's Servitor Mumu (34)
(25192,125920,190208,-3291,0,43200,129600,0,258849,1296), -- Earth Protector Panathen (43)
(25198,102656,157424,-3735,0,43200,129600,0,1777317,2639), -- Fafurion's Herald Lokness (70)
(25199,108096,157408,-3688,0,43200,129600,0,912634,2707), -- Water Dragon Seer Sheshark (72)
(25202,119760,157392,-3744,0,43200,129600,0,935092,2777), -- Krokian Padisha Sobekk (74)
(25205,123808,153408,-3671,0,43200,129600,0,956490,3274), -- Ocean Flame Ashakiel (76)
(25208,73776,201552,-3760,0,43200,129600,0,218810,1722), -- Water Couatle Ateka (40)
(25211,76352,193216,-3648,0,43200,129600,0,174646,1975), -- Sebek (36)
(25214,112112,209936,-3616,0,43200,129600,0,218810,2368), -- Fafurion's Page Sika (40)
(25217,89904,105712,-3292,0,43200,129600,0,369009,1722), -- Cursed Clara (50)
(25220,113551,17083,-2120,0,43200,129600,0,924022,3274), -- Death Lord Hallate (73)
(25223,43152,152352,-2848,0,43200,129600,0,165289,1237), -- Soul Collector Acheron (35)
(25226,104240,-3664,-3392,0,43200,129600,0,768537,2502), -- Roaring Lord Kastor (62)
(25229,137568,-19488,-3552,0,43200,129600,0,1891801,3420), -- Storm Winged Naga (75)
(25230,66672,46704,-3920,0,43200,129600,0,482650,2169), -- Timak Seer Ragoth (57)
(25233,185800,-26500,-2000,0,43200,129600,0,1256671,3643), -- Spirit Of Andras, The Betrayer (69)
(25234,120080,111248,-3047,0,43200,129600,0,1052436,2707), -- Ancient Weird Drake (60)
(25235,116400,-62528,-3264,0,43200,129600,0,912634,3202), -- Vanor Chief Kandra (72)
(25238,155000,85400,-3200,0,43200,129600,0,512194,2846), -- Abyss Brukunt (59)
(25241,165984,88048,-2384,0,43200,129600,0,624464,2639), -- Harit Hero Tamash (55)
(25244,187360,45840,-5856,0,43200,129600,0,1891801,3420), -- Last Lesser Giant Olkuth (75)
(25245,172000,55000,-5400,0,43200,129600,0,977229,3643), -- Last Lesser Giant Glaki (78)
(25248,127903,-13399,-3720,0,43200,129600,0,1825269,3274), -- Doom Blade Tanatos (72)
(25249,147104,-20560,-3377,0,43200,129600,0,945900,3420), -- Palatanos Of Horrific Power (75)
(25252,192376,22087,-3608,0,43200,129600,0,888658,3058), -- Palibati Queen Themis (70)
(25255,170048,-24896,-3440,0,43200,129600,0,1637918,2707), -- Gargoyle Lord Tiphon (65)
(25256,170320,42640,-4832,0,43200,129600,0,526218,2368), -- Taik High Prefect Arak (60)
(25259,42050,208107,-3752,0,43200,129600,0,1248928,2039), -- Zaken's Butcher Krantz (55)
(25260,93120,19440,-3607,0,43200,129600,0,392985,1722), -- Iron Giant Totem (45)
(25263,144400,-28192,-1920,0,43200,129600,0,848789,2846), -- Kernon's Faithful Servant Kelone (67)
(25266,188983,13647,-2672,0,43200,129600,0,945900,3420), -- Bloody Empress Decarbia (75)
(25269,123504,-23696,-3481,0,43200,129600,0,888658,3058), -- Beast Lord Behemoth (70)
(25272,49248,127792,-3552,0,43200,129600,0,233163,1415), -- Partisan Leader Talakin (28)
-- (25273,23800,119500,-8976,0,43200,129600,0,507285,2104), -- Carnamakos (50) -- (Spawn by Quest)
(25276,154088,-14116,-3736,0,43200,129600,0,1891801,3420), -- Death Lord Ipos (75)
(25277,54651,180269,-4976,0,43200,129600,0,507285,1722), -- Lilith's Witch Marilion (50)
(25280,85622,88766,-5120,0,43200,129600,0,1248928,2039), -- Pagan Watcher Cerberon (55)
(25281,151053,88124,-5424,0,43200,129600,0,1777317,3058), -- Anakim's Nemesis Zakaron (70)
(25282,179311,-7632,-4896,0,43200,129600,0,1891801,3420), -- Death Lord Shax (75)
-- (25283,184410,-10111,-5488,0,43200,129600,0,1639146,3793), -- Lilith (80) -- (Spawn by Seven Signs)
-- (25286,185000,-13000,-5488,0,43200,129600,0,1639146,3793), -- Anakim (80) -- (Spawn by Seven Signs)
(25290,-106500,-252700,-15542,0,43200,129600,0,977229,3718), -- Daimon The White-Eyed (78) -- (Spawn by Quest)
(25293,134672,-115600,-1216,0,43200,129600,0,977229,3568), -- Hestia, Guardian Deity Of The Hot Springs (78)
(25296,-104700,-252700,-15542,0,43200,129600,0,935092,3718), -- Icicle Emperor Bumbalump (74) -- (Spawn by Quest)
(25299,148160,-73808,-4919,0,43200,129600,0,714778,3718), -- Ketra's Hero Hekaton (80)
(25302,145504,-81664,-6016,0,43200,129600,0,743801,4183), -- Ketra's Commander Tayr (84)
(25305,145008,-84992,-6240,0,43200,129600,0,1532678,4553), -- Ketra's Chief Brakki (87)
(25306,-105300,-252700,-15542,0,43200,129600,0,534922,3718), -- Soul Of Fire Nastron (87) -- (Spawn by Quest)
(25309,115552,-39200,-2480,0,43200,129600,0,714778,3718), -- Varka's Hero Shadith (80)
(25312,109216,-36160,-938,0,43200,129600,0,743801,4183), -- Varka's Commander Mos (84)
(25315,105584,-43024,-1728,0,43200,129600,0,1532678,4553), -- Varka's Chief Horus (87)
(25316,-105900,-252700,-15542,0,43200,129600,0,534922,3718), -- Soul Of Water Ashutar (87) -- (Spawn by Quest)
(25319,184542,-106330,-6304,0,43200,129600,0,1048567,4304), -- Ember (85)
(25322,93296,-75104,-1824,0,43200,129600,0,834231,2707), -- Demon's Agent Falston (66)
(25325,91008,-85904,-2736,0,43200,129600,0,888658,2987), -- Flame Of Splendor Barakiel (70)
(25328,59331,-42403,-3003,0,43200,129600,0,900867,3058), -- Eilhalder Von Hellmann (71) -- (Only Spawn at Night)
(25352,-16912,174912,-3264,0,43200,129600,0,127782,731), -- Giant Wasteland Basilisk (30)
(25354,-16096,184288,-3817,0,43200,129600,0,165289,893), -- Gargoyle Lord Sirocco (35)
(25357,-3456,112864,-3456,0,43200,129600,0,90169,455), -- Sukar Wererat Chief (21)
(25360,29216,179280,-3624,0,43200,129600,0,107186,606), -- Tiger Hornet (26)
(25362,-55920,186768,-3336,0,43200,129600,0,95986,514), -- Tracker Leader Sharuk (23)
(25365,-62000,190256,-3687,0,43200,129600,0,214372,606), -- Patriarch Kuroboros (26)
(25366,-62368,179440,-3594,0,43200,129600,0,95986,514), -- Kuroboros' Priest (23)
(25369,-45616,111024,-3808,0,43200,129600,0,103092,575), -- Soul Scavenger (25)
(25372,48000,243376,-6611,0,43200,129600,0,175392,426), -- Discarded Guardian (20)
(25373,9649,77467,-3808,0,43200,129600,0,90169,455), -- Malex Herald Of Dagoniel (21)
(25375,22500,80300,-3195,0,43200,129600,0,87696,426), -- Zombie Lord Farakelsus (20)
(25378,-54096,84288,-3512,0,43200,129600,0,87696,426), -- Madness Beast (20)
(25380,-47367,51548,-5904,0,43200,129600,0,90169,455), -- Kaysha Herald Of Icarus (21)
(25383,51632,153920,-3552,0,43200,129600,0,156584,860), -- Revenant Of Sir Calibus (34)
(25385,53600,143472,-3872,0,43200,129600,0,174646,927), -- Evil Spirit Tempest (36)
(25388,40128,101920,-1241,0,43200,129600,0,165289,893), -- Red Eye Captain Trakia (35)
(25391,45600,120592,-2455,0,43200,129600,0,297015,827), -- Nurka's Messenger (33)
(25392,29928,107160,-3708,0,43200,129600,0,141034,795), -- Captain Of Queen's Royal Guards (32)
(25394,101888,200224,-3680,0,43200,129600,0,390743,994), -- Premo Prime (38)
(25395,15000,119000,-11900,0,43200,129600,0,288415,1355), -- Archon Suscepter (45)
(25398,5000,189000,-3728,0,43200,129600,0,165289,893), -- Eye Of Beleth (35)
(25401,117808,102880,-3600,0,43200,129600,0,141034,795), -- Skyla (32)
(25404,35992,191312,-3104,0,43200,129600,0,148507,827), -- Corsair Captain Kylon (33)
(25407,115072,112272,-3018,0,43200,129600,0,526218,2301), -- Lord Ishka (60)
(25410,72192,125424,-3657,0,43200,129600,0,218810,1062), -- Road Scavenger Leader (40)
(25412,81920,113136,-3056,0,43200,129600,0,319791,1476), -- Necrosentinel Royal Guard (47)
(25415,128352,138464,-3467,0,43200,129600,0,218810,1062), -- Nakondas (40)
(25418,62416,8096,-3376,0,43200,129600,0,273375,1296), -- Dread Avenger Kraven (44)
(25420,42032,24128,-4704,0,43200,129600,0,335987,1537), -- Orfen's Handmaiden (48)
(25423,113600,47120,-4640,0,43200,129600,0,539706,2368), -- Fairy Queen Timiniel (61)
(25426,-18048,-101264,-2112,0,43200,129600,0,103092,575), -- Betrayer Of Urutu Freki (25)
(25429,172064,-214752,-3565,0,43200,129600,0,103092,575), -- Mammon Collector Talos (25)
(25431,79648,18320,-5232,0,43200,129600,0,273375,1296), -- Flamestone Golem (44)
(25434,104096,-16896,-1803,0,43200,129600,0,451391,1975), -- Bandit Leader Barda (55)
(25437,67296,64128,-3723,0,43200,129600,0,576831,1355), -- Timak Orc Gosmos (45)
(25438,107000,92000,-2272,0,43200,129600,0,273375,1296), -- Thief Kelbar (44)
(25441,111440,82912,-2912,0,43200,129600,0,288415,1355), -- Evil Spirit Cyrion (45)
(25444,113232,17456,-4384,0,43200,129600,0,588136,2639), -- Enmity Ghost Ramdal (65)
(25447,113200,17552,-1424,0,43200,129600,0,645953,3058), -- Immortal Savior Mardil (71)
(25450,113600,15104,9559,0,43200,129600,0,987470,3643), -- Cherub Galaxia (79)
(25453,156704,-6096,-4185,0,43200,129600,0,888658,2987), -- Meanas Anor (70)
(25456,133632,87072,-3623,0,43200,129600,0,352421,1598), -- Mirror Of Oblivion (49)
(25460,150304,67776,-3688,0,43200,129600,0,385670,1722), -- Deadman Ereve (51)
(25463,166288,68096,-3264,0,43200,129600,0,467209,2039), -- Harit Guardian Garangky (56)
(25467,186192,61472,-4160,0,43200,129600,0,576851,3718), -- Gorgolos (64)
(25470,186896,56276,-4576,0,43200,129600,0,598898,3718), -- Last Titan Utenus (66)
(25473,175712,29856,-3776,0,43200,129600,0,402319,3718), -- Grave Robber Kim (52)
(25475,183568,24560,-3184,0,43200,129600,0,451391,3718), -- Ghost Knight Kabed (55)
(25478,168288,28368,-3632,0,43200,129600,0,588136,3718), -- Shilen's Priest Hisilrome (65)
(25481,53517,205413,-3728,0,43200,129600,0,66938,3718), -- Magus Kenishee (53)
(25484,43160,220463,-3680,0,43200,129600,0,369009,3718), -- Zaken's Chief Mate Tillion (50)
(25487,83056,183232,-3616,0,43200,129600,0,218810,3718), -- Water Spirit Lian (40)
(25490,86528,216864,-3584,0,43200,129600,0,218810,3718), -- Gwindorr (40)
(25493,83174,254428,-10873,0,43200,129600,0,451391,3718), -- Eva's Spirit Niniel (55)
(25496,88300,258000,-10200,0,43200,129600,0,402319,3718), -- Fafurion's Envoy Pingolpin (52)
(25498,126624,174448,-3056,0,43200,129600,0,288415,3718), -- Fafurion's Henchman Istary (45)
-- (25501,48575,-106191,-1568,0,43200,129600,0,127782,9999), -- Boss Akata (30) -- stats to be done
-- (25504,123000,-141000,-1100,0,43200,129600,0,206753,9999), -- Nellis' Vengeful Spirit (39) -- stats & proper coords to be done
-- (25506,127900,-160600,-1100,0,43200,129600,0,184670,9999), -- Rayito The Looter (37) -- stats & proper coords to be done
-- (25509,74000,-102000,900,0,43200,129600,0,418874,9999), -- Dark Shaman Varangka (53) -- stats & proper coords to be done
(25514,79635,-55434,-6135,0,43200,129600,0,714778,3718), -- Queen Shyeed (80)
-- (25517,113000,-76000,200,0,43200,129600,0,1069643,9999), -- Master Anays (87) -- (Spawn by Quest)
(25523,170000,-60000,-3500,33000,43200,129600,0,1848045,9999), -- Plague Golem (73)
(25524,144143,-5731,-4722,0,43200,129600,0,956490,3247), -- Flamestone Giant (76)
(25527,3776,-6768,-3288,0,43200,129600,0,1532678,3718), -- Uruka (86)

(29030,189964,-116820,-1624,0,201600,604800,0,256613,3793), -- Fenril Hound Kerinne
(29033,193260,-116820,-1444,0,201600,604800,0,257118,3869), -- Fenril Hound Freki
(29036,189388,-109476,-1064,0,201600,604800,0,514943,3945), -- Fenril Hound Uruz
(29037,192156,-108980,-1060,0,201600,604800,0,257674,4022), -- Fenril Hound Kinaz
(29040,190000,-106002,-782,28905,201600,604800,0,515450,4099), -- Wings of Flame Ixion

--
-- Following mobs need their type checked, corrected & determined whether they actually belong in here
--
-- (29054,11882,-49216,-3008,0,43200,129600,0,1352750,1494), -- Benom (75) (spawn 1hr before siege in rune start)
-- (29056,102800,-126000,-2500,0,43200,129600,0,350000,9999), -- Ice Fairy Sirra (60) -- stats to be done (Spawn by Quest)
-- (29060,106000,-128000,-3000,0,43200,129600,0,350000,9999), -- Captain Of The Ice Queen's Royal Guard (59) -- stats to be done
-- (29062,-16382,-53450,-10432,0,43200,129600,0,400000,9999), -- High Priestess Van Halter (87) -- stats to be done (Spawn by Quest)
-- (29065,26528,-8244,-20,0,43200,129600,0,400000,9999), -- Sailren (87) -- stats to be done (Spawn by Quest)
(29095,141569,-45908,-2387,0,43200,129600,0,400000,9999); -- Gordon (87) -- walking around Goddard