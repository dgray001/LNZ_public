package LNZApplet;

public interface Constants {

  // Program constants
  static final boolean DEV = true;
  static final String credits =
  "LNZ" +
  "\nCreated by Daniel Gray" +
  "\nBeta v0.8.0ap: 20230408" +
  "\n\nLines: 75194 (v0.8.0al)" +
  "\nImages: 1575 (v0.8.0al)" +
  "\nSounds: 360 (v0.8.0al)" +
  "";
  static final String version_history =
  "LNZ" +
  "\nCreated by Daniel Gray" +
  "\n\n202211: v0.8: Beta Version" +
  "\n\n202205: v0.7: Alpha Version" +
  "\n\n202203: v0.6: Advanced Mechanics" +
  "\n\n202202: v0.5: Recreated Logic" +
  "\n\n202201: v0.4: Recreated Program" +
  "\n\n2019: v0.3: Legacy Version" +
  "";
  static final String tips_and_tricks =
  "Here you can find various tips and tricks not explicitly taught in the " +
    "tutorial. This list can always be opened from the options menu or by " +
    "typing the '?' key:" +
  "\n\n - Holding the SHIFT key while moving will make your character sneak, " +
    "making them move slower and quieter." +
  "\n\n - Use CTRL-n to open information about the current state of the game." +
  "\n\n - Use 't' to select whatever your hero is holding." +
  "\n\n - Use 'y' to stop whatever you are doing." +
  "\n\n - Use 'v' to jump, the height you jump is based on your agility." +
  "\n\n - Agility also decreases terrain-based slows, with each point in " +
    "agility decreasing the slow by 15%." +
  "\n\n - Holding CTRL allows you to face your mouse without moving. This is " +
    "especially helpful when aiming a ranged weapon or throwing an item." +
  "\n\n - By default you will interact with a map feature without using the " +
    "item in your hand, if possible.\nHold CTRL to interact with the map " +
    "feature using the item in your hand." +
  "\n\n - By default you won't interact with the map terrain itself, but by " +
    "holding ALT you can interact with it using the item you are holding." +
  "\n\n - Don't be afraid to skip various quests, etc. in a campaign, as you " +
    "can always redo campaigns later when you are stronger, or even with a " +
    "different hero." +
  "\n\n - Don't become too attached to your gear: it has finite durability and " +
    "there is probably better gear out there!" +
  "";
  static final int frameUpdateTime = 401; // prime number
  static final int frameAverageCache = 3;
  static final int maxFPS = 120;
  static final int exit_delay = 300;
  static final double default_cursor_size = 35;
  static final double small_number = 0.001; // for double miscalculations
  static final double inverse_root_two = 0.70710678;
  static final double root_two = 1.41421356;
  static final double errorForm_width = 400;
  static final double errorForm_height = 400;
  static final int color_black = -16777216;
  static final int color_fog = 1688906410;
  static final int color_transparent = 65793;
  static final int notification_slide_time = 200;
  static final int notification_display_time = 3000;
  static final double notification_achievement_width = 220;
  static final double notification_achievement_height = 120;
  static final double esc_button_height = 30;
  static final double escFormWidth = 350;
  static final double escFormHeight = 350;

  // Initial Interface
  static final int initialInterface_size = 400;
  static final int initialInterface_buttonWidth = 80;
  static final int initialInterface_buttonGap = 25;

  // Profile
  static final double profile_treeForm_width = 300;
  static final double profile_treeForm_height = 340;
  static final double profile_tree_nodeHeight = 80;
  static final double profile_tree_nodeGap = 50;
  static final double profile_heroesFormWidth = 850;
  static final double profile_heroesFormHeight = 600;
  static final double profile_heroFormWidth = 400;
  static final double profile_heroFormHeight = 500;
  static final double profile_xpMultiplierI = 1.1;
  static final double profile_xpMultiplierII = 1.2;
  static final double profile_xpMultiplierIII = 1.4;
  static final double profile_xpMultiplierIV = 1.8;
  static final double profile_xpMultiplierV = 2.5;

  // Achievements
  static final int achievement_kills_I = 3;
  static final int achievement_kills_II = 25;
  static final int achievement_kills_III = 100;
  static final int achievement_kills_IV = 300;
  static final int achievement_kills_V = 1000;
  static final int achievement_kills_VI = 5000;
  static final int achievement_kills_VII = 20000;
  static final int achievement_kills_VIII = 100000;
  static final int achievement_kills_IX = 1000000;
  static final int achievement_kills_X = 10000000;
  static final int achievement_deaths_I = 2;
  static final int achievement_deaths_II = 6;
  static final int achievement_deaths_III = 20;
  static final int achievement_deaths_IV = 60;
  static final int achievement_deaths_V = 200;
  static final int achievement_deaths_VI = 600;
  static final int achievement_deaths_VII = 200;
  static final int achievement_deaths_VIII = 6000;
  static final int achievement_deaths_IX = 20000;
  static final int achievement_deaths_X = 60000;
  static final int achievement_walk_I = 100;
  static final int achievement_walk_II = 500;
  static final int achievement_walk_III = 300000;
  static final int achievement_walk_IV = 300000;
  static final int achievement_walk_V = 300000;
  static final int achievement_walk_VI = 300000;
  static final int achievement_walk_VII = 300000;
  static final int achievement_walk_VIII = 300000;
  static final int achievement_walk_IX = 300000;
  static final int achievement_walk_X = 300000;

