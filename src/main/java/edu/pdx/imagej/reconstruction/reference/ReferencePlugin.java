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

package edu.pdx.imagej.reconstruction.reference;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.SubReconstructionPlugin;

/** A plugin that retrieves a reference hologram.  It is made to be used by
 * {@link Reference}.  Because this is a {@link
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin}, you can still do things during any other step of the
 * process, but you are <strong>not</strong> allowed to do something for {@link
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin#process_filtered_field
 * ReconstructionPlugin#process_filtered_field}.  Calling that function will
 * throw an <code>UnsupportedOperationException</code>.  Instead, you need to
 * use {@link get_reference_holo get_reference_holo}.
 * <p>
 * Most of the time, you should not implement this interface directly, and
 * instead extend {@link AbstractReferencePlugin}.
 */
public interface ReferencePlugin extends SubReconstructionPlugin {
    /** Get the reference hologram.  If you need more parameters than these two,
     * find some other way to access them.  Many of the default plugins use
     * {@link edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
     * ReconstructionPlugin}'s methods to get other things they need.  If this
     * returns <code>null</code>, {@link Reference} interprets that as the
     * plugin not wanting to apply a reference hologram.
     *
     * @param field The filtered field.  You may not need it.
     * @param t The time slice used to get the field.  You may not need it.
     * @return The reference hologram field.
     */
    ReconstructionField get_reference_holo(ConstReconstructionField field,
                                           int t);
    /** Whether or not you want the option to use the same {@link
     * edu.pdx.imagej.reconstruction.filter.Filter Filter} as everything else.
     * It defaults to allowing the option.
     *
     * @return <code>true</code> if you want to <em>disable</em> the option,
     *         <code>false</code> if you want to <em>enable</em> the option.
     */
    default boolean dont_use_same_roi() {return false;}
    /** Throws an <code>UnsupportedOperationException</code>.
     * @param field Unused.
     * @param t Unused.
     */
    @Override
    default void process_filtered_field(ReconstructionField field, int t)
    {
        throw new UnsupportedOperationException("A ReferencePlugin should not "
            + "use process_filtered_field to get the reference hologram.  Use "
            + "get_reference_holo instead.");
    }
}
