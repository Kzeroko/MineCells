package com.github.mim1q.minecells.block.setupblocks;

import com.github.mim1q.minecells.entity.boss.MineCellsBossEntity;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.rpgdifficulty.api.MobStrengthener;

import java.util.ArrayList;
import java.util.List;

public class MonsterBoxBlock extends SetupBlock {

  private final List<Entry> entries = new ArrayList<>();
  private final float chance;

  public MonsterBoxBlock(float chance, Entry ...entries) {
    super(Settings.of(Material.STONE));
    this.entries.addAll(List.of(entries));
    this.chance = chance;
  }

  public MonsterBoxBlock(EntityType<?> entityType) {
    this(1.0F, new Entry(entityType, 1));
  }

  @Override
  public boolean setup(World world, BlockPos pos, BlockState state) {
    world.removeBlock(pos, false);
    if (world.getRandom().nextFloat() > chance) {
      return true;
    }
    EntityType<?> entityType = chooseRandomEntry(world.getRandom()).entityType;
    HostileEntity e = (HostileEntity) entityType.create(world);
    if (e != null) {
      e.setPersistent();
      e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
      e.initialize((ServerWorldAccess) world, world.getLocalDifficulty(pos), SpawnReason.EVENT, null, null);
      if (FabricLoader.getInstance().isModLoaded("rpgdifficulty")) {
        if (e instanceof MineCellsBossEntity) {
          MobStrengthener.changeBossAttributes(e, (ServerWorld) world);
        } else {
          MobStrengthener.changeAttributes(e, (ServerWorld) world);
        }
      }
      world.spawnEntity(e);
      return true;
    }
    return false;
  }

  protected Entry chooseRandomEntry(Random random) {
    int totalWeight = 0;
    for (Entry entry : entries) {
      totalWeight += entry.weight;
    }
    float chance = random.nextInt(totalWeight + 1);
    for (Entry entry : entries) {
      chance -= entry.weight;
      if (chance <= 0) {
        return entry;
      }
    }
    return entries.get(0);
  }

  public record Entry(EntityType<?> entityType, int weight) { }
}