  // MainMenu Interface
  static final double profileForm_width = 400;
  static final double profileForm_height = 500;
  static final double newProfileForm_width = 400;
  static final double newProfileForm_height = 500;
  static final double optionsForm_widthOffset = 30;
  static final double optionsForm_heightOffset = 20;
  static final double optionsForm_threshhold_master = 0.12;
  static final double optionsForm_threshhold_other = 0.15;
  static final double achievementsForm_widthOffset = 300;
  static final double achievementsForm_heightOffset = 100;
  static final double banner_maxWidthRatio = 0.8;
  static final double banner_maxHeightRatio = 0.2;
  static final double creditsForm_width = 300;
  static final double creditsForm_height = 320;
  static final double tipsForm_width = 400;
  static final double tipsForm_height = 600;
  static final double playButton_scaleFactor = 1.7;
  static final double profileButton_offset = 45;
  static final double profileButton_growfactor = 1.6;

  // Options
  static final int options_defaultVolume = 40;
  static final int options_defaultMusicVolume = 30;
  static final double options_volumeMin = 0.01;
  static final int options_volumeMax = 100;
  static final double options_volumeGainMultiplier = 8;

  // MapEditor Interface
  static final double mapEditor_panelMinWidth = 220;
  static final double mapEditor_panelMaxWidth = 400;
  static final double mapEditor_panelStartWidth = 300;
  static final double mapEditor_buttonGapSize = 10;
  static final double mapEditor_listBoxGap = 5;
  static final double mapEditor_formWidth = 400;
  static final double mapEditor_formHeight = 500;
  static final double mapEditor_formWidth_small = 250;
  static final double mapEditor_formHeight_small = 250;
  static final double mapEditor_rightClickBoxWidth = 100;
  static final double mapEditor_rightClickBoxMaxHeight = 100;

  // Minigame Interface
  static final double minigames_panelWidth = 180;
  static final double minigames_minigameButtonWidth = 65;
  static final double minigames_edgeGap = 5;
  static final double minigames_buttonGap = 15;
  static final double minigames_scrollbarWidth = 18;
  static final double minigames_chessPanelsSize = 400;

  // Playing Interface
  static final double playing_worldMapDefaultZoom = 0.8;
  static final double playing_worldMapMinZoom = 0.5;
  static final double playing_worldMapMaxZoom = 3;
  static final double playing_scrollZoomFactor = 0.02;
  static final double playing_viewMoveSpeedFactor = 0.0005;

