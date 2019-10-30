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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.List;

import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;

import edu.pdx.imagej.dynamic_parameters.TestDialog;

import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.units.UnitService;

public class ZParameterTest {
    @Test public void testSingleInit()
    {
        ZParameter param = getTestParam();
        ZParameter.SingleZ test = param.new SingleZ();
        test.initialize();
        List<DistanceUnitValue> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(0.0, list.get(0).asMeter());
    }
    @Test public void testSingleChange()
    {
        ZParameter param = getTestParam();
        ZParameter.SingleZ test = param.new SingleZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getDouble(0).value = 1.5;
        test.readFromDialog();
        List<DistanceUnitValue> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(1.5, list.get(0).asMeter());
    }

    @Test public void testListInit()
    {
        ZParameter param = getTestParam();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        assertTrue(test.getError() != null);
    }
    @Test public void testListSingle()
    {
        ZParameter param = getTestParam();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getString(0).value = "3.5";
        test.readFromDialog();

        List<DistanceUnitValue> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(3.5, list.get(0).asMeter());
    }
    @Test public void testListMulti()
    {
        ZParameter param = getTestParam();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getString(0).value = "3.5, \t  3,-1.5,0";
        test.readFromDialog();

        List<DistanceUnitValue> list = test.getValue();
        assertEquals(4, list.size());
        assertEquals(3.5, list.get(0).asMeter());
        assertEquals(3.0, list.get(1).asMeter());
        assertEquals(-1.5, list.get(2).asMeter());
        assertEquals(0.0, list.get(3).asMeter());
    }
    @Test public void testListError()
    {
        ZParameter param = getTestParam();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        dialog.getString(0).value = "";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "3";
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getString(0).value = ".";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "a";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "3a";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "3,.";
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getString(0).value = "3,,3";
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }

    @Test public void testRangeInit()
    {
        ZParameter param = getTestParam();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        List<DistanceUnitValue> list = test.getValue();
        assertEquals(1, list.size());
        assertEquals(0.0, list.get(0).asMeter());
    }
    @Test public void testRangeUp()
    {
        ZParameter param = getTestParam();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getDouble(0).value = -3.0;
        dialog.getDouble(1).value = 5.0;
        dialog.getDouble(2).value = 2.0;
        test.readFromDialog();

        List<DistanceUnitValue> list = test.getValue();
        assertEquals(5, list.size());
        assertEquals(-3.0, list.get(0).asMeter());
        assertEquals(-1.0, list.get(1).asMeter());
        assertEquals(1.0, list.get(2).asMeter());
        assertEquals(3.0, list.get(3).asMeter());
        assertEquals(5.0, list.get(4).asMeter());
    }
    @Test public void testRangeDown()
    {
        ZParameter param = getTestParam();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        dialog.getDouble(0).value = 3.0;
        dialog.getDouble(1).value = -5.0;
        dialog.getDouble(2).value = -4.0;
        test.readFromDialog();

        List<DistanceUnitValue> list = test.getValue();
        assertEquals(3, list.size());
        assertEquals(3.0, list.get(0).asMeter());
        assertEquals(-1.0, list.get(1).asMeter());
        assertEquals(-5.0, list.get(2).asMeter());
    }
    @Test public void testRangeErrorStep()
    {
        ZParameter param = getTestParam();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        assertTrue(test.getError() == null);

        dialog.getDouble(2).value = 0.0;
        test.readFromDialog();
        assertTrue(test.getError() != null);
        dialog.getDouble(2).value = 1.0;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    @Test public void testRangeErrorSign()
    {
        ZParameter param = getTestParam();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        // Test that nothing weird happens when begin == end
        dialog.getDouble(2).value = -1.0;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        // Test that it's okay to go up
        dialog.getDouble(1).value = 1.0;
        dialog.getDouble(2).value = 1.0;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        // Test that going down with a positive step is bad
        dialog.getDouble(1).value = -1.0;
        test.readFromDialog();
        assertTrue(test.getError() != null);
        dialog.getDouble(1).value = 1.0;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        // Test that going up with a negative step is bad
        dialog.getDouble(2).value = -1.0;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        // Test that going down with a negative step is okay
        dialog.getDouble(1).value = -1.0;
        test.readFromDialog();
        assertTrue(test.getError() == null);
    }
    // Override the default unit service so that we can always get the same
    // units
    @Plugin(type = UnitService.class, priority = Priority.LOW)
    public static class TestUnitService extends AbstractService
                                        implements UnitService {
        @Override public DistanceUnits wavelength()
            {return DistanceUnits.Meter;}
        @Override public DistanceUnits image()
            {return DistanceUnits.Meter;}
        @Override public DistanceUnits z()
            {return DistanceUnits.Meter;}
        @Override public void setWavelength(String val) {}
        @Override public void setImage(String val) {}
        @Override public void setZ(String val) {}
    }
    private Context M_context = new Context(TestUnitService.class);
    private ZParameter getTestParam()
    {
        ZParameter result = new ZParameter();
        M_context.inject(result);
        return result;
    }
}
