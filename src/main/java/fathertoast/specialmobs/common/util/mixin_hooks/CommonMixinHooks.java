package fathertoast.specialmobs.common.util.mixin_hooks;

import fathertoast.specialmobs.common.core.register.SMTags;
import fathertoast.specialmobs.common.entity.blaze.CinderBlazeEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Blaze;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Logic called from this mod's mixins. Kept out of the mixin classes so they stay tiny. */
public class CommonMixinHooks {
    
    /** @return The particle a blaze emits while burning; cinder blazes emit smaller smoke. */
    public static ParticleOptions getBlazeSmoke( Blaze blaze, ParticleOptions original ) {
        return blaze instanceof CinderBlazeEntity ? ParticleTypes.SMOKE : original;
    }
    
    public static void handleIsZombified( EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir ) {
        if( entityType.is( SMTags.EntityTypes.ZOMBIFIED_PIGLINS ) ) cir.setReturnValue( true );
    }
    
    /** @return {@link EntityType#ENDERMAN} for any special enderman, otherwise the type unchanged. */
    public static EntityType<?> asVanillaEnderman( EntityType<?> type ) {
        return type.is( SMTags.EntityTypes.ENDERMEN ) ? EntityType.ENDERMAN : type;
    }
}
