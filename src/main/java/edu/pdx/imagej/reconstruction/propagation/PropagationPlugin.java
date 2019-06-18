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

import ij.ImagePlus;

import org.scijava.Prioritized;
import net.imagej.ImageJPlugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.SubReconstructionPlugin;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

/** A plugin that implements a propagation algorithm.  It is made to be used by
 * {@link Propagation}.  Because this is a {@link
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin}, you can still do things during any other step of the
 * process, but you are <strong>not</strong> allowed to do something for {@link
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin#process_propagated_field
 * ReconstructionPlugin#process_propagated_field}.  Calling that function will
 * throw an <code>UnsupportedOperationException</code>.  Instead, you need to
 * use {@link process_starting_field
 * process_starting_field(ConstReconstructionField)} or {@link propagate
 * propagate(ConstReconstructionField, DistanceUnitValue, ReconstructionField,
 * DistanceUnitValue)}.
 */
public interface PropagationPlugin extends SubReconstructionPlugin {
    /** Perform any processing before propagating.  Some things may be able to
     * be computed that are common to all z steps, so this is a good place to do
     * it.
     *
     * @param field The original field, right before propagation.
     */
    default void process_starting_field(ConstReconstructionField field) {}
    /** Perform propagation, storing the result in <code>field</code>.
     * There are two ways that you could decide to propagate: first, you can
     * always propagate from the original field to the one you need to go to.
     * This is usually how an algorithm would propagate.  However, some methods
     * could propagate from the most recently propagated field, and so the
     * previous field and its z value are included here.  This could be useful
     * because users often want to propagate with a set z step size, and by
     * always propagating from the previous field, the z propagation distance is
     * always the same.  This can allow for caching of the propagation values.
     * No matter what way you propagate, you must put the result in <code>field
     * </code>.
     *
     * @param original_field The original field before propagation.
     * @param z The z value to propagate to.
     * @param field The most recently-propagated field at this time slice, as
     *              well as the output.
     * @param last_z The z value that was used to make <code>field</code>.
     */
    void propagate(ConstReconstructionField original_field,
                   DistanceUnitValue z,
                   ReconstructionField field,
                   DistanceUnitValue last_z);
    /** Throws an <code>UnsupportedOperationException</code>.
     * @param field Unused.
     * @param t Unused.
     * @param z Unused.
     */
    @Override
    default void process_propagated_field(ReconstructionField field,
                                          int t, DistanceUnitValue z)
    {
        throw new UnsupportedOperationException("PropagationPlugin should use "
            + "the process_starting_field and propagate functions instead of "
            + "process_propagated_field.");
    }
}
