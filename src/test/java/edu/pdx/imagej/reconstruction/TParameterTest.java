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

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

public class TParameterTest {
    @Test public void test_single_init()
    {
        TParameter param = get_test_param(1);
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void test_single_change()
    {
        TParameter param = get_test_param(1);
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_integer(0).value = 2;
        test.read_from_dialog();
        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).intValue());
    }
    @Test public void test_single_error()
    {
        TParameter param = get_test_param(2);
        param.update_max_t();
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        assertTrue(test.get_error() == null);

        dialog.get_integer(0).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 2;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_single_error_change()
    {
        ImageParameter images = get_image_param(2, 3);
        TParameter param = get_test_param(images);
        param.update_max_t();
        TParameter.SingleT test = param.new SingleT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        images.add_to_dialog(dialog);
        test.add_to_dialog(dialog);

        dialog.get_integer(0).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string_index(0).value = 1;
        images.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string_index(0).value = 0;
        images.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
    }

    @Test public void test_current_init()
    {
        TParameter param = get_test_param(2);
        TParameter.CurrentT test = param.new CurrentT();
        test.initialize();
        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void test_current_change()
    {
        ImageParameter image = get_image_param(2);
        TParameter param = get_test_param(image);
        TParameter.CurrentT test = param.new CurrentT();
        test.initialize();
        image.get_value().setPosition(2);
        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(2, list.get(0).intValue());
    }

    @Test public void test_all()
    {
        ImageParameter image = get_image_param(2, 3);
        TParameter param = get_test_param(image);
        param.update_max_t();
        TParameter.AllT test = param.new AllT();
        test.initialize();

        List<Integer> list = test.get_value();
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());

        TestDialog dialog = new TestDialog();
        image.add_to_dialog(dialog);
        dialog.get_string_index(0).value = 1;
        image.read_from_dialog();
        param.update_max_t();

        list = test.get_value();
        assertEquals(3, list.size());
        assertEquals(1, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(3, list.get(2).intValue());
    }

    @Test public void test_list_init()
    {
        TParameter param = get_test_param(1);
        TParameter.ListT test = param.new ListT();
        test.initialize();
        assertTrue(test.get_error() != null);
    }
    @Test public void test_list_single()
    {
        TParameter param = get_test_param(1);
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_string(0).value = "3";
        test.read_from_dialog();

        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(3, list.get(0).intValue());
    }
    @Test public void test_list_single_bounds()
    {
        TParameter param = get_test_param(2);
        param.update_max_t();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        dialog.get_string(0).value = "1";
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string(0).value = "3";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "2";
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_list_multi()
    {
        TParameter param = get_test_param(1);
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_string(0).value = "3, \t 2,5";
        test.read_from_dialog();

        List<Integer> list = test.get_value();
        assertEquals(3, list.size());
        assertEquals(3, list.get(0).intValue());
        assertEquals(2, list.get(1).intValue());
        assertEquals(5, list.get(2).intValue());
    }
    @Test public void test_list_multi_bounds()
    {
        TParameter param = get_test_param(2);
        param.update_max_t();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        dialog.get_string(0).value = "1, 2";
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string(0).value = "1, 3";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "1, 2";
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_list_change_bounds()
    {
        ImageParameter image = get_image_param(2, 3);
        TParameter param = get_test_param(image);
        param.update_max_t();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        image.add_to_dialog(dialog);
        test.add_to_dialog(dialog);

        dialog.get_string(0).value = "1, 3";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string_index(0).value = 1;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_list_parse()
    {
        TParameter param = get_test_param(1);
        param.update_max_t();
        TParameter.ListT test = param.new ListT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        dialog.get_string(0).value = "";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "1";
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string(0).value = ".";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "a";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "1a";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "1,.";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "1,,1";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string(0).value = "1.5";
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
    }

    @Test public void test_range_init()
    {
        TParameter param = get_test_param();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void test_range_up()
    {
        TParameter param = get_test_param();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_integer(0).value = 3;
        dialog.get_integer(1).value = 7;
        dialog.get_integer(2).value = 2;
        test.read_from_dialog();

        List<Integer> list = test.get_value();
        assertEquals(3, list.size());
        assertEquals(3, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        assertEquals(7, list.get(2).intValue());
    }
    @Test public void test_range_down()
    {
        TParameter param = get_test_param();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_integer(0).value = 8;
        dialog.get_integer(1).value = 2;
        dialog.get_integer(2).value = -3;
        test.read_from_dialog();

        List<Integer> list = test.get_value();
        assertEquals(3, list.size());
        assertEquals(8, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        assertEquals(2, list.get(2).intValue());
    }
    @Test public void test_range_error_step()
    {
        TParameter param = get_test_param(1);
        param.update_max_t();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        assertTrue(test.get_error() == null);

        dialog.get_integer(2).value = 0;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
        dialog.get_integer(2).value = 1;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_range_error_sign()
    {
        TParameter param = get_test_param(2);
        param.update_max_t();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        // Test that nothing weird happens when begin == end
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        // Test that it's okay to go up
        dialog.get_integer(1).value = 2;
        dialog.get_integer(2).value = 1;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        // Test that going down with a positive step is bad
        dialog.get_integer(0).value = 2;
        dialog.get_integer(1).value = 1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
        dialog.get_integer(0).value = 1;
        dialog.get_integer(1).value = 2;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        // Test that going up with a negative step is bad
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        // Test that going down with a negative step is okay
        dialog.get_integer(0).value = 2;
        dialog.get_integer(1).value = 1;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_range_bounds()
    {
        TParameter param = get_test_param(2);
        param.update_max_t();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        dialog.get_integer(1).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 3;
        dialog.get_integer(1).value = 4;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 3;
        dialog.get_integer(1).value = 1;
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 1;
        dialog.get_integer(1).value = 2;
        dialog.get_integer(2).value = 1;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_integer(0).value = 3;
        dialog.get_integer(1).value = 1;
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 4;
        dialog.get_integer(1).value = 3;
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 3;
        dialog.get_integer(1).value = 1;
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 2;
        dialog.get_integer(1).value = 1;
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_range_changing_bound()
    {
        ImageParameter image = get_image_param(2, 3);
        TParameter param = get_test_param(image);
        param.update_max_t();
        TParameter.RangeT test = param.new RangeT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        image.add_to_dialog(dialog);
        test.add_to_dialog(dialog);

        dialog.get_integer(1).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string_index(0).value = 1;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string_index(0).value = 0;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 3;
        dialog.get_integer(1).value = 1;
        dialog.get_integer(2).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string_index(0).value = 1;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string_index(0).value = 0;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
    }

    @Test public void test_continuous_init()
    {
        TParameter param = get_test_param(1);
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();

        List<Integer> list = test.get_value();
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).intValue());
    }
    @Test public void test_continuous_up()
    {
        TParameter param = get_test_param(1);
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_integer(0).value = 2;
        dialog.get_integer(1).value = 5;
        test.read_from_dialog();

        List<Integer> list = test.get_value();
        assertEquals(4, list.size());
        assertEquals(2, list.get(0).intValue());
        assertEquals(3, list.get(1).intValue());
        assertEquals(4, list.get(2).intValue());
        assertEquals(5, list.get(3).intValue());
    }
    @Test public void test_continuous_down()
    {
        TParameter param = get_test_param(1);
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        dialog.get_integer(0).value = 6;
        dialog.get_integer(1).value = 4;
        test.read_from_dialog();

        List<Integer> list = test.get_value();
        assertEquals(3, list.size());
        assertEquals(6, list.get(0).intValue());
        assertEquals(5, list.get(1).intValue());
        assertEquals(4, list.get(2).intValue());
    }
    @Test public void test_continuous_bounds()
    {
        TParameter param = get_test_param(2);
        param.update_max_t();
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        assertTrue(test.get_error() == null);

        dialog.get_integer(0).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(1).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(1).value = 2;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 2;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_integer(1).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 2;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(1).value = 2;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);
    }
    @Test public void test_continuous_bounds_change()
    {
        ImageParameter image = get_image_param(2, 3);
        TParameter param = get_test_param(image);
        param.update_max_t();
        TParameter.ContinuousT test = param.new ContinuousT();
        test.initialize();
        TestDialog dialog = new TestDialog();
        image.add_to_dialog(dialog);
        test.add_to_dialog(dialog);

        dialog.get_integer(0).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string_index(0).value = 1;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string_index(0).value = 0;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 1;
        dialog.get_integer(1).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_string_index(0).value = 1;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_string_index(0).value = 0;
        image.read_from_dialog();
        param.update_max_t();
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
    }

    private ImageParameter get_image_param(Integer... num_slices)
    {
        ImagePlus[] imgs = new ImagePlus[num_slices.length];
        for (int i = 0; i < num_slices.length; ++i) {
            int num_slice = num_slices[i];
            ImageStack stack = new ImageStack(1, 1);
            for (int j = 0; j < num_slice; ++j) {
                stack.addSlice(new FloatProcessor(1, 1));
            }
            imgs[i] = new ImagePlus("", stack);
        }
        return new ImageParameter("", imgs);
    }
    private TParameter get_test_param(ImageParameter param)
    {
        return new TParameter(param, TParameter.PossibleTypes.All, "");
    }
    private TParameter get_test_param(Integer... num_slices)
    {
        return get_test_param(get_image_param(num_slices));
    }
}
