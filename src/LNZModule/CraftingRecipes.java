package LNZModule;

import java.util.*;

class CraftingRecipes {
  static HashMap<Integer, CraftingRecipe> getAllCraftingRecipes() {
    HashMap<Integer, CraftingRecipe> all_recipes = new HashMap<Integer, CraftingRecipe>();
    int[][] ingredients;

    // ### Food
    // Hot pockets from package
    ingredients = new int[][]{{2124}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2125, 5, new ToolCode[]{}));

    // ### Household Crafting
    // Workbench
    ingredients = new int[][]{{2816, 2816}, {2818, 2818}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2167, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Bed
    ingredients = new int[][]{{2819, 2819, 2819}, {2816, 2816, 2816}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2168, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Wooden Box
    ingredients = new int[][]{{2818, 2818}, {2818, 2818}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2169, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Wooden Crate
    ingredients = new int[][]{{2818, 0, 2818}, {2818, 2818, 2818}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2170, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    ingredients = new int[][]{{2816, 2816}, {2816, 2816}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2170, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Wooden Chest
    ingredients = new int[][]{{2818, 2818, 2818}, {2818, 0, 2818}, {2818, 2818, 2818}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2171, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    ingredients = new int[][]{{2816, 0, 2816}, {2816, 2816, 2816}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2171, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Large Wooden Chest
    ingredients = new int[][]{{2171, 2171}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2172, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Candlestick from broken candlestick
    ingredients = new int[][]{{2161}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2162, 1, new ToolCode[]{ToolCode.GLUE, ToolCode.CLAMP}));
    // Candle
    ingredients = new int[][]{{0, 2809, 0}, {2810, 2809, 2810}, {2810, 2810, 2810}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2163, 1, new ToolCode[]{}));
    // lords day candle
    ingredients = new int[][]{{2163}, {2162}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2164, 1, new ToolCode[]{}));
    // Crumpled paper from paper
    ingredients = new int[][]{{2913}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2916, 1, new ToolCode[]{}));

    // ### Melee Weapons
    // Knife
    ingredients = new int[][]{{2834}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2203, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.CLAMP}));
    // Wooden Sword
    ingredients = new int[][]{{2818}, {2818}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2205, 1, new ToolCode[]{ToolCode.GLUE, ToolCode.CLAMP}));
    // Wooden Spear
    ingredients = new int[][]{{2818}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2207, 1, new ToolCode[]{ToolCode.GLUE, ToolCode.CLAMP}));
    // Talc Sword
    ingredients = new int[][]{{2802}, {2802}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2206, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Talc Spear
    ingredients = new int[][]{{2802}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2208, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Gypsum Sword
    ingredients = new int[][]{{2812}, {2812}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2212, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Gypsum Spear
    ingredients = new int[][]{{2812}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2213, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Board with Nails
    ingredients = new int[][]{{2985}, {2818}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2214, 1, new ToolCode[]{ToolCode.DRIVER}));
    // Calcite Sword
    ingredients = new int[][]{{2822}, {2822}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2221, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Calcite Spear
    ingredients = new int[][]{{2822}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2222, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Fluorite sword
    ingredients = new int[][]{{2832}, {2832}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2231, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Fluorite Spear
    ingredients = new int[][]{{2832}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2232, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Iron sword
    ingredients = new int[][]{{2834}, {2834}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2233, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Iron Spear
    ingredients = new int[][]{{2834}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2234, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Apatite Sword
    ingredients = new int[][]{{2842}, {2842}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2241, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Apatite Spear
    ingredients = new int[][]{{2842}, {2817}, {2817}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2242, 1, new ToolCode[]{ToolCode.FASTENER, ToolCode.DRIVER}));
    // Orthoclase Sword
    ingredients = new int[][]{{2852}, {2852}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2251, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Orthoclase Spear
    ingredients = new int[][]{{2852}, {2843}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2252, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Quartz Sword
    ingredients = new int[][]{{2862}, {2862}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2261, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Quartz Spear
    ingredients = new int[][]{{2862}, {2843}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2262, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Topaz Sword
    ingredients = new int[][]{{2872}, {2872}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2271, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Topaz Spear
    ingredients = new int[][]{{2872}, {2843}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2272, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Corundum Sword
    ingredients = new int[][]{{2882}, {2882}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2281, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Corundum Spear
    ingredients = new int[][]{{2882}, {2843}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2282, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Diamond Sword
    ingredients = new int[][]{{2892}, {2892}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2291, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));
    // Diamond Spear
    ingredients = new int[][]{{2892}, {2843}, {2843}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2292, 1, new ToolCode[]{ToolCode.MECHANICAL_FASTENER, ToolCode.MECHANICAL_DRIVER}));

    // ### Headgear
    // Talc Helmet
    ingredients = new int[][]{{2802, 2802, 2802}, {2802, 0, 2802}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2401, 1, new ToolCode[]{}));
    // Gypsum Helmet
    ingredients = new int[][]{{2812, 2812, 2812}, {2812, 0, 2812}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2411, 1, new ToolCode[]{}));
    // Leather Helmet
    ingredients = new int[][]{{5811, 5811, 5811}, {5811, 0, 5811}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2412, 1, new ToolCode[]{}));
    // Calcite Helmet
    ingredients = new int[][]{{2822, 2822, 2822}, {2822, 0, 2822}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2421, 1, new ToolCode[]{}));
    // Fluorite Helmet
    ingredients = new int[][]{{2832, 2832, 2832}, {2832, 0, 2832}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2431, 1, new ToolCode[]{}));
    // Iron Helmet
    ingredients = new int[][]{{2834, 2834, 2834}, {2834, 0, 2834}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2432, 1, new ToolCode[]{}));
    // Apatite Helmet
    ingredients = new int[][]{{2842, 2842, 2842}, {2842, 0, 2842}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2441, 1, new ToolCode[]{}));
    // Orthoclase Helmet
    ingredients = new int[][]{{2852, 2852, 2852}, {2852, 0, 2852}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2451, 1, new ToolCode[]{}));
    // Quartz Helmet
    ingredients = new int[][]{{2862, 2862, 2862}, {2862, 0, 2862}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2461, 1, new ToolCode[]{}));
    // Topaz Helmet
    ingredients = new int[][]{{2872, 2872, 2872}, {2872, 0, 2872}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2471, 1, new ToolCode[]{}));
    // Corundum Helmet
    ingredients = new int[][]{{2882, 2882, 2882}, {2882, 0, 2882}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2481, 1, new ToolCode[]{}));
    // Diamond Helmet
    ingredients = new int[][]{{2892, 2892, 2892}, {2892, 0, 2892}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2491, 1, new ToolCode[]{}));

    // ### Chestgear
    // Talc Chestplate
    ingredients = new int[][]{{2802, 0, 2802}, {2802, 2802, 2802}, {2802, 2802, 2802}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2501, 1, new ToolCode[]{}));
    // Gypsum Chestplate
    ingredients = new int[][]{{2812, 0, 2812}, {2812, 2812, 2812}, {2812, 2812, 2812}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2511, 1, new ToolCode[]{}));
    // Leather Shirt
    ingredients = new int[][]{{5811, 0, 5811}, {5811, 5811, 5811}, {5811, 5811, 5811}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2514, 1, new ToolCode[]{}));
    // Calcite Chestplate
    ingredients = new int[][]{{2822, 0, 2822}, {2822, 2822, 2822}, {2822, 2822, 2822}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2521, 1, new ToolCode[]{}));
    // Fluorite Chestplate
    ingredients = new int[][]{{2832, 0, 2832}, {2832, 2832, 2832}, {2832, 2832, 2832}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2531, 1, new ToolCode[]{}));
    // Iron Chestplate
    ingredients = new int[][]{{2834, 0, 2834}, {2834, 2834, 2834}, {2834, 2834, 2834}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2532, 1, new ToolCode[]{}));
    // Apatite Chestplate
    ingredients = new int[][]{{2842, 0, 2842}, {2842, 2842, 2842}, {2842, 2842, 2842}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2541, 1, new ToolCode[]{}));
    // Orthoclase Chestplate
    ingredients = new int[][]{{2852, 0, 2852}, {2852, 2852, 2852}, {2852, 2852, 2852}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2551, 1, new ToolCode[]{}));
    // Quartz Chestplate
    ingredients = new int[][]{{2862, 0, 2862}, {2862, 2862, 2862}, {2862, 2862, 2862}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2561, 1, new ToolCode[]{}));
    // Topaz Chestplate
    ingredients = new int[][]{{2872, 0, 2872}, {2872, 2872, 2872}, {2872, 2872, 2872}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2571, 1, new ToolCode[]{}));
    // Corundum Chestplate
    ingredients = new int[][]{{2882, 0, 2882}, {2882, 2882, 2882}, {2882, 2882, 2882}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2581, 1, new ToolCode[]{}));
    // Diamond Chestplate
    ingredients = new int[][]{{2892, 0, 2892}, {2892, 2892, 2892}, {2892, 2892, 2892}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2591, 1, new ToolCode[]{}));

    // ### Leggear
    // Talc Greaves
    ingredients = new int[][]{{2802, 2802, 2802}, {2802, 0, 2802}, {2802, 0, 2802}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2601, 1, new ToolCode[]{}));
    // Gypsum Greaves
    ingredients = new int[][]{{2812, 2812, 2812}, {2812, 0, 2812}, {2812, 0, 2812}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2611, 1, new ToolCode[]{}));
    // Leather Pants
    ingredients = new int[][]{{5811, 5811, 5811}, {5811, 0, 5811}, {5811, 0, 5811}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2612, 1, new ToolCode[]{}));
    // Calcite Greaves
    ingredients = new int[][]{{2822, 2822, 2822}, {2822, 0, 2822}, {2822, 0, 2822}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2621, 1, new ToolCode[]{}));
    // Fluorite Greaves
    ingredients = new int[][]{{2832, 2832, 2832}, {2832, 0, 2832}, {2832, 0, 2832}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2631, 1, new ToolCode[]{}));
    // Iron Greaves
    ingredients = new int[][]{{2834, 2834, 2834}, {2834, 0, 2834}, {2834, 0, 2834}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2632, 1, new ToolCode[]{}));
    // Apatite Greaves
    ingredients = new int[][]{{2842, 2842, 2842}, {2842, 0, 2842}, {2842, 0, 2842}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2641, 1, new ToolCode[]{}));
    // Orthoclase Greaves
    ingredients = new int[][]{{2852, 2852, 2852}, {2852, 0, 2852}, {2852, 0, 2852}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2651, 1, new ToolCode[]{}));
    // Quartz Greaves
    ingredients = new int[][]{{2862, 2862, 2862}, {2862, 0, 2862}, {2862, 0, 2862}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2661, 1, new ToolCode[]{}));
    // Topaz Greaves
    ingredients = new int[][]{{2872, 2872, 2872}, {2872, 0, 2872}, {2872, 0, 2872}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2671, 1, new ToolCode[]{}));
    // Corundum Greaves
    ingredients = new int[][]{{2882, 2882, 2882}, {2882, 0, 2882}, {2882, 0, 2882}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2681, 1, new ToolCode[]{}));
    // Diamond Greaves
    ingredients = new int[][]{{2892, 2892, 2892}, {2892, 0, 2892}, {2892, 0, 2892}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2691, 1, new ToolCode[]{}));

    // ### Footgear
    // Talc Boots
    ingredients = new int[][]{{2802, 0, 2802}, {2802, 0, 2802}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2701, 1, new ToolCode[]{}));
    // Gypsum Boots
    ingredients = new int[][]{{2812, 0, 2812}, {2812, 0, 2812}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2711, 1, new ToolCode[]{}));
    // Leather Boots
    ingredients = new int[][]{{5811, 0, 5811}, {5811, 0, 5811}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2715, 1, new ToolCode[]{}));
    // Calcite Boots
    ingredients = new int[][]{{2822, 0, 2822}, {2822, 0, 2822}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2721, 1, new ToolCode[]{}));
    // Fluorite Boots
    ingredients = new int[][]{{2832, 0, 2832}, {2832, 0, 2832}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2731, 1, new ToolCode[]{}));
    // Iron Boots
    ingredients = new int[][]{{2834, 0, 2834}, {2834, 0, 2834}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2732, 1, new ToolCode[]{}));
    // Apatite Boots
    ingredients = new int[][]{{2842, 0, 2842}, {2842, 0, 2842}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2741, 1, new ToolCode[]{}));
    // Orthoclase Boots
    ingredients = new int[][]{{2852, 0, 2852}, {2852, 0, 2852}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2751, 1, new ToolCode[]{}));
    // Quartz Boots
    ingredients = new int[][]{{2862, 0, 2862}, {2862, 0, 2862}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2761, 1, new ToolCode[]{}));
    // Topaz Boots
    ingredients = new int[][]{{2872, 0, 2872}, {2872, 0, 2872}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2771, 1, new ToolCode[]{}));
    // Corundum Boots
    ingredients = new int[][]{{2882, 0, 2882}, {2882, 0, 2882}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2781, 1, new ToolCode[]{}));
    // Diamond Boots
    ingredients = new int[][]{{2892, 0, 2892}, {2892, 0, 2892}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2791, 1, new ToolCode[]{}));

    // ### Materials
    // String (vertical)
    ingredients = new int[][]{{5801}, {5801}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2809, 1, new ToolCode[]{}));
    // String (horizontal)
    ingredients = new int[][]{{5801, 5801}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2809, 1, new ToolCode[]{}));
    // Wooden Planks (hand)
    ingredients = new int[][]{{2969}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2816, 1, new ToolCode[]{ToolCode.SAW}));
    // Wooden Planks (mechanical)
    ingredients = new int[][]{{2969, 2969}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2816, 4, new ToolCode[]{ToolCode.MECHANICAL_SAW}));
    // Wooden Piece (hand)
    ingredients = new int[][]{{2816}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2818, 2, new ToolCode[]{ToolCode.SAW}));
    // Wooden Piece (mechanical)
    ingredients = new int[][]{{2816, 2816}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2818, 8, new ToolCode[]{ToolCode.MECHANICAL_SAW}));
    // Wooden Handle
    ingredients = new int[][]{{2818}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2817, 1, new ToolCode[]{ToolCode.SAW}));
    // Iron Handle
    ingredients = new int[][]{{2834}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2817, 1, new ToolCode[]{}));

    // ### Tools
    // Hammerstone
    ingredients = new int[][]{{2931}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2934, 1, new ToolCode[]{}));
    ingredients = new int[][]{{2933}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2934, 1, new ToolCode[]{}));
    // Stick from branches
    ingredients = new int[][]{{2965}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2963, 1, new ToolCode[]{}));
    ingredients = new int[][]{{2966}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2963, 1, new ToolCode[]{}));
    ingredients = new int[][]{{2967}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2963, 1, new ToolCode[]{}));
    ingredients = new int[][]{{2968}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2963, 1, new ToolCode[]{}));
    // Kindling
    ingredients = new int[][]{{2963, 2963}, {2963, 2963}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2964, 1, new ToolCode[]{}));
    // Wooden Peg
    ingredients = new int[][]{{2963}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2970, 1, new ToolCode[]{}));
    // Stone Hatchet
    ingredients = new int[][]{{2931}, {2963}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2977, 1, new ToolCode[]{ToolCode.STRING}));
    // Waterskin
    ingredients = new int[][]{{2820, 2820}, {2820, 2820}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2930, 1, new ToolCode[]{ToolCode.STRING}));
    ingredients = new int[][]{{5811, 5811}, {5811, 5811}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 2930, 1, new ToolCode[]{ToolCode.STRING}));
    // Large Waterskin
    ingredients = new int[][]{{2820, 2820, 2820}, {2820, 2820, 2820}, {0, 2820, 0}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 5921, 1, new ToolCode[]{ToolCode.STRING, ToolCode.STRING}));
    ingredients = new int[][]{{5811, 5811, 5811}, {5811, 5811, 5811}, {0, 5811, 0}};
    all_recipes.putIfAbsent(Arrays.deepHashCode(ingredients), new CraftingRecipe(
      ingredients, 5921, 1, new ToolCode[]{ToolCode.STRING, ToolCode.STRING}));

    return all_recipes;
  }
}