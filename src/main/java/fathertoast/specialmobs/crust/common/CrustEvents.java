package fathertoast.specialmobs.crust.common;

import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.crust.api.lib.CrustObjects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Event handlers for the mob effects that used to come from Crust.
 * <p>
 * Port notes (1.21): {@code LivingHurtEvent} is now {@code LivingIncomingDamageEvent}; effects are accessed by Holder.
 */
@EventBusSubscriber( modid = SpecialMobs.MOD_ID )
public final class CrustEvents {
    
    /** Vulnerability: take 25% more damage per effect level (vs. Damage Resistance's 20% less per level). */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onIncomingDamage( LivingIncomingDamageEvent event ) {
        final LivingEntity entity = event.getEntity();
        if( event.getSource().is( DamageTypeTags.BYPASSES_INVULNERABILITY ) ||
                event.getSource().is( DamageTypeTags.BYPASSES_ENCHANTMENTS ) ) return;
        
        final MobEffectInstance vulnerability = entity.getEffect( CrustObjects.Effects.VULNERABILITY );
        if( vulnerability == null ) return;
        event.setAmount( Math.max( event.getAmount() * (1.0F + 0.25F * (vulnerability.getAmplifier() + 1)), 0.0F ) );
    }
    
    /** Weight: increase effective fall distance by ~33% per effect level. */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onFall( LivingFallEvent event ) {
        final MobEffectInstance weight = event.getEntity().getEffect( CrustObjects.Effects.WEIGHT );
        if( weight == null ) return;
        event.setDamageMultiplier( event.getDamageMultiplier() * (1.0F + 0.3334F * (weight.getAmplifier() + 1)) );
    }
    
    private CrustEvents() { }
}
