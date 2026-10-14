package fathertoast.specialmobs.client;

import fathertoast.specialmobs.common.core.SpecialMobs;
import fathertoast.specialmobs.crust.common.network.CrustNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber( value = Dist.CLIENT, modid = SpecialMobs.MOD_ID )
public final class ClientEventHandler {
    
    /** When leaving a remote server (no ServerStoppedEvent fires on a pure client), drop the server's synced values. */
    @SubscribeEvent
    public static void onLoggingOut( ClientPlayerNetworkEvent.LoggingOut event ) { CrustNetwork.clearSyncData(); }
}
