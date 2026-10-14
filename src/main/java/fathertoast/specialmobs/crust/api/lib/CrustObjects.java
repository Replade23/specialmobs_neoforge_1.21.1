package fathertoast.specialmobs.crust.api.lib;

import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.crust.common.potion.WeightEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * References to the registry objects that used to be provided by the Crust library
 * (Special Mobs only needs two mob effects from it).
 * <p>
 * Port note: in 1.21 {@code RegistryObject} is replaced by {@link DeferredHolder}, and mob effects
 * are always handled as {@link net.minecraft.core.Holder}s.
 */
public final class CrustObjects {
    private static final DeferredRegister<MobEffect> EFFECT_REGISTRY = DeferredRegister.create( Registries.MOB_EFFECT, SpecialMobs.MOD_ID );
    
    // These MUST be fields of this class (not of a nested interface): CrustObjects.register() runs in the mod
    // constructor, which initializes this class and so registers the entries in time. Fields of a nested interface
    // are only initialized on first access, which can be long after the registry event has fired (crash!).
    private static final DeferredHolder<MobEffect, MobEffect> VULNERABILITY_EFFECT = EFFECT_REGISTRY.register( "vulnerability",
            () -> new SimpleEffect( MobEffectCategory.HARMFUL, 0x96848D ) );
    private static final DeferredHolder<MobEffect, MobEffect> WEIGHT_EFFECT = EFFECT_REGISTRY.register( "weight",
            () -> new WeightEffect( MobEffectCategory.HARMFUL, 0x3E3E3E ) );
    
    /** The mob effects provided by Crust (aliases of the fields above, safe to touch at any time). */
    public interface Effects {
        DeferredHolder<MobEffect, MobEffect> VULNERABILITY = VULNERABILITY_EFFECT;
        DeferredHolder<MobEffect, MobEffect> WEIGHT = WEIGHT_EFFECT;
    }
    
    /** Registers the objects. Call from the mod constructor. */
    public static void register( IEventBus modBus ) { EFFECT_REGISTRY.register( modBus ); }
    
    /** A mob effect that does nothing by itself; its behavior is implemented by event handlers. */
    private static class SimpleEffect extends MobEffect {
        SimpleEffect( MobEffectCategory category, int color ) { super( category, color ); }
    }
    
    // Utility class
    private CrustObjects() { }
}
