/* Copyright (C) 2018 Portland State University
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

package edu.pdx.imagej.reconstruction.parameter;

import ij.gui.GenericDialog;
import ij.gui.Line;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;

import edu.pdx.imagej.reconstruction.CenterOptions;

public class CenterParameter extends HoldingParameter<CenterOptions> {
    public CenterParameter(ImageParameter holo)
    {
        M_holo = holo;
    }
    @Override
    public void initialize()
    {
        M_do = add_parameter(BoolParameter.class, "Automatic correction of tilt", false);
        M_degree = add_parameter(IntParameter.class, 1, "Polynomial Degree");
        M_choice = add_parameter(ChoiceParameter.class, "Line Selection Type", S_choices, "Auto");
        M_param_middle = new MiddleCenter();
        M_param_manual = add_parameter(ManualCenter.class, this);
        M_param_auto = new AutoCenter();
        set_visibilities();
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        super.read_from_dialog(gd);
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
        M_param_manual.set_new_visibility(false);
        if (M_do.get_value()) {
            M_degree.set_new_visibility(true);
            M_choice.set_new_visibility(true);
            if (Choices.valueOf(M_choice.get_value()) == Choices.Manual) {
                M_param_manual.set_new_visibility(true);
            }
        }
        else {
            M_degree.set_new_visibility(false);
            M_choice.set_new_visibility(false);
        }
    }
    public CenterOptions get_value()
    {
        DParameter<CenterOptions> current = current_param();
        if (current != null) {
            CenterOptions semi = current.get_value();
            return new CenterOptions(true, M_degree.get_value(), semi.h_line(), semi.v_line());
        }
        else {
            return new CenterOptions(false, 0, null, null);
        }
    }

    private DParameter<CenterOptions> current_param()
    {
        if (M_do.get_value()) {
            switch (Choices.valueOf(M_choice.get_value())) {
                case Middle: return M_param_middle;
                case Manual: return M_param_manual;
                case Auto: return M_param_auto;
            }
        }
        return null;
    }

    private enum Choices {
        Middle, Manual, Auto;
    }
    private static String[] S_choices = {Choices.Middle.toString(), Choices.Manual.toString(), Choices.Auto.toString()};

    private BoolParameter M_do;
    private IntParameter M_degree;
    private ChoiceParameter M_choice;

    private MiddleCenter M_param_middle;
    private ManualCenter M_param_manual;
    private AutoCenter M_param_auto;

    private ImageParameter M_holo;




    public class MiddleCenter extends AbstractDParameter<CenterOptions> {
        @Override public CenterOptions get_value()
        {
            int[] dimensions = M_holo.get_value().getDimensions();
            int width = dimensions[0];
            int height = dimensions[1];
            Line h_line = new Line(width / 8, height / 2, width * 7 / 8, height / 2);
            Line v_line = new Line(width / 2, height / 8, width / 2, height * 7 / 8);
            return new CenterOptions(true, 2, h_line, v_line);
        }
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
    }



    public class ManualCenter extends HoldingParameter<CenterOptions> {
        @Override
        public void initialize()
        {
            set_dimensions1();
            M_h_val = add_parameter(IntParameter.class, M_height / 2, "Pixel value for horizontal line");
            M_h_start = add_parameter(IntParameter.class, 0, "Horizontal line start");
            M_h_end = add_parameter(IntParameter.class, M_width, "Horizontal line start");
            M_v_val = add_parameter(IntParameter.class, M_width / 2,  "Pixel value for vertical line");
            M_v_start = add_parameter(IntParameter.class, 0, "Vertical line start");
            M_v_end = add_parameter(IntParameter.class, M_height, "Vertical line start");
            set_dimensions2();
        }
        @Override
        public CenterOptions get_value()
        {
            int h_val = M_h_val.get_value();
            int h1 = M_h_start.get_value();
            int h2 = M_h_end.get_value();
            int h_start = Math.min(h1, h2);
            int h_end = Math.max(h1, h2);
            int v_val = M_v_val.get_value();
            int v1 = M_v_start.get_value();
            int v2 = M_v_end.get_value();
            int v_start = Math.min(v1, v2);
            int v_end = Math.max(v1, v2);
            Line h_line = new Line(h_start, h_val, h_end, h_val);
            Line v_line = new Line(v_val, v_start, v_val, v_end);
            return new CenterOptions(true, 2, h_line, v_line);
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            set_dimensions();
            super.read_from_dialog(gd);
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            set_dimensions();
            super.read_from_prefs(c, name);
        }

        private void set_dimensions()
        {
            set_dimensions1();
            set_dimensions2();
        }
        private void set_dimensions1()
        {
            int[] dimensions = M_holo.get_value().getDimensions();
            M_width = dimensions[0];
            M_height = dimensions[1];
        }
        private void set_dimensions2()
        {
            M_h_val.set_bounds(0, M_height);
            M_h_start.set_bounds(0, M_width);
            M_h_end.set_bounds(0, M_width);
            M_v_val.set_bounds(0, M_width);
            M_v_start.set_bounds(0, M_height);
            M_v_end.set_bounds(0, M_height);
        }
        private IntParameter M_h_val;
        private IntParameter M_h_start;
        private IntParameter M_h_end;
        private IntParameter M_v_val;
        private IntParameter M_v_start;
        private IntParameter M_v_end;
        private int          M_width;
        private int          M_height;
    }



    public static class AutoCenter extends AbstractDParameter<CenterOptions> {
        @Override public CenterOptions get_value() {return new CenterOptions(true, 1, null, null);}
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
    }
}
