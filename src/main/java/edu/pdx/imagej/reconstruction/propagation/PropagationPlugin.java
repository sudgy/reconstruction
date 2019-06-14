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

import java.util.LinkedHashMap;

import ij.ImagePlus;

import org.scijava.Prioritized;
import net.imagej.ImageJPlugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.SubReconstructionPlugin;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

public interface PropagationPlugin extends SubReconstructionPlugin {
    default void process_starting_field(ConstReconstructionField field) {}
    void propagate(ConstReconstructionField original_field,
                   ReconstructionField current_field,
                   DistanceUnitValue z_from, DistanceUnitValue z_to);
    @Override
    default void process_propagated_field(
        ConstReconstructionField original_field,
        ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        throw new UnsupportedOperationException("PropagationPlugin should use "
            + "the process_starting_field and propagate functions instead of "
            + "process_propagated_field.");
    }
}
