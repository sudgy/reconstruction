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

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionHoldingParameter;

/** Parameter that gets options for {@link Reference}.  It is just used by that
 * class.  This extends {@link
 * edu.pdx.imagej.reconstruction.plugin.ReconstructionHoldingParameter
 * ReconstructionHoldingParameter}, although it holds more than just the plugin.
 * Thus, {@link get_value} isn't really necessary other than to make the class
 * concrete.
 */
@Plugin(type = DParameter.class)
public class ReferenceParameter
             extends ReconstructionHoldingParameter<ReferencePlugin> {
    /** Create a parameter that will use the {@link ReferencePlugin} <code>param
     * </code> gets.
     *
     * @param param A dynamic parameter whose plugin will be the one used in
     *              {@link Reference}.
     */
    public ReferenceParameter(DParameter<ReferencePlugin> param)
    {
        super("ReferenceHoloBase");
        M_param = param;
    }
    /** Initializes all of the sub parameters.
     */
    @Override
    public void initialize()
    {
        add_premade_parameter(M_param);
        M_phase = add_parameter(BoolParameter.class,
                                "Reference Hologram Phase",
                                true);
        M_amplitude = add_parameter(BoolParameter.class,
                                    "Reference Hologram Amplitude",
                                    false);
        M_use_same_roi = add_parameter(BoolParameter.class,
                                       "Use same ROI for reference hologram?",
                                       true);
    }
    /** Get the {@link ReferencePlugin} held by this parameter.
     *
     * @return The {@link ReferencePlugin} that was selected.
     */
    @Override
    public ReferencePlugin get_value() {return M_param.get_value();}
    @Override
    public void read_from_dialog()
    {
        super.read_from_dialog();
        set_visibilities();
    }
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        super.read_from_prefs(c, name);
        set_visibilities();
    }

    /** Whether or not to use the reference hologram to cancel phase noise.
     * @return Whether or not to use the reference hologram to cancel phase
     *         noise.
     */
    public boolean phase() {return M_phase.get_value();}
    /** Whether or not to use the reference hologram to cancel amplitude noise.
     * @return Whether or not to use the reference hologram to cancel amplitude
     *         noise.
     */
    public boolean amplitude() {return M_amplitude.get_value();}
    /** Whether or not the reference hologram should be filtered with the same
     * filter as everything else.
     * @return Whether or not the reference hologram should be filtered with the
     *         same filter as everything else.
     */
    public boolean use_same_roi() {return M_use_same_roi.get_value();}

    private void set_visibilities()
    {
        if (M_param.get_value() instanceof None) {
            M_phase       .set_new_visibility(false);
            M_amplitude   .set_new_visibility(false);
            M_use_same_roi.set_new_visibility(false);
        }
        else {
            M_phase       .set_new_visibility(true);
            M_amplitude   .set_new_visibility(true);
            if (M_param.get_value().dont_use_same_roi()) {
                M_use_same_roi.set_new_visibility(false);
            }
            else {
                M_use_same_roi.set_new_visibility(true);
            }
        }
    }

    private DParameter<ReferencePlugin> M_param;
    BoolParameter M_phase;
    BoolParameter M_amplitude;
    BoolParameter M_use_same_roi;
}
