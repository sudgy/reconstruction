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
public class PolyTiltParameter
             extends ReconstructionHoldingParameter<PolyTiltPlugin>
{
    public PolyTiltParameter(DParameter<PolyTiltPlugin> param)
    {
        super("PolyTiltBase");
        M_param = param;
    }
    @Override
    public void initialize()
    {
        M_do = add_parameter(BoolParameter.class, "Polynomial Tilt Correction",
                             false);
        M_degree = add_parameter(IntParameter.class, 1, "Polynomial Degree");
        M_degree.set_bounds(1, Integer.MAX_VALUE);
        add_premade_parameter(M_param);
    }
    @Override
    public PolyTiltPlugin get_value()
    {
        return M_param.get_value();
    }
    public boolean do_poly_tilt() {return M_do.get_value();}
    public int degree() {return M_degree.get_value();}
    public Iterable<Point> h_line() {return M_param.get_value().get_h_line();}
    public Iterable<Point> v_line() {return M_param.get_value().get_v_line();}
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

    private void set_visibilities()
    {
        if (M_do.get_value() == false) {
            M_degree.set_new_visibility(false);
            M_param .set_new_visibility(false);
        }
        else {
            M_degree.set_new_visibility(true);
            M_param .set_new_visibility(true);
        }
    }

    private BoolParameter M_do;
    private IntParameter M_degree;
    private DParameter<PolyTiltPlugin> M_param;
}
