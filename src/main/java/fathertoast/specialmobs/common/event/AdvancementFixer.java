package fathertoast.specialmobs.common.event;

import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.config.Config;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.core.register.SMTags;
import fathertoast.specialmobs.common.entity.ISpecialMob;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * Grants players vanilla advancements that would be impossible to get otherwise, because Special Mobs replaces the
 * vanilla mobs those advancements ask you to kill.
 * <p>
 * Differences from 1.20.1: advancements are data now and cannot be edited as they load (the old Crust load event is gone).
 * Instead of adding criteria to the advancements, we award the vanilla criterion ourselves when the matching special mob dies.
 * Nothing is replaced in the data, so datapacks that touch these advancements keep working.
 */
@EventBusSubscriber( modid = SpecialMobs.MOD_ID )
public final class AdvancementFixer {
    
    private static final ResourceLocation KILL_A_MOB = ResourceLocation.withDefaultNamespace( "adventure/kill_a_mob" );
    private static final ResourceLocation KILL_ALL_MOBS = ResourceLocation.withDefaultNamespace( "adventure/kill_all_mobs" );
    private static final ResourceLocation SNIPER_DUEL = ResourceLocation.withDefaultNamespace( "adventure/sniper_duel" );
    private static final ResourceLocation RETURN_TO_SENDER = ResourceLocation.withDefaultNamespace( "nether/return_to_sender" );
    private static final ResourceLocation UNEASY_ALLIANCE = ResourceLocation.withDefaultNamespace( "nether/uneasy_alliance" );
    
    private AdvancementFixer() { }
    
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void onLivingDeath( LivingDeathEvent event ) {
        final LivingEntity dead = event.getEntity();
        final DamageSource source = event.getSource();
        
        if( dead.level().isClientSide || !(source.getEntity() instanceof ServerPlayer player) ) return;
        if( !Config.MAIN.GENERAL.patchAdvancements.get() ) return;
        
        // "Monster Hunter" and "Monsters Hunted": a special mob counts as the vanilla mob(s) it replaces
        if( dead instanceof ISpecialMob<?> special ) {
            for( EntityType<?> type : special.getSpecies().family.replaceableTypes ) {
                final String criterion = BuiltInRegistries.ENTITY_TYPE.getKey( type ).toString();
                award( player, KILL_A_MOB, criterion );
                award( player, KILL_ALL_MOBS, criterion );
            }
        }
        
        final EntityType<?> type = dead.getType();
        
        // "Sniper Duel": shoot a skeleton from at least 50 blocks away
        if( type == MobFamily.SKELETON.vanillaReplacement.entityType.get() && source.is( DamageTypeTags.IS_PROJECTILE ) ) {
            final double dX = dead.getX() - player.getX(), dZ = dead.getZ() - player.getZ();
            if( dX * dX + dZ * dZ >= 50.0 * 50.0 ) award( player, SNIPER_DUEL, "killed_skeleton" );
        }
        // "Return to Sender": kill a ghast with a fireball (vanilla requires the fireball to be the direct cause)
        if( type == MobFamily.GHAST.vanillaReplacement.entityType.get() && source.is( DamageTypeTags.IS_PROJECTILE ) ) {
            final Entity direct = source.getDirectEntity();
            if( direct != null && direct.getType() == EntityType.FIREBALL ) award( player, RETURN_TO_SENDER, "killed_ghast" );
        }
        // "Uneasy Alliance": kill any ghast variant in the Overworld
        if( type.is( SMTags.EntityTypes.GHASTS ) && dead.level().dimension() == Level.OVERWORLD ) {
            award( player, UNEASY_ALLIANCE, "killed_ghast" );
        }
    }
    
    /** Awards one criterion, doing nothing if the advancement does not exist or is already complete. */
    private static void award( ServerPlayer player, ResourceLocation id, String criterion ) {
        final AdvancementHolder advancement = player.server.getAdvancements().get( id );
        if( advancement == null || player.getAdvancements().getOrStartProgress( advancement ).isDone() ) return;
        player.getAdvancements().award( advancement, criterion );
    }
}
