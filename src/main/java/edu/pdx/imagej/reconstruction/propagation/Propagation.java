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

package edu.pdx.imagej.reconstruction.propagation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ij.ImagePlus;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.HoldingSinglePlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

/** A {@link edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin} that performs numerical propagation.  The actual
 * algorithm used is customizable through {@link PropagationPlugin}.  It might
 * be confusing that <code>Propagation</code> is just a random plugin when the
 * propagation methods assume propagation happens between steps, but this plugin
 * has first priority and so is the first to have {@link
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin#processPropagatedField
 * ReconstructionPlugin#processPropagatedField} called, and it does the
 * propagation then.  So, please don't have a priority before first, because
 * then it won't actually be propagated yet.
 */
@Plugin(type = ReconstructionPlugin.class, name = "Propagation",
        priority = Priority.FIRST)
public class Propagation extends HoldingSinglePlugin<PropagationPlugin>
                         implements MainReconstructionPlugin {
    /** Constructor intended for live use of the plugin.
     */
    public Propagation()
    {
        super("Propagation Algorithm", PropagationPlugin.class);
    }
    /** Constructor intended for programmatic use of the plugin.
     *
     * @param plugin The propagation algorithm to use.
     */
    public Propagation(PropagationPlugin plugin)
    {
        super(plugin);
    }
    @Override public Propagation duplicate()
    {
        return new Propagation((PropagationPlugin)getPlugin().duplicate());
    }
    /** Propagate the field to the distance z.
     *
     * @param field The field to propagate.
     * @param t The time slice used to get this field.
     * @param z The z value to propagate to.
     */
    @Override public void processPropagatedField(ReconstructionField field,
                                                   int t, DistanceUnitValue z)
    {
        if (M_tsProcessed.add(t)) {
            M_lastZ = new DistanceUnitValue();
            if (field != null) {
                M_originalField = new ConstReconstructionField(field.copy());
            }
            getPlugin().processStartingField(M_originalField);
        }
        getPlugin().propagate(M_originalField, z, field, M_lastZ);
        M_lastZ = z;
    }
    /** Returns a singleton list of <code>{@link
     * PropagationPlugin}.class</code>.
     */
    @Override public List<Class<? extends ReconstructionPlugin>> subPlugins()
    {
        ArrayList<Class<? extends ReconstructionPlugin>> result
            = new ArrayList<>();
        result.add(PropagationPlugin.class);
        return result;
    }

    private HashSet<Integer> M_tsProcessed = new HashSet<>();
    private ConstReconstructionField M_originalField;
    private DistanceUnitValue M_lastZ;
}
