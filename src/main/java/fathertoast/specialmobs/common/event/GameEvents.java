package fathertoast.specialmobs.common.event;

import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.entity.MobHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class GameEvents {
    
    /**
     * Check if thrown tridents have a potion effect, and apply it if so.
     * <p>
     * Differences from 1.20.1: LivingHurtEvent no longer exists; the post-damage event fires only if the hit landed.
     */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public void onLivingDamagePost( LivingDamageEvent.Post event ) {
        if( event.getSource().getDirectEntity() instanceof ThrownTrident thrownTrident ) {
            final ItemStack tridentStack = thrownTrident.getPickupItemStackOrigin();
            final MobEffectInstance effectInstance = MobHelper.getTridentEffect( tridentStack );
            
            if( effectInstance != null ) {
                event.getEntity().addEffect( effectInstance );
            }
        }
    }
    
    /**
     * Make the relevant vanilla replacements have the same visibility
     * multipliers that their true vanilla counterpart gets from equipped heads/skulls.
     */
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public void onLivingVisibility( LivingEvent.LivingVisibilityEvent event ) {
        if( event.getLookingEntity() == null ) return;
        
        final ItemStack headStack = event.getEntity().getItemBySlot( EquipmentSlot.HEAD );
        if( headStack.isEmpty() ) return; // by far the most common case; skip the type lookups
        
        final EntityType<?> lookingEntity = event.getLookingEntity().getType();
        
        if( lookingEntity == MobFamily.CREEPER.vanillaReplacement.entityType.get() && headStack.is( Items.CREEPER_HEAD )
                || lookingEntity == MobFamily.ZOMBIE.vanillaReplacement.entityType.get() && headStack.is( Items.ZOMBIE_HEAD )
                || lookingEntity == MobFamily.SKELETON.vanillaReplacement.entityType.get() && headStack.is( Items.SKELETON_SKULL ) ) {
            
            event.modifyVisibility( 0.5D );
        }
    }
}
