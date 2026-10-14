package fathertoast.specialmobs.crust.api.config.common.file.action;

import com.electronwill.nightconfig.core.io.CharacterOutput;
import fathertoast.specialmobs.crust.api.config.common.file.CrustTomlWriter;

/** Represents a single action performed by the spec when reading or writing the config file. */
public interface ISpecAction {
    /** Called when the config is loaded. */
    boolean onLoad();
    
    /** Called when the config is saved. */
    void write( CrustTomlWriter writer, CharacterOutput output );
    
}