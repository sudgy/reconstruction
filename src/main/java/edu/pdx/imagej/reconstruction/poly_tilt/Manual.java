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

import ij.ImagePlus;
import ij.gui.Line;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.reconstruction.plugin.HologramPluginParameter;

@Plugin(type = PolyTiltPlugin.class, name = "Manual",
        priority = Priority.HIGH * 0.999)
public class Manual extends AbstractPolyTiltPlugin {
    public Manual()
    {
        M_param = new ManualParameter();
    }
    @Override
    public Iterable<Point> get_h_line()
    {
        if (M_h_line == null) {
            Line[] lines = M_param.get_value();
            M_h_line = lines[0];
            M_v_line = lines[1];
        }
        return M_h_line;
    }
    @Override
    public Iterable<Point> get_v_line()
    {
        if (M_v_line == null) {
            Line[] lines = M_param.get_value();
            M_h_line = lines[0];
            M_v_line = lines[1];
        }
        return M_v_line;
    }
    @Override public ManualParameter param()
    {
        return M_param;
    }

    private Line M_h_line;
    private Line M_v_line;
    private ManualParameter M_param;

    public class ManualParameter extends HoldingParameter<Line[]>
                                 implements HologramPluginParameter
    {
        ManualParameter() {super("ManualCenterParams");}
        @Override
        public void set_hologram(ImageParameter hologram)
        {
            M_holo = hologram;
            if (M_h_val == null) {
                set_dimensions1();
                M_h_val = add_parameter(IntParameter.class, M_height / 2, "Pixel_value_for_horizontal_line");
                M_h_start = add_parameter(IntParameter.class, 0, "Horizontal_line_start");
                M_h_end = add_parameter(IntParameter.class, M_width, "Horizontal_line_end");
                M_v_val = add_parameter(IntParameter.class, M_width / 2,  "Pixel_value_for_vertical_line");
                M_v_start = add_parameter(IntParameter.class, 0, "Vertical_line_start");
                M_v_end = add_parameter(IntParameter.class, M_height, "Vertical_line_end");
                set_dimensions2();
            }
        }
        @Override
        public void read_from_dialog()
        {
            if (M_holo != null) {
                int[] dimensions = M_holo.get_value().getDimensions();
                if (M_width != dimensions[0] || M_height != dimensions[1]) {
                    set_dimensions();
                }
            }
            super.read_from_dialog();
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            if (M_holo != null) set_dimensions();
            super.read_from_prefs(c, name);
        }
        @Override
        public Line[] get_value()
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
            Line[] result = new Line[2];
            result[0] = new Line(h_start, h_val, h_end, h_val);
            result[1] = new Line(v_val, v_start, v_val, v_end);
            return result;
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

        private ImageParameter M_holo;
    }
}
