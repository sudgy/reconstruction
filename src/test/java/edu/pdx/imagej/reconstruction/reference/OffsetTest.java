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

package edu.pdx.imagej.reconstruction.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

import edu.pdx.imagej.dynamic_parameters.TestDialog;

import edu.pdx.imagej.reconstruction.ReconstructionField;

public class OffsetTest {
    @Test public void test_get()
    {
        ImageStack stack = new ImageStack(2, 2);
        stack.addSlice("", new FloatProcessor(new float[][] {{1, 2}, {3, 4}}));
        stack.addSlice("", new FloatProcessor(new float[][] {{5, 6}, {7, 8}}));
        ArrayList<ImagePlus> images = new ArrayList<>();
        images.add(new ImagePlus("", stack));
        Offset test = new Offset(images);

        TestDialog dialog = new TestDialog();
        test.param().initialize();
        test.param().add_to_dialog(dialog);
        dialog.get_string_index(0).value = 0; // Select the image
        dialog.get_integer(0).value = -1; // Set the offset
        test.param().read_from_dialog();

        ReconstructionField field = test.get_reference_holo(null, 1);
        assertEquals(field.field().get_real(0, 0), 1);
        assertEquals(field.field().get_real(0, 1), 2);
        assertEquals(field.field().get_real(1, 0), 3);
        assertEquals(field.field().get_real(1, 1), 4);
        field = test.get_reference_holo(null, 2);
        assertEquals(field.field().get_real(0, 0), 1);
        assertEquals(field.field().get_real(0, 1), 2);
        assertEquals(field.field().get_real(1, 0), 3);
        assertEquals(field.field().get_real(1, 1), 4);
        field = test.get_reference_holo(null, 3);
        assertEquals(field.field().get_real(0, 0), 5);
        assertEquals(field.field().get_real(0, 1), 6);
        assertEquals(field.field().get_real(1, 0), 7);
        assertEquals(field.field().get_real(1, 1), 8);
    }
}
