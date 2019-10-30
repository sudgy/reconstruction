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

package edu.pdx.imagej.reconstruction.gray_phase;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.BoolParameter;

import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.ReconstructionField;

@Plugin(
    type = ReconstructionPlugin.class,
    name = "Gray Phase",
    priority = Priority.LOW
)
public class GrayPhase extends AbstractReconstructionPlugin
                       implements MainReconstructionPlugin {
    public GrayPhase()
    {
        M_live = true;
    }
    public GrayPhase(boolean doGray)
    {
        M_doGray = doGray;
    }
    @Override public GrayPhase duplicate()
    {
        if (M_live) M_doGray = M_param.getValue();
        return new GrayPhase(M_doGray);
    }
    @Override public BoolParameter param() {return M_param;}

    @Override
    public void processPropagatedField(
        ReconstructionField field,
        int t,
        DistanceUnitValue z
    )
    {
        if (M_live) M_doGray = M_param.getValue();
        if (!M_doGray) return;
    }

    private boolean M_live = false;
    private boolean M_doGray = false;
    private BoolParameter M_param
        = new BoolParameter("Make Phase Background Gray", true);
}