  // Help strings
  static final String help_mapEditor_maps = "Maps\n\nThis view displays the " +
    "maps you've made. Double-click a map to edit it, or right click one to " +
    "see more options.";
  static final String help_mapEditor_areas = "Areas\n\nThis view ...";
  static final String help_mapEditor_levels = "Levels\n\nThis view displays the " +
    "levels you've made. Double-click a level to edit it, or right click one to " +
    "see more options.";
  static final String help_mapEditor_terrain = "Terrain\n\nIn this view you " +
    "can select terrain to add to the map.\n\nHotkeys:\n n: Hold to prevent " +
    "selected terrain from dragging over existing terrain\n z: Toggle grid\n x: " +
    "Toggle fog\n c: Toggle rectangle mode\n v: Toggle square mode\n b: Edit " +
    "selected object on map.";
  static final String help_mapEditor_features = "Features\n\nIn this view you " +
    "can select features to add to the map.\n\nHotkeys:\n z: Toggle grid\n x: " +
    "Toggle fog\n c: Toggle rectangle mode\n v: Toggle square mode\n b: Edit " +
    "selected object on map.";
  static final String help_mapEditor_units = "Units\n\nIn this view you " +
    "can select units to add to the map.\n\nHotkeys:\n z: Toggle grid\n x: " +
    "Toggle fog\n c: Toggle rectangle mode\n v: Toggle square mode\n b: Edit " +
    "selected object on map.";
  static final String help_mapEditor_items = "Items\n\nIn this view you " +
    "can select items to add to the map.\n\nHotkeys:\n z: Toggle grid\n x: " +
    "Toggle fog\n c: Toggle rectangle mode\n v: Toggle square mode\n b: Edit " +
    "selected object on map.";
  static final String help_mapEditor_levelInfo = "Level Editor\n\nIn this " +
    "view you see an overview of your level and the maps in it.\nDouble-click " +
    "a map to view it.\n\nHotkeys:\n z: Toggle grid\n x: Toggle fog\n c: Toggle " +
    "rectangle mode\n v: Toggle square mode\n s: Save last rectangle\n S: Set " +
    "player start/respawn location";
  static final String help_mapEditor_levelMaps = "Maps\n\nIn this view you " +
    "can see the maps that can be added to your level.\nDouble-click a map " +
    "to add it to your level.\n\nHotkeys:\n z: Toggle grid\n x: Toggle fog\n c: " +
    "Toggle rectangle mode\n v: Toggle square mode\n s: Save last rectangle\n S: " +
    "Set player start/respawn location\n d: Remove selected map from level";
  static final String help_mapEditor_linkers = "Linkers\n\nIn this view you " +
    "see the linkers in your level.\n\nHotkeys:\n z: Toggle grid\n x: Toggle " +
    "fog\n c: Toggle rectangle mode\n v: Toggle square mode\n s: Save last " +
    "rectangle\n S: Set player start/respawn location\n a: Add linker from current " +
    "rectangles\n d: Delete selected linker from level";
  static final String help_mapEditor_triggers = "Triggers\n\nIn this view " +
    "you see the triggers in your level.\nDouble-click a trigger to open the " +
    "trigger editor.\n\nHotkeys:\n z: Toggle grid\n x: Toggle fog\n c: Toggle " +
    "rectangle mode\n v: Toggle square mode\n s: Save last rectangle\n S: Set " +
    "player start/respawn location\n a: Add a new trigger to level\n d: Delete selected " +
    "trigger from level";
  static final String help_mapEditor_triggerEditor = "Trigger Editor\n\nIn " +
    "this view you can edit the trigger you selected.\n\nHotkeys:\n z: " +
    "Toggle grid\n x: Toggle fog\n c: Toggle rectangle mode\n v: Toggle square " +
    "mode\n s: Save last rectangle\n S: Set player start/respawn location\n d: Delete " +
    "selected trigger component from trigger";
  static final String help_mapEditor_conditionEditor = "Condition Editor\n\nIn " +
    "this view you can edit the condition you selected.\n\nHotkeys:\n z: " +
    "Toggle grid\n x: Toggle fog\n c: Toggle rectangle mode\n v: Toggle square " +
    "mode\n s: Save last rectangle\n S: Set player start/respawn location\n a: Add " +
    "current rectangle to condition\n d: Delete selected trigger component " +
    "from trigger";
  static final String help_mapEditor_effectEditor = "Effect Editor\n\nIn " +
    "this view you can edit the effect you selected.\n\nHotkeys:\n z: " +
    "Toggle grid\n x: Toggle fog\n c: Toggle rectangle mode\n v: Toggle square " +
    "mode\n s: Save last rectangle\n S: Set player start/respawn location\n a: Add " +
    "current rectangle to effect\n d: Delete selected trigger component " +
    "from trigger";

  // GameMap
  static final double map_borderSize = 30;
  static final int map_terrainResolutionDefault = 64;
  static final int map_fogResolution = 32;
  static final double map_defaultZoom = 100;
  static final double map_minZoom = 60;
  static final double map_maxZoom = 150;
  static final double map_scrollZoomFactor = -1.5;
  static final double map_minCameraSpeed = 0.001;
  static final double map_maxCameraSpeed = 0.1;
  static final double map_defaultCameraSpeed = 0.01;
  static final double map_defaultHeaderMessageTextSize = 28;
  static final int map_headerMessageFadeTime = 250;
  static final int map_headerMessageShowTime = 3000;
  static final double map_selectedObjectTitleTextSize = 22;
  static final double map_selectedObjectPanelGap = 4;
  static final double map_selectedObjectImageGap = 8;
  static final double map_moveLogicCap = 0.12; // longest movable distance at one logical go
  static final double map_tierImageHeight = 50;
  static final double map_statusImageHeight = 30;
  static final int map_maxHeaderMessages = 5;
  static final double map_defaultMaxSoundDistance = 8;
  static final double map_timer_refresh_fog_default = 350;
  static final double map_timer_refresh_fog_min = 50;
  static final double map_timer_refresh_fog_max = 950;
  static final int map_maxHeight = 15;
  static final int map_minHeight = -9;
  static final double map_lightDecay = 0.8; // per square
  static final double map_lightDecayDim = 0.15; // per square
  static final int map_lightUpdateIterations = 4; // per refresh
  static final int map_chunkWidth = 30;
  static final int map_noiseOffsetX = 123456;
  static final int map_noiseOffsetY = 654321;
  static final int map_noiseOctaves = 8;
  static final double map_chunkPerlinMultiplier = 0.2;
  static final double map_mapPerlinMultiplier = 0.05;
  static final int map_refreshChunkTimer = 200;
  static final int map_timerChunkSpawnUnits = 3000;
  static final int map_maxUnits = 100;
  static final double map_unitSpawnMinDistance = 12;
  static final int map_updateSquaresTimer = 5000;

