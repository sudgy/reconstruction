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
class ReferenceParameter
      extends ReconstructionHoldingParameter<ReferencePlugin> {
    public ReferenceParameter(DParameter<ReferencePlugin> param)
    {
        super("ReferenceHoloBase");
        M_param = param;
    }
    @Override
    public void initialize()
    {
        addParameter(M_param);
        M_phase = addParameter(new BoolParameter(
                                "Reference Hologram Phase",
                                true));
        M_amplitude = addParameter(new BoolParameter(
                                    "Reference Hologram Amplitude",
                                    false));
        M_useSameRoi = addParameter(new BoolParameter(
                                       "Use same ROI for reference hologram?",
                                       true));
    }
    @Override
    public ReferencePlugin getValue() {return M_param.getValue();}
    @Override
    public void readFromDialog()
    {
        super.readFromDialog();
        setVisibilities();
    }
    @Override
    public void readFromPrefs(Class<?> c, String name)
    {
        super.readFromPrefs(c, name);
        setVisibilities();
    }

    public boolean phase() {return M_phase.getValue();}
    public boolean amplitude() {return M_amplitude.getValue();}
    public boolean useSameRoi() {return M_useSameRoi.getValue();}

    private void setVisibilities()
    {
        if (M_param.getValue() instanceof None) {
            M_phase       .setNewVisibility(false);
            M_amplitude   .setNewVisibility(false);
            M_useSameRoi.setNewVisibility(false);
        }
        else {
            M_phase       .setNewVisibility(true);
            M_amplitude   .setNewVisibility(true);
            if (M_param.getValue().dontUseSameRoi()) {
                M_useSameRoi.setNewVisibility(false);
            }
            else {
                M_useSameRoi.setNewVisibility(true);
            }
        }
    }

    private DParameter<ReferencePlugin> M_param;
    BoolParameter M_phase;
    BoolParameter M_amplitude;
    BoolParameter M_useSameRoi;
}
