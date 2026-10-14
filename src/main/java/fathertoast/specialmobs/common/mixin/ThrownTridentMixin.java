package fathertoast.specialmobs.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fathertoast.specialmobs.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Tridents do not bounce off / get stuck in special endermen, just like with vanilla endermen.
 * Same approach as {@link AbstractArrowMixin}: vanilla checks {@code entity.getType() == EntityType.ENDERMAN} once.
 */
@Mixin( ThrownTrident.class )
public abstract class ThrownTridentMixin {
    
    @ModifyExpressionValue(
            method = "onHitEntity",
            at = @At( value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getType()Lnet/minecraft/world/entity/EntityType;" )
    )
    private EntityType<?> specialmobs$treatSpecialEndermenAsEndermen( EntityType<?> original ) {
        return CommonMixinHooks.asVanillaEnderman( original );
    }
}
