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

import ij.gui.Line;

import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.DPDialog;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

public class PolyTiltParameterTest {
    @Test public void test_do()
    {
        PolyTiltParameter param = new PolyTiltParameter(new TestParameter());
        param.initialize();
        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        assertEquals(param.do_poly_tilt(), false, "\"do\" should default to "
            + "false.");
        dialog.get_boolean(0).value = true;
        param.read_from_dialog();
        assertEquals(param.do_poly_tilt(), true, "\"do\" should be changed "
            + "correctly.");
    }
    @Test public void test_degree()
    {
        PolyTiltParameter param = new PolyTiltParameter(new TestParameter());
        param.initialize();
        TestDialog dialog = new TestDialog();
        param.add_to_dialog(dialog);
        assertEquals(param.degree(), 1, "degree should be one by default.");
        dialog.get_integer(0).value = 2;
        param.read_from_dialog();
        assertEquals(param.degree(), 2, "degree should be set correctly.");

        dialog.get_integer(0).value = 0;
        param.read_from_dialog();
        assertTrue(param.get_error() != null, "setting the polynomial degree to"
            + " zero should cause an error.");
        dialog.get_integer(0).value = 1;
        param.read_from_dialog();
        assertTrue(param.get_error() == null, "setting the polynomial degree to"
            + " one should remove the error.");
    }
    @Test public void test_lines()
    {
        PolyTiltParameter param = new PolyTiltParameter(new TestParameter());
        for (Point p : param.h_line()) assertEquals(p.y, 0);
        for (Point p : param.v_line()) assertEquals(p.x, 0);
    }
    public class TestPlugin extends AbstractPolyTiltPlugin {
        @Override public Line get_h_line() {return new Line(0, 0, 2, 0);}
        @Override public Line get_v_line() {return new Line(0, 0, 0, 2);}
    }
    public class TestParameter extends AbstractDParameter<PolyTiltPlugin>
    {
        public TestParameter() {super("Test");}
        @Override public PolyTiltPlugin get_value() {return new TestPlugin();}
        @Override public void add_to_dialog(DPDialog d) {}
        @Override public void read_from_dialog() {}
        @Override public void save_to_prefs(Class<?> c, String s) {}
        @Override public void read_from_prefs(Class<?> c, String s) {}
    }
}
