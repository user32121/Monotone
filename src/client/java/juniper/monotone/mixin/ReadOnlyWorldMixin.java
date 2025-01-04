package juniper.monotone.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import juniper.monotone.mixinInterface.ReadOnlyWorldInterface;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

@Mixin(ClientWorld.class)
public abstract class ReadOnlyWorldMixin implements ReadOnlyWorldInterface {
    private boolean enabled = false;
    private List<Pair<BlockPos, BlockState>> blockChanges = new ArrayList<>();

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public List<Pair<BlockPos, BlockState>> getBlockChanges() {
        return blockChanges;
    }

    @Override
    public void clearBlockChanges() {
        blockChanges.clear();
    }

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    private void setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> info) {
        if (enabled) {
            blockChanges.add(new Pair<BlockPos, BlockState>(pos, state));
            info.setReturnValue(true);
        }
    }

    @Inject(method = "playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/sound/SoundCategory;FFJ)V", at = @At("HEAD"), cancellable = true)
    private void playSound(@Nullable PlayerEntity source, double x, double y, double z, RegistryEntry<SoundEvent> sound,
            SoundCategory category, float volume, float pitch, long seed, CallbackInfo info) {
        if (enabled) {
            info.cancel();
        }
    }
}
