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

package edu.pdx.imagej.reconstruction.poly_tilt;

import java.awt.Point;

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionHoldingParameter;

@Plugin(type = DParameter.class)
class PolyTiltParameter extends ReconstructionHoldingParameter<PolyTiltPlugin>
{
    public PolyTiltParameter(DParameter<PolyTiltPlugin> param)
    {
        super("PolyTiltBase");
        M_param = param;
    }
    @Override
    public void initialize()
    {
        M_do = addParameter(new BoolParameter("Polynomial Tilt Correction",
                             false));
        M_degree = addParameter(new IntParameter(1, "Polynomial Degree"));
        M_degree.setBounds(1, Integer.MAX_VALUE);
        addParameter(M_param);
    }
    @Override
    public PolyTiltPlugin getValue()
    {
        return M_param.getValue();
    }
    public boolean doPolyTilt() {return M_do.getValue();}
    public int degree() {return M_degree.getValue();}
    public Iterable<Point> hLine() {return M_param.getValue().getHLine();}
    public Iterable<Point> vLine() {return M_param.getValue().getVLine();}
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

    private void setVisibilities()
    {
        if (M_do.getValue() == false) {
            M_degree.setNewVisibility(false);
            M_param .setNewVisibility(false);
        }
        else {
            M_degree.setNewVisibility(true);
            M_param .setNewVisibility(true);
        }
    }

    private BoolParameter M_do;
    private IntParameter M_degree;
    private DParameter<PolyTiltPlugin> M_param;
}
