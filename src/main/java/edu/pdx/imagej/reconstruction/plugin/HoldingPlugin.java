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
 * use this, you must override {@link getPlugins} and {@link sortPlugins}.  It
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
    default void readPlugins(List<ReconstructionPlugin> plugins)
    {
        for (T plugin : getPlugins()) {
            plugin.readPlugins(plugins);
        }
    }
    @Override
    default void processBeforeParam()
    {
        for (T plugin : getPlugins()) {
            plugin.processBeforeParam();
        }
    }
    @Override
    default void processHologramParam(ImagePlus hologram)
    {
        for (T plugin : getPlugins()) {
            plugin.processHologramParam(hologram);
        }
    }
    @Override
    default void processWavelengthParam(DistanceUnitValue wavelength)
    {
        for (T plugin : getPlugins()) {
            plugin.processWavelengthParam(wavelength);
        }
    }
    @Override
    default void processDimensionsParam(DistanceUnitValue width,
                                          DistanceUnitValue height)
    {
        for (T plugin : getPlugins()) {
            plugin.processDimensionsParam(width, height);
        }
    }
    @Override
    default void processTsParam(List<Integer> ts)
    {
        for (T plugin : getPlugins()) {
            plugin.processTsParam(ts);
        }
    }
    @Override
    default void processZsParam(List<DistanceUnitValue> zs)
    {
        for (T plugin : getPlugins()) {
            plugin.processZsParam(zs);
        }
    }
    @Override
    default void processBeginning()
    {
        for (T plugin : getPlugins()) {
            plugin.processBeginning();
        }
    }
    @Override
    default void processOriginalHologram(ConstReconstructionField field)
    {
        for (T plugin : getPlugins()) {
            plugin.processOriginalHologram(field);
        }
    }
    @Override
    default void processHologram(ReconstructionField field, int t)
    {
        for (T plugin : getPlugins()) {
            plugin.processHologram(field, t);
        }
    }
    @Override
    default void processFilteredField(ReconstructionField field, int t)
    {
        for (T plugin : getPlugins()) {
            plugin.processFilteredField(field, t);
        }
    }
    @Override
    default void processPropagatedField(ReconstructionField field,
                                          int t, DistanceUnitValue z)
    {
        for (T plugin : getPlugins()) {
            plugin.processPropagatedField(field, t, z);
        }
    }
    @Override
    default void processEnding()
    {
        for (T plugin : getPlugins()) {
            plugin.processEnding();
        }
    }
    @Override
    default void setBeginningPriority()
    {
        for (T plugin : getPlugins()) {
            plugin.setBeginningPriority();
        }
        sortPlugins();
    }
    @Override
    default void setOriginalHologramPriority()
    {
        for (T plugin : getPlugins()) {
            plugin.setOriginalHologramPriority();
        }
        sortPlugins();
    }
    @Override
    default void setHologramPriority()
    {
        for (T plugin : getPlugins()) {
            plugin.setHologramPriority();
        }
        sortPlugins();
    }
    @Override
    default void setFilteredFieldPriority()
    {
        for (T plugin : getPlugins()) {
            plugin.setFilteredFieldPriority();
        }
        sortPlugins();
    }
    @Override
    default void setPropagatedFieldPriority()
    {
        for (T plugin : getPlugins()) {
            plugin.setPropagatedFieldPriority();
        }
        sortPlugins();
    }
    @Override
    default void setEndingPriority()
    {
        for (T plugin : getPlugins()) {
            plugin.setEndingPriority();
        }
        sortPlugins();
    }
    @Override
    default boolean hasError()
    {
        for (T plugin : getPlugins()) {
            if (plugin.hasError()) return true;
        }
        return false;
    }

    /** Get all of the plugins contained in this one.  Whenever doing some
     * processing, the plugins will process <em>in order</em>, so make sure that
     * if you care about this order that you return something that is ordered.
     *
     * @return The plugins that are held by this plugin.
     */
    Iterable<T> getPlugins();
    /** Sort the plugins for the iteration order in {@link getPlugins}.  This
     * will be called after priorities might have changed.
     */
    void sortPlugins();
}
