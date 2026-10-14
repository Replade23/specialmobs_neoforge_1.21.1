package fathertoast.specialmobs.common.entity.drowned;

import fathertoast.specialmobs.crust.api.config.common.ConfigManager;
import fathertoast.specialmobs.common.bestiary.BestiaryInfo;
import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.bestiary.SpecialMob;
import fathertoast.specialmobs.common.config.Config;
import fathertoast.specialmobs.common.config.species.DrownedSpeciesConfig;
import fathertoast.specialmobs.common.config.species.SpeciesConfig;
import fathertoast.specialmobs.common.entity.ai.IAngler;
import fathertoast.specialmobs.common.entity.ai.goal.AnglerGoal;
import fathertoast.specialmobs.common.util.References;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

@SpecialMob
public class FishingDrownedEntity extends _SpecialDrownedEntity implements IAngler {
    
    //--------------- Static Special Mob Hooks ----------------
    
    @SpecialMob.SpeciesReference
    public static MobFamily.Species<FishingDrownedEntity> SPECIES;
    
    @SpecialMob.BestiaryInfoSupplier
    public static void getBestiaryInfo( BestiaryInfo.Builder bestiaryInfo ) {
        bestiaryInfo.color( 0x2D41F4 )
                .modBaseTexture( "textures/entity/drowned/base_drowned.png" ).modOverlayTexture( "textures/entity/drowned/drowned_overlay.png" )
                .weight( BestiaryInfo.DefaultWeight.LOW )
                .addExperience( 2 ).drownImmune().fluidPushImmune()
                .convertRangedAttackToFishing( 1.0, 40, 15.0 )
                .multiplyAttribute( Attributes.MOVEMENT_SPEED, 0.8 );
    }
    
    @SpecialMob.ConfigSupplier
    public static SpeciesConfig createConfig( ConfigManager manager, MobFamily.Species<?> species ) {
        return new DrownedSpeciesConfig( manager, species, 0.0, 0.0 );
    }
    
    @SpecialMob.LanguageProvider
    public static String[] getTranslations( String langKey ) {
        return References.translations( langKey, "Drowned Fisher",
                "", "", "", "", "", "" );//TODO
    }
    
    
    @SpecialMob.Factory
    public static EntityType.EntityFactory<FishingDrownedEntity> getVariantFactory() { return FishingDrownedEntity::new; }
    
    /** @return This entity's mob species. */
    @SpecialMob.SpeciesSupplier
    @Override
    public MobFamily.Species<? extends FishingDrownedEntity> getSpecies() { return SPECIES; }
    
    
    //--------------- Variant-Specific Implementations ----------------
    
    public FishingDrownedEntity( EntityType<? extends _SpecialDrownedEntity> entityType, Level level ) { super( entityType, level ); }
    
    /** Override to change this entity's AI goals. */
    @Override
    protected void registerVariantGoals() {
        // Don't bother removing the trident attack goal, too much effort
        goalSelector.addGoal( 2, new AnglerGoal<>( this ) );
    }
    
    /** Override to change starting equipment or stats. */
    @Override
    public void finalizeVariantSpawn( ServerLevelAccessor level, DifficultyInstance difficulty, @Nullable MobSpawnType spawnType,
                                      @Nullable SpawnGroupData groupData ) {
        setItemSlot( EquipmentSlot.MAINHAND, new ItemStack( Items.FISHING_ROD ) );
        if( getItemBySlot( EquipmentSlot.FEET ).isEmpty() ) {
            ItemStack booties = new ItemStack( Items.LEATHER_BOOTS );
            booties.set( DataComponents.DYED_COLOR, new DyedItemColor( 0xFFFF00, false ) );
            setItemSlot( EquipmentSlot.FEET, booties );
        }
        setCanPickUpLoot( false );
    }
    
    
    //--------------- IAngler Implementations ----------------
    
    /** The parameter for baby status. */
    private static final EntityDataAccessor<Boolean> IS_LINE_OUT = SynchedEntityData.defineId( FishingDrownedEntity.class, EntityDataSerializers.BOOLEAN );
    
    /** Called from the Entity.class constructor to define data watcher variables. */
    @Override
    protected void defineSynchedData( SynchedEntityData.Builder builder ) {
        super.defineSynchedData( builder );
        builder.define( IS_LINE_OUT, false );
    }
    
    /** Sets this angler's line as out (or in). */
    @Override
    public void setLineOut( boolean value ) { getEntityData().set( IS_LINE_OUT, value ); }
    
    /** @return Whether this angler's line is out. */
    @Override
    public boolean isLineOut() { return getEntityData().get( IS_LINE_OUT ); }
    
    /** @return The item equipped in a particular slot. */
    @Override
    public ItemStack getItemBySlot( EquipmentSlot slot ) {
        // Display a stick in place of the "cast fishing rod" when the fancy render is disabled
        if( level().isClientSide() && !Config.MAIN.GENERAL.fancyFishingMobs.get() && EquipmentSlot.MAINHAND.equals( slot ) ) {
            final ItemStack held = super.getItemBySlot( slot );
            if( held.getItem() == Items.FISHING_ROD && isLineOut() ) {
                return new ItemStack( Items.STICK );
            }
            return held;
        }
        return super.getItemBySlot( slot );
    }
}