  // Features
  static final double feature_defaultInteractionDistance = 0.3;
  static final int feature_woodenTableHealth = 4;
  static final int feature_woodenDeskHealth = 10;
  static final int feature_woodenChairHealth = 2;
  static final int feature_furnitureInteractionTime = 200;
  static final int feature_couchHealth = 4;
  static final int feature_woodenBenchSmallHealth = 4;
  static final int feature_woodenBenchLargeHealth = 8;
  static final int feature_bedHealth = 3;
  static final int feature_wardrobeHealth = 10;
  static final int feature_signCooldown = 2000;
  static final String feature_signDescriptionDelimiter = "***";
  static final int feature_showerStallCooldown = 6000;
  static final int feature_urinalCooldown = 6000;
  static final int feature_toiletCooldown = 10000;
  static final int feature_pickleJarCooldown = 2000;
  static final int feature_movableBrickWallInteractionTime = 1500;
  static final int feature_gravelInteractionTime = 300;
  static final int feature_gravelMaxNumberRocks = 4;
  static final int feature_wireFenceInteractionTime = 400;
  static final int feature_ivyGrowTimer = 30000;
  static final int feature_treeInteractionTime = 350;
  static final int feature_treeHealth = 8;
  static final int feature_treeBigHealth = 12;
  static final int feature_treeTimer = 220000;
  static final double feature_treeDropChance = 0.5;
  static final double feature_treeChanceEndBranches = 0.6;
  static final int feature_bushInteractionTime = 350;
  static final int feature_bushHealth = 3;
  static final int feature_bushTimer = 160000;
  static final double feature_bushDropChance = 0.5;
  static final double feature_vendingEatMoneyChance = 0.3;
  static final int feature_bedSleepTimer = 2200;
  static final double feature_workbenchMinimumToolsButtonWidth = 350;
  static final int feature_fenceRefreshTime = 1400;

  // Units
  static final double unit_defaultSize = 0.35;
  static final int unit_defaultHeight = 5;
  static final double unit_defaultSight = 9;
  static final double unit_sneakSpeed = 0.5;
  static final double unit_moveCollisionStopActionTime = 300;
  static final double unit_small_facing_threshhold = 0.01;
  static final double unit_defaultBaseAttackCooldown = 1200;
  static final double unit_defaultBaseAttackTime = 300;
  static final double unit_defaultBaseAttackRange = 0.2;
  static final double unit_defaultRangedAutoAttackSpeed = 7;
  static final double unit_weaponDisplayScaleFactor = 0.8;
  static final double unit_attackAnimation_ratio1 = 0.6;
  static final double unit_attackAnimation_ratio2 = 0.85;
  static final double unit_attackAnimation_amount1 = 0.33 * Math.PI;
  static final double unit_attackAnimation_amount2 = -0.5 * Math.PI;
  static final double unit_attackAnimation_amount3 = Constants.unit_attackAnimation_amount1 + Constants.unit_attackAnimation_amount2;
  static final double unit_attackAnimation_multiplier1 =
    Constants.unit_attackAnimation_amount1 / Constants.unit_attackAnimation_ratio1;
  static final double unit_attackAnimation_multiplier2 = Constants.unit_attackAnimation_amount2 /
    (Constants.unit_attackAnimation_ratio2 - Constants.unit_attackAnimation_ratio1);
  static double unit_attackAnimationAngle(double timerRatio) {
    if (timerRatio < Constants.unit_attackAnimation_ratio1) {
      return timerRatio * Constants.unit_attackAnimation_multiplier1;
    }
    else if (timerRatio < Constants.unit_attackAnimation_ratio2) {
      return Constants.unit_attackAnimation_amount1 + (timerRatio -
        Constants.unit_attackAnimation_ratio1) * Constants.unit_attackAnimation_multiplier2;
    }
    else {
      return Constants.unit_attackAnimation_amount3 * (1 - timerRatio) / (1 - Constants.unit_attackAnimation_ratio2);
    }
  }
  static final double unit_healthbarWidth = 0.9;
  static final double unit_healthbarHeight = 0.15;
  static final double unit_healthbarDamageAnimationTime = 250;
  static final double unit_fallTimer = 100;
  static final int unit_maxAgility = 5;
  static final double unit_agilitySlowDecrease = 0.15;
  static final int unit_noDamageFallHeight = 2;
  static final double unit_fallDamageMultiplier = 0.08;
  static final int unit_timer_talk = 9000;
  static final int unit_timer_target_sound = 4000;
  static final int unit_timer_walk = 380;
  static final int unit_timer_resolve_floor_height_cooldown = 400;
  static final int unit_update_pathfinding_timer = 500;
  static final double unit_footgearDurabilityDistance = 100;

