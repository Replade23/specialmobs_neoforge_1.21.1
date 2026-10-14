package fathertoast.specialmobs.common.entity;

import fathertoast.specialmobs.common.core.SpecialMobs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

/**
 * In 1.21 arrows no longer have a knockback ("punch") field; the enchantment is data-driven.
 * Special mobs mark their arrows with extra punch here and this handler applies it when the arrow damages a target.
 */
@EventBusSubscriber( modid = SpecialMobs.MOD_ID )
public final class ArrowPunch {
    private static final String TAG = "sm_punch";
    
    /** Adds extra punch levels to an arrow (what used to be setKnockback(getKnockback() + n)). */
    public static void add( AbstractArrow arrow, int levels ) {
        arrow.getPersistentData().putInt( TAG, arrow.getPersistentData().getInt( TAG ) + levels );
    }
    
    @SubscribeEvent
    public static void onDamaged( LivingDamageEvent.Post event ) {
        if( event.getSource().getDirectEntity() instanceof AbstractArrow arrow ) {
            final int levels = arrow.getPersistentData().getInt( TAG );
            if( levels <= 0 ) return;
            final LivingEntity target = event.getEntity();
            final Vec3 push = arrow.getDeltaMovement().multiply( 1.0, 0.0, 1.0 );
            if( push.lengthSqr() < 1.0E-7 ) return;
            final double resist = 1.0 - target.getAttributeValue( Attributes.KNOCKBACK_RESISTANCE );
            final Vec3 v = push.normalize().scale( levels * 0.6 * Math.max( 0.0, resist ) );
            target.push( v.x, 0.1, v.z );
            target.hurtMarked = true;
        }
    }
    
    private ArrowPunch() { }
}
