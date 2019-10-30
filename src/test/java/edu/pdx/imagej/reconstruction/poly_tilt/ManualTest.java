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
    @Test public void testInitialValues()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs4);
        test.setHologram(image);
        Line[] lines = test.getValue();
        Line hLine = lines[0];
        Line vLine = lines[1];
        int i = 0;
        for (Point p : hLine) {
            assertEquals(i++, p.x);
            assertEquals(2, p.y); // In the middle
        }
        assertEquals(4, i, "It should end at the end of the image.");
        i = 0;
        for (Point p : vLine) {
            assertEquals(2, p.x); // In the middle
            assertEquals(i++, p.y);
        }
        assertEquals(4, i, "It should end at the end of the image.");
    }
    @Test public void testChange()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs4);
        test.setHologram(image);
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);
        // h line goes forward, while v line goes backwards
        dialog.getInteger(0).value = 1;
        dialog.getInteger(1).value = 1;
        dialog.getInteger(2).value = 2;
        dialog.getInteger(3).value = 3;
        dialog.getInteger(4).value = 3;
        dialog.getInteger(5).value = 1;
        test.readFromDialog();
        Line[] lines = test.getValue();
        Line hLine = lines[0];
        Line vLine = lines[1];
        int i = 1;
        for (Point p : hLine) {
            assertEquals(i++, p.x);
            assertEquals(1, p.y);
        }
        assertEquals(3, i);
        i = 1;
        for (Point p : vLine) {
            assertEquals(3, p.x);
            assertEquals(i++, p.y);
        }
        assertEquals(4, i);
    }
    @Test public void testBounds()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs4);
        test.setHologram(image);
        TestDialog dialog = new TestDialog();
        test.addToDialog(dialog);

        assertTrue(test.getError() == null);

        dialog.getInteger(0).value = 4;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getInteger(0).value = 3;
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getInteger(0).value = -1;
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }
    @Test public void testChangingBounds()
    {
        Manual.ManualParameter test = new Manual.ManualParameter();
        ImageParameter image = new ImageParameter("", M_imgs);
        test.setHologram(image);
        TestDialog dialog = new TestDialog();
        image.addToDialog(dialog);
        test.addToDialog(dialog);

        dialog.getInteger(0).value = 4;
        test.readFromDialog();
        assertTrue(test.getError() != null);

        dialog.getStringIndex(0).value = 1;
        image.readFromDialog();
        test.readFromDialog();
        assertTrue(test.getError() == null);

        dialog.getStringIndex(0).value = 0;
        image.readFromDialog();
        test.readFromDialog();
        assertTrue(test.getError() != null);
    }
    private ImagePlus M_img4 = new ImagePlus("", new FloatProcessor(4, 4));
    private ImagePlus M_img8 = new ImagePlus("", new FloatProcessor(8, 8));
    private ImagePlus[] M_imgs4 = {M_img4};
    private ImagePlus[] M_imgs = {M_img4, M_img8};
}
