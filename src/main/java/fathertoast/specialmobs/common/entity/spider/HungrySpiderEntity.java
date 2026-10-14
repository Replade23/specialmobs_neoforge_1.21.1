package fathertoast.specialmobs.common.entity.spider;

import fathertoast.specialmobs.crust.api.lib.NBTHelper;
import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.entity.MobHelper;
import fathertoast.specialmobs.common.util.References;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

import java.util.UUID;

@SpecialMob
public class HungrySpiderEntity extends _SpecialSpiderEntity {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<HungrySpiderEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static void getBestiaryInfo( BestiaryInfo.Builder bestiaryInfo ) {
        bestiaryInfo.color( 0x799C65 ).theme( BestiaryInfo.Theme.MOUNTAIN )
                .uniqueTextureWithEyes()
                .size( 1.5F, 1.9F, 1.3F )
                .addExperience( 2 ).regen( 40 ).disableRangedAttack()
                .addToAttribute( Attributes.MAX_HEALTH, 4.0 )
                .addToAttribute( Attributes.ATTACK_DAMAGE, -1.0 );
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Hungry Spider",
                "", "", "", "", "", "" );//TODO
    }
    
    
    @SpecialMob.Factory
    public static EntityType.EntityFactory<HungrySpiderEntity> getVariantFactory() { return HungrySpiderEntity::new; }
    
    /** @return This entity's mob species. */
    @SpecialMob.SpeciesSupplier
    @Override
    public MobFamily.Species<? extends HungrySpiderEntity> getSpecies() { return SPECIES; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    /** The damage boost to apply from growth level. */
    private static final AttributeModifier DAMAGE_BOOST = new AttributeModifier( SpecialMobs.rl( "feeding_damage_boost" ), 1.0, AttributeModifier.Operation.ADD_VALUE );
    /** The health boost to apply from max health stacks. */
    private static final AttributeModifier HEALTH_BOOST = new AttributeModifier( SpecialMobs.rl( "feeding_health_boost" ), 2.0, AttributeModifier.Operation.ADD_VALUE );
    
    /** The level of increased attack damage gained. */
    private int growthLevel;
    /** The level of increased max health gained. */
    private int maxHealthStacks;
    
    public HungrySpiderEntity( EntityType<? extends _SpecialSpiderEntity> entityType, Level level ) { super( entityType, level ); }
    
    /** Override to apply effects when this entity hits a target with a melee attack. */
    @Override
    protected void onVariantAttack( LivingEntity target ) {
        if( level().isClientSide() ) return;
        
        if( target instanceof Player player && EventHooks.canEntityGrief( level(), this ) ) {
            final ItemStack food = MobHelper.stealRandomFood( player );
            if( !food.isEmpty() ) {
                final float previousHealth = getMaxHealth();
                if( maxHealthStacks < 32 ) maxHealthStacks++;
                if( growthLevel < 7 ) growthLevel++;
                updateFeedingLevels();
                setHealth( getHealth() + getMaxHealth() - previousHealth );
                
                final FoodProperties foodStats = food.getItem().getFoodProperties( food, this );
                heal( Math.max( foodStats == null ? 0.0F : foodStats.nutrition(), 1.0F ) );
                playSound( SoundEvents.PLAYER_BURP, 0.5F, random.nextFloat() * 0.1F + 0.9F );
            }
        }
        else {
            MobHelper.stealLife( this, target, 2.0F );
        }
    }
    
    /** Recalculates the modifiers associated with this entity's feeding level counters. */
    private void updateFeedingLevels() {
        if( !level().isClientSide ) {
            final AttributeInstance health = getAttribute( Attributes.MAX_HEALTH );
            final AttributeInstance damage = getAttribute( Attributes.ATTACK_DAMAGE );
            //noinspection ConstantConditions
            health.removeModifier( HEALTH_BOOST.id() );
            //noinspection ConstantConditions
            damage.removeModifier( DAMAGE_BOOST.id() );
            if( maxHealthStacks > 0 ) {
                // Health, in particular, must be permanent to avoid health getting throttled on reload
                health.addPermanentModifier( new AttributeModifier( HEALTH_BOOST.id(),
                        HEALTH_BOOST.amount() * maxHealthStacks, HEALTH_BOOST.operation() ) );
            }
            if( growthLevel > 0 ) {
                damage.addPermanentModifier( new AttributeModifier( DAMAGE_BOOST.id(),
                        DAMAGE_BOOST.amount() * growthLevel, DAMAGE_BOOST.operation() ) );
            }
        }
    }
    
    /** Override to save data to this entity's NBT data. */
    @Override
    public void addVariantSaveData( CompoundTag saveTag ) {
        saveTag.putByte( References.TAG_HEALTH_STACKS, (byte) maxHealthStacks );
        saveTag.putByte( References.TAG_GROWTH_LEVEL, (byte) growthLevel );
    }
    
    /** Override to load data from this entity's NBT data. */
    @Override
    public void readVariantSaveData( CompoundTag saveTag ) {
        if( NBTHelper.containsNumber( saveTag, References.TAG_HEALTH_STACKS ) )
            maxHealthStacks = saveTag.getByte( References.TAG_HEALTH_STACKS );
        if( NBTHelper.containsNumber( saveTag, References.TAG_GROWTH_LEVEL ) )
            growthLevel = saveTag.getByte( References.TAG_GROWTH_LEVEL );
        updateFeedingLevels();
    }
}