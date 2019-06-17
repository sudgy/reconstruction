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

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;

/** An extension to dynamic parameter's <code>HoldingParameter</code> that is a
 * {@link HologramPluginParameter}.  It passes along <code>set_hologram</code>
 * to all of its sub parameters.
 *
 * @param <T> The type that this parameter gets.
 */
@Plugin(type = DParameter.class)
public abstract class ReconstructionHoldingParameter<T>
             extends HoldingParameter<T> implements HologramPluginParameter {
    /** Normal constructor, the same as <code>HoldingParameter</code>.
     *
     * @param label The label to use on the dialog for this parameter
     */
    public ReconstructionHoldingParameter(String label)
    {
        super(label);
    }
    /** {@inheritDoc}
     * <p>
     * This looks through all of the parameters and calls <code>set_hologram
     * </code> on all of them.
     */
    @Override
    public void set_hologram(ImageParameter hologram)
    {
        for (DParameter<?> param : get_all_params()) {
            if (param instanceof HologramPluginParameter) {
                ((HologramPluginParameter)param).set_hologram(hologram);
            }
        }
    }
}