  // Resistances
  static final double resistance_default = 1;
  static final double resistance_blue_blue = 0.85;
  static final double resistance_blue_red = 0.8;
  static final double resistance_blue_brown = 1.2;
  static final double resistance_red_red = 0.85;
  static final double resistance_red_cyan = 0.8;
  static final double resistance_red_blue = 1.2;
  static final double resistance_cyan_cyan = 0.85;
  static final double resistance_cyan_orange = 0.8;
  static final double resistance_cyan_red = 1.2;
  static final double resistance_orange_orange = 0.85;
  static final double resistance_orange_brown = 0.8;
  static final double resistance_orange_cyan = 1.2;
  static final double resistance_brown_brown = 0.85;
  static final double resistance_brown_blue = 0.8;
  static final double resistance_brown_orange = 1.2;
  static final double resistance_purple_purple = 0.85;
  static final double resistance_purple_yellow = 0.8;
  static final double resistance_purple_magenta = 1.2;
  static final double resistance_yellow_yellow = 0.85;
  static final double resistance_yellow_magenta = 0.8;
  static final double resistance_yellow_purple = 1.2;
  static final double resistance_magenta_magenta = 0.85;
  static final double resistance_magenta_purple = 0.8;
  static final double resistance_magenta_yellow = 1.2;

  // Status Effects
  static final int status_hunger_tickTimer = 1400;
  static final double status_hunger_dot = 0.02;
  static final double status_hunger_damageLimit = 0.5;
  static final double status_hunger_weakPercentage = 0.1;
  static final double status_weak_multiplier = 0.9;
  static final int status_thirst_tickTimer = 1400;
  static final double status_thirst_dot = 0.02;
  static final double status_thirst_damageLimit = 0.35;
  static final double status_thirst_woozyPercentage = 0.07;
  static final double status_thirst_confusedPercentage = 0.07;
  static final int status_woozy_tickMaxTimer = 10000;
  static final double status_woozy_maxAmount = 0.5 * Math.PI;
  static final int status_confused_tickMaxTimer = 10000;
  static final double status_confused_maxAmount = 3;
  static final int status_bleed_tickTimer = 1200;
  static final double status_bleed_dot = 0.02;
  static final double status_bleed_damageLimit = 0.1;
  static final double status_bleed_hemorrhagePercentage = 0.1;
  static final int status_hemorrhage_tickTimer = 900;
  static final double status_hemorrhage_dot = 0.04;
  static final double status_hemorrhage_damageLimit = 0;
  static final double status_hemorrhage_bleedPercentage = 0.7;
  static final double status_wilted_multiplier = 0.8;
  static final double status_withered_multiplier = 0.7;
  static final double status_running_multiplier = 1.35;
  static final double status_slowed_multiplier = 0.7;
  static final double status_relaxed_multiplier = 0.85;
  static final double status_relaxed_healMultiplier = 0.01;
  static final double status_drenched_multiplier = 1.3;
  static final int status_drenched_tickTimer = 1200;
  static final double status_drenched_dot = 0.025;
  static final double status_drenched_damageLimit = 0.2;
  static final int status_drowning_tickTimer = 500;
  static final double status_drowning_dot = 0.05;
  static final double status_drowning_damageLimit = 0;
  static final double status_drowning_damageLimitBlue = 0.05;
  static final double status_drowning_drenchedPercentage = 0.7;
  static final int status_burnt_tickTimer = 1200;
  static final double status_burnt_dot = 0.025;
  static final double status_burnt_damageLimit = 0;
  static final double status_burnt_damageLimitRed = 0.1;
  static final double status_burnt_charredPercentage = 0.1;
  static final int status_charred_tickTimer = 600;
  static final double status_charred_dot = 0.03;
  static final double status_charred_damageLimit = 0;
  static final double status_charred_damageLimitRed = 0.05;
  static final double status_chilled_speedMultiplier = 0.5;
  static final double status_chilled_speedMultiplierCyan = 0.8;
  static final double status_chilled_cooldownMultiplier = 0.5;
  static final double status_chilled_cooldownMultiplierCyan = 0.8;
  static final int status_frozen_tickTimer = 1600;
  static final double status_frozen_dot = 0.025;
  static final double status_frozen_damageLimit = 0.1;
  static final double status_sick_damageMultiplier = 1.15;
  static final double status_sick_defenseMultiplier = 0.85;
  static final double status_diseased_damageMultiplier = 1.3;
  static final double status_diseased_defenseMultiplier = 0.7;
  static final int status_rotting_tickTimer = 1000;
  static final double status_rotting_dot = 0.015;
  static final double status_rotting_damageLimit = 0.1;
  static final double status_rotting_damageLimitBrown = 0.2;
  static final double status_rotting_damageLimitBlue = 0.0;
  static final double status_rotting_decayedPercentage = 0.1;
  static final int status_decayed_tickTimer = 1000;
  static final double status_decayed_dot = 0.025;
  static final double status_decayed_damageLimit = 0;
  static final double status_decayed_damageLimitBrown = 0.1;

