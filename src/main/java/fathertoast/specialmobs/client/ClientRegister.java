package fathertoast.specialmobs.client;

import fathertoast.specialmobs.client.renderer.entity.family.SpecialCreeperRenderer;
import fathertoast.specialmobs.client.renderer.entity.model.ScopeCreeperModel;
import fathertoast.specialmobs.client.renderer.entity.species.EnderCreeperRenderer;
import fathertoast.specialmobs.client.renderer.entity.species.ImplodingCreeperRenderer;
import fathertoast.specialmobs.client.renderer.entity.species.ScopeCreeperRenderer;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.entity.creeper.EnderCreeperEntity;
import fathertoast.specialmobs.common.entity.creeper.ImplodingCreeperEntity;
import fathertoast.specialmobs.common.entity.creeper.ScopeCreeperEntity;
import fathertoast.specialmobs.client.renderer.entity.family.*;
import fathertoast.specialmobs.client.renderer.entity.projectile.SpecialFishingBobberRenderer;
import fathertoast.specialmobs.client.renderer.entity.species.*;
import fathertoast.specialmobs.client.renderer.entity.model.SlabGhastModel;
import fathertoast.specialmobs.common.entity.blaze.ArmoredBlazeEntity;
import fathertoast.specialmobs.common.entity.ghast.CorporealShiftGhastEntity;
import fathertoast.specialmobs.common.entity.ghast.SlabGhastEntity;
import fathertoast.specialmobs.common.entity.slime.PotionSlimeEntity;
import fathertoast.specialmobs.common.entity.enderman.RunicEndermanEntity;
import fathertoast.specialmobs.client.renderer.entity.projectile.BoneShrapnelRenderer;
import fathertoast.specialmobs.client.renderer.entity.projectile.BugSpitRenderer;
import fathertoast.specialmobs.client.renderer.entity.species.ShortSilverfishRenderer;
import fathertoast.specialmobs.common.entity.silverfish.PufferSilverfishEntity;
import fathertoast.specialmobs.client.renderer.entity.species.NinjaSkeletonRenderer;
import fathertoast.specialmobs.common.entity.skeleton.NinjaSkeletonEntity;
import fathertoast.specialmobs.common.entity.witherskeleton.NinjaWitherSkeletonEntity;
import net.minecraft.client.model.geom.ModelLayers;
import fathertoast.specialmobs.client.renderer.entity.species.SpecialZombieVillagerRenderer;
import fathertoast.specialmobs.common.core.register.SMEntities;
import fathertoast.specialmobs.common.entity.zombie.MadScientistZombieEntity;
import fathertoast.specialmobs.common.entity.zombifiedpiglin.VampireZombifiedPiglinEntity;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * Client-side registration (renderers and model layers).
 * <p>
 * NOTE (port): only the creeper family is registered for now; the other families are added back as they are ported.
 * Differences from 1.20.1: {@code @Mod.EventBusSubscriber} became {@code @EventBusSubscriber}, and
 * {@code ItemBlockRenderTypes.setRenderLayer} is gone (the render type now lives in the block model JSON).
 */
