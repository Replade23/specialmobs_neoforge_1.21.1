package fathertoast.specialmobs.crust.common.network;

import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.specialmobs.crust.api.event.config.CrustConfigEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Server -> client packet carrying the values of one config file's synced fields.
 * <p>
 * Forge 1.20 used a SimpleChannel with encode/decode/handle methods; NeoForge 1.21 uses a
 * {@link CustomPacketPayload} (the data) plus a {@link StreamCodec} (how to read/write it) plus a handler.
 */
public record ConfigSyncPayload( CrustConfigSpec spec, List<Object> values ) implements CustomPacketPayload {
    
    public static final Type<ConfigSyncPayload> TYPE = new Type<>( SpecialMobs.rl( "config_sync" ) );
    
    public static final StreamCodec<FriendlyByteBuf, ConfigSyncPayload> CODEC = StreamCodec.of(
            ( buffer, message ) -> message.spec().serialize( message.values(), buffer, true ),
            buffer -> {
                CrustConfigSpec spec = CrustConfigSpec.readSpec( buffer );
                return new ConfigSyncPayload( spec, spec.deserialize( buffer, true ) );
            } );
    
    /** Builds a sync message from the spec's current local values. */
    public ConfigSyncPayload( CrustConfigSpec spec ) { this( spec, spec.buildValueList( false, true ) ); }
    
    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
    
    /** Client-side handler. NeoForge runs it on the main thread by default, so no enqueueWork needed. */
    public static void handle( ConfigSyncPayload message, IPayloadContext context ) {
        message.spec().applyValueListRemote( message.values(), true );
        NeoForge.EVENT_BUS.post( new CrustConfigEvent.File.Synced( message.spec().FILE, context.player() ) );
    }
}