  // AI
  static final double ai_chickenMoveDistance = 2;
  static final int ai_chickenTimer1 = 3000;
  static final int ai_chickenTimer2 = 45000;

  // Items
  static final int item_disappearTimer = 300000; // 5 minutes
  static final double item_defaultSize = 0.25;
  static final int item_bounceConstant = 800;
  static final double item_bounceOffset = 0.15;
  static final int item_starPieceFrames = 4;
  static final double item_starPieceAnimationTime = 450;
  static final double item_defaultInteractionDistance = 0.3;
  static final double item_stackTextSizeRatio = 0.25;
  static final int item_cigarLitTime = 200000;

  // Projectiles
  static final double projectile_defaultSize = 0.25;
  static final double projectile_autoAttackSize = 0.1;
  static final double projectile_threshholdSpeed = 3;
  static final double projectile_grenadeExplosionRadius = 2;
  static final double projectile_mustangAndSallyExplosionRadius = 2.6;
  static final double projectile_rpgExplosionRadius = 1.6;
  static final double projectile_rpgIIExplosionRadius = 1.8;
  static final double projectile_rayGunExplosionRadius = 1;
  static final double projectile_rayGunIIExplosionRadius = 1.2;

  // Gifs
  static final int gif_move_frames = 35;
  static final int gif_move_time = 1200;
  static final int gif_poof_frames = 8;
  static final int gif_poof_time = 900;
  static final int gif_amphibiousLeap_frames = 10;
  static final int gif_amphibiousLeap_time = 400;
  static final int gif_quizmoQuestion_frames = 3;
  static final int gif_quizmoQuestion_time = 400;
  static final int gif_explosionBig_frames = 23;
  static final int gif_explosionBig_time = 850;
  static final int gif_explosionCrackel_frames = 17;
  static final int gif_explosionCrackel_time = 600;
  static final int gif_explosionFire_frames = 23;
  static final int gif_explosionFire_time = 600;
  static final int gif_explosionNormal_frames = 14;
  static final int gif_explosionNormal_time = 550;
  static final int gif_explosionGreen_frames = 102;
  static final int gif_explosionGreen_time = 650;
  static final int gif_fire_frames = 31;
  static final int gif_fire_time = 2500;
  static final int gif_lava_frames = 18;
  static final int gif_lava_time = 2600;
  static final int gif_drenched_frames = 4;
  static final int gif_drenched_time = 500;
  static final int gif_arrow_frames = 3;
  static final int gif_arrow_time = 350;
  static final int gif_loading_frames = 30;
  static final int gif_loading_time = 950;

  // Hero
  static final double hero_defaultInventoryButtonSize = 50;
  static final int hero_inventoryMaxRows = 6;
  static final int hero_inventoryMaxCols = 9;
  static final int hero_inventoryDefaultStartSlots = 0;
  static final double hero_experienceNextLevel_level = 1.4;
  static final double hero_experienceNextLevel_power = 2.0;
  static final double hero_experienceNextLevel_tier = 3.0;
  static final double hero_killExponent = 1.5;
  static final int hero_maxLevel = 100;
  static final double hero_defaultInventoryBarHeight = 120;
  static final double hero_inventoryBarGap = 10;
  static final double hero_abilityDescriptionMinWidth = 250;
  static final int hero_maxHunger = 100;
  static final int hero_maxThirst = 100;
  static final int hero_hungerTimer = 9000;
  static final int hero_thirstTimer = 5000;
  static final int hero_abilityNumber = 5;
  static final int hero_hungerThreshhold = 20;
  static final int hero_thirstThreshhold = 20;
  static final double hero_statusDescription_width = 160;
  static final double hero_statusDescription_height = 120;
  static final double hero_leftPanelBarHeight = 10;
  static final double hero_leftPanelButtonHoverTimer = 400;
  static final double hero_treeButtonDefaultRadius = 60;
  static final double hero_treeButtonCenterRadius = 90;
  static final double hero_treeForm_width = 300;
  static final double hero_treeForm_height = 340;
  static final double hero_manabarHeight = 0.05;
  static final double hero_experienceRespawnMultiplier = 0.3;
  static final double hero_moneyRespawnMultiplier = 0.3;
  static final double hero_passiveHealPercent = 0.005;
  static final int hero_multicraftTimer = 50;
  static final double hero_scaling_health = 0.6;
  static final double hero_scaling_attack = 0.08;
  static final double hero_scaling_magic = 0.04;
  static final double hero_scaling_defense = 0.02;
  static final double hero_scaling_resistance = 0.01;
  static final double hero_scaling_piercing = 0.00001;
  static final double hero_scaling_penetration = 0.000006;
  static final int hero_timerMagneticHands = 200;
  static final double hero_magneticHandsDistanceMultiplier = 0.9;

