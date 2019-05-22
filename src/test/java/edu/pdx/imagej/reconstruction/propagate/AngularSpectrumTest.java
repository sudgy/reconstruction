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

import java.util.HashMap;

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
        test.process_beginning(M_odd_hologram, M_wavelength, M_width, M_height);
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
        test.process_beginning(M_odd_hologram, M_wavelength, M_width, M_height);
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
    // Test that many reconstructions on the same field is stable
    // This is mainly a numerical accuracy test
    @Test public void test_big_combination()
    {
        ReconstructionFieldImpl field = make_even_field();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram, M_wavelength,
                               M_width, M_height);
        DistanceUnitValue z1 = new DistanceUnitValue(1, DistanceUnits.Micro);
        for (int i = 0; i < 100; ++i) {
            test.propagate(null, field, M_z0, z1);
        }
        double[][] result1 = field.field().get_field();
        field = make_even_field();
        test.propagate(null, field, M_z0, M_z100);
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
        test.process_beginning(M_odd_hologram, M_wavelength, M_width, M_height);
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
    // cause an inverse-linear change in the phase of the fourier transform
    @Test public void test_wavelength_middle()
    {
        ReconstructionFieldImpl field = make_odd_field();
        double arg1 = field.fourier().get_arg()[2][2];
        DistanceUnitValue z10 = new DistanceUnitValue(10, DistanceUnits.Nano);
        DistanceUnitValue wavelength500
            = new DistanceUnitValue(500, DistanceUnits.Nano);
        DistanceUnitValue wavelength600
            = new DistanceUnitValue(600, DistanceUnits.Nano);
        DistanceUnitValue wavelength700
            = new DistanceUnitValue(700, DistanceUnits.Nano);
        AngularSpectrum test = new AngularSpectrum();

        test.process_beginning(M_odd_hologram, wavelength500,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z10);
        double arg2 = field.fourier().get_arg()[2][2];

        field = make_odd_field();
        test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, wavelength600,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z10);
        double arg3 = field.fourier().get_arg()[2][2];

        field = make_odd_field();
        test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, wavelength700,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z10);
        double arg4 = field.fourier().get_arg()[2][2];

        double dif1 = arg2 - arg1;
        double dif2 = arg3 - arg1;
        double dif3 = arg4 - arg1;
        if (dif1 < 0) dif1 += 2*Math.PI;
        if (dif2 < 0) dif2 += 2*Math.PI;
        if (dif3 < 0) dif3 += 2*Math.PI;
        double val1 = dif1 * 500;
        double val2 = dif2 * 600;
        double val3 = dif3 * 700;
        assertEquals(val1, val2, 1e-6, "The phase should be inversely "
            + "proportional to the wavelength.  Before propagation, the phase "
            + "was " + arg1 + ", and after propagation at 500, 600, and 700 nm "
            + "the phase was " + arg2 + ", " + arg3 + ", and " + arg4
            + ", respectively.  The differences were " + dif1 + ", " + dif2
            + ", and " + dif3 + ".");
        assertEquals(val1, val3, 1e-6, "The phase should be inversely "
            + "proportional to the wavelength.  Before propagation, the phase "
            + "was " + arg1 + ", and after propagation at 500, 600, and 700 nm "
            + "the phase was " + arg2 + ", " + arg3 + ", and " + arg4
            + ", respectively.  The differences were " + dif1 + ", " + dif2
            + ", and " + dif3 + ".");
    }
    // Test that in the outer parts of the image, changing the wavelength with
    // a corresponding change in z to compensate for the linear change yields
    // a sqrt(1-aλ^2) change
    @Test public void test_wavelength_outer()
    {
        // Here's what we do:
        // Given two different wavelengths λ₁ and λ₂ such that λ₂ = aλ₁, then
        // the phase difference for λ₁ is Δϕ₁ = b*sqrt(1 - (cλ₁)²) for some
        // constants b and c, and the phase difference for λ₂ is
        // Δϕ₂ = b*sqrt(1 - a²(cλ₁)²).  Squaring each one and taking the
        // difference yields Δϕ₁² - Δϕ₂² = b²(cλ₁)²(a² - 1).  This means that
        // (Δϕ₁² - Δϕ₂²)/(a² - 1) = b²(cλ₁)², a constant.  So, the test computes
        // the left-hand side of the equation and checks if it's constant for
        // various values of λ.
        ReconstructionFieldImpl field = make_odd_field();
        double[][] arg1_array = field.fourier().get_arg();
        DistanceUnitValue z5  = new DistanceUnitValue(5,  DistanceUnits.Nano);
        DistanceUnitValue z10 = new DistanceUnitValue(10, DistanceUnits.Nano);
        DistanceUnitValue z20 = new DistanceUnitValue(20, DistanceUnits.Nano);
        DistanceUnitValue wavelength500
            = new DistanceUnitValue(500, DistanceUnits.Nano);
        DistanceUnitValue wavelength1000
            = new DistanceUnitValue(1000, DistanceUnits.Nano);
        DistanceUnitValue wavelength2000
            = new DistanceUnitValue(2000, DistanceUnits.Nano);
        AngularSpectrum test = new AngularSpectrum();

        test.process_beginning(M_odd_hologram, wavelength500,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z5);
        double[][] arg2_array = field.fourier().get_arg();

        field = make_odd_field();
        test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, wavelength1000,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z10);
        double[][] arg3_array = field.fourier().get_arg();

        field = make_odd_field();
        test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, wavelength2000,
                               M_width, M_height);
        test.propagate(null, field, M_z0, z20);
        double[][] arg4_array = field.fourier().get_arg();

        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                double arg1 = arg1_array[x][y];
                double arg2 = arg2_array[x][y];
                double arg3 = arg3_array[x][y];
                double arg4 = arg4_array[x][y];
                double dif1 = arg2 - arg1;
                double dif2 = arg3 - arg1;
                double dif3 = arg4 - arg1;
                if (dif1 < 0) dif1 += 2*Math.PI;
                if (dif2 < 0) dif2 += 2*Math.PI;
                if (dif3 < 0) dif3 += 2*Math.PI;
                // 3 is (2² - 1), and 15 is (4² - 1)
                double val1 = (dif1 * dif1 - dif2 * dif2) / 3;
                double val2 = (dif2 * dif2 - dif3 * dif3) / 3;
                double val3 = (dif1 * dif1 - dif3 * dif3) / 15;
                assertEquals(val1, val2, 1e-6, "At (" + x + ", " + y + "), "
                    + "before propagation, the phase was " + arg1 + ", and "
                    + "after propagation at 500, 1000, and 2000 nm the phase "
                    + "was " + arg2 + ", " + arg3 + ", and " + arg4 + ", "
                    + "respectively.  The differences were " + dif1 + ", "
                    + dif2 + ", and " + dif3 + ".");
                assertEquals(val1, val3, 1e-6, "At (" + x + ", " + y + "), "
                    + "before propagation, the phase was " + arg1 + ", and "
                    + "after propagation at 500, 1000, and 2000 nm the phase "
                    + "was " + arg2 + ", " + arg3 + ", and " + arg4 + ", "
                    + "respectively.  The differences were " + dif1 + ", "
                    + dif2 + ", and " + dif3 + ".");
            }
        }
    }
    // Test that Δx and Δy cause a sqrt(1-a/(Δx)^2) change
    @Test public void test_dimensions()
    {
        // The idea here is kind of similar to test_wavelength_outer, but more
        // complicated.  To be more general, let f(x) and g(y) be functions.
        // The phase difference for given values of x and y is
        // Δϕ = a*sqrt(1 - bf(x) - cg(y)) for some constants a, b and c.  The
        // difference of the squares at x₁, y₁ and at x₂, y₂ ends up as
        // Δϕ₁² - Δϕ₂² = a²(b(f(x₂) - f(x₁)) + c(g(y₂) - g(y₁))).  To make
        // things simpler, let Δf = f(x₂) - f(x₁) and Δg = g(y₂) - g(y₁) so that
        // the equation now reads Δϕ₁² - Δϕ₂² = a²(bΔf + cΔg).  To do anything
        // with this equation, assume that there is some constant value d such
        // that Δg = dΔf, so that (Δϕ₁² - Δϕ₂²)/Δf = a²(b + cd), a constant, and
        // this value can be checked for being constant.
        //
        // In our case, x = Δx and f(Δx) = 1/Δx², and likewise for y.  We
        // calculate Δf and Δg, calculate d, and then check if all of the values
        // for a given d are constant.
        double[][][] arg_array = new double[16][][];
        int[] width_array = new int[16];
        int[] height_array = new int[16];
        ReconstructionFieldImpl field = make_odd_field();
        double[][] arg0_array = field.fourier().get_arg();
        DistanceUnitValue z10 = new DistanceUnitValue(10, DistanceUnits.Nano);
        AngularSpectrum test;

        int i = 0;
        for (int width = 100; width < 500; width += 100) {
            for (int height = 100; height < 500; height += 100) {
                field = make_odd_field();
                test = new AngularSpectrum();
                test.process_beginning(
                    M_odd_hologram, M_wavelength,
                    new DistanceUnitValue(width, DistanceUnits.Micro),
                    new DistanceUnitValue(height, DistanceUnits.Micro));
                test.propagate(null, field, M_z0, z10);
                arg_array[i] = field.fourier().get_arg();
                width_array[i] = width;
                height_array[i] = height;
                ++i;
            }
        }
        HashMap<Integer, double[][]> vals = new HashMap<>();
        for (i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (i == j) continue;

                double df = 1.0 / (width_array[j]  * width_array[j])
                          - 1.0 / (width_array[i]  * width_array[i]);
                double dg = 1.0 / (height_array[j] * height_array[j])
                          - 1.0 / (height_array[i] * height_array[i]);
                int d = (int)Math.round(dg / df * 1000);
                boolean df0 = Math.abs(df) < 1e-6;
                if (df0) d = Integer.MAX_VALUE;

                double[][] current_vals = vals.get(d);
                boolean first = current_vals == null;
                if (first) {
                    current_vals = new double[5][5];
                    vals.put(d, current_vals);
                }

                for (int x = 0; x < 5; ++x) {
                    for (int y = 0; y < 5; ++y) {

                        double arg1 = arg_array[i][x][y];
                        double arg2 = arg_array[j][x][y];
                        double dif1 = arg1 - arg0_array[x][y];
                        double dif2 = arg2 - arg0_array[x][y];
                        if (dif1 < 0) dif1 += 2*Math.PI;
                        if (dif2 < 0) dif2 += 2*Math.PI;
                        double val = dif1*dif1 - dif2*dif2;
                        if (df0) val /= dg;
                        else val /= df;

                        if (first) current_vals[x][y] = val;
                        else {
                            assertEquals(current_vals[x][y], val, 1e-6,
                                  "\ni       = " + i
                                + "\nj       = " + j
                                + "\nx       = " + x
                                + "\ny       = " + y
                                + "\ndf      = " + df
                                + "\ndg      = " + dg
                                + "\narg1    = " + arg1
                                + "\narg2    = " + arg2
                                + "\ndif1    = " + dif1
                                + "\ndif2    = " + dif2
                                + "\nwidth1  = " + width_array[i]
                                + "\nwidth2  = " + width_array[j]
                                + "\nheight1 = " + height_array[i]
                                + "\nheight2 = " + height_array[j]
                            );
                        }
                    }
                }
            }
        }
    }
    // Test that fₓ and f_y decrease the change in phase further out on the
    // Fourier transform all at the same rate
    @Test public void test_fx()
    {
        // Sadly, because fₓ and f_y are allowed to be off by tiny amounts, no
        // exact answer can be calculated here.  The best we can do is check
        // that the phase changes slower on the outside than on the inside.
        DistanceUnitValue z10 = new DistanceUnitValue(10, DistanceUnits.Nano);
        ReconstructionFieldImpl field = make_odd_field();
        double[][] arg1_array = field.fourier().get_arg();
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_odd_hologram, M_wavelength, M_width, M_z200);
        test.propagate(null, field, M_z0, z10);
        double[][] arg2_array = field.fourier().get_arg();

        double[][] difs = new double[5][5];
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                double arg1 = arg1_array[x][y];
                double arg2 = arg2_array[x][y];
                double dif = arg2 - arg1;
                if (dif < 0) dif += 2*Math.PI;
                difs[x][y] = dif;
            }
        }
        // At this point difs should be something like
        // 4 3 2 3 4
        // 3 2 1 2 3
        // 2 1 0 1 2
        // 3 2 1 2 3
        // 4 3 2 3 4
        // We want to test that all the values increase as they should, and that
        // all values are equal that should be equal.

        // Check that all that need to be equal are equal
        assertEquals(difs[2][1], difs[2][3], 1e-6);
        assertEquals(difs[2][0], difs[2][4], 1e-6);
        assertEquals(difs[1][2], difs[3][2], 1e-6);
        assertEquals(difs[0][2], difs[4][2], 1e-6);

        assertEquals(difs[1][1], difs[1][3], 1e-6);
        assertEquals(difs[1][1], difs[3][3], 1e-6);
        assertEquals(difs[1][1], difs[3][1], 1e-6);
        assertEquals(difs[0][0], difs[0][4], 1e-6);
        assertEquals(difs[0][0], difs[4][4], 1e-6);
        assertEquals(difs[0][0], difs[4][0], 1e-6);

        assertEquals(difs[1][0], difs[3][0], 1e-6);
        assertEquals(difs[1][0], difs[3][4], 1e-6);
        assertEquals(difs[1][0], difs[1][4], 1e-6);
        assertEquals(difs[0][1], difs[0][3], 1e-6);
        assertEquals(difs[0][1], difs[4][3], 1e-6);
        assertEquals(difs[0][1], difs[4][1], 1e-6);

        // Check that all that need to be greater than are greater than
        for (int x = -2; x <= 2; ++x) {
            for (int y = -2; y <= 2; ++y) {
                int ix = x + 2;
                int iy = y + 2;
                String err = "\nx    = " + x
                           + "\ny    = " + y
                           + "\ndif1 = " + difs[ix][iy]
                           + "\ndif2 = ";

                if (x >= 0 && x != 2) {
                    assertTrue(difs[ix+1][iy] < difs[ix][iy],
                               err + difs[ix+1][iy]);
                }
                if (x <= 0 && x != -2) {
                    assertTrue(difs[ix-1][iy] < difs[ix][iy],
                               err + difs[ix-1][iy]);
                }
                if (y >= 0 && y != 2) {
                    assertTrue(difs[ix][iy+1] < difs[ix][iy],
                               err + difs[ix][iy+1]);
                }
                if (y <= 0 && y != -2) {
                    assertTrue(difs[ix][iy-1] < difs[ix][iy],
                               err + difs[ix][iy-1]);
                }
            }
        }
    }
    // Test that the algorithm deals with the negative square root correctly
    @Test public void test_square_root()
    {
        ReconstructionFieldImpl field = make_odd_field();
        double arg1 = field.fourier().get_arg()[0][0];
        AngularSpectrum test = new AngularSpectrum();
        // Yes, the size and the wavelength are supposed to be swapped.  It
        // makes the square root negative.
        test.process_beginning(M_odd_hologram, M_width,
                               M_wavelength, M_wavelength);
        test.propagate(null, field, M_z0, M_z100);
        double arg2 = field.fourier().get_arg()[0][0];
        assertEquals(arg1, arg2);
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
