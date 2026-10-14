package fathertoast.specialmobs.common.util;

import fathertoast.specialmobs.common.core.register.SMItems;
import fathertoast.specialmobs.common.entity.projectile.IncorporealFireballEntity;
import fathertoast.specialmobs.common.entity.projectile.SlabFireballEntity;
import fathertoast.specialmobs.crust.api.lib.LevelEventHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

/** Dispenser behaviors for the fire charges. (1.21: BlockSource is a record and projectile dispensing changed.) */
public class SMDispenserBehavior {
    
    public static void registerBehaviors() {
        DispenserBlock.registerBehavior( SMItems.INCORPOREAL_FIREBALL.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute( BlockSource source, ItemStack itemStack ) {
                final ServerLevel level = source.level();
                final Position pos = DispenserBlock.getDispensePosition( source );
                final double x = pos.x();
                final double y = pos.y();
                final double z = pos.z();
                final LivingEntity nearestEntity = level.getNearestEntity( LivingEntity.class, TargetingConditions.DEFAULT, null, x, y, z,
                        new AABB( BlockPos.containing( x + 0.5D, y + 0.5D, z + 0.5D ) ).inflate( 50.0F ) );
                
                level.addFreshEntity( new IncorporealFireballEntity( level, null, nearestEntity, x, y, z ) );
                itemStack.shrink( 1 );
                return itemStack;
            }
            
            @Override
            protected void playSound( BlockSource source ) {
                LevelEventHelper.BLAZE_SHOOT.play( source.level(), source.pos() );
            }
        } );
        
        DispenserBlock.registerBehavior( SMItems.SLAB_FIREBALL.get(), new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute( BlockSource source, ItemStack itemStack ) {
                final Direction direction = source.state().getValue( DispenserBlock.FACING );
                final Position pos = DispenserBlock.getDispensePosition( source );
                
                final double x = pos.x() + (double) ((float) direction.getStepX() * 0.3F);
                final double y = pos.y() + (double) ((float) direction.getStepY() * 0.3F);
                final double z = pos.z() + (double) ((float) direction.getStepZ() * 0.3F);
                
                final ServerLevel level = source.level();
                final RandomSource random = level.random;
                
                final double dx = random.triangle( direction.getStepX(), 0.1 );
                final double dy = random.triangle( direction.getStepY(), 0.1 );
                final double dz = random.triangle( direction.getStepZ(), 0.1 );
                
                final SlabFireballEntity slabFireball = new SlabFireballEntity( level, x, y, z, dx, dy, dz );
                level.addFreshEntity( slabFireball );
                itemStack.shrink( 1 );
                return itemStack;
            }
            
            @Override
            protected void playSound( BlockSource source ) {
                LevelEventHelper.BLAZE_SHOOT.play( source.level(), source.pos() );
            }
        } );
    }
}
