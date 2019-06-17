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
import java.util.LinkedHashMap;

import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;

public class AutoTest {
    @Test public void test_small()
    {
        Auto test = new Auto();
        test.read_plugins(S_plugins);
        S_pt.M_phase = new double[2][2];
        S_pt.M_degree = 1;
        Iterable<Point> line = test.get_h_line();
        int i = 0;
        for (Point p : line) ++i;
        assertTrue(i != 0, "Auto should never create no line.");

        test = new Auto();
        test.read_plugins(S_plugins);
        S_pt.M_phase = new double[6][6];
        line = test.get_h_line();
        i = 0;
        for (Point p : line) ++i;
        assertEquals(4, i);
    }
    @Test public void test_perfect_linear()
    {
        Auto test = new Auto();
        test.read_plugins(S_plugins);
        S_pt.M_phase = new double[][]{
            {0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
            {0, 1, 2, 3, 0},
            {0, 1, 0, 1, 0},
            {0, 1, 0, 1, 0},
        };
        S_pt.M_degree = 1;
        Iterable<Point> line = test.get_v_line();
        int i = 1;
        for (Point p : line) {
            assertEquals(i++, p.y);
            assertEquals(2, p.x);
        }
        assertEquals(4, i);
    }
    @Test public void test_perfect_quadratic()
    {
        Auto test = new Auto();
        test.read_plugins(S_plugins);
        S_pt.M_phase = new double[][]{
            {0, 1  , 2  , 0  , 1  , 2},
            {0, 1  , 2  , 0  , 1  , 2},
            {0, 1  , 2  , 0  , 1  , 2},
            {0, 1  , 2  , 0  , 1  , 2},
            {0, 0.1, 0.4, 0.9, 1.6, 2},
            {0, 1  , 2  , 0  , 1  , 2},
        };
        S_pt.M_degree = 2;
        Iterable<Point> line = test.get_v_line();
        int i = 1;
        for (Point p : line) {
            assertEquals(i++, p.y);
            assertEquals(4, p.x);
        }
        assertEquals(5, i);
    }
    @Test public void test_nonperfect()
    {
        Auto test = new Auto();
        test.read_plugins(S_plugins);
        S_pt.M_phase = new double[][]{
            {0, 1  , 2  , 0, 1  , 2},
            {0, 1  , 2  , 0, 1  , 2},
            {0, 1  , 2  , 0, 1  , 2},
            {0, 1  , 2  , 0, 1  , 2},
            {0, 0.1, 0.3, 1, 1.5, 2},
            {0, 1  , 2  , 0, 1  , 2},
        };
        S_pt.M_degree = 2;
        Iterable<Point> line = test.get_v_line();
        int i = 1;
        for (Point p : line) {
            assertEquals(i++, p.y);
            assertEquals(4, p.x);
        }
        assertEquals(5, i);
    }
    @Test public void test_phase()
    {
        Auto test = new Auto();
        test.read_plugins(S_plugins);
        S_pt.M_phase = new double[][]{
            {0, 1  , 2  , 0  ,  1  , 2},
            {0, 1  , 2  , 0  ,  1  , 2},
            {0, 1  , 2  , 0  ,  1  , 2},
            {0, 1  , 2  , 0  ,  1  , 2},
            {0, 0.2, 0.8, 1.8, -3.1, 2},
            {0, 1  , 2  , 0  ,  1  , 2},
        };
        S_pt.M_degree = 2;
        Iterable<Point> line = test.get_v_line();
        int i = 1;
        for (Point p : line) {
            assertEquals(i++, p.y);
            assertEquals(4, p.x);
        }
        assertEquals(5, i);
    }
    private static PolyTilt S_pt = new PolyTilt();
    private static LinkedHashMap<Class<?>, ReconstructionPlugin> S_plugins
        = get_plugins();
    private static LinkedHashMap<Class<?>, ReconstructionPlugin> get_plugins()
    {
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins
            = new LinkedHashMap<>();
        plugins.put(PolyTilt.class, S_pt);
        return plugins;
    }
}
