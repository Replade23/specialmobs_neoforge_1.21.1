package fathertoast.specialmobs.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fathertoast.specialmobs.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Makes arrows treat every special enderman like a vanilla enderman (no ignition, no "stuck in target" logic).
 * <p>
 * Differences from 1.20.1: the original modified the local variable 'flag' by its slot index, which breaks as soon as the
 * method's locals shift. Vanilla computes that flag as {@code entity.getType() == EntityType.ENDERMAN}, so here we hook
 * the single {@code getType()} call instead and report special endermen as plain endermen.
 */
@Mixin( AbstractArrow.class )
public abstract class AbstractArrowMixin {
    
    @ModifyExpressionValue(
            method = "onHitEntity",
            at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getType()Lnet/minecraft/world/entity/EntityType;" )
    )
    private EntityType<?> specialmobs$treatSpecialEndermenAsEndermen( EntityType<?> original ) {
        return CommonMixinHooks.asVanillaEnderman( original );
    }
}
