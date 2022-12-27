package com.github.mim1q.minecells.datagen;

import com.github.mim1q.minecells.registry.MineCellsBlocks;
import com.github.mim1q.minecells.registry.MineCellsItems;
import com.github.mim1q.minecells.util.DropUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class MineCellsBlockLootTableProvider extends MineCellsLootTableHelper {
  public MineCellsBlockLootTableProvider(FabricDataGenerator dataGenerator) {
    super(dataGenerator, LootContextTypes.BLOCK);
  }

  @Override
  public void accept(BiConsumer<Identifier, LootTable.Builder> biConsumer) {
    generateSelfDroppingBlocks(biConsumer,
      // Wood
      MineCellsBlocks.PUTRID_LOG,
      MineCellsBlocks.STRIPPED_PUTRID_LOG,
      MineCellsBlocks.PUTRID_WOOD,
      MineCellsBlocks.STRIPPED_PUTRID_WOOD,
      // Other
      MineCellsBlocks.BIG_CHAIN,
      MineCellsBlocks.ELEVATOR_ASSEMBLER,
      MineCellsBlocks.BROKEN_CAGE
    );

    generateBlock(biConsumer, MineCellsBlocks.BIOME_BANNER, MineCellsItems.BIOME_BANNER);

    biConsumer.accept(
      MineCellsBlocks.HANGED_SKELETON.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(ItemEntry.builder(Items.BONE), 2, 5))
        .pool(simplePool(ItemEntry.builder(Items.SKELETON_SKULL), 1))
    );

    biConsumer.accept(
      MineCellsBlocks.CHAIN_PILE_BLOCK.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(silkTouchEntry(MineCellsBlocks.CHAIN_PILE_BLOCK), 1))
        .pool(simplePool(noSilkTouchEntry(MineCellsBlocks.BIG_CHAIN), 1, 2))
        .pool(simplePool(noSilkTouchEntry(Blocks.CHAIN), 1, 2))
    );

    biConsumer.accept(
      MineCellsBlocks.CHAIN_PILE.getLootTableId(),
      LootTable.builder()
        .pool(simplePool(silkTouchEntry(MineCellsBlocks.CHAIN_PILE), 1))
        .pool(simplePool(noSilkTouchEntry(MineCellsBlocks.BIG_CHAIN), 1))
    );

    var crateLoot = List.of(
      new Pair<>(Items.GOLD_NUGGET, 9),
      new Pair<>(Items.IRON_NUGGET, 9),
      new Pair<>(Items.IRON_INGOT, 1),
      new Pair<>(Items.GOLD_INGOT, 1)
    );

    generateTresureLootTable(biConsumer, MineCellsBlocks.CRATE, 2, 4, crateLoot);
    generateTresureLootTable(biConsumer, MineCellsBlocks.SMALL_CRATE, 1, 3, crateLoot);
    generateTresureLootTable(biConsumer, MineCellsBlocks.BRITTLE_BARREL, 2, 3, crateLoot);
  }

  public static void generateTresureLootTable(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block block,
    int minRolls,
    int maxRolls,
    Collection<Pair<Item, Integer>> items
  ) {
    LootTable.Builder builder = LootTable.builder();
    LootPool.Builder poolBuilder = LootPool.builder().rolls(UniformLootNumberProvider.create(minRolls, maxRolls));

    for (Pair<Item, Integer> item : items) {
      poolBuilder.with(noSilkTouchEntry(item.getLeft()).weight(item.getRight()));
    }

    builder.pool(poolBuilder).pool(simplePool(silkTouchEntry(block), 1));
    biConsumer.accept(block.getLootTableId(), builder);
  }

  public static void generateSelfDroppingBlock(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block block) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(block));
  }

  public static void generateSelfDroppingBlocks(BiConsumer<Identifier, LootTable.Builder> biConsumer, Block... blocks) {
    for (Block block : blocks) {
      generateSelfDroppingBlock(biConsumer, block);
    }
  }

  public static void generateBlock(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block block,
    ItemConvertible drop
  ) {
    biConsumer.accept(block.getLootTableId(), FabricBlockLootTableProvider.drops(drop));
  }

  public static void generateShearsOrSilkTouchDrop(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block block,
    ItemConvertible drop
  ) {
    biConsumer.accept(block.getLootTableId(), LootTable.builder()
      .pool(simplePool(conditionalEntry(drop, DropUtils.SHEARS_OR_SILK_TOUCH, ConstantLootNumberProvider.create(1)), 1))
    );
  }

  public static void generateBlocksDroppingSelfWithSilkTouchOrShears(
    BiConsumer<Identifier, LootTable.Builder> biConsumer,
    Block... blocks
  ) {
    for (Block block : blocks) {
      generateShearsOrSilkTouchDrop(biConsumer, block, block);
    }
  }

  public static LeafEntry.Builder<?> silkTouchEntry(ItemConvertible item, boolean silkTouch) {
    var condition = MatchToolLootCondition.builder(
      ItemPredicate.Builder.create().enchantment(
        new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))
      )
    );
    return ItemEntry.builder(item).conditionally(silkTouch ? condition : condition.invert());
  }

  public static LeafEntry.Builder<?> silkTouchEntry(ItemConvertible item) {
    return silkTouchEntry(item, true);
  }

  public static LeafEntry.Builder<?> noSilkTouchEntry(ItemConvertible item) {
    return silkTouchEntry(item, false);
  }
}
