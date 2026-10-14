package fathertoast.specialmobs.common.core.register;

import fathertoast.specialmobs.common.block.MeltingIceBlock;
import fathertoast.specialmobs.common.core.SpecialMobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SMBlocks {
    /** The deferred register for this mod's blocks. */
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( BuiltInRegistries.BLOCK, SpecialMobs.MOD_ID );
    
    public static final DeferredHolder<Block, MeltingIceBlock> MELTING_ICE = registerBlockNoItem( "melting_ice", MeltingIceBlock::new );
    
    public static final java.util.List<DeferredHolder<Block, Block>> INFESTED_CORAL;
    
    static {
        final java.util.ArrayList<DeferredHolder<Block, Block>> infestedCoral = new java.util.ArrayList<>();
        for( fathertoast.specialmobs.common.block.UnderwaterSilverfishBlock.Type type : fathertoast.specialmobs.common.block.UnderwaterSilverfishBlock.Type.values() ) {
            infestedCoral.add( registerBlock( type.blockId(), type::blockSupplier ) );
        }
        infestedCoral.trimToSize();
        INFESTED_CORAL = java.util.Collections.unmodifiableList( infestedCoral );
    }
    
    /** Registers a block and a simple item for it. */
    private static <T extends Block> DeferredHolder<Block, T> registerBlock( String name, Supplier<T> blockSupplier ) {
        final DeferredHolder<Block, T> holder = registerBlockNoItem( name, blockSupplier );
        SMItems.REGISTRY.register( name, () -> new net.minecraft.world.item.BlockItem( holder.get(), new net.minecraft.world.item.Item.Properties() ) );
        return holder;
    }
    
    public static final java.util.List<DeferredHolder<Block, Block>> SLAB_FIRES;
    
    static {
        final java.util.ArrayList<DeferredHolder<Block, Block>> slabFires = new java.util.ArrayList<>();
        for( fathertoast.specialmobs.common.block.SlabFireBlock.Type type : fathertoast.specialmobs.common.block.SlabFireBlock.Type.values() ) {
            slabFires.add( registerBlockNoItem( type.blockId(), type::blockSupplier ) );
        }
        slabFires.trimToSize();
        SLAB_FIRES = java.util.Collections.unmodifiableList( slabFires );
    }
    
    /** Registers a block with no item. */
    private static <T extends Block> DeferredHolder<Block, T> registerBlockNoItem( String name, Supplier<T> blockSupplier ) {
        return REGISTRY.register( name, blockSupplier );
    }
}
