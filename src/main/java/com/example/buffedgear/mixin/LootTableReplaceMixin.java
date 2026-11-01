package com.example.buffedgear.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import com.example.buffedgear.item.BuffedGear;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Consumer;

/**
 * Every loot table path (chests, mob drops, fishing, archaeology...) ends in
 * getRandomItemsRaw. Wrapping its output consumer swaps vanilla diamond/netherite
 * gear for the buffed versions before loot functions (enchanting etc.) run.
 */
@Mixin(LootTable.class)
public abstract class LootTableReplaceMixin {
    @ModifyVariable(
            method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"), argsOnly = true)
    private Consumer<ItemStack> overgeared$swapGear(Consumer<ItemStack> out) {
        return stack -> out.accept(BuffedGear.swap(stack));
    }
}
