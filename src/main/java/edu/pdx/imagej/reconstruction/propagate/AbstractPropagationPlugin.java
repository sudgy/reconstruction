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

import org.scijava.plugin.AbstractRichPlugin;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

public abstract class AbstractPropagationPlugin
    extends AbstractRichPlugin
    implements PropagationPlugin
{
    @Override
    final public void process_propagated_field(
        ConstReconstructionField original_field,
        ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        PropagationPlugin.super.process_propagated_field(original_field,
                                                         current_field,
                                                         t, z_from, z_to);
    }
}
