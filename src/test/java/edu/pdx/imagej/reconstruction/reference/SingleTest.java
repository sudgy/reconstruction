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
import ij.process.FloatProcessor;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

import edu.pdx.imagej.reconstruction.ReconstructionField;

public class SingleTest {
    @Test public void test_param()
    {
        FloatProcessor proc = new FloatProcessor(1, 1);
        ImagePlus hologram = new ImagePlus("A", proc);
        ArrayList<ImagePlus> images = new ArrayList<>();
        images.add(hologram);
        images.add(new ImagePlus("B", proc));
        Single test = new Single(images);
        Single.SingleParameter param = test.param();
        ImageParameter holo_param = new ImageParameter("", images);
        param.set_hologram(holo_param);

        TestDialog dialog = new TestDialog();
        holo_param.add_to_dialog(dialog);
        param.add_to_dialog(dialog);
        dialog.get_string_index(0).value = 0;
        dialog.get_string_index(1).value = 0;
        holo_param.read_from_dialog();
        param.read_from_dialog();
        assertTrue(param.get_warning() != null, "Selecting the same image for "
            + "the reference hologram should give a warning.");
        dialog.get_string_index(1).value = 1;
        holo_param.read_from_dialog();
        param.read_from_dialog();
        assertTrue(param.get_warning() == null, "There should be no warning "
            + "once you have selected a different image.");

        dialog.get_string_index(0).value = 1;
        holo_param.read_from_dialog();
        param.read_from_dialog();
        assertTrue(param.get_warning() != null, "There should be a warning "
            + "if the hologram was changed to the reference hologram.");
        dialog.get_string_index(0).value = 0;
        holo_param.read_from_dialog();
        param.read_from_dialog();
        assertTrue(param.get_warning() == null, "There should be no warning "
            + "if the hologram was changed to not be the reference hologram.");
    }
    @Test public void test_get()
    {
        FloatProcessor proc
            = new FloatProcessor(new float[][] {{1, 0}, {4, 2}});
        ArrayList<ImagePlus> images = new ArrayList<>();
        images.add(new ImagePlus("", proc));
        Single test = new Single(images);
        ReconstructionField field = test.get_reference_holo(null, 0);
        assertEquals(field.field().get_real(0, 0), 1);
        assertEquals(field.field().get_real(0, 1), 0);
        assertEquals(field.field().get_real(1, 0), 4);
        assertEquals(field.field().get_real(1, 1), 2);
        assertEquals(field.field().get_imag(0, 0), 0);
        assertEquals(field.field().get_imag(0, 1), 0);
        assertEquals(field.field().get_imag(1, 0), 0);
        assertEquals(field.field().get_imag(1, 1), 0);
    }
}
