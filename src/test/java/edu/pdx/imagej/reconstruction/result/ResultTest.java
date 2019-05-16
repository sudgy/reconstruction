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

package edu.pdx.imagej.reconstruction.result;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

public class ResultTest {
    @Test public void test_normal()
    {
        Result test = new Result();
        FloatProcessor proc = new FloatProcessor(new float[][]{{1, 2}, {3, 4}});
        ImagePlus imp = new ImagePlus("A", proc);
        test.M_options = new ResultOptions();
        test.M_options.amplitude = true;
        test.M_options.real = true;
        ArrayList<Integer> ts = new ArrayList<>();
        ts.add(1);
        ArrayList<Double> zs = new ArrayList<>();
        zs.add(0.0);

        test.process_hologram_param(imp);
        test.process_dimensions_param(2, 3);
        test.process_ts_param(ts);
        test.process_zs_param(zs);
        test.process_beginning();
        double[][] real = {
            {1, 0},
            {3, 0}
        };
        double[][] imag = {
            {0, 0},
            {4, 1}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.process_propagated_field(field, 1, 0);
        test.almost_process_ending();

        assertTrue(test.M_phase_imp == null);
        assertTrue(test.M_imaginary_imp == null);
        float[][] amp2 = test.M_amplitude_imp.getProcessor().getFloatArray();
        float[][] real2 = test.M_real_imp.getProcessor().getFloatArray();
        assertEquals(amp2[0][0], 1);
        assertEquals(amp2[0][1], 0);
        assertEquals(amp2[1][0], 5);
        assertEquals(amp2[1][1], 1);
        assertEquals(real2[0][0], 1);
        assertEquals(real2[0][1], 0);
        assertEquals(real2[1][0], 3);
        assertEquals(real2[1][1], 0);
        assertEquals(test.M_real_imp.getCalibration().pixelHeight, 1.5);
        assertEquals(test.M_real_imp.getCalibration().pixelWidth, 1);
        assertEquals(test.M_amplitude_imp.getTitle(), "Amplitude");
        assertEquals(test.M_real_imp.getStack().getSliceLabel(1),
                     "A, z = 0.000");
    }
    @Test public void test_stack()
    {
        Result test = new Result();
        ImagePlus stack = create_the_stack();
        test.M_options = new ResultOptions();
        test.M_options.real = true;
        ArrayList<Integer> ts = new ArrayList<>();
        ts.add(1); ts.add(3);
        ArrayList<Double> zs = new ArrayList<>();
        zs.add(0.0); zs.add(1.0);

        test.process_hologram_param(stack);
        test.process_dimensions_param(2, 3);
        test.process_ts_param(ts);
        test.process_zs_param(zs);
        test.process_beginning();

        double[][] real = {{0, 0}, {0, 0}};
        double[][] imag = {{0, 0}, {0, 0}};
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        test.process_propagated_field(field, 1, 0.0);
        field.field().get_field()[0][0] = 1;
        test.process_propagated_field(field, 1, 1.0);
        field.field().get_field()[0][0] = 2;
        test.process_propagated_field(field, 3, 0.0);
        field.field().get_field()[0][0] = 3;
        test.process_propagated_field(field, 3, 1.0);
        test.almost_process_ending();

        ImagePlus imp = test.M_real_imp;
        ImageStack s = imp.getStack();
        int real10_index = imp.getStackIndex(1, 1, 1);
        int real11_index = imp.getStackIndex(1, 2, 1);
        int real30_index = imp.getStackIndex(1, 1, 2);
        int real31_index = imp.getStackIndex(1, 2, 2);
        float[][] real10 = s.getProcessor(real10_index).getFloatArray();
        float[][] real11 = s.getProcessor(real11_index).getFloatArray();
        float[][] real30 = s.getProcessor(real30_index).getFloatArray();
        float[][] real31 = s.getProcessor(real31_index).getFloatArray();
        String real10_label = s.getSliceLabel(real10_index);
        String real11_label = s.getSliceLabel(real11_index);
        String real30_label = s.getSliceLabel(real30_index);
        String real31_label = s.getSliceLabel(real31_index);

        assertEquals(real10[0][0], 0);
        assertEquals(real11[0][0], 1);
        assertEquals(real30[0][0], 2);
        assertEquals(real31[0][0], 3);
        assertEquals(real10_label, "1, z = 0.000");
        assertEquals(real11_label, "1, z = 1.000");
        assertEquals(real30_label, "A, z = 0.000");
        assertEquals(real31_label, "A, z = 1.000");
    }
    @Test public void test_save_to_file() throws java.io.IOException
    {
        String dir = "./reconstruction_result_sandbox";
        try {
            Result test = new Result();
            ImagePlus stack = create_the_stack();
            test.M_options = new ResultOptions();
            test.M_options.real = true;
            test.M_options.save_to_file = true;
            test.M_options.save_directory = dir;
            ArrayList<Integer> ts = new ArrayList<>();
            ts.add(1); ts.add(3);
            ArrayList<Double> zs = new ArrayList<>();
            zs.add(0.0); zs.add(1.0);

            test.process_hologram_param(stack);
            test.process_dimensions_param(2, 3);
            test.process_ts_param(ts);
            test.process_zs_param(zs);
            test.process_beginning();

            assertTrue(new File(Paths.get(dir, "Real", "0.000").toString())
                               .exists());
            assertTrue(new File(Paths.get(dir, "Real", "1.000").toString())
                               .exists());

            double[][] real = {{0, 0}, {0, 0}};
            double[][] imag = {{0, 0}, {0, 0}};
            ReconstructionField field = new ReconstructionFieldImpl(real, imag);
            test.process_propagated_field(field, 1, 0.0);
            field.field().get_field()[0][0] = 1;
            test.process_propagated_field(field, 1, 1.0);
            field.field().get_field()[0][0] = 2;
            test.process_propagated_field(field, 3, 0.0);
            field.field().get_field()[0][0] = 3;
            test.process_propagated_field(field, 3, 1.0);
            test.almost_process_ending();

            assertTrue(test.M_real_imp == null, "There should be no result in "
                + "memory when saving to file.");
            String real10_path
                = Paths.get(dir, "Real", "0.000", "00001.tif").toString();
            String real11_path
                = Paths.get(dir, "Real", "1.000", "00001.tif").toString();
            String real30_path
                = Paths.get(dir, "Real", "0.000", "00003.tif").toString();
            String real31_path
                = Paths.get(dir, "Real", "1.000", "00003.tif").toString();
            assertTrue(new File(real10_path).exists());
            assertTrue(new File(real11_path).exists());
            assertTrue(new File(real30_path).exists());
            assertTrue(new File(real31_path).exists());

            ImagePlus real10_imp = IJ.openImage(real10_path);
            ImagePlus real11_imp = IJ.openImage(real11_path);
            ImagePlus real30_imp = IJ.openImage(real30_path);
            ImagePlus real31_imp = IJ.openImage(real31_path);
            float[][] real10 = real10_imp.getProcessor().getFloatArray();
            float[][] real11 = real11_imp.getProcessor().getFloatArray();
            float[][] real30 = real30_imp.getProcessor().getFloatArray();
            float[][] real31 = real31_imp.getProcessor().getFloatArray();
            String real10_label = real10_imp.getTitle();
            String real11_label = real11_imp.getTitle();
            String real30_label = real30_imp.getTitle();
            String real31_label = real31_imp.getTitle();

            assertEquals(real10[0][0], 0);
            assertEquals(real11[0][0], 1);
            assertEquals(real30[0][0], 2);
            assertEquals(real31[0][0], 3);
            assertEquals(real10_label, "00001.tif");
            assertEquals(real11_label, "00001.tif");
            assertEquals(real30_label, "00003.tif");
            assertEquals(real31_label, "00003.tif");
        }
        finally {
            FileUtils.deleteDirectory(new File(dir));
        }
    }

    private static ImagePlus create_the_stack()
    {
        ImageStack stack = new ImageStack(2, 2);
        stack.addSlice("1", new FloatProcessor(new float[][]{{0, 0}, {0, 0}}));
        stack.addSlice("2", new FloatProcessor(new float[][]{{1, 0}, {0, 0}}));
        stack.addSlice(new FloatProcessor(new float[][]{{2, 0}, {0, 0}}));
        ImagePlus imp = new ImagePlus("A", stack);
        imp.setDimensions(1, 1, 3);
        return imp;
    }
}
