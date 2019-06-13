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
import java.util.LinkedHashMap;

import ij.ImagePlus;
import ij.gui.Line;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;

@Plugin(type = PolyTiltPlugin.class, name = "Auto",
        priority = Priority.VERY_HIGH)
public class Auto extends AbstractPolyTiltPlugin {
    @Override
    public void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins)
    {
        for (ReconstructionPlugin plugin : plugins.values()) {
            if (plugin instanceof PolyTilt) M_poly_tilt = (PolyTilt)plugin;
        }
    }
    @Override
    public Iterable<Point> get_h_line()
    {
        if (M_h_line == null) calculate_lines();
        return M_h_line;
    }
    @Override
    public Iterable<Point> get_v_line()
    {
        if (M_v_line == null) calculate_lines();
        return M_v_line;
    }

    private void calculate_lines()
    {
        calculate_h();
        calculate_v();
    }
    private void calculate_h()
    {
        final int width = M_poly_tilt.get_phase().length;
        final int height = M_poly_tilt.get_phase()[0].length;
        final int num_lines = 10;
        final int start = width / 8;
        final int end = start * 7;
        final int vert_space = height / (num_lines + 1);
        Line[] lines = new Line[num_lines];
        for (int y = 0; y < num_lines; ++y) {
            final int this_y = (y + 1) * vert_space;
            lines[y] = new Line(start, this_y, end, this_y);
        }
        M_h_line = best_fit(lines);
    }
    private void calculate_v()
    {
        final int width = M_poly_tilt.get_phase().length;
        final int height = M_poly_tilt.get_phase()[0].length;
        final int num_lines = 10;
        final int start = height / 8;
        final int end = start * 7;
        final int hor_space = width / (num_lines + 1);
        Line[] lines = new Line[num_lines];
        for (int x = 0; x < num_lines; ++x) {
            final int this_x = (x + 1) * hor_space;
            lines[x] = new Line(this_x, start, this_x, end);
        }
        M_v_line = best_fit(lines);
    }
    private Line best_fit(Line[] lines)
    {
        double[][] fits = new double[lines.length][];
        double[] squares = new double[lines.length];
        for (int i = 0; i < lines.length; ++i) {
            fits[i] = M_poly_tilt.fit_along_including_constant(lines[i]);
            int x = 0;
            for (Point p : lines[i]) {
                double poly_val = M_poly_tilt.poly_eval(fits[i], x);
                double val = poly_val - M_poly_tilt.get_phase()[p.x][p.y];
                squares[i] += val*val;
                ++x;
            }
        }
        int least_index = 0;
        double least = Double.MAX_VALUE;
        for (int i = 0; i < lines.length; ++i) {
            if (squares[i] < least) {
                least = squares[i];
                least_index = i;
            }
        }
        return lines[least_index];
    }

    private Line M_h_line;
    private Line M_v_line;
    private PolyTilt M_poly_tilt;
}
