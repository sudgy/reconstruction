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

import java.util.HashSet;

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
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin#process_propagated_field
 * ReconstructionPlugin#process_propagated_field} called, and it does the
 * propagation then.  So, please don't have a priority before first, because
 * then it won't actually be propagated yet.
 */
@Plugin(type = ReconstructionPlugin.class, priority = Priority.FIRST)
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
    /** Propagate the field to the distance z.
     *
     * @param field The field to propagate.
     * @param t The time slice used to get this field.
     * @param z The z value to propagate to.
     */
    @Override public void process_propagated_field(ReconstructionField field,
                                                   int t, DistanceUnitValue z)
    {
        if (M_ts_processed.add(t)) {
            M_last_z = new DistanceUnitValue();
            if (field != null) {
                M_original_field = new ConstReconstructionField(field.copy());
            }
            get_plugin().process_starting_field(M_original_field);
        }
        get_plugin().propagate(M_original_field, field, M_last_z, z);
        M_last_z = z;
    }

    private HashSet<Integer> M_ts_processed = new HashSet<>();
    private ConstReconstructionField M_original_field;
    private DistanceUnitValue M_last_z;
}
