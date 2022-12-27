from presets.preset_generator import Preset
from template import Template, TemplateType


class SimpleDrop(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block, {"item": block}),
        ])


class SilkTouchOrShearsOnlyDrop(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCK_LOOT_TABLE, "silk_touch_or_shears_only", block, {"item": block}),
        ])


class ItemBlockModel(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.ITEM_MODEL, "block_model", block, {"block": block}),
        ])


class AlternativeItemBlockModel(Preset):
    def __init__(self, item: str, block: str):
        super().__init__([
            Template(TemplateType.ITEM_MODEL, "block_model", item, {"block": block}),
        ])


class GeneratedItemModel(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.ITEM_MODEL, "generated", block, {"texture": block}),
        ])


class SimpleBlock(Preset):
    def __init__(self, block: str):
        super().__init__([
            Template(TemplateType.BLOCKSTATE, "single_state", block, {"block": block}),
            Template(TemplateType.BLOCK_MODEL, "cube_all", block, {"texture": block}),
            Template(TemplateType.BLOCK_LOOT_TABLE, "simple_drop", block, {"item": block}),
            Template(TemplateType.ITEM_MODEL, "block_model", block, {"block": block}),
        ])


class Stairs(Preset):
    def __init__(self, base: str, block: str = None, stone = False):
        if block is None:
            block = base
        templates = [
            Template(TemplateType.BLOCKSTATE, "stairs", base + "_stairs", {"base": base}),
            Template(TemplateType.BLOCK_MODEL, "stairs", base + "_stairs", {"texture": block}),
            Template(TemplateType.BLOCK_MODEL, "stairs_inner", base + "_stairs_inner", {"texture": block}),
            Template(TemplateType.BLOCK_MODEL, "stairs_outer", base + "_stairs_outer", {"texture": block}),
            Template(TemplateType.RECIPE, "stairs", base + "_stairs", {"input": block, "output": base + "_stairs"}),
            Template(TemplateType.RECIPE, "stairs_flipped", base + "_stairs_flipped", {"input": block, "output": base + "_stairs"}),
        ]
        if stone:
            templates.append(Template(TemplateType.RECIPE, "stonecutting", base + "_stairs_stonecutting", {"input": block, "output": base + "_stairs", "count": 1}))
        super().__init__(
            templates,
            [
                SimpleDrop(base + "_stairs"),
                ItemBlockModel(base + "_stairs"),
            ]
        )


class Slab(Preset):
    def __init__(self, base: str, block: str = None, stone: bool = False):
        if block is None:
            block = base
        templates = [
            Template(TemplateType.BLOCKSTATE, "slab", base + "_slab", {"block": block, "base": base}),
            Template(TemplateType.BLOCK_MODEL, "slab", base + "_slab", {"texture": block}),
            Template(TemplateType.BLOCK_MODEL, "slab_top", base + "_slab_top", {"texture": block}),
            Template(TemplateType.RECIPE, "slab", base + "_slab", {"input": block, "output": base + "_slab"}),
        ]
        if stone:
            templates.append(Template(TemplateType.RECIPE, "stonecutting", base + "_slab_stonecutting", {"input": block, "output": base + "_slab", "count": 2}))
        super().__init__(
            templates,
            [
                SimpleDrop(base + "_slab"),
                ItemBlockModel(base + "_slab"),
            ]
        )


class Wall(Preset):
    def __init__(self, base: str, block: str = None):
        if block is None:
            block = base
        super().__init__(
            [
                Template(TemplateType.BLOCKSTATE, "wall", base + "_wall", {"base": base}),
                Template(TemplateType.BLOCK_MODEL, "wall_post", base + "_wall_post", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "wall_side", base + "_wall_side", {"texture": block}),
                Template(TemplateType.BLOCK_MODEL, "wall_side_tall", base + "_wall_side_tall", {"texture": block}),
                Template(TemplateType.ITEM_MODEL, "wall", base + "_wall", {"texture": block}),
                Template(TemplateType.RECIPE, "packed_3x2", base + "_wall", {"input": block, "output": base + "_wall", "count": 6}),
                Template(TemplateType.RECIPE, "stonecutting", base + "_wall_stonecutting", {"input": block, "output": base + "_wall", "count": 1}),
            ],
            [
                SimpleDrop(base + "_wall")
            ]
        )
