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
    @Test public void test_single_init()
    {
        ZParameter param = get_test_param();
        ZParameter.SingleZ test = param.new SingleZ();
        test.initialize();
        List<DistanceUnitValue> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(0.0, list.get(0).as_meter());
    }
    @Test public void test_single_change()
    {
        ZParameter param = get_test_param();
        ZParameter.SingleZ test = param.new SingleZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_double(0).value = 1.5;
        test.read_from_dialog();
        List<DistanceUnitValue> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(1.5, list.get(0).as_meter());
    }

    @Test public void test_list_init()
    {
        ZParameter param = get_test_param();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        assertTrue(test.get_error() != null);
    }
    @Test public void test_list_single()
    {
        ZParameter param = get_test_param();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_string(0).value = "3.5";
        test.read_from_dialog();

        List<DistanceUnitValue> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(3.5, list.get(0).as_meter());
    }
    @Test public void test_list_multi()
    {
        ZParameter param = get_test_param();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_string(0).value = "3.5, \t  3,-1.5,0";
        test.read_from_dialog();

        List<DistanceUnitValue> list = test.get_value();
        assertEquals(4, list.size());
        assertEquals(3.5, list.get(0).as_meter());
        assertEquals(3.0, list.get(1).as_meter());
        assertEquals(-1.5, list.get(2).as_meter());
        assertEquals(0.0, list.get(3).as_meter());
    }
    @Test public void test_list_error()
    {
        ZParameter param = get_test_param();
        ZParameter.ListZ test = param.new ListZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        dialog.get_string(0).value = "";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "3";
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string(0).value = ".";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "a";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "3a";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "3,.";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "3,,3";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
    }

    @Test public void test_range_init()
    {
        ZParameter param = get_test_param();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        List<DistanceUnitValue> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(0.0, list.get(0).as_meter());
    }
    @Test public void test_range_up()
    {
        ZParameter param = get_test_param();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_double(0).value = -3.0;
        dialog.get_double(1).value = 5.0;
        dialog.get_double(2).value = 2.0;
        test.read_from_dialog();

        List<DistanceUnitValue> list = test.get_value();
        assertEquals(5, list.size());
        assertEquals(-3.0, list.get(0).as_meter());
        assertEquals(-1.0, list.get(1).as_meter());
        assertEquals(1.0, list.get(2).as_meter());
        assertEquals(3.0, list.get(3).as_meter());
        assertEquals(5.0, list.get(4).as_meter());
    }
    @Test public void test_range_down()
    {
        ZParameter param = get_test_param();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_double(0).value = 3.0;
        dialog.get_double(1).value = -5.0;
        dialog.get_double(2).value = -4.0;
        test.read_from_dialog();

        List<DistanceUnitValue> list = test.get_value();
        assertEquals(3, list.size());
        assertEquals(3.0, list.get(0).as_meter());
        assertEquals(-1.0, list.get(1).as_meter());
        assertEquals(-5.0, list.get(2).as_meter());
    }
    @Test public void test_range_error_step()
    {
        ZParameter param = get_test_param();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        assertTrue(test.get_error() == null);

        dialog.get_double(2).value = 0.0;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
        dialog.get_double(2).value = 1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_range_error_sign()
    {
        ZParameter param = get_test_param();
        ZParameter.RangeZ test = param.new RangeZ();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        // Test that nothing weird happens when begin == end
        dialog.get_double(2).value = -1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        // Test that it's okay to go up
        dialog.get_double(1).value = 1.0;
        dialog.get_double(2).value = 1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        // Test that going down with a positive step is bad
        dialog.get_double(1).value = -1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
        dialog.get_double(1).value = 1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        // Test that going up with a negative step is bad
        dialog.get_double(2).value = -1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        // Test that going down with a negative step is okay
        dialog.get_double(1).value = -1.0;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
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
        @Override public void set_wavelength(String val) {}
        @Override public void set_image(String val) {}
        @Override public void set_z(String val) {}
    }
    private Context M_context = new Context(TestUnitService.class);
    private ZParameter get_test_param()
    {
        ZParameter result = new ZParameter();
        M_context.inject(result);
        return result;
    }
}
