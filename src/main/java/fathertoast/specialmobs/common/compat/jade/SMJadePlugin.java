package fathertoast.specialmobs.common.compat.jade;

import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.common.entity.ai.INinja;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IWailaConfig;

/**
 * Our Jade plugin: makes Jade show a disguised ninja as the block it hides as, instead of giving it away.
 * Jade is optional; this class is only loaded by Jade itself when Jade is installed.
 */
@WailaPlugin
public class SMJadePlugin implements IWailaPlugin {
    
    private static final ResourceLocation DISPLAY_NINJA_DISGUISES = SpecialMobs.rl( "display_ninja_disguises" );
    
    @Override
    public void register( IWailaCommonRegistration registration ) { }
    
    @Override
    public void registerClient( IWailaClientRegistration registration ) {
        registration.addConfig( DISPLAY_NINJA_DISGUISES, true );
        registration.markAsClientFeature( DISPLAY_NINJA_DISGUISES );
        
        registration.addRayTraceCallback( ( hitResult, accessor, originalAccessor ) -> {
            if( accessor instanceof EntityAccessor entityAccessor && entityAccessor.getEntity() instanceof INinja ninja ) {
                final BlockState disguise = ninja.getHiddenDragon();
                
                if( disguise != null && IWailaConfig.get().getPlugin().get( DISPLAY_NINJA_DISGUISES ) ) {
                    final Vec3 vec3 = entityAccessor.getHitResult().getLocation();
                    
                    return registration.blockAccessor()
                            .blockEntity( () -> null )
                            .blockState( disguise )
                            .level( entityAccessor.getLevel() )
                            .player( entityAccessor.getPlayer() )
                            .serverConnected( entityAccessor.isServerConnected() )
                            .showDetails( false )
                            .hit( new BlockHitResult( vec3, Direction.getNearest( vec3.x, vec3.y, vec3.z ),
                                    entityAccessor.getHitResult().getEntity().blockPosition(), false ) )
                            .build();
                }
            }
            return accessor;
        } );
    }
}
