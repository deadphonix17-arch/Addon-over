package com.example.buffedgear;

import com.example.buffedgear.item.BuffedGear;
import net.fabricmc.api.ModInitializer;

public class BuffedGearMod implements ModInitializer {
    @Override
    public void onInitialize() {
        BuffedGear.register();
    }
}
