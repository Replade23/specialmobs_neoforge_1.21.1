package fathertoast.specialmobs.common.entity.projectile;

import fathertoast.specialmobs.common.core.register.SMEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BoneShrapnelEntity extends AbstractArrow {
    
    /** Ticks in ground remaining before de-spawning. */
    public int life = 150 + random.nextInt( 16 );
    
    public BoneShrapnelEntity( EntityType<? extends BoneShrapnelEntity> entityType, Level level ) { super( entityType, level ); }
    
    //public BoneShrapnelEntity( World world, double x, double y, double z ) { super( SMEntities.BONE_SHRAPNEL.get(), x, y, z, world ); }
    
    public BoneShrapnelEntity( LivingEntity shooter ) { super( SMEntities.BONE_SHRAPNEL.get(), shooter, shooter.level(), new ItemStack( Items.BONE_MEAL ), null ); }
    
    /** Called each tick this arrow is in the ground. */
    @Override
    protected void tickDespawn() {
        // Unlike classic arrows, shrapnel has a very short (and random) de-spawn time and does not get refreshed when disturbed
        if( life-- <= 0 ) discard();
    }
    
    /** Called when the player touches this entity. */
    @Override
    public void playerTouch( Player player ) { }
    
    /** Pierce level (the vanilla setter is private in 1.21). */
    private byte pierce = 0;
    /** Extra knockback (the vanilla field is gone in 1.21). */
    private int extraKnockback = 0;
    
    public void setShrapnelPierce( int value ) { pierce = (byte) value; }
    
    public void addShrapnelKnockback( int value ) { extraKnockback += value; }
    
    @Override
    public byte getPierceLevel() { return pierce; }
    
    @Override
    protected void doKnockback( LivingEntity target, net.minecraft.world.damagesource.DamageSource source ) {
        super.doKnockback( target, source );
        if( extraKnockback > 0 ) {
            final net.minecraft.world.phys.Vec3 push = getDeltaMovement().multiply( 1.0, 0.0, 1.0 ).normalize().scale( extraKnockback * 0.6 );
            target.push( push.x, 0.1, push.z );
        }
    }
    
    /** @return The item version of this arrow. */
    @Override
    protected ItemStack getDefaultPickupItem() { return new ItemStack( Items.BONE_MEAL ); }
    
    /** Saves data to this entity's base NBT compound that is specific to its subclass. */
    @Override
    public void addAdditionalSaveData( CompoundTag tag ) {
        super.addAdditionalSaveData( tag );
        tag.putShort( "life", (short) life );
    }
    
    /** Loads data from this entity's base NBT compound that is specific to its subclass. */
    @Override
    public void readAdditionalSaveData( CompoundTag tag ) {
        super.readAdditionalSaveData( tag );
        life = tag.getShort( "life" );
    }
}