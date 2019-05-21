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

package edu.pdx.imagej.reconstruction.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ij.ImagePlus;
import ij.process.FloatProcessor;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;
import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

public class AngularSpectrumTest {
    // Test that the process changes anything at all
    // Yes, this does need to be here.  It failed after I first wrote it :/
    @Test public void test_change()
    {
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram, M_wavelength,
                               M_width, M_height);
        ReconstructionFieldImpl field = make_even_field();
        test.propagate(null, field, M_z0, M_z100);
        double[][] result = field.field().get_field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertTrue(M_even_real[x][y] != result[x][2*y],
                    "The real value should not be the same at " + coord);
                assertTrue(M_even_imag[x][y] != result[x][2*y+1],
                    "The imaginary value should not be the same at " + coord);
            }
        }
    }
    // Test that changing units but having the same values does nothing
    @Test public void test_units()
    {
        ReconstructionFieldImpl field = make_even_field();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram,
                               new DistanceUnitValue(500, DistanceUnits.Nano),
                               new DistanceUnitValue(300, DistanceUnits.Micro),
                               new DistanceUnitValue(310, DistanceUnits.Micro));
        test.propagate(null, field, M_z0, M_z100);
        double[][] result1 = field.field().get_field();

        field = make_even_field();
        test = new AngularSpectrum();
        test.process_beginning(M_even_hologram,
                               new DistanceUnitValue(0.5, DistanceUnits.Micro),
                               new DistanceUnitValue(0.3, DistanceUnits.Milli),
                               new DistanceUnitValue(0.031, DistanceUnits.Centi)
                              );
        test.propagate(null, field, M_z0,
                       new DistanceUnitValue(0.0001, DistanceUnits.Meter));
        double[][] result2 = field.field().get_field();

        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertEquals(result1[x][2*y], result2[x][2*y], "The real value "
                    + "should be the same at " + coord);
                assertEquals(result1[x][2*y+1], result2[x][2*y+1], "The "
                    + "imaginary value should be the same at " + coord);
            }
        }
    }
    // Test that propagating forwards then backwards gives the original image
    @Test public void test_inverse_even()
    {
        ReconstructionFieldImpl field = make_even_field();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram, M_wavelength,
                               M_width, M_height);
        test.propagate(null, field, M_z0, M_z100);
        test.propagate(null, field, M_z100, M_z0);
        double[][] result = field.field().get_field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertEquals(M_even_real[x][y], result[x][2*y], 1e-6,
                    "The real value should be the same at " + coord);
                assertEquals(M_even_imag[x][y], result[x][2*y+1], 1e-6,
                    "The imaginary value should be the same at " + coord);
            }
        }
    }
    // Test that propagating forwards then backwards gives the original image
    @Test public void test_inverse_odd()
    {
        ReconstructionFieldImpl field = make_odd_field();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, M_wavelength,
                               M_width, M_height);
        test.propagate(null, field, M_z0, M_z100);
        test.propagate(null, field, M_z100, M_z0);
        double[][] result = field.field().get_field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertEquals(M_odd_real[x][y], result[x][2*y], 1e-6,
                    "The real value should be the same at " + coord);
                assertEquals(M_odd_imag[x][y], result[x][2*y+1], 1e-6,
                    "The imaginary value should be the same at " + coord);
            }
        }
    }
    // Test that propagating forwards twice in smaller increments is the same as
    // propagating forwards once in a bigger increment
    @Test public void test_combination_even()
    {
        ReconstructionFieldImpl field = make_even_field();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram, M_wavelength,
                               M_width, M_height);
        test.propagate(null, field, M_z0, M_z100);
        test.propagate(null, field, M_z100, M_z200);
        double[][] result1 = field.field().get_field();
        field = make_even_field();
        test.propagate(null, field, M_z0, M_z200);
        double[][] result2 = field.field().get_field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertEquals(result1[x][2*y], result2[x][2*y], 1e-6,
                    "The real value should be the same at " + coord);
                assertEquals(result1[x][2*y+1], result2[x][2*y+1], 1e-6,
                    "The imaginary value should be the same at " + coord);
            }
        }
    }
    // Test that propagating forwards twice in smaller increments is the same as
    // propagating forwards once in a bigger increment
    @Test public void test_combination_odd()
    {
        ReconstructionFieldImpl field = make_odd_field();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, M_wavelength,
                               M_width, M_height);
        test.propagate(null, field, M_z0, M_z100);
        test.propagate(null, field, M_z100, M_z200);
        double[][] result1 = field.field().get_field();
        field = make_odd_field();
        test.propagate(null, field, M_z0, M_z200);
        double[][] result2 = field.field().get_field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertEquals(result1[x][2*y], result2[x][2*y], 1e-6,
                    "The real value should be the same at " + coord);
                assertEquals(result1[x][2*y+1], result2[x][2*y+1], 1e-6,
                    "The imaginary value should be the same at " + coord);
            }
        }
    }
    // Test that the amplitude of the fourier transform is constant
    @Test public void test_amplitude()
    {
        ReconstructionFieldImpl field = make_even_field();
        double[][] amp1 = field.fourier().get_amp();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram, M_wavelength,
                               M_width, M_height);
        test.propagate(null, field, M_z0, M_z100);
        double[][] amp2 = field.fourier().get_amp();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                assertEquals(amp1[x][y], amp2[x][y], 1e-6, "The amplitudes "
                    + "should be the same at (" + x + ", " + y + ").");
            }
        }
    }
    // Test that a change in z causes a linear change in the fourier transform's
    // phase, no matter where on the image
    @Test public void test_z()
    {
        DistanceUnitValue z10 = new DistanceUnitValue(10, DistanceUnits.Nano);
        DistanceUnitValue z20 = new DistanceUnitValue(20, DistanceUnits.Nano);
        ReconstructionFieldImpl field = make_odd_field();
        double[][] arg1 = field.fourier().get_arg();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, M_wavelength,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z10);
        double[][] arg2 = field.fourier().get_arg();
        test.propagate(null, field, z10, z20);
        double[][] arg3 = field.fourier().get_arg();
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                double dif1 = arg2[x][y] - arg1[x][y];
                double dif2 = arg3[x][y] - arg2[x][y];
                if (dif1 < 0) dif1 += 2*Math.PI;
                if (dif2 < 0) dif2 += 2*Math.PI;
                String coord = "(" + x + ", " + y + ").";
                assertEquals(dif1, dif2, 1e-6, "The difference in phase should "
                    + "be identical at " + coord + "  The actual values were "
                    + arg1[x][y] + ", " + arg2[x][y] + ", and " + arg3[x][y]
                    + ".");
            }
        }
    }
    // Test that in the middle of the image, changing the wavelength should
    // cause a linear change in the phase of the fourier transform
    @Test public void test_wavelength_middle()
    {
    }
    // Test that in the outer parts of the image, changing the wavelength with
    // a corresponding change in z to compensate for the linear change yields
    // a sqrt(1-λ^2) change
    @Test public void test_wavelength_outer()
    {
    }
    // Test Δx TODO: describe
    @Test public void test_dimensions()
    {

    }
    // Test fₓ TODO: describe
    @Test public void test_fx()
    {

    }

    private static ImagePlus M_even_hologram
        = new ImagePlus("", new FloatProcessor(new float[4][4]));
    private static ImagePlus M_odd_hologram
        = new ImagePlus("", new FloatProcessor(new float[5][5]));
    private static DistanceUnitValue M_wavelength
        = new DistanceUnitValue(500, DistanceUnits.Nano);
    private static DistanceUnitValue M_width
        = new DistanceUnitValue(300, DistanceUnits.Micro);
    private static DistanceUnitValue M_height
        = new DistanceUnitValue(300, DistanceUnits.Micro);
    private static DistanceUnitValue M_z0
        = new DistanceUnitValue(0, DistanceUnits.Micro);
    private static DistanceUnitValue M_z100
        = new DistanceUnitValue(100, DistanceUnits.Micro);
    private static DistanceUnitValue M_z200
        = new DistanceUnitValue(200, DistanceUnits.Micro);
    private static double[][] M_even_real = new double[][] {
        {0.7491333299, 0.5542820629, 0.1879272540, 0.8584170661},
        {0.0305604090, 0.7808111477, 0.6247602260, 0.6811765293},
        {0.6611121864, 0.3942249921, 0.1238077507, 0.1966343374},
        {0.5457368629, 0.9026601034, 0.7550818323, 0.5276090343}
    };
    private static double[][] M_even_imag = new double[][] {
        {0.6906533149, 0.1062510323, 0.5731869642, 0.2101399789},
        {0.6916196061, 0.2327204311, 0.9912915487, 0.5350163478},
        {0.2655718145, 0.1526346228, 0.2690232265, 0.7611883011},
        {0.7277631769, 0.6861068860, 0.9135765966, 0.0137632145}
    };
    private static double[][] M_odd_real = new double[][] {
        {0.728168521, 0.886166144, 0.854714648, 0.840785173, 0.358469178},
        {0.708248611, 0.142697564, 0.632468376, 0.757623067, 0.775829661},
        {0.475437958, 0.887524302, 0.787616284, 0.042850949, 0.940263144},
        {0.994871946, 0.561355935, 0.658125355, 0.722114895, 0.027024077},
        {0.636014248, 0.207731296, 0.212560867, 0.439091794, 0.550814607}
    };
    private static double[][] M_odd_imag = new double[][] {
        {0.605117650, 0.711108696, 0.455784125, 0.870364407, 0.044710641},
        {0.538116496, 0.848686224, 0.895711212, 0.290614150, 0.727374234},
        {0.989027023, 0.097340723, 0.098410347, 0.966053456, 0.085034564},
        {0.524764103, 0.462075268, 0.156010638, 0.880572436, 0.056135696},
        {0.148962797, 0.486935852, 0.015705864, 0.824656030, 0.282585016}
    };
    private static ReconstructionFieldImpl make_even_field()
    {
        return new ReconstructionFieldImpl(M_even_real, M_even_imag);
    }
    private static ReconstructionFieldImpl make_odd_field()
    {
        return new ReconstructionFieldImpl(M_odd_real, M_odd_imag);
    }
}
