package fathertoast.specialmobs.crust.api.config.common.file.action;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.specialmobs.crust.api.config.common.file.CrustTomlWriter;

/** Represents a read-only spec action. */
@SuppressWarnings( "ClassCanBeRecord" )
public class ReadAction implements ISpecAction {
    
    /** The method to call on read. */
    protected final Runnable CALLBACK;
    
    /** Create a new field action that will load/create and save the field value. */
    public ReadAction( Runnable callback ) { CALLBACK = callback; }
    
    /** Called when the config is loaded. */
    @Override
    public boolean onLoad() {
        CALLBACK.run();
        return false;
    }
    
    /** Called when the config is saved. */
    @Override // Read actions do not affect file writing
    public final void write( CrustTomlWriter writer, CharacterOutput output ) {}
    
}