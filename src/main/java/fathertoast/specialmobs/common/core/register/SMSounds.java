package fathertoast.specialmobs.common.core.register;

import fathertoast.specialmobs.common.core.SpecialMobs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SMSounds {
    
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create( BuiltInRegistries.SOUND_EVENT, SpecialMobs.MOD_ID );
    
    
    public static final DeferredHolder<SoundEvent, SoundEvent> IMPLODING_CREEPER_IMPLODE = register( "entity.imploding_creeper.implode" );
    
    
    private static DeferredHolder<SoundEvent, SoundEvent> register( String name ) {
        return REGISTRY.register( name, () -> SoundEvent.createVariableRangeEvent( SpecialMobs.rl( name ) ) );
    }
}