@EventBusSubscriber( value = Dist.CLIENT, modid = SpecialMobs.MOD_ID )
public final class ClientRegister {
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        ReadMeConfig.INSTANCE.SPEC.initialize();
    }
    
    @SubscribeEvent
    public static void registerLayerDefs( EntityRenderersEvent.RegisterLayerDefinitions event ) {
        event.registerLayerDefinition( SMModelLayers.SCOPE_CREEPER, ScopeCreeperModel::createBodyLayer );
        event.registerLayerDefinition( SMModelLayers.SLAB_GHAST, SlabGhastModel::createBodyLayer );
        event.registerLayerDefinition( SMModelLayers.CREEPER_OUTER_LAYER, () -> CreeperModel.createBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.BLAZE_OUTER_LAYER, () -> SMModelLayers.blazeBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.BLAZE_OUTER_ARMOR_LAYER, () -> SMModelLayers.blazeBodyLayer( new CubeDeformation( 1.0F ) ) );
        event.registerLayerDefinition( SMModelLayers.ENDERMAN_OUTER_LAYER, () -> SMModelLayers.endermanBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.GHAST_OUTER_LAYER, () -> SMModelLayers.ghastBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.MAGMA_CUBE_OUTER_LAYER, () -> SMModelLayers.magmaCubeBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.SLIME_OUTER_LAYER, () -> SMModelLayers.slimeOuterBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.SILVERFISH_OUTER_LAYER, () -> SMModelLayers.silverfishBodyLayer( new CubeDeformation( 0.25F ) ) );
        event.registerLayerDefinition( SMModelLayers.SPIDER_OUTER_LAYER, () -> SMModelLayers.spiderBodyLayer( new CubeDeformation( 0.25F ) ) );
        
        event.registerLayerDefinition( SMModelLayers.PIGLIN, () -> LayerDefinition.create( PiglinModel.createMesh( CubeDeformation.NONE ), 64, 64 ) );
        event.registerLayerDefinition( SMModelLayers.PIGLIN_OUTER_LAYER, () -> LayerDefinition.create( PiglinModel.createMesh( new CubeDeformation( 0.25F ) ), 64, 64 ) );
        event.registerLayerDefinition( SMModelLayers.PIGLIN_INNER_ARMOR, () -> LayerDefinition.create( HumanoidArmorModel.createBodyLayer( new CubeDeformation( 0.5F ) ), 64, 32 ) );
        event.registerLayerDefinition( SMModelLayers.PIGLIN_OUTER_ARMOR, () -> LayerDefinition.create( HumanoidArmorModel.createBodyLayer( new CubeDeformation( 1.02F ) ), 64, 32 ) );
        
        event.registerLayerDefinition( SMModelLayers.ZOMBIFIED_PIGLIN, () -> LayerDefinition.create( PiglinModel.createMesh( CubeDeformation.NONE ), 64, 64 ) );
        event.registerLayerDefinition( SMModelLayers.ZOMBIFIED_PIGLIN_OUTER_LAYER, () -> LayerDefinition.create( PiglinModel.createMesh( new CubeDeformation( 0.25F ) ), 64, 64 ) );
        event.registerLayerDefinition( SMModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, () -> LayerDefinition.create( HumanoidArmorModel.createBodyLayer( new CubeDeformation( 0.5F ) ), 64, 32 ) );
        event.registerLayerDefinition( SMModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, () -> LayerDefinition.create( HumanoidArmorModel.createBodyLayer( new CubeDeformation( 1.02F ) ), 64, 32 ) );
    }
    
    @SubscribeEvent
    public static void registerEntityRenderers( EntityRenderersEvent.RegisterRenderers event ) {
        // Family-based renderers
        registerFamilyRenderers( event, MobFamily.CREEPER, SpecialCreeperRenderer::new );
        registerFamilyRenderers( event, MobFamily.ZOMBIE, SpecialZombieRenderer::new );
        registerFamilyRenderers( event, MobFamily.DROWNED, SpecialDrownedRenderer::new );
        registerFamilyRenderers( event, MobFamily.ZOMBIFIED_PIGLIN, SpecialPiglinRenderer::newMissingRightEar );
        registerFamilyRenderers( event, MobFamily.SKELETON, ( context ) -> new SpecialSkeletonRenderer( context, ModelLayers.SKELETON_INNER_ARMOR ) );
        registerFamilyRenderers( event, MobFamily.SLIME, SpecialSlimeRenderer::new );
        registerFamilyRenderers( event, MobFamily.MAGMA_CUBE, SpecialMagmaCubeRenderer::new );
        registerFamilyRenderers( event, MobFamily.SPIDER, SpecialSpiderRenderer::new );
        registerFamilyRenderers( event, MobFamily.CAVE_SPIDER, SpecialSpiderRenderer::new );
        registerFamilyRenderers( event, MobFamily.SILVERFISH, SpecialSilverfishRenderer::new );
        registerFamilyRenderers( event, MobFamily.ENDERMAN, SpecialEndermanRenderer::new );
        registerFamilyRenderers( event, MobFamily.WITCH, SpecialWitchRenderer::new );
        registerFamilyRenderers( event, MobFamily.GHAST, SpecialGhastRenderer::new );
        registerFamilyRenderers( event, MobFamily.BLAZE, SpecialBlazeRenderer::new );
        registerFamilyRenderers( event, MobFamily.WITHER_SKELETON, ( context ) -> new SpecialSkeletonRenderer( context, ModelLayers.WITHER_SKELETON_INNER_ARMOR ) );
        
        // Species overrides
        registerSpeciesRenderer( event, EnderCreeperEntity.SPECIES, EnderCreeperRenderer::new );
        registerSpeciesRenderer( event, ImplodingCreeperEntity.SPECIES, ImplodingCreeperRenderer::new );
        registerSpeciesRenderer( event, ScopeCreeperEntity.SPECIES, ScopeCreeperRenderer::new );
        
        registerSpeciesRenderer( event, MadScientistZombieEntity.SPECIES, SpecialZombieVillagerRenderer::new );
        registerSpeciesRenderer( event, VampireZombifiedPiglinEntity.SPECIES, SpecialPiglinRenderer::newBothEars );
        registerSpeciesRenderer( event, ArmoredBlazeEntity.SPECIES, ArmoredBlazeRenderer::new );
        registerSpeciesRenderer( event, PotionSlimeEntity.SPECIES, PotionSlimeRenderer::new );
        registerSpeciesRenderer( event, PufferSilverfishEntity.SPECIES, ShortSilverfishRenderer::new );
        registerSpeciesRenderer( event, CorporealShiftGhastEntity.SPECIES, CorporealShiftGhastRenderer::new );
        registerSpeciesRenderer( event, SlabGhastEntity.SPECIES, SlabGhastRenderer::new );
        registerSpeciesRenderer( event, RunicEndermanEntity.SPECIES, RunicEndermanRenderer::new );
        registerSpeciesRenderer( event, NinjaSkeletonEntity.SPECIES, ( context ) -> new NinjaSkeletonRenderer( context, ModelLayers.SKELETON_INNER_ARMOR ) );
        registerSpeciesRenderer( event, NinjaWitherSkeletonEntity.SPECIES, ( context ) -> new NinjaSkeletonRenderer( context, ModelLayers.WITHER_SKELETON_INNER_ARMOR ) );
        
        // Other
        registerRenderer( event, SMEntities.FISHING_BOBBER, SpecialFishingBobberRenderer::new );
        event.registerEntityRenderer( SMEntities.MOB_BOAT.get(), context -> new net.minecraft.client.renderer.entity.BoatRenderer( context, false ) );
        registerRenderer( event, SMEntities.BUG_SPIT, BugSpitRenderer::new );
        registerSpriteRenderer( event, SMEntities.INCORPOREAL_FIREBALL, 3.0F, true );
        registerSpriteRenderer( event, SMEntities.SLAB_FIREBALL, 3.0F, true );
        registerRenderer( event, SMEntities.BONE_SHRAPNEL, BoneShrapnelRenderer::new );
    }
    
    private static <T extends Mob, E extends LivingEntity> void registerFamilyRenderers( EntityRenderersEvent.RegisterRenderers event, MobFamily<T, ?> family, EntityRendererProvider<E> renderFactory ) {
        //noinspection unchecked
        event.registerEntityRenderer( family.vanillaReplacement.entityType.get(), (EntityRendererProvider<T>) renderFactory );
        for( MobFamily.Species<? extends T> species : family.variants )
            registerSpeciesRenderer( event, species, renderFactory );
    }
    
    private static <T extends Entity> void registerRenderer( EntityRenderersEvent.RegisterRenderers event, DeferredHolder<EntityType<?>, EntityType<T>> entityType, EntityRendererProvider<T> renderFactory ) {
        event.registerEntityRenderer( entityType.get(), renderFactory );
    }
    
    private static <T extends Mob, E extends LivingEntity> void registerSpeciesRenderer( EntityRenderersEvent.RegisterRenderers event, MobFamily.Species<T> species, EntityRendererProvider<E> renderFactory ) {
        //noinspection unchecked
        event.registerEntityRenderer( species.entityType.get(), (EntityRendererProvider<T>) renderFactory );
    }
    
    private static <T extends Entity & net.minecraft.world.entity.projectile.ItemSupplier>
    void registerSpriteRenderer( EntityRenderersEvent.RegisterRenderers event, DeferredHolder<EntityType<?>, EntityType<T>> entityType, float scale, boolean fullBright ) {
        event.registerEntityRenderer( entityType.get(), ( context ) ->
                new net.minecraft.client.renderer.entity.ThrownItemRenderer<>( context, scale, fullBright ) );
    }
}
