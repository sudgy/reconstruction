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

@Plugin(type = ReconstructionPlugin.class, priority = Priority.FIRST)
public class Propagation extends HoldingSinglePlugin<PropagationPlugin>
                         implements MainReconstructionPlugin {
    public Propagation()
    {
        super("Propagation Algorithm", PropagationPlugin.class);
    }
    public Propagation(PropagationPlugin plugin)
    {
        super(plugin);
    }
    @Override public void process_propagated_field(
        ConstReconstructionField original_field,
        ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        if (M_ts_processed.add(t)) {
            get_plugin().process_starting_field(original_field);
        }
        get_plugin().propagate(original_field, current_field, z_from, z_to);
    }

    private HashSet<Integer> M_ts_processed = new HashSet<>();
}
