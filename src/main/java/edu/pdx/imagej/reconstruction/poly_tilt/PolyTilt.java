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

/** A {@link edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin} that performs tilt correction on holograms through a
 * polynomial-fit method.  It picks a horizontal and a vertical line that should
 * be flat, and then uses a polynomial fit to cancel out the phase to try to
 * force it to be flat.
 * <p>
 * The flat line determination is customizable by making a {@link
 * PolyTiltPlugin}.  See that page for more info.
 * <p>
 * This class does include many methods for determining polynomials and
 * evaluating them, but they only work once process_original_hologram has
 * started.  At that point the sub plugins can call these methods and use them.
 * They are not really intended to be used for other purposes.
 */
@Plugin(type = ReconstructionPlugin.class)
public class PolyTilt extends HoldingSinglePlugin<PolyTiltPlugin>
                      implements MainReconstructionPlugin
{
    /** Constructor intended for live use of this plugin.
     */
    public PolyTilt()
    {
        super("Line Selection Type", PolyTiltPlugin.class);
    }
    /** Constructor intended for programmtic use of this plugin.
     *
     * @param plugin The {@link PolyTiltPlugin} to use to determine the flat
     *               lines.
     */
    public PolyTilt(PolyTiltPlugin plugin)
    {
        super(plugin);
    }

    /** Get the parameter for this plugin.
     */
    @Override
    public PolyTiltParameter param()
    {
        if (M_param == null) {
            M_param = new PolyTiltParameter(super.param());
        }
        return M_param;
    }
    /** Get the filter from the list of plugins.
     */
    @Override
    public void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins)
    {
        super.read_plugins(plugins);
        for (ReconstructionPlugin plugin : plugins.values()) {
            if (plugin instanceof Filter) M_filter = (Filter)plugin;
        }
    }
    /** Get the polynomial fit.
     *
     * @param field The field to apply the polynomial fit to.
     */
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
    /** Apply the polynomial fit.  You may use this yourself on an arbitrary
     * field if you wish.
     *
     * @param field The field to apply the polynomial fit to.
     * @param t Unused.
     */
    @Override
    public void process_filtered_field(ReconstructionField field, int t)
    {
        if (!M_param.do_poly_tilt()) return;
        field.field().multiply_in_place(M_poly_field);
    }

    /** Get the polynomial fit along a certain set of points.  It will use the
     * phase value and degree that were set previously.
     *
     * @param line The points to fit along
     * @return An array of doubles representing the coefficients of a
     *         polynomial.  The first element is the linear term, the second
     *         element is the quadratic term, etc.  There is no constant term.
     *         If you need a constant term, use {@link
     *         fit_along_including_constant}.
     */
    public double[] fit_along(Iterable<Point> line)
    {
        return remove_constant(fit_along_including_constant(line));
    }
    /** Get the polynomial fit along a certain set of points, including the
     * constant term.  It will use the phase value and degree that were set
     * previously.
     *
     * @param line The points to fit along
     * @return An array of doubles representing the coefficients of a
     *         polynomial.  The first element is the constant term, the second
     *         element is the linear term, etc.
     *
     * @see fit_along
     */
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
    /** Evaluate a polynomial.
     *
     * @param poly An array representing the polynomial to evaluate.  The first
     *             term is the constant term, the second term is the linear
     *             term, etc.
     * @param x The input to the polynomial to evaluate.
     * @return The value of the polynomial at x.
     */
    public static double poly_eval(double[] poly, double x)
    {
        double result = 0;
        for (int i = 0; i < poly.length; ++i) {
            result += poly[i] * Math.pow(x, i);
        }
        return result;
    }
    /** Get the phase used to evaluate the polynomial.  Even though this is a
     * reference to the original data, you should not change it.
     *
     * @return A two-dimensional array of phase values.
     */
    public double[][] get_phase() {return M_phase;}

    private static double[] remove_constant(double[] poly)
    {
        return Arrays.copyOfRange(poly, 1, poly.length);
    }

    private Filter M_filter;
    private PolyTiltParameter M_param;
    double[][] M_phase; // Package private for testing
    private double[][] M_poly_field;
    int M_degree; // Package private for testing
}
