package fathertoast.specialmobs.common.core;

import fathertoast.specialmobs.common.config.Config;
import fathertoast.specialmobs.common.core.register.*;
import fathertoast.specialmobs.crust.common.network.CrustNetwork;
import fathertoast.specialmobs.crust.api.lib.CrustObjects;
import fathertoast.specialmobs.common.event.GameEvents;
import fathertoast.specialmobs.common.event.NaturalSpawnManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

@Mod( SpecialMobs.MOD_ID )
public class SpecialMobs {
    /* Feature List:
     * (KEY: - = complete in current version, o = incomplete feature from previous version,
     *       + = incomplete new feature, ? = feature to consider adding)
     *  - general
     *      - mob replacer
     *      + alternate spawn method
     *      - environment-sensitive configs
     *  - natural spawning
     *      - copied spawns
     *          - vanilla spiders -> vanilla cave spiders
     *          - vanilla endermen -> ender creepers
     *      - ocean/river spawns
     *          - drowning creepers
     *          - blueberry slimes
     *      - nether spawns
     *          - vanilla wither skeletons (outside of fortresses)
     *          - vanilla blazes (outside of fortresses)
     *          - fire creepers/zombies/spiders
     *          ? warped/crimson mobs
     *      + phantom spawns
     *  - potions
     *      - vulnerability (moved to Crust)
     *      - weight (moved to Crust)
     *  - blocks
     *      - infested coral (spawns puffer silverfish)
     *      - melting ice (similar to frosted ice)
     *  - entities
     *      + TODO bestiary
     *      - configurable, nbt-driven stats (bestiary info + special mob data)
     *      - configurable weapon type chance
     *      - bone shrapnel
     *      - bug spit
     *      - fish hook
     *      - Vanishing boat (for pirate skeleton)
     *  - monster families (see doc for specifics)
     *      - creepers
     *          - Scope creeper
     *          - chance to spawn charged during thunderstorms
     *          - chance to become supercharged when charged
     *          - explosion stats (while wet, while burning, when shot)
     *      - zombies
     *          - transformations (husk -> any other non-water-sensitive zombie -> analogous drowned)
     *          - ranged attack AI (using bow)
     *          - use shields
     *      - drowned
     *          - use shields
     *          - bug fixes (can move in shallow water, alert regular zombies)
     *      - zombified piglins
     *          - ranged attack AI (using bow)
     *          + ranged attack AI (using crossbow)
     *          - use shields
     *      - skeletons
     *          - use shields
     *          - melee chance
     *          - babies
     *      - wither skeletons
     *          - use shields
     *          - bow chance
     *          - babies
     *      - slimes
     *          - smallest size can deal damage
     *      - magma cubes
     *      - spiders
     *          - ranged attack AI (spitter)
     *      - cave spiders
     *          - ranged attack AI (spitter)
     *      - silverfish
     *          - ranged attack AI (spitter)
     *          - chance to spawn already calling for reinforcements
     *      - endermen
     *      - witches
     *          - ability to equip held items (wonky)
     *          - use splash speed instead of regular
     *          - nerf poison potions, cuz like, damn, yo
     *      - ghasts
     *          - melee attack AI
     *          - remove vertical targeting restriction
     *      - blazes
     *          - melee attack AI
     *          - configurable fireball attack
     *      + guardians
     *          + vortex
     *      + phantoms
     *      + the goat
     *      ? hoglins
     *      ? zoglins
     *      ? shulkers
     */
    
    /** Our mod ID. */
    public static final String MOD_ID = "specialmobs";
    
    /** Logger instance for the mod. */
    public static final Logger LOG = LoggerFactory.getLogger( MOD_ID );
    
    // NeoForge: the constructor receives the mod event bus and container (replaces FMLJavaModLoadingContext).
    public SpecialMobs( IEventBus modEventBus, ModContainer container ) {
        // Load config (the config dir is already available at construction time)
        // Hook config sync BEFORE loading configs (a load may rewrite+sync a file), then load
        CrustNetwork.init();
        Config.initialize();
        
        SMBlocks.REGISTRY.register( modEventBus );
        SMItems.REGISTRY.register( modEventBus );
        SMCreativeTabs.REGISTRY.register( modEventBus );
        SMEntities.REGISTRY.register( modEventBus );
        SMSounds.REGISTRY.register( modEventBus );
        SMBiomeMods.REGISTRY.register( modEventBus );
        CrustObjects.register( modEventBus );
        
        modEventBus.addListener( SMEntities::createAttributes );
        modEventBus.addListener( SMCreativeTabs::buildCreativeContents );
        modEventBus.addListener( NaturalSpawnManager::registerSpawnPlacements );
        modEventBus.addListener( CrustNetwork::registerPayloads );
        modEventBus.addListener( this::commonSetup );
        
        NeoForge.EVENT_BUS.register( new GameEvents() );
    }
    
    public void commonSetup( net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event ) {
        event.enqueueWork( fathertoast.specialmobs.common.util.SMDispenserBehavior::registerBehaviors );
    }
    
    /** @return A ResourceLocation with the mod's namespace. */
    public static ResourceLocation rl( String path ) { return ResourceLocation.fromNamespaceAndPath( MOD_ID, path ); }
    
    /** @return Returns a registry entry as a string, or "null" if it is null. */
    public static <T> String toString( @Nullable T regEntry, Registry<T> registry ) {
        return regEntry == null || registry.getKey( regEntry ) == null
                ? "null"
                : toString( registry.getKey( regEntry ) );
    }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}
