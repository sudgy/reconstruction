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

import ij.ImagePlus;
import ij.gui.Line;
import ij.process.FloatProcessor;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

public class ManualTest {
    @Test public void test_initial_values()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs4);
        test.set_hologram(image);
        Line[] lines = test.get_value();
        Line h_line = lines[0];
        Line v_line = lines[1];
        int i = 0;
        for (Point p : h_line) {
            assertEquals(i++, p.x);
            assertEquals(2, p.y); // In the middle
        }
        assertEquals(4, i, "It should end at the end of the image.");
        i = 0;
        for (Point p : v_line) {
            assertEquals(2, p.x); // In the middle
            assertEquals(i++, p.y);
        }
        assertEquals(4, i, "It should end at the end of the image.");
    }
    @Test public void test_change()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs4);
        test.set_hologram(image);
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);
        // h line goes forward, while v line goes backwards
        dialog.get_integer(0).value = 1;
        dialog.get_integer(1).value = 1;
        dialog.get_integer(2).value = 2;
        dialog.get_integer(3).value = 3;
        dialog.get_integer(4).value = 3;
        dialog.get_integer(5).value = 1;
        test.read_from_dialog();
        Line[] lines = test.get_value();
        Line h_line = lines[0];
        Line v_line = lines[1];
        int i = 1;
        for (Point p : h_line) {
            assertEquals(i++, p.x);
            assertEquals(1, p.y);
        }
        assertEquals(3, i);
        i = 1;
        for (Point p : v_line) {
            assertEquals(3, p.x);
            assertEquals(i++, p.y);
        }
        assertEquals(4, i);
    }
    @Test public void test_bounds()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs4);
        test.set_hologram(image);
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        assertTrue(test.get_error() == null);

        dialog.get_integer(0).value = 4;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);

        dialog.get_integer(0).value = 3;
        test.read_from_dialog();
        assertTrue(test.get_error() == null);

        dialog.get_integer(0).value = -1;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
    }
    @Test public void test_changing_bounds()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image4 = new ImageParameter("", M_imgs4);
        ImageParameter image8 = new ImageParameter("", M_imgs8);
        test.set_hologram(image4);
        TestDialog dialog = new TestDialog();
        test.add_to_dialog(dialog);

        dialog.get_integer(0).value = 4;
        test.read_from_dialog();
        assertTrue(test.get_error() != null);
        test.set_hologram(image8);
        assertTrue(test.get_error() == null);
        test.set_hologram(image4);
        assertTrue(test.get_error() != null);
    }
    private ImagePlus M_img4 = new ImagePlus("", new FloatProcessor(4, 4));
    private ImagePlus M_img8 = new ImagePlus("", new FloatProcessor(8, 8));
    private ImagePlus[] M_imgs4 = {M_img4};
    private ImagePlus[] M_imgs8 = {M_img8};
}
