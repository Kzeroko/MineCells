package datagen.custom

import tada.lib.presets.Preset
import tada.lib.presets.common.CommonModelPresets
import tada.lib.resources.model.ParentedModel

object ModItemModels {
  fun generated() = Preset {
    listOf(
      "cage", "broken_cage", "blank_rune", "conjunctivius_respawn_rune", "vine_rune", "guts", "monsters_eye", "sewage_bucket",
      "ancient_sewage_bucket", "phaser", "health_flask", "king_statue", "barrier_rune", "elevator_mechanism", "prison_doorway",
      "block/red_putrid_sapling", "reset_rune"
    ).forEach {
      if (it.startsWith("block/")) {
        add(CommonModelPresets.generatedItemModel("minecells:${it.removePrefix("block/")}", "block"))
      } else {
        add(CommonModelPresets.generatedItemModel("minecells:$it"))
      }
    }
  }

  fun handheld() = Preset {
    listOf("assassins_dagger", "cursed_sword", "tentacle").forEach {
      add(it, ParentedModel.item("minecraft:item/handheld") {
        texture("layer0", "minecells:item/$it")
      })
    }
  }

  fun spawnEggs() = Preset {
    listOf(
      "leaping_zombie", "shocker", "grenadier", "disgusting_worm", "inquisitor", "kamikaze", "protector", "undead_archer",
      "shieldbearer", "mutated_bat", "sewers_tentacle", "rancid_rat", "runner", "scorpion",
    ).forEach {
      add("${it}_spawn_egg", ParentedModel.item("minecraft:item/template_spawn_egg"))
    }
  }

  fun blockModels() = Preset {
    listOf("brittle_barrel", "spikes", "flag_pole", "spawner_rune").forEach {
      add(CommonModelPresets.itemBlockModel("minecells:$it"))
    }
  }
}