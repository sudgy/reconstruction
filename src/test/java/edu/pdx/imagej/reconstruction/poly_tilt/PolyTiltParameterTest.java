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
    @Test public void testDo()
    {
        PolyTiltParameter param = new PolyTiltParameter(new TestParameter());
        param.initialize();
        TestDialog dialog = new TestDialog();
        param.addToDialog(dialog);
        assertEquals(param.doPolyTilt(), false, "\"do\" should default to "
            + "false.");
        dialog.getBoolean(0).value = true;
        param.readFromDialog();
        assertEquals(param.doPolyTilt(), true, "\"do\" should be changed "
            + "correctly.");
    }
    @Test public void testDegree()
    {
        PolyTiltParameter param = new PolyTiltParameter(new TestParameter());
        param.initialize();
        TestDialog dialog = new TestDialog();
        param.addToDialog(dialog);
        assertEquals(param.degree(), 1, "degree should be one by default.");
        dialog.getInteger(0).value = 2;
        param.readFromDialog();
        assertEquals(param.degree(), 2, "degree should be set correctly.");

        dialog.getInteger(0).value = 0;
        param.readFromDialog();
        assertTrue(param.getError() != null, "setting the polynomial degree to"
            + " zero should cause an error.");
        dialog.getInteger(0).value = 1;
        param.readFromDialog();
        assertTrue(param.getError() == null, "setting the polynomial degree to"
            + " one should remove the error.");
    }
    @Test public void testLines()
    {
        PolyTiltParameter param = new PolyTiltParameter(new TestParameter());
        for (Point p : param.hLine()) assertEquals(p.y, 0);
        for (Point p : param.vLine()) assertEquals(p.x, 0);
    }
    public class TestPlugin extends AbstractPolyTiltPlugin {
        @Override public Line getHLine() {return new Line(0, 0, 2, 0);}
        @Override public Line getVLine() {return new Line(0, 0, 0, 2);}
        @Override public TestPlugin duplicate() {return new TestPlugin();}
    }
    public class TestParameter extends AbstractDParameter<PolyTiltPlugin>
    {
        public TestParameter() {super("Test");}
        @Override public PolyTiltPlugin getValue() {return new TestPlugin();}
        @Override public void addToDialog(DPDialog d) {}
        @Override public void readFromDialog() {}
        @Override public void saveToPrefs(Class<?> c, String s) {}
        @Override public void readFromPrefs(Class<?> c, String s) {}
    }
}
