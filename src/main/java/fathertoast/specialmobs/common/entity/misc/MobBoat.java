package fathertoast.specialmobs.common.entity.misc;

import fathertoast.specialmobs.common.core.register.SMEntities;
import fathertoast.specialmobs.common.entity.ai.IBoatRider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

/**
 * A boat that can be steered by a mob.
 * <p>
 * The 1.20 version was a full copy of the vanilla boat plus a custom packet to sync the "inputs". In 1.21 the boat
 * is simulated by whoever controls it, and a non-player rider is controlled by the server, so subclassing the
 * vanilla boat is enough: the server runs the paddle logic and vanilla syncs the position to clients.
 * When left without passengers for a while it silently turns into a plain vanilla boat.
 */
public class MobBoat extends Boat {
    
    private boolean inLeft, inRight, inUp;
    private int timeNoPassengers;
    
    public MobBoat( EntityType<? extends MobBoat> entityType, Level level ) { super( entityType, level ); }
    
    public MobBoat( Level level, double x, double y, double z ) {
        this( SMEntities.MOB_BOAT.get(), level );
        setPos( x, y, z );
        xo = x;
        yo = y;
        zo = z;
    }
    
    /** Replaces a mob boat with an equivalent vanilla boat. */
    public static Boat copyFromMobBoat( MobBoat mobBoat ) {
        final Boat boat = new Boat( mobBoat.level(), mobBoat.getX(), mobBoat.getY(), mobBoat.getZ() );
        boat.setXRot( mobBoat.getXRot() );
        boat.setYRot( mobBoat.getYRot() );
        boat.xo = mobBoat.xo;
        boat.yo = mobBoat.yo;
        boat.zo = mobBoat.zo;
        boat.setVariant( mobBoat.getVariant() );
        return boat;
    }
    
    @Override
    public void setInput( boolean left, boolean right, boolean up, boolean down ) {
        super.setInput( left, right, up, down );
        inLeft = left;
        inRight = right;
        inUp = up;
    }
    
    @Override
    public void tick() {
        super.tick();
        if( level().isClientSide ) return;
        
        final Entity rider = getControllingPassenger();
        if( rider != null && !(rider instanceof Player) ) {
            // Vanilla only calls this on the player's client; for mobs the server does it
            controlBoat();
            setPaddleState( inRight || inUp, inLeft || inUp );
            timeNoPassengers = 0;
        }
        else if( getPassengers().isEmpty() && ++timeNoPassengers > 60 ) {
            // Silently replace mob boat with the equivalent vanilla boat
            discard();
            level().addFreshEntity( copyFromMobBoat( this ) );
        }
    }
    
    /** Despawn this boat if its rider was discarded (e.g. the pirate despawned). */
    @Override
    protected void removePassenger( Entity passenger ) {
        super.removePassenger( passenger );
        if( passenger instanceof IBoatRider && passenger.getRemovalReason() == RemovalReason.DISCARDED ) discard();
    }
}
