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

@Plugin(type = DParameter.class)
public class ReferenceParameter
             extends ReconstructionHoldingParameter<ReferencePlugin> {
    public ReferenceParameter(DParameter<ReferencePlugin> param)
    {
        super("ReferenceHoloBase");
        M_param = param;
    }
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

    public boolean phase()        {return M_phase       .get_value();}
    public boolean amplitude()    {return M_amplitude   .get_value();}
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
            M_use_same_roi.set_new_visibility(true);
        }
    }

    private DParameter<ReferencePlugin> M_param;
    private BoolParameter M_phase;
    private BoolParameter M_amplitude;
    private BoolParameter M_use_same_roi;
}
