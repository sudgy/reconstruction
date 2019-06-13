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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.HoldingSinglePlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.filter.Filter;

@Plugin(type = ReconstructionPlugin.class,
        priority = Priority.EXTREMELY_LOW * 1.1)
public class PolyTilt extends HoldingSinglePlugin<PolyTiltPlugin>
                      implements MainReconstructionPlugin
{
    public PolyTilt()
    {
        super("Line Selection Type", PolyTiltPlugin.class);
    }
    public PolyTilt(PolyTiltPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public PolyTiltParameter param()
    {
        if (M_param == null) {
            M_param = new PolyTiltParameter(super.param());
        }
        return M_param;
    }
    @Override
    public void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins)
    {
        super.read_plugins(plugins);
        for (ReconstructionPlugin plugin : plugins.values()) {
            if (plugin instanceof Filter) M_filter = (Filter)plugin;
        }
    }
    @Override
    public void process_original_hologram(ConstReconstructionField field)
    {
        super.process_original_hologram(field);
        if (!M_param.do_poly_tilt()) return;
        ReconstructionField filtered_field = field.copy();
        M_filter.filter_field(filtered_field);
        M_phase = filtered_field.field().get_arg();
        M_degree = M_param.degree();
        double[] h_poly = fit_along(M_param.h_line());
        double[] v_poly = fit_along(M_param.v_line());

        final int width = M_phase.length;
        final int height = M_phase[0].length;
        M_poly_field = new double[width][height*2];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                double val = 0;
                for (int i = 0; i < h_poly.length; ++i) {
                    val += Math.pow(h_poly[i] * x, i + 1);
                    val += Math.pow(v_poly[i] * y, i + 1);
                }
                val *= -1;
                M_poly_field[x][2*y  ] = (float)Math.cos(val);
                M_poly_field[x][2*y+1] = (float)Math.sin(val);
            }
        }
    }
    @Override
    public void process_filtered_field(ReconstructionField field, int t)
    {
        if (!M_param.do_poly_tilt()) return;
        field.field().multiply_in_place(M_poly_field);
    }

    public double[] fit_along(Iterable<Point> line)
    {
        return remove_constant(fit_along_including_constant(line));
    }
    public double[] fit_along_including_constant(Iterable<Point> line)
    {
        WeightedObservedPoints points = new WeightedObservedPoints();
        int x = 0;
        double current_phase = 0;
        double last_value = 0;
        final double C_phase = 2*Math.PI;
        final double C_half_phase = C_phase / 2;
        for (Point p : line) {
            int px = p.x;
            int py = p.y;
            if (px < 0 || py < 0 || px >= M_phase.length
                                 || py >= M_phase[0].length) continue;
            double value = M_phase[px][py];
            if (value > last_value + C_half_phase) {
                current_phase -= C_phase;
            }
            else if (value < last_value - C_half_phase) {
                current_phase += C_phase;
            }
            last_value = value;
            value += current_phase;
            points.add(x, value);
            ++x;
        }
        List<WeightedObservedPoint> points_list = points.toList();
        PolynomialCurveFitter fit
            = PolynomialCurveFitter.create(M_degree);
        return fit.fit(points_list);
    }
    public static double poly_eval(double[] poly, double x)
    {
        double result = 0;
        for (int i = 0; i < poly.length; ++i) {
            result += poly[i] * Math.pow(x, i);
        }
        return result;
    }
    public double[][] get_phase() {return M_phase;}

    private static double[] remove_constant(double[] poly)
    {
        return Arrays.copyOfRange(poly, 1, poly.length);
    }

    private Filter M_filter;
    private PolyTiltParameter M_param;
    private double[][] M_phase;
    private double[][] M_poly_field;
    private int M_degree;
}
