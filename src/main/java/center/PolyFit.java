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

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class PolyFit {
    public static double[] fit(float[][] phase, Iterable<Point> line)
    {
        WeightedObservedPoints points = new WeightedObservedPoints();
        int x = 0;
        final int M = phase.length;
        final int N = phase[0].length;
        float current_phase = 0;
        final float C_half_phase = (float)Math.PI;
        final float C_phase = C_half_phase * 2;
        float last_value = 0;
        for (Point p : line) {
            int px = p.x;
            int py = p.y;
            if (px < 0 || py < 0 || px >= M || py >= N) continue;
            float value = phase[px][py];
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
        PolynomialCurveFitter fit = PolynomialCurveFitter.create(2);
        double[] almost_result = fit.fit(points.toList());
        double[] result = {almost_result[1], almost_result[2]};
        return result;
    }
}
