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
import org.scijava.prefs.PrefService;

import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.TestDialog;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

public class PolyTiltTest {
    @Test public void testLinearFit()
    {
        PolyTilt test = new PolyTilt();
        test.M_phase = new double[][]{{1, 3, 5}};
        test.M_degree = 1;
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(0, 0));
        points.add(new Point(0, 1));
        points.add(new Point(0, 2));
        double[] poly = test.fitAlong(points);
        assertEquals(1, poly.length);
        assertEquals(2, poly[0]);
    }
    @Test public void testQuadraticFit()
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
        double[] poly = test.fitAlong(points);
        assertEquals(2, poly.length);
        assertEquals(0.5, 1e-6, poly[0]);
        assertEquals(0.1, 1e-6, poly[1]);
    }
    @Test public void testLive()
    {
        Context context = new Context(PluginService.class, PrefService.class);
        PolyTilt test = new PolyTilt();
        context.inject(test.param());
        TestDialog dialog = new TestDialog();
        test.param().initialize();
        test.param().setHologram(M_testImage);
        test.param().addToDialog(dialog);
        // Set do to true to make everything else appear
        dialog.getBoolean(0).value = true;
        test.param().readFromDialog();
        dialog = new TestDialog();
        test.param().addToDialog(dialog);
        dialog.getInteger(0).value = 2; // Set to quadratic
        // Manual is annoying, but it's the only one that gives us as much
        // control as we want
        dialog.getString(0).value = "Manual";
        test.param().readFromDialog();
        dialog = new TestDialog();
        test.param().addToDialog(dialog);
        // Manual is finally on the dialog.  Its integer indices start at one
        // becuase the degree is zero.
        dialog.getInteger(1).value = 0;
        dialog.getInteger(2).value = 0;
        dialog.getInteger(3).value = 4;
        dialog.getInteger(4).value = 0;
        dialog.getInteger(5).value = 0;
        dialog.getInteger(6).value = 2;
        test.param().readFromDialog();
        testCommon(test);
    }
    @Test public void testProgrammatic()
    {
        testCommon(new PolyTilt(new Manual(0, 0, 4, 0, 0, 2), 2));

    }
    private void testCommon(PolyTilt test)
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
        test.processOriginalHologram(new ConstReconstructionField(
            new ReconstructionFieldImpl(real, imag)));
        ReconstructionField field = new ReconstructionFieldImpl(real1, imag1);
        test.processFilteredField(field, 0);
        double[][] newPhase = field.field().getArg();
        assertEquals(0, newPhase[0][0], 1e-6);
        assertEquals(-0.2, newPhase[1][0], 1e-6);
        assertEquals(-0.8, newPhase[2][0], 1e-6);
        assertEquals(-1.8, newPhase[3][0], 1e-6);
        assertEquals(-0.1, newPhase[0][1], 1e-6);
        assertEquals(-0.4, newPhase[0][2], 1e-6);
        assertEquals(-0.9, newPhase[2][1], 1e-6);
    }
    private ImageParameter M_testImage = new ImageParameter("", new ImagePlus[]
        {new ImagePlus("", new FloatProcessor(5, 3))});
}