  // Upgrade
  static final int upgrade_inventoryI = 2;
  static final int upgrade_inventoryII = 4;
  static final int upgrade_inventory_bar_slots = 3;
  static final double upgrade_healthI = 2.5;
  static final double upgrade_attackI = 1.5;
  static final double upgrade_defenseI = 0.8;
  static final double upgrade_piercingI = 0.05;
  static final double upgrade_speedI = 0.3;
  static final double upgrade_sightI = 1.5;
  static final double upgrade_tenacityI = 0.05;
  static final int upgrade_agilityI = 1;
  static final double upgrade_magicI = 5;
  static final double upgrade_resistanceI = 3;
  static final double upgrade_penetrationI = 0.05;
  static final double upgrade_healthII = 25;
  static final double upgrade_attackII = 12;
  static final double upgrade_defenseII = 6.2;
  static final double upgrade_piercingII = 0.07;
  static final double upgrade_speedII = 0.5;
  static final double upgrade_sightII = 2;
  static final double upgrade_tenacityII = 0.07;
  static final int upgrade_agilityII = 1;
  static final double upgrade_magicII = 20;
  static final double upgrade_resistanceII = 7;
  static final double upgrade_penetrationII = 0.07;
  static final double upgrade_healthIII = 120;

  // Abilities
  // Ben Nelson
  static final int ability_101_rageGain = 2;
  static final int ability_101_rageGainKill = 6;
  static final double ability_101_cooldownTimer = 4000;
  static final double ability_101_tickTimer = 500;
  static final double ability_101_bonusAmount = 0.0025;
  static final double ability_102_powerBase = 1;
  static final double ability_102_powerRatio = 0.35;
  static final double ability_102_distance = 5;
  static final double ability_102_powerBasePen = 5;
  static final double ability_102_powerRatioPen = 0.7;
  static final double ability_102_healRatio = 0.2;
  static final double ability_103_range = 2;
  static final double ability_103_castTime = 800;
  static final double ability_103_coneAngle = 0.15 * Math.PI;
  static final double ability_103_debuff = 0.85;
  static final double ability_103_time = 3000;
  static final double ability_104_passiveHealAmount = 0.01;
  static final double ability_104_passiveHealTimer = 2000;
  static final double ability_104_activeHealAmount = 0.2;
  static final double ability_104_speedBuff = 1.25;
  static final double ability_104_speedBuffTimer = 3000;
  static final int ability_105_rageGain = 40;
  static final double ability_105_buffAmount = 1.4;
  static final double ability_105_buffTime = 4500;
  static final double ability_105_rageGainBonus = 1.8;
  static final double ability_105_fullRageBonus = 1.3;
  static final double ability_105_shakeConstant = 4;
  static final int ability_106_rageGain = 5;
  static final int ability_106_rageGainKill = 10;
  static final double ability_106_cooldownTimer = 6000;
  static final double ability_106_tickTimer = 800;
  static final double ability_106_bonusAmount = 0.004;
  static final double ability_107_powerBase = 10;
  static final double ability_107_powerRatio = 0.5;
  static final double ability_107_distance = 5;
  static final double ability_107_powerBasePen = 50;
  static final double ability_107_powerRatioPen = 1;
  static final double ability_107_healRatio = 0.3;
  static final double ability_108_range = 3;
  static final double ability_108_castTime = 800;
  static final double ability_108_coneAngle = 0.15 * Math.PI;
  static final double ability_108_debuff = 0.75;
  static final double ability_108_time = 5000;
  static final double ability_109_passiveHealAmount = 0.01;
  static final double ability_109_passiveHealTimer = 1500;
  static final double ability_109_activeHealAmount = 0.25;
  static final double ability_109_speedBuff = 1.35;
  static final double ability_109_speedBuffTimer = 4500;
  static final int ability_110_rageGain = 60;
  static final double ability_110_buffAmount = 1.5;
  static final double ability_110_buffTime = 7000;
  static final double ability_110_rageGainBonus = 1.6;
  static final double ability_110_fullRageBonus = 1.5;
  static final double ability_110_shakeConstant = 6;
  // Daniel Gray
  static final double ability_111_stillTime = 3500;
  static final double ability_111_distance = 0.2;
  static final double ability_111_powerBuff = 1.4;
  static final double ability_111_regenTime = 1400;
  static final double ability_112_basePower = 5;
  static final double ability_112_physicalRatio = 0.15;
  static final double ability_112_magicalRatio = 0.4;
  static final double ability_112_distance = 2;
  static final double ability_112_castTime = 500;
  static final double ability_112_slowAmount = 0.7;
  static final double ability_112_slowTime = 3000;
  static final double ability_113_jumpDistance = 2;
  static final double ability_113_jumpHeight = 5;
  static final double ability_113_jumpSpeed = 4.5;
  static final double ability_113_basePower = 3;
  static final double ability_113_physicalRatio = 0.1;
  static final double ability_113_magicalRatio = 0.7;
  static final double ability_113_stunTime = 1000;
  static final double ability_113_splashRadius = 0.5;
  static final double ability_113_killCooldownReduction = 0.5;
  static final double ability_113_drenchedJumpDistance = 3;
  static final double ability_113_drenchedSplashRadius = 0.8;
  static final double ability_114_currHealth = 0.01;
  static final double ability_114_basePower = 1;
  static final double ability_114_magicRatio = 0.05;
  static final double ability_114_range = 0.8;
  static final double ability_114_rotTime = 1200;
  static final double ability_114_tickTime = 500;
  static final double ability_115_range = 0.4;
  static final double ability_115_maxTime = 3500;
  static final double ability_115_basePower = 8;
  static final double ability_115_physicalRatio = 0.1;
  static final double ability_115_magicalRatio = 0.7;
  static final double ability_115_regurgitateSpeed = 5;
  static final double ability_115_regurgitateDistance = 2.5;
  static final double ability_116_stillTime = 2000;
  static final double ability_116_distance = 0.1;
  static final double ability_116_powerBuff = 1.7;
  static final double ability_116_regenTime = 1200;
  static final double ability_117_basePower = 15;
  static final double ability_117_physicalRatio = 0.20;
  static final double ability_117_magicalRatio = 0.8;
  static final double ability_117_distance = 2.5;
  static final double ability_117_slowTime = 4000;
  static final double ability_118_jumpDistance = 2.5;
  static final double ability_118_basePower = 12;
  static final double ability_118_physicalRatio = 0.2;
  static final double ability_118_magicalRatio = 1.2;
  static final double ability_118_stunTime = 1200;
  static final double ability_118_splashRadius = 0.6;
  static final double ability_118_killCooldownReduction = 0.2;
  static final double ability_118_drenchedJumpDistance = 4;
  static final double ability_118_drenchedSplashRadius = 1;
  static final double ability_119_currHealth = 0.015;
  static final double ability_119_basePower = 2;
  static final double ability_119_magicRatio = 0.08;
  static final double ability_119_range = 1;
  static final double ability_120_maxTime = 5000;
  static final double ability_120_basePower = 15;
  static final double ability_120_physicalRatio = 0.2;
  static final double ability_120_magicalRatio = 1.4;
  // Cathy Heck
  static final double ability_1001_range = 2.7;
  static final double ability_1001_castTime = 600;
  static final double ability_1001_coneAngle = 0.12 * Math.PI;
  static final double ability_1001_tanConeAngle = 2 * (double)Math.tan(Constants.ability_1001_coneAngle);
  static final double ability_1001_basePower = 2;
  static final double ability_1001_magicRatio = 0.8;
  static final double ability_1001_woozyTime = 5000;
  static final double ability_1002_castTime = 600;
  static final double ability_1002_range = 30;
  static final double ability_1002_basePower = 6;
  static final double ability_1002_magicRatio = 1.1;
  static final double ability_1003_basePower = 10;
  static final double ability_1003_magicRatio = 1.4;
  static final double ability_1003_maxHealth = 0.05;
  static final double ability_1003_statusTime = 2500;
  static final double ability_1003_castTime = 500;
  static final double ability_1003_size_w = 1.7;
  static final double ability_1003_size_h = 1.24;
  // Ben Kohring
  static final double ability_1021_time = 3600;
  static final double ability_1021_speed = 1.8;
  static final double ability_1021_lifesteal = 0.08;
  static final double ability_1021_piercing = 0.22;
  static final double ability_1021_attackspeed = 0.5;
  static final double ability_1022_powerBase = 4;
  static final double ability_1022_powerRatio = 0.55;
  static final double ability_1022_distance = 5;

  // Level
  static final double level_questBoxHeightRatio = 0.25;
  static final double level_decisionFormWidth = 500;
  static final double level_decisionFormHeight = 500;
  static final double level_vendingFormWidth = 300;
  static final double level_vendingFormHeight = 600;
  static final double level_quizmoFormWidth = 400;
  static final double level_quizmoFormHeight = 650;
  static final double level_quizmoTimeDelay = 900;
  static final double level_khalilFormWidth = 550;
  static final double level_khalilFormHeight = 750;
  static final double level_vehicleFormWidth = 450;
  static final double level_vehicleFormHeight = 380;
  static final int level_questBlinkTime = 500;
  static final int level_questBlinks = 3;
  static final double level_timeConstants = 0.00002; // 20 minute day / night cycles
  static final double level_dayLightLevel = 9.5;
  static final double level_nightLightLevel = 3.5;
  static final double level_zombieSpawnLightThreshhold = 5;
  static final int level_defaultRespawnTimer = 5000;
}
