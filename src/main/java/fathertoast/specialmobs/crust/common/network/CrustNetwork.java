package fathertoast.specialmobs.crust.common.network;

import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.crust.api.config.common.ConfigManager;
import fathertoast.specialmobs.crust.api.config.common.file.CrustConfigSpec;
import fathertoast.specialmobs.crust.api.event.config.CrustConfigEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Config sync for multiplayer: the server's values for "synced" fields override the client's local ones.
 * Replaces Crust's Forge SimpleChannel (CrustPacketHandler).
 */
@EventBusSubscriber( modid = SpecialMobs.MOD_ID )
public final class CrustNetwork {
    
    /** Bump when the packet format changes. Client and server must match or the connection is refused. */
    private static final String PROTOCOL_VERSION = "1";
    
    /** Called on the MOD bus: registers our payloads. */
    public static void registerPayloads( RegisterPayloadHandlersEvent event ) {
        PayloadRegistrar registrar = event.registrar( PROTOCOL_VERSION );
        registrar.playToClient( ConfigSyncPayload.TYPE, ConfigSyncPayload.CODEC, ConfigSyncPayload::handle );
    }
    
    /** Hooks the reload-sync callback used by CrustConfigSpec.onLoad. */
    public static void init() { ConfigManager.SYNC_SPEC_CONSUMER = CrustNetwork::sendConfigSync; }
    
    /** Syncs each relevant config to one player (on login). */
    public static void sendConfigSync( ServerPlayer player ) {
        ConfigManager.getAll().forEach( cfgManager -> cfgManager.getConfigs().forEach( file -> {
            if( file.SPEC.isSynced() ) {
                PacketDistributor.sendToPlayer( player, new ConfigSyncPayload( file.SPEC ) );
                NeoForge.EVENT_BUS.post( new CrustConfigEvent.File.Synced( file, player ) );
            }
        } ) );
    }
    
    /** Syncs one config to all players (after a reload). Does nothing if there is no running server yet. */
    public static void sendConfigSync( CrustConfigSpec spec ) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if( server == null || !spec.isSynced() ) return;
        PacketDistributor.sendToAllPlayers( new ConfigSyncPayload( spec ) );
        server.getPlayerList().getPlayers().forEach( player ->
                NeoForge.EVENT_BUS.post( new CrustConfigEvent.File.Synced( spec.FILE, player ) ) );
    }
    
    /** Clears synced values so the next world/server starts from local config values. */
    public static void clearSyncData() {
        ConfigManager.getAll().forEach( manager -> manager.getConfigs().forEach( file -> file.SPEC.clearSyncData() ) );
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedIn( PlayerEvent.PlayerLoggedInEvent event ) {
        if( event.getEntity() instanceof ServerPlayer player ) sendConfigSync( player );
    }
    
    @SubscribeEvent
    public static void onServerStopped( ServerStoppedEvent event ) { clearSyncData(); }
    
    private CrustNetwork() {}
}
