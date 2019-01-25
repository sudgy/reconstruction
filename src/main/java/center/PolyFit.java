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

package edu.pdx.imagej.reconstruction;

import java.awt.Point;
import java.util.List;
import java.util.Arrays;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import ij.gui.Line;

public class PolyFit {
    public PolyFit(float[][] phase)
    {
        M_phase = phase;
        M_width = phase.length;
        M_height = phase[0].length;
    }
    public double[] fit_whole_poly(Iterable<Point> line, int degree)
    {
        WeightedObservedPoints points = new WeightedObservedPoints();
        int x = 0;
        float current_phase = 0;
        float last_value = 0;
        for (Point p : line) {
            int px = p.x;
            int py = p.y;
            if (px < 0 || py < 0 || px >= M_width || py >= M_height) continue;
            float value = M_phase[px][py];
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
        M_last_points = points.toList();
        PolynomialCurveFitter fit = PolynomialCurveFitter.create(degree);
        return fit.fit(M_last_points);
    }
    public double[] fit(Iterable<Point> line, int degree)
    {
        return remove_first(fit_whole_poly(line, degree));
    }
    public double[] auto_h(int num_lines, int degree)
    {
        final int start = M_width / 8;
        final int end = start * 7;
        final int vert_space = M_height / (num_lines + 1);
        Line[] lines = new Line[num_lines];
        for (int y = 0; y < num_lines; ++y) {
            final int this_y = (y + 1) * vert_space;
            lines[y] = new Line(start, this_y, end, this_y);
        }
        return best_fit(lines, degree);
    }
    public double[] auto_v(int num_lines, int degree)
    {
        final int start = M_height / 8;
        final int end = start * 7;
        final int hor_space = M_width / (num_lines + 1);
        Line[] lines = new Line[num_lines];
        for (int x = 0; x < num_lines; ++x) {
            final int this_x = (x + 1) * hor_space;
            lines[x] = new Line(this_x, start, this_x, end);
        }
        return best_fit(lines, degree);
    }
    public double[] best_fit(Iterable<Point>[] lines, int degree)
    {
        double[][] fits = new double[lines.length][];
        double[] squares = new double[lines.length];
        for (int i = 0; i < lines.length; ++i) {
            Iterable<Point> line = lines[i];
            double[] poly = fit_whole_poly(line, degree);
            fits[i] = poly;
            int x = 0;
            for (WeightedObservedPoint p : M_last_points) {
                double poly_val = poly_eval(poly, x);
                squares[i] += Math.pow(poly_val - p.getY(), 2);
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
        return remove_first(fits[least_index]);
    }

    public static double poly_eval(double[] poly, double x)
    {
        double result = 0;
        for (int i = 0; i < poly.length; ++i) {
            result += poly[i] * Math.pow(x, i);
        }
        return result;
    }
    public static double[] remove_first(double[] input)
    {
        return Arrays.copyOfRange(input, 1, input.length);
    }

    private final float[][] M_phase;
    private final int M_width;
    private final int M_height;
    private final static float C_half_phase = (float)Math.PI;
    private final static float C_phase = C_half_phase * 2;
    private List<WeightedObservedPoint> M_last_points;
}
