package fathertoast.specialmobs.common.entity.blaze;

import fathertoast.specialmobs.crust.api.lib.NBTHelper;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.core.register.SMTags;
import fathertoast.specialmobs.common.entity.MobHelper;
import fathertoast.specialmobs.common.util.References;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import java.util.UUID;

@SpecialMob
public class ConflagrationBlazeEntity extends _SpecialBlazeEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<ConflagrationBlazeEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static void getBestiaryInfo( BestiaryInfo.Builder bestiaryInfo ) {
        bestiaryInfo.color( 0xFFF87E )
                .weight( BestiaryInfo.DefaultWeight.LOW )
                .uniqueTextureBaseOnly()
                .size( 1.5F, 0.9F, 2.7F )
                .addExperience( 4 ).regen( 20 );
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Conflagration",
                "", "", "", "", "", "" );//TODO
    }
    
    
    @SpecialMob.Factory
    public static EntityType.EntityFactory<ConflagrationBlazeEntity> getVariantFactory() { return ConflagrationBlazeEntity::new; }
    
    /** @return This entity's mob species. */
    @SpecialMob.SpeciesSupplier
    @Override
    public MobFamily.Species<? extends ConflagrationBlazeEntity> getSpecies() { return SPECIES; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    /** The damage boost to apply from growth level. */
    private static final AttributeModifier DAMAGE_BOOST = new AttributeModifier( SpecialMobs.rl( "feeding_damage_boost" ), 1.0, AttributeModifier.Operation.ADD_VALUE );
    
    /** The level of increased attack damage gained. */
    private int growthLevel;
    
    public ConflagrationBlazeEntity( EntityType<? extends _SpecialBlazeEntity> entityType, Level level ) { super( entityType, level ); }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    @Override
    protected void onVariantAttack( LivingEntity target ) {
        MobHelper.stealLife( this, target, 2.0F );
    }
    
    /** @return Attempts to damage this entity; returns true if the hit was successful. */
    @Override
    public boolean hurt( DamageSource source, float amount ) {
        if( isInvulnerableTo( source ) || fireImmune() && source.is( DamageTypeTags.IS_FIRE ) ) return false;
        
        if( !source.is( DamageTypeTags.IS_EXPLOSION ) && !source.is( SMTags.DamageTypes.IS_MAGIC ) && !source.is( DamageTypeTags.IS_DROWNING ) &&
                !(source.getDirectEntity() instanceof Snowball) ) {
            
            if( !level().isClientSide() && growthLevel < 7 ) {
                growthLevel++;
                updateFeedingLevels();
            }
            amount /= 2.0F;
        }
        return super.hurt( source, amount );
    }
    
    /** Recalculates the modifiers associated with this entity's feeding level counters. */
    private void updateFeedingLevels() {
        if( !level().isClientSide ) {
            final int cooldownReduction = Mth.floor( getConfig().GENERAL.rangedAttackCooldown.get() * growthLevel * 0.1 );
            getSpecialData().setRangedAttackCooldown( getConfig().GENERAL.rangedAttackCooldown.get() - cooldownReduction );
            getSpecialData().setRangedAttackMaxCooldown( getConfig().GENERAL.rangedAttackMaxCooldown.get() - cooldownReduction );
            
            fireballBurstCount = getConfig().BLAZES.fireballBurstCount.get();
            if( growthLevel >= 3 ) fireballBurstCount++;
            if( growthLevel >= 7 ) fireballBurstCount++;
            
            final AttributeInstance damage = getAttribute( Attributes.ATTACK_DAMAGE );
            //noinspection ConstantConditions
            damage.removeModifier( DAMAGE_BOOST.id() );
            if( growthLevel > 0 ) {
                damage.addPermanentModifier( new AttributeModifier( DAMAGE_BOOST.id(),
                        DAMAGE_BOOST.amount() * growthLevel, DAMAGE_BOOST.operation() ) );
            }
        }
    }
    
    /** Override to save data to this entity's NBT data. */
    @Override
    public void addVariantSaveData( CompoundTag saveTag ) {
        saveTag.putByte( References.TAG_GROWTH_LEVEL, (byte) growthLevel );
    }
    
    /** Override to load data from this entity's NBT data. */
    @Override
    public void readVariantSaveData( CompoundTag saveTag ) {
        if( NBTHelper.containsNumber( saveTag, References.TAG_GROWTH_LEVEL ) )
            growthLevel = saveTag.getByte( References.TAG_GROWTH_LEVEL );
        updateFeedingLevels();
    }
}