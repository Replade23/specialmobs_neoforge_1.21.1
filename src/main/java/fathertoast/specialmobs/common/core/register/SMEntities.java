package fathertoast.specialmobs.common.core.register;

import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.util.AnnotationHelper;
import fathertoast.specialmobs.crust.api.config.common.value.ConfigDrivenAttributeSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import fathertoast.specialmobs.common.entity.projectile.SpecialFishingBobberEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The entity types added by this mod.
 * <p>
 * Note that the vast majority of entity types are registered by and stored in the bestiary through the
 * {@link MobFamily.Species}; a specific species is typically accessed through its entity class's static
 * field annotated with {@link fathertoast.specialmobs.common.bestiary.SpecialMob.SpeciesReference}.
 */
public class SMEntities {
    
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create( BuiltInRegistries.ENTITY_TYPE, SpecialMobs.MOD_ID );
    
    public static final DeferredHolder<EntityType<?>, EntityType<SpecialFishingBobberEntity>> FISHING_BOBBER = register( "fishing_bobber",
            EntityType.Builder.<SpecialFishingBobberEntity>of( SpecialFishingBobberEntity::new, MobCategory.MISC ).noSave().noSummon()
                    .sized( 0.25F, 0.25F ).clientTrackingRange( 4 ).updateInterval( 5 ) );
    
    public static final DeferredHolder<EntityType<?>, EntityType<fathertoast.specialmobs.common.entity.projectile.BoneShrapnelEntity>> BONE_SHRAPNEL = register( "bone_shrapnel",
            EntityType.Builder.<fathertoast.specialmobs.common.entity.projectile.BoneShrapnelEntity>of( fathertoast.specialmobs.common.entity.projectile.BoneShrapnelEntity::new, MobCategory.MISC )
                    .sized( 0.5F, 0.5F ).clientTrackingRange( 4 ).updateInterval( 20 ) );
    
    public static final DeferredHolder<EntityType<?>, EntityType<fathertoast.specialmobs.common.entity.misc.MobBoat>> MOB_BOAT = register( "mob_boat",
            EntityType.Builder.<fathertoast.specialmobs.common.entity.misc.MobBoat>of( fathertoast.specialmobs.common.entity.misc.MobBoat::new, MobCategory.MISC )
                    .sized( 1.375F, 0.5625F ).eyeHeight( 0.5625F ).clientTrackingRange( 10 ).noSummon() );
    
    public static final DeferredHolder<EntityType<?>, EntityType<fathertoast.specialmobs.common.entity.projectile.BugSpitEntity>> BUG_SPIT = register( "bug_spit",
            EntityType.Builder.<fathertoast.specialmobs.common.entity.projectile.BugSpitEntity>of( fathertoast.specialmobs.common.entity.projectile.BugSpitEntity::new, MobCategory.MISC )
                    .sized( 0.25F, 0.25F ).clientTrackingRange( 4 ).updateInterval( 10 ) );
    
    public static final DeferredHolder<EntityType<?>, EntityType<fathertoast.specialmobs.common.entity.projectile.IncorporealFireballEntity>> INCORPOREAL_FIREBALL = register( "incorporeal_fireball",
            EntityType.Builder.<fathertoast.specialmobs.common.entity.projectile.IncorporealFireballEntity>of( fathertoast.specialmobs.common.entity.projectile.IncorporealFireballEntity::new, MobCategory.MISC )
                    .sized( 1.0F, 1.0F ).clientTrackingRange( 4 ).updateInterval( 2 ) );
    
    public static final DeferredHolder<EntityType<?>, EntityType<fathertoast.specialmobs.common.entity.projectile.SlabFireballEntity>> SLAB_FIREBALL = register( "slab_fireball",
            EntityType.Builder.<fathertoast.specialmobs.common.entity.projectile.SlabFireballEntity>of( fathertoast.specialmobs.common.entity.projectile.SlabFireballEntity::new, MobCategory.MISC )
                    .sized( 1.0F, 1.0F ).clientTrackingRange( 4 ).updateInterval( 2 ) );
    
    // TODO: (nothing left) was ported together with their mob families
    
    /** Registers an entity type to the deferred register. */
    public static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register( String name, EntityType.Builder<T> builder ) {
        return REGISTRY.register( name, () -> builder.build( name ) );
    }
    
    /** Sets the default attributes for entity types, such as max health, attack damage etc. */
    public static void createAttributes( EntityAttributeCreationEvent event ) {
        // Bestiary-generated entities
        for( MobFamily.Species<?> species : MobFamily.getAllSpecies() ) {
            event.put( species.entityType.get(), new ConfigDrivenAttributeSupplier(
                    species.config.GENERAL.attributeChanges, AnnotationHelper.createAttributes( species ) ) );
        }
    }
}
