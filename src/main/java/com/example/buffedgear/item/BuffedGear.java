package com.example.buffedgear.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Buffed versions of vanilla diamond and netherite gear with buffed stats.
 * Registered as overgeared_buffed_gear:diamond_* and overgeared:netherite_*.
 *
 * Tune the two multipliers below. Everything (durability, mining speed,
 * attack damage, armor, toughness) is derived from the vanilla numbers.
 * NOTE: armor points are integers in 1.20.1, so they are rounded.
 */
public final class BuffedGear {
    public static final String MOD_ID = "overgeared_buffed_gear";

    public static final double DIAMOND_BUFF = 1.6;
    public static final double NETHERITE_BUFF = 1.8;

    /** vanilla item -> replacement, used by the loot table mixin. */
    private static final Map<Item, Item> REPLACEMENTS = new HashMap<>();
    private static final List<Item> COMBAT_TAB = new ArrayList<>();
    private static final List<Item> TOOLS_TAB = new ArrayList<>();

    private BuffedGear() {}

    // ------------------------------------------------------------------ materials

    public enum Armor implements ArmorMaterial {
        // name must be the vanilla material name so the vanilla armor textures are reused
        DIAMOND("diamond", 33, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, DIAMOND_BUFF,
                () -> Ingredient.of(Items.DIAMOND)),
        NETHERITE("netherite", 37, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, NETHERITE_BUFF,
                () -> Ingredient.of(Items.NETHERITE_INGOT));

        private final String name;
        private final int durabilityMultiplier;
        private final int enchantmentValue;
        private final SoundEvent equipSound;
        private final float toughness;
        private final float knockbackResistance;
        private final double buff;
        private final Supplier<Ingredient> repair;

        Armor(String name, int durabilityMultiplier, int enchantmentValue, SoundEvent equipSound,
              float toughness, float knockbackResistance, double buff, Supplier<Ingredient> repair) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.enchantmentValue = enchantmentValue;
            this.equipSound = equipSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.buff = buff;
            this.repair = repair;
        }

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            int base = switch (type) {
                case HELMET -> 11;
                case CHESTPLATE -> 16;
                case LEGGINGS -> 15;
                case BOOTS -> 13;
            };
            return (int) Math.round(base * durabilityMultiplier * buff);
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            int base = switch (type) {
                case HELMET -> 3;
                case CHESTPLATE -> 8;
                case LEGGINGS -> 6;
                case BOOTS -> 3;
            };
            return (int) Math.round(base * buff);
        }

        @Override public int getEnchantmentValue() { return enchantmentValue; }
        @Override public SoundEvent getEquipSound() { return equipSound; }
        @Override public Ingredient getRepairIngredient() { return repair.get(); }
        @Override public String getName() { return name; }
        @Override public float getToughness() { return (float) (toughness * buff); }
        @Override public float getKnockbackResistance() { return knockbackResistance; }
    }

    public enum Tools implements Tier {
        DIAMOND(3, 1561, 8.0F, 3.0F, 10, DIAMOND_BUFF, () -> Ingredient.of(Items.DIAMOND)),
        NETHERITE(4, 2031, 9.0F, 4.0F, 15, NETHERITE_BUFF, () -> Ingredient.of(Items.NETHERITE_INGOT));

        private final int level;
        private final int uses;
        private final float speed;
        private final float damage;
        private final int enchantmentValue;
        private final double buff;
        private final Supplier<Ingredient> repair;

        Tools(int level, int uses, float speed, float damage, int enchantmentValue, double buff,
              Supplier<Ingredient> repair) {
            this.level = level;
            this.uses = uses;
            this.speed = speed;
            this.damage = damage;
            this.enchantmentValue = enchantmentValue;
            this.buff = buff;
            this.repair = repair;
        }

        @Override public int getUses() { return (int) Math.round(uses * buff); }
        @Override public float getSpeed() { return (float) (speed * buff); }
        @Override public float getAttackDamageBonus() { return (float) (damage * buff); }
        @Override public int getLevel() { return level; }
        @Override public int getEnchantmentValue() { return enchantmentValue; }
        @Override public Ingredient getRepairIngredient() { return repair.get(); }
    }

    // ------------------------------------------------------------------ registration

    public static void register() {
        set("diamond", Tools.DIAMOND, Armor.DIAMOND, DIAMOND_BUFF, Item.Properties::new,
                Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL,
                Items.DIAMOND_HOE, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE,
                Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);

        set("netherite", Tools.NETHERITE, Armor.NETHERITE, NETHERITE_BUFF, () -> new Item.Properties().fireResistant(),
                Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL,
                Items.NETHERITE_HOE, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
                .register(entries -> COMBAT_TAB.forEach(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> TOOLS_TAB.forEach(entries::accept));
    }

    private static void set(String p, Tools t, Armor a, double buff, Supplier<Item.Properties> props,
                            Item vSword, Item vPick, Item vAxe, Item vShovel, Item vHoe,
                            Item vHelm, Item vChest, Item vLegs, Item vBoots) {
        // attack damage params are scaled by the same buff as the material bonus
        Item sword = reg(p + "_sword", new SwordItem(t, (int) Math.round(3 * buff), -2.4F, props.get()), vSword);
        Item pick = reg(p + "_pickaxe", new PickaxeItem(t, (int) Math.round(1 * buff), -2.8F, props.get()), vPick);
        Item axe = reg(p + "_axe", new AxeItem(t, (float) (5.0 * buff), -3.0F, props.get()), vAxe);
        Item shovel = reg(p + "_shovel", new ShovelItem(t, (float) (1.5 * buff), -3.0F, props.get()), vShovel);
        // hoes keep roughly vanilla (near zero) attack damage
        Item hoe = reg(p + "_hoe", new HoeItem(t, -(int) Math.floor(t.getAttackDamageBonus()), 0.0F, props.get()), vHoe);
        Item helm = reg(p + "_helmet", new ArmorItem(a, ArmorItem.Type.HELMET, props.get()), vHelm);
        Item chest = reg(p + "_chestplate", new ArmorItem(a, ArmorItem.Type.CHESTPLATE, props.get()), vChest);
        Item legs = reg(p + "_leggings", new ArmorItem(a, ArmorItem.Type.LEGGINGS, props.get()), vLegs);
        Item boots = reg(p + "_boots", new ArmorItem(a, ArmorItem.Type.BOOTS, props.get()), vBoots);

        COMBAT_TAB.addAll(List.of(sword, axe, helm, chest, legs, boots));
        TOOLS_TAB.addAll(List.of(pick, axe, shovel, hoe));
    }

    private static Item reg(String name, Item item, Item vanilla) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, name), item);
        REPLACEMENTS.put(vanilla, item);
        return item;
    }

    // ------------------------------------------------------------------ loot replacement

    /** Returns the Overgeared version of a vanilla diamond/netherite stack, keeping count and NBT. */
    public static ItemStack swap(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        Item to = REPLACEMENTS.get(stack.getItem());
        if (to == null) return stack;
        ItemStack out = new ItemStack(to, stack.getCount());
        if (stack.hasTag()) out.setTag(stack.getTag().copy());
        return out;
    }
}
