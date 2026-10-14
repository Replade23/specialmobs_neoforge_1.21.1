package fathertoast.specialmobs.common.core.register;

import fathertoast.specialmobs.common.bestiary.MobFamily;
import fathertoast.specialmobs.common.core.SpecialMobs;
import net.minecraft.core.registries.BuiltInRegistries;
import fathertoast.specialmobs.common.item.SyringeItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

/**
 * The items added by this mod.
 * <p>
 * Note that the vast majority of items are spawn eggs, which are registered by and stored in the bestiary
 * through the {@link MobFamily.Species}; a specific species is typically accessed through its entity class's
 * static field annotated with {@link fathertoast.specialmobs.common.bestiary.SpecialMob.SpeciesReference}.
 */
public class SMItems {
    
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create( BuiltInRegistries.ITEM, SpecialMobs.MOD_ID );
    
    public static final DeferredHolder<Item, Item> SYRINGE = REGISTRY.register( "syringe", SyringeItem::new );
    
    public static final DeferredHolder<Item, Item> INCORPOREAL_FIREBALL = REGISTRY.register( "incorporeal_fire_charge", fathertoast.specialmobs.common.item.IncorporealFireChargeItem::new );
    public static final DeferredHolder<Item, Item> SLAB_FIREBALL = REGISTRY.register( "slab_fire_charge", fathertoast.specialmobs.common.item.SlabFireChargeItem::new );
    
    /** Registers an entity type's spawn egg item to the deferred register. */
    public static <T extends Mob> DeferredHolder<Item, DeferredSpawnEggItem> registerSpawnEgg(
            DeferredHolder<EntityType<?>, EntityType<T>> entityType, int eggBaseColor, int eggSpotsColor ) {
        final String name = Objects.requireNonNull( entityType.getId() ).getPath() + "_spawn_egg";
        return REGISTRY.register( name, () ->
                new DeferredSpawnEggItem( entityType, eggBaseColor, eggSpotsColor, new Item.Properties() )
        );
    }
}
