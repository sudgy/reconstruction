/* Copyright (C) 2019 Portland State University
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of version 3 of the GNU Lesser General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * For any questions regarding the license, please contact the Free Software
 * Foundation.  For any other questions regarding this program, please contact
 * David Cohoe at dcohoe@pdx.edu.
 */

package edu.pdx.imagej.reconstruction.plugin;

import java.util.List;

import ij.ImagePlus;

import org.scijava.plugin.AbstractRichPlugin;

import edu.pdx.imagej.dynamic_parameters.PluginParameter;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

/** A {@link ReconstructionPlugin} that holds other ReconstructionPlugins.  To
 * use this, you must override {@link get_plugins} and {@link sort_plugins}.  It
 * automatically calls all of the ReconstructionPlugin's methods on all of the
 * plugins contained within this one, so if you override any of these methods,
 * you <strong>must</strong> call <code>super.[method]</code> if you want that
 * method to be called for the plugins you are containing.
 *
 * @param <T> The type of plugins you are holding
 */
public interface HoldingPlugin<T extends ReconstructionPlugin>
    extends ReconstructionPlugin
{
    @Override
    default void read_plugins(List<ReconstructionPlugin> plugins)
    {
        for (T plugin : get_plugins()) {
            plugin.read_plugins(plugins);
        }
    }
    @Override
    default void process_before_param()
    {
        for (T plugin : get_plugins()) {
            plugin.process_before_param();
        }
    }
    @Override
    default void process_hologram_param(ImagePlus hologram)
    {
        for (T plugin : get_plugins()) {
            plugin.process_hologram_param(hologram);
        }
    }
    @Override
    default void process_wavelength_param(DistanceUnitValue wavelength)
    {
        for (T plugin : get_plugins()) {
            plugin.process_wavelength_param(wavelength);
        }
    }
    @Override
    default void process_dimensions_param(DistanceUnitValue width,
                                          DistanceUnitValue height)
    {
        for (T plugin : get_plugins()) {
            plugin.process_dimensions_param(width, height);
        }
    }
    @Override
    default void process_ts_param(List<Integer> ts)
    {
        for (T plugin : get_plugins()) {
            plugin.process_ts_param(ts);
        }
    }
    @Override
    default void process_zs_param(List<DistanceUnitValue> zs)
    {
        for (T plugin : get_plugins()) {
            plugin.process_zs_param(zs);
        }
    }
    @Override
    default void process_beginning()
    {
        for (T plugin : get_plugins()) {
            plugin.process_beginning();
        }
    }
    @Override
    default void process_original_hologram(ConstReconstructionField field)
    {
        for (T plugin : get_plugins()) {
            plugin.process_original_hologram(field);
        }
    }
    @Override
    default void process_hologram(ReconstructionField field, int t)
    {
        for (T plugin : get_plugins()) {
            plugin.process_hologram(field, t);
        }
    }
    @Override
    default void process_filtered_field(ReconstructionField field, int t)
    {
        for (T plugin : get_plugins()) {
            plugin.process_filtered_field(field, t);
        }
    }
    @Override
    default void process_propagated_field(
        ConstReconstructionField original_field,
        ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        for (T plugin : get_plugins()) {
            plugin.process_propagated_field(original_field, current_field, t,
                                            z_from, z_to);
        }
    }
    @Override
    default void process_ending()
    {
        for (T plugin : get_plugins()) {
            plugin.process_ending();
        }
    }
    @Override
    default void set_beginning_priority()
    {
        for (T plugin : get_plugins()) {
            plugin.set_beginning_priority();
        }
        sort_plugins();
    }
    @Override
    default void set_original_hologram_priority()
    {
        for (T plugin : get_plugins()) {
            plugin.set_original_hologram_priority();
        }
        sort_plugins();
    }
    @Override
    default void set_hologram_priority()
    {
        for (T plugin : get_plugins()) {
            plugin.set_hologram_priority();
        }
        sort_plugins();
    }
    @Override
    default void set_filtered_field_priority()
    {
        for (T plugin : get_plugins()) {
            plugin.set_filtered_field_priority();
        }
        sort_plugins();
    }
    @Override
    default void set_propagated_field_priority()
    {
        for (T plugin : get_plugins()) {
            plugin.set_propagated_field_priority();
        }
        sort_plugins();
    }
    @Override
    default void set_ending_priority()
    {
        for (T plugin : get_plugins()) {
            plugin.set_ending_priority();
        }
        sort_plugins();
    }
    @Override
    default boolean has_error()
    {
        for (T plugin : get_plugins()) {
            if (plugin.has_error()) return true;
        }
        return false;
    }

    /** Get all of the plugins contained in this one.  Whenever doing some
     * processing, the plugins will process <em>in order</em>, so make sure that
     * if you care about this order that you return something that is ordered.
     *
     * @return The plugins that are held by this plugin.
     */
    Iterable<T> get_plugins();
    /** Sort the plugins for the iteration order in {@link get_plugins}.  This
     * will be called after priorities might have changed.
     */
    void sort_plugins();
}
