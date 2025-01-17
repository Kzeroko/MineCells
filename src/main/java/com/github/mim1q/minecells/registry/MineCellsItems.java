package com.github.mim1q.minecells.registry;

import com.github.mim1q.minecells.MineCells;
import com.github.mim1q.minecells.item.BiomeBannerItem;
import com.github.mim1q.minecells.item.DoorwayItem;
import com.github.mim1q.minecells.item.HealthFlaskItem;
import com.github.mim1q.minecells.item.ResetRuneItem;
import com.github.mim1q.minecells.item.skill.PhaserItem;
import com.github.mim1q.minecells.item.weapon.*;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;

public class MineCellsItems {
  public static final Item ELEVATOR_MECHANISM = register(
    new Item(new FabricItemSettings()),
    "elevator_mechanism"
  );

  public static final Item BLANK_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1)),
    "blank_rune"
  );

  public static final Item CONJUNCTIVIUS_RESPAWN_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1)),
    "conjunctivius_respawn_rune"
  );

  public static final Item VINE_RUNE = register(
    new Item(new FabricItemSettings().maxCount(1).maxDamage(8)),
    "vine_rune"
  );

  public static final Item RESET_RUNE = register(
    new ResetRuneItem(new FabricItemSettings().maxCount(1)),
    "reset_rune"
  );

  public static final Item GUTS = register(
    new Item(new FabricItemSettings().food(FoodComponents.BEEF)),
    "guts"
  );

  public static final Item MONSTERS_EYE = register(
    new Item(new FabricItemSettings().food(FoodComponents.COOKED_BEEF)),
    "monsters_eye"
  );

  public static final BiomeBannerItem BIOME_BANNER = register(
    new BiomeBannerItem(new FabricItemSettings()),
    "biome_banner"
  );

  public static final Item SEWAGE_BUCKET = register(
    new BucketItem(MineCellsFluids.STILL_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
      
    ),
    "sewage_bucket"
  );

  public static final Item ANCIENT_SEWAGE_BUCKET = register(
    new BucketItem(MineCellsFluids.STILL_ANCIENT_SEWAGE, new FabricItemSettings()
      .maxCount(1)
      .recipeRemainder(Items.BUCKET)
      
    ),
    "ancient_sewage_bucket"
  );

  // Weapons
  public static final Item ASSASSINS_DAGGER = register(new AssassinsDaggerItem(2, -2.1F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      
      .rarity(Rarity.UNCOMMON)
    ), "assassins_dagger"
  );

  public static final Item BLOOD_SWORD = register(new BloodSwordItem(4, -2.4F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.UNCOMMON)
      
    ), "blood_sword"
  );

  public static final Item CURSED_SWORD = register(new CursedSwordItem(22, -3.0F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(600)
      
    ), "cursed_sword"
  );

  public static final Item TENTACLE = register(
    new TentacleItem(9, 0, -3.0F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(800)
      .rarity(Rarity.EPIC)
      
    ), "tentacle"
  );

  public static final Item HATTORIS_KATANA = register(
    new HattorisKatanaItem(4, -2.2F, new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1200)
      .rarity(Rarity.RARE)
      
    ), "hattoris_katana"
  );

  public static final Item BROADSWORD = register(new SwordItem(ToolMaterials.IRON, 7, -2.9F,
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(1000)
      .rarity(Rarity.COMMON)
      
    ), "broadsword"
  );

  public static final Item BALANCED_BLADE = register(new BalancedBladeItem(ToolMaterials.IRON, 2, -2.4F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1200)
        .rarity(Rarity.COMMON)
        
    ), "balanced_blade"
  );

  public static final Item CROWBAR = register(new CrowbarItem(ToolMaterials.IRON, 3, -2.4F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1100)
        .rarity(Rarity.COMMON)
        
    ), "crowbar"
  );

  public static final Item NUTCRACKER = register(new NutcrackerItem(7.0F, -3.0F,
      new FabricItemSettings()
        .maxCount(1)
        .maxDamage(1000)
        .rarity(Rarity.COMMON)
        
    ), "nutcracker"
  );

  public static final Item FROST_BLAST = register(new FrostBlastItem(
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(32)
      .rarity(Rarity.COMMON)
      
    ), "frost_blast"
  );

  // Skills
  public static final PhaserItem PHASER = register(new PhaserItem(
    new FabricItemSettings()
      .maxCount(1)
      .maxDamage(32)
      .rarity(Rarity.COMMON)
      
    ), "phaser"
  );

  public static final Item HEALTH_FLASK = register(
    new HealthFlaskItem(new FabricItemSettings()
      .maxCount(16)
      
    ), "health_flask"
  );

  public static final DoorwayItem PRISON_DOORWAY = register(
    new DoorwayItem(new FabricItemSettings()
      .maxCount(1)
      
    ), "prison_doorway"
  );

  public static void init() {
    AttackBlockCallback.EVENT.register(
      (player, world, hand, pos, direction) -> {
        ItemStack stack = player.getStackInHand(player.getActiveHand());
        if (stack.isOf(CROWBAR) && world.getBlockState(pos).isIn(BlockTags.WOODEN_DOORS)) {
          world.breakBlock(pos, false, player);
          stack.getOrCreateNbt().putLong("lastDoorBreakTime", world.getTime());
          return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
      }
    );
  }

  public static <E extends Item> E register(E item, String name) {
    Registry.register(Registries.ITEM, MineCells.createId(name), item);
    return item;
  }
}
