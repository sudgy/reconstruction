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
import java.util.ArrayList;

import ij.ImagePlus;
import ij.process.FloatProcessor;

import org.scijava.Context;
import org.scijava.plugin.PluginService;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

public class PolyTiltTest {
    @Test public void test_linear_fit()
    {
        PolyTilt test = new PolyTilt();
        test.M_phase = new double[][]{{1, 3, 5}};
        test.M_degree = 1;
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(0, 1));
        points.add(new Point(0, 2));
        double[] poly = test.fit_along(points);
        assertEquals(1, poly.length);
        assertEquals(2, poly[0]);
    }
    @Test public void test_quadratic_fit()
    {
        PolyTilt test = new PolyTilt();
        // The polynomial is 0.1x^2 + 0.5x + 1
        test.M_phase = new double[][]{{1.0, 1.6, 2.4, 3.4, 4.6}};
        test.M_degree = 2;
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0, 1));
        points.add(new Point(0, 2));
        points.add(new Point(0, 3));
        points.add(new Point(0, 4));
        double[] poly = test.fit_along(points);
        assertEquals(2, poly.length);
        assertEquals(0.5, 1e-6, poly[0]);
        assertEquals(0.1, 1e-6, poly[1]);
    }
    @Test public void test_live()
    {
        Context context = new Context(PluginService.class);
        PolyTilt test = new PolyTilt();
        context.inject(test.param());
        TestDialog dialog = new TestDialog();
        test.param().initialize();
        test.param().set_hologram(M_test_image);
        test.param().add_to_dialog(dialog);
        // Set do to true to make everything else appear
        dialog.get_boolean(0).value = true;
        test.param().read_from_dialog();
        dialog = new TestDialog();
        test.param().add_to_dialog(dialog);
        dialog.get_integer(0).value = 2; // Set to quadratic
        // Manual is annoying, but it's the only one that gives us as much
        // control as we want
        dialog.get_string(0).value = "Manual";
        test.param().read_from_dialog();
        dialog = new TestDialog();
        test.param().add_to_dialog(dialog);
        // Manual is finally on the dialog.  Its integer indices start at one
        // becuase the degree is zero.
        dialog.get_integer(1).value = 0;
        dialog.get_integer(2).value = 0;
        dialog.get_integer(3).value = 4;
        dialog.get_integer(4).value = 0;
        dialog.get_integer(5).value = 0;
        dialog.get_integer(6).value = 2;
        test.param().read_from_dialog();
        test_common(test);
    }
    @Test public void test_programmatic()
    {
        test_common(new PolyTilt(new Manual(0, 0, 4, 0, 0, 2), 2));

    }
    private void test_common(PolyTilt test)
    {
        double[][] phase = {
            {0,   0.1, 0.4},
            {0.2, 0,   0},
            {0.8, 0,   0},
            {1.8, 0,   0},
            {3.2, 0,   0}
        };
        double[][] real = new double[5][3];
        double[][] real1 = new double[5][3];
        double[][] imag = new double[5][3];
        double[][] imag1 = new double[5][3];
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 3; ++y) {
                real[x][y] = Math.cos(phase[x][y]);
                imag[x][y] = Math.sin(phase[x][y]);
                real1[x][y] = 1;
                imag1[x][y] = 0;
            }
        }
        test.process_original_hologram(new ConstReconstructionField(
            new ReconstructionFieldImpl(real, imag)));
        ReconstructionField field = new ReconstructionFieldImpl(real1, imag1);
        test.process_filtered_field(field, 0);
        double[][] new_phase = field.field().get_arg();
        assertEquals(0, new_phase[0][0], 1e-6);
        assertEquals(-0.2, new_phase[1][0], 1e-6);
        assertEquals(-0.8, new_phase[2][0], 1e-6);
        assertEquals(-1.8, new_phase[3][0], 1e-6);
        assertEquals(-0.1, new_phase[0][1], 1e-6);
        assertEquals(-0.4, new_phase[0][2], 1e-6);
        assertEquals(-0.9, new_phase[2][1], 1e-6);
    }
    private ImageParameter M_test_image = new ImageParameter("", new ImagePlus[]
        {new ImagePlus("", new FloatProcessor(5, 3))});
}
