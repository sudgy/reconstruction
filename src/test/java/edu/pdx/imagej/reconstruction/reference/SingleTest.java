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
    @Test public void testParam()
    {
        FloatProcessor proc = new FloatProcessor(1, 1);
        ImagePlus hologram = new ImagePlus("A", proc);
        ArrayList<ImagePlus> images = new ArrayList<>();
        images.add(hologram);
        images.add(new ImagePlus("B", proc));
        Single test = new Single(images);
        Single.SingleParameter param = test.param();
        ImageParameter holoParam = new ImageParameter("", images);
        param.setHologram(holoParam);

        TestDialog dialog = new TestDialog();
        holoParam.addToDialog(dialog);
        param.addToDialog(dialog);
        dialog.getStringIndex(0).value = 0;
        dialog.getStringIndex(1).value = 0;
        holoParam.readFromDialog();
        param.readFromDialog();
        assertTrue(param.getWarning() != null, "Selecting the same image for "
            + "the reference hologram should give a warning.");
        dialog.getStringIndex(1).value = 1;
        holoParam.readFromDialog();
        param.readFromDialog();
        assertTrue(param.getWarning() == null, "There should be no warning "
            + "once you have selected a different image.");

        dialog.getStringIndex(0).value = 1;
        holoParam.readFromDialog();
        param.readFromDialog();
        assertTrue(param.getWarning() != null, "There should be a warning "
            + "if the hologram was changed to the reference hologram.");
        dialog.getStringIndex(0).value = 0;
        holoParam.readFromDialog();
        param.readFromDialog();
        assertTrue(param.getWarning() == null, "There should be no warning "
            + "if the hologram was changed to not be the reference hologram.");
    }
    @Test public void testLive() // This doesn't even use a TestDialog :/
    {
        FloatProcessor proc
            = new FloatProcessor(new float[][] {{1, 0}, {4, 2}});
        ArrayList<ImagePlus> images = new ArrayList<>();
        images.add(new ImagePlus("", proc));
        Single test = new Single(images);
        ReconstructionField field = test.getReferenceHolo(null, 0);
        assertEquals(field.field().getReal(0, 0), 1);
        assertEquals(field.field().getReal(0, 1), 0);
        assertEquals(field.field().getReal(1, 0), 4);
        assertEquals(field.field().getReal(1, 1), 2);
        assertEquals(field.field().getImag(0, 0), 0);
        assertEquals(field.field().getImag(0, 1), 0);
        assertEquals(field.field().getImag(1, 0), 0);
        assertEquals(field.field().getImag(1, 1), 0);
    }
    @Test public void testProgrammatic()
    {
        FloatProcessor proc
            = new FloatProcessor(new float[][] {{1, 0}, {4, 2}});
        ImagePlus imp = new ImagePlus("", proc);
        Single test = new Single(imp);
        ReconstructionField field = test.getReferenceHolo(null, 0);
        assertEquals(field.field().getReal(0, 0), 1);
        assertEquals(field.field().getReal(0, 1), 0);
        assertEquals(field.field().getReal(1, 0), 4);
        assertEquals(field.field().getReal(1, 1), 2);
        assertEquals(field.field().getImag(0, 0), 0);
        assertEquals(field.field().getImag(0, 1), 0);
        assertEquals(field.field().getImag(1, 0), 0);
        assertEquals(field.field().getImag(1, 1), 0);
    }
}
