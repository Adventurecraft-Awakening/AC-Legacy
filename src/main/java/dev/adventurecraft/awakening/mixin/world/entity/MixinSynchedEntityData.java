package dev.adventurecraft.awakening.mixin.world.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.world.entity.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SynchedEntityData.class)
public abstract class MixinSynchedEntityData {

    @Shadow
    private final Map<Integer, SynchedEntityData.DataItem> itemsById = new Int2ObjectArrayMap<>();

    private byte dataFlag;

    @Inject(method = "define", cancellable = true)
    private void redirectDataFlag(int id, Object data, CallbackInfo ci) {
        if (id == 0) {
            ci.cancel();
        }
    }
}
