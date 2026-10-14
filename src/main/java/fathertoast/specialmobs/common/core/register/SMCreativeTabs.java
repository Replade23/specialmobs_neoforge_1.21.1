package fathertoast.specialmobs.common.core.register;

import fathertoast.specialmobs.common.core.SpecialMobs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SMCreativeTabs {
    
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create( Registries.CREATIVE_MODE_TAB, SpecialMobs.MOD_ID );
    
    
    public static final RegObj EGGS_TAB = register( "all", () -> CreativeModeTab.builder()
            .icon( () -> new ItemStack( Items.CREEPER_SPAWN_EGG ) )
            .title( Component.translatable( "itemGroup.specialmobs.spawn_eggs" ) )
            .build() );
    
    
    private static RegObj register( String name, Supplier<CreativeModeTab> supplier ) {
        DeferredHolder<CreativeModeTab, CreativeModeTab> regObj = REGISTRY.register( name, supplier );
        return new RegObj( regObj, ResourceKey.create( Registries.CREATIVE_MODE_TAB, SpecialMobs.rl( name ) ) );
    }
    
    
    public static void buildCreativeContents( final BuildCreativeModeTabContentsEvent event ) {
        // The SEARCH tab aggregates every other tab automatically in 1.21; adding items there too throws 'already exists'.
        if( event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.FUNCTIONAL_BLOCKS ) {
            SMBlocks.INFESTED_CORAL.forEach( block -> event.accept( block.get() ) );
        }
        else if( event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES ) {
            event.accept( SMItems.SYRINGE.get() );
            event.accept( SMItems.INCORPOREAL_FIREBALL.get() );
            event.accept( SMItems.SLAB_FIREBALL.get() );
        }
        if( event.getTabKey() == EGGS_TAB.key() ) {
            for( DeferredHolder<Item, ? extends Item> item : SMItems.REGISTRY.getEntries() ) {
                if( item.get() instanceof DeferredSpawnEggItem )
                    event.accept( item.get() );
            }
        }
    }
    
    
    public record RegObj( DeferredHolder<CreativeModeTab, CreativeModeTab> regObj, ResourceKey<CreativeModeTab> key ) {
        @SuppressWarnings( "unused" )
        public CreativeModeTab getTab() { return regObj.get(); }
    }
}
