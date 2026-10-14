package fathertoast.specialmobs.crust.api.config.common.value;

import fathertoast.specialmobs.crust.api.config.common.field.collection.AttributeOpListField;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An attribute supplier whose values are driven by a config field.
 * <p>
 * The base attributes are given by the builder; the config operations are applied on top of them (lazily, and
 * again each time the config field is reloaded).
 * <p>
 * Port note (1.21): attributes are now referenced by {@link Holder}, and modifiers are identified by
 * {@link ResourceLocation} instead of UUID.
 */
public class ConfigDrivenAttributeSupplier extends AttributeSupplier {
    
    private final AttributeOpListField FIELD;
    private final Map<Holder<Attribute>, AttributeInstance> BASE_ATTRIBUTES;
    
    private AttributeSupplier underlyingMap;
    
    public ConfigDrivenAttributeSupplier( AttributeOpListField field, AttributeSupplier.Builder builder ) {
        super( builder.builder ); // Requires an access transformer, see META-INF/accesstransformer.cfg
        FIELD = field;
        BASE_ATTRIBUTES = builder.builder;
        field.linkedAttributeSupplier = this;
    }
    
    public AttributeOpListField getField() { return FIELD; }
    
    /** Discards the cached values so that they are rebuilt from the config on next access. */
    public void invalidate() { underlyingMap = null; }
    
    private void validate() {
        if( underlyingMap != null ) return;
        
        // Create a deep clone of the base attribute map
        final AttributeSupplier.Builder builder = AttributeSupplier.builder();
        for( Map.Entry<Holder<Attribute>, AttributeInstance> entry : BASE_ATTRIBUTES.entrySet() ) {
            builder.add( entry.getKey(), entry.getValue().getBaseValue() );
        }
        FIELD.apply( builder );
        underlyingMap = builder.build();
    }
    
    @Override
    public double getValue( Holder<Attribute> attribute ) {
        validate();
        return underlyingMap.getValue( attribute );
    }
    
    @Override
    public double getBaseValue( Holder<Attribute> attribute ) {
        validate();
        return underlyingMap.getBaseValue( attribute );
    }
    
    @Override
    public double getModifierValue( Holder<Attribute> attribute, ResourceLocation id ) {
        validate();
        return underlyingMap.getModifierValue( attribute, id );
    }
    
    @Override
    @Nullable
    public AttributeInstance createInstance( Consumer<AttributeInstance> onChanged, Holder<Attribute> attribute ) {
        validate();
        return underlyingMap.createInstance( onChanged, attribute );
    }
    
    @Override
    public boolean hasAttribute( Holder<Attribute> attribute ) { return BASE_ATTRIBUTES.containsKey( attribute ); }
    
    @Override
    public boolean hasModifier( Holder<Attribute> attribute, ResourceLocation id ) {
        validate();
        return underlyingMap.hasModifier( attribute, id );
    }
}
