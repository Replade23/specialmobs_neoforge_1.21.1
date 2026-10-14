package fathertoast.specialmobs.common.mixin;

import fathertoast.specialmobs.common.util.mixin_hooks.CommonMixinHooks;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.monster.Blaze;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Lets the Cinder blaze emit small smoke instead of the large smoke of other blazes. */
@Mixin( Blaze.class )
public abstract class BlazeEntityMixin {
    
    @ModifyArg(
            method = "aiStep",
            at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V" ),
            index = 0
    )
    private ParticleOptions specialmobs$smokeType( ParticleOptions original ) {
        return CommonMixinHooks.getBlazeSmoke( (Blaze) (Object) this, original );
    }
}
