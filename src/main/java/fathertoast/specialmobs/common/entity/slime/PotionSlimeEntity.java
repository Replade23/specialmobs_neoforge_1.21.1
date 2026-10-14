package fathertoast.specialmobs.common.entity.slime;

import fathertoast.specialmobs.crust.api.config.common.ConfigManager;
import fathertoast.specialmobs.crust.api.lib.CrustMath;
import fathertoast.specialmobs.crust.api.lib.NBTHelper;
import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.config.Config;
import fathertoast.specialmobs.common.config.species.PotionSlimeSpeciesConfig;
import fathertoast.specialmobs.common.config.species.SpeciesConfig;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.entity.MobHelper;
import fathertoast.specialmobs.common.util.References;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ColorParticleOption;

import javax.annotation.Nullable;

@SpecialMob
public class PotionSlimeEntity extends _SpecialSlimeEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<PotionSlimeEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static void getBestiaryInfo( BestiaryInfo.Builder bestiaryInfo ) {
        bestiaryInfo.color( 0xA94725 ).weight( BestiaryInfo.DefaultWeight.LOW ).theme( BestiaryInfo.Theme.FOREST )
                .uniqueTextureWithOverlay()
                .addExperience( 2 )
                .addToAttribute( Attributes.MAX_HEALTH, 2.0 )
                .multiplyAttribute( Attributes.MOVEMENT_SPEED, 1.2 );
    }
    
    @SpecialMob.ConfigSupplier
    public static SpeciesConfig createConfig( ConfigManager manager, MobFamily.Species<?> species ) {
        return new PotionSlimeSpeciesConfig( manager, species );
    }
    
    /** @return This entity's species config. */
    public PotionSlimeSpeciesConfig getConfig() { return (PotionSlimeSpeciesConfig) getSpecies().config; }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Potion Slime",
                "", "", "", "", "", "" );//TODO
    }
    
    
    @SpecialMob.Factory
    public static EntityType.EntityFactory<PotionSlimeEntity> getVariantFactory() { return PotionSlimeEntity::new; }
    
    /** @return This entity's mob species. */
    @SpecialMob.SpeciesSupplier
    @Override
    public MobFamily.Species<? extends PotionSlimeEntity> getSpecies() { return SPECIES; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    /** The parameter for potion color. */
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId( PotionSlimeEntity.class, EntityDataSerializers.INT );
    /** The color of an empty potion. */
    private static final int EMPTY_POTION_COLOR = 0x385DC6;
    
    private MobEffect potionEffect;
    
    public PotionSlimeEntity( EntityType<? extends _SpecialSlimeEntity> entityType, Level level ) {
        super( entityType, level );
        setRandomPotionFill();
    }
    
    /** Called from the Entity.class constructor to define data watcher variables. */
    @Override
    protected void defineSynchedData( SynchedEntityData.Builder builder ) {
        super.defineSynchedData( builder );
        builder.define( COLOR, EMPTY_POTION_COLOR );
    }
    
    /** Sets the potion fill of this slime to a random effect based on config settings. */
    private void setRandomPotionFill() {
        MobEffect next = getConfig().POTION.potionChoices.next( random );
        if( next == null || !Config.MAIN.GENERAL.enableNausea.get() && next.equals( MobEffects.CONFUSION ) ) {
            setPotionFill( MobEffects.HARM.value() );
        }
        setPotionFill( next );
    }
    
    /** Sets the potion fill of this slime. */
    private void setPotionFill( @Nullable MobEffect effect ) {
        potionEffect = effect;
        entityData.set( COLOR, effect == null ? EMPTY_POTION_COLOR : effect.getColor() );
    }
    
    /** @return The color of the potion contained in this slime. */
    public int getPotionColor() { return entityData.get( COLOR ); }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    @Override
    protected void onVariantAttack( LivingEntity target ) {
        if( potionEffect == null ) target.clearFire();
        else MobHelper.applyEffect( target, BuiltInRegistries.MOB_EFFECT.wrapAsHolder( potionEffect ) );
    }
    
    /**
     * Called when the slime spawns particles on landing, see onUpdate.
     * Return true to prevent the spawning of the default particles.
     */
    @Override
    protected boolean spawnCustomParticles() {
        final int color = getPotionColor();
        
        final int size = getSize();
        for( int i = 0; i < size * 8; i++ ) {
            final float angle = random.nextFloat() * 2.0F * (float) Math.PI;
            final float distance = (random.nextFloat() * 0.25F + 0.25F) * size;
            level().addParticle( ColorParticleOption.create( ParticleTypes.ENTITY_EFFECT, color | 0xFF000000 ),
                    getX() + Mth.sin( angle ) * distance,
                    getY(),
                    getZ() + Mth.cos( angle ) * distance,
                    0.0, 0.0, 0.0 );
        }
        return true;
    }
    
    /** @return True if the effect can be applied to this entity. */
    @Override
    public boolean canBeAffected( MobEffectInstance effect ) {
        // Immune to debuffs
        return effect.getEffect().value().getCategory() != MobEffectCategory.HARMFUL && super.canBeAffected( effect );
    }
    
    /** Override to save data to this entity's NBT data. */
    @Override
    public void addVariantSaveData( CompoundTag saveTag ) {
        saveTag.putString( References.TAG_AMMO, SpecialMobs.toString( BuiltInRegistries.MOB_EFFECT.getKey( potionEffect ) ) );
    }
    
    /** Override to load data from this entity's NBT data. */
    @Override
    public void readVariantSaveData( CompoundTag saveTag ) {
        if( NBTHelper.containsString( saveTag, References.TAG_FUSE_TIME ) )
            setPotionFill( BuiltInRegistries.MOB_EFFECT.get( ResourceLocation.tryParse( saveTag.getString( References.TAG_AMMO ) ) ) );
    }
}