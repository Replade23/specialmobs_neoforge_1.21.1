package fathertoast.specialmobs.common.core.register;

import com.mojang.serialization.MapCodec;
import fathertoast.specialmobs.common.biome.modifier.NaturalAddSpawnModifier;
import fathertoast.specialmobs.common.core.SpecialMobs;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class SMBiomeMods {
    
    /** The deferred register for this mod's biome modifiers. */
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> REGISTRY =
            DeferredRegister.create( NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SpecialMobs.MOD_ID );
    
    
    @SuppressWarnings( "unused" )
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<NaturalAddSpawnModifier>> NATURAL_ADD_SPAWN = REGISTRY
            .register( "natural_add_spawn", () -> NaturalAddSpawnModifier.CODEC );
}
