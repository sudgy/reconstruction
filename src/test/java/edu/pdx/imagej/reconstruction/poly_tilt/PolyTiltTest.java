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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.ArrayList;

public class PolyTiltTest {
    @Test public void test_linear_fit()
    {
        PolyTilt test = new PolyTilt();
        test.M_phase = new double[][]{{1, 3, 5}};
        test.M_degree = 1;
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(0, 1));
        points.add(new Point(0, 2));
        double[] poly = test.fit_along(points);
        assertEquals(1, poly.length);
        assertEquals(2, poly[0]);
    }
    @Test public void test_quadratic_fit()
    {
        PolyTilt test = new PolyTilt();
        // The polynomial is 0.1x^2 + 0.5x + 1
        test.M_phase = new double[][]{{1.0, 1.6, 2.4, 3.4, 4.6}};
        test.M_degree = 2;
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0, 1));
        points.add(new Point(0, 2));
        points.add(new Point(0, 3));
        points.add(new Point(0, 4));
        double[] poly = test.fit_along(points);
        assertEquals(2, poly.length);
        assertEquals(0.5, 1e-6, poly[0]);
        assertEquals(0.1, 1e-6, poly[1]);
    }
}
