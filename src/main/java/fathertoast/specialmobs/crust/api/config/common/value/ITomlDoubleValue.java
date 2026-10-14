package fathertoast.specialmobs.crust.api.config.common.value;

import fathertoast.specialmobs.crust.api.config.common.file.TomlHelper;

import java.util.function.Supplier;

/**
 * A TOML value intended to wrap a {@link Double} type to support prettier printing for
 * {@link TomlHelper#fieldInfoRange(ITomlDoubleValue, ITomlDoubleValue, ITomlDoubleValue)}
 * and config file values.
 */
public interface ITomlDoubleValue extends ITomlValue, Supplier<Double> { }