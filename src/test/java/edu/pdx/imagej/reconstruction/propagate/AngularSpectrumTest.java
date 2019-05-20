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
    @Test public void test_change()
    {
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram, M_wavelength,
                               M_width, M_height);
        double[][] real = new double[][] {
            {0.7491333299, 0.5542820629, 0.1879272540, 0.8584170661},
            {0.0305604090, 0.7808111477, 0.6247602260, 0.6811765293},
            {0.6611121864, 0.3942249921, 0.1238077507, 0.1966343374},
            {0.5457368629, 0.9026601034, 0.7550818323, 0.5276090343}
        };
        double[][] imag = new double[][] {
            {0.6906533149, 0.1062510323, 0.5731869642, 0.2101399789},
            {0.6916196061, 0.2327204311, 0.9912915487, 0.5350163478},
            {0.2655718145, 0.1526346228, 0.2690232265, 0.7611883011},
            {0.7277631769, 0.6861068860, 0.9135765966, 0.0137632145}
        };
        ReconstructionFieldImpl field = new ReconstructionFieldImpl(real, imag);
        test.propagate(null, field, M_z0,
                       new DistanceUnitValue(100, DistanceUnits.Micro));
        double[][] result = field.field().get_field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                String coord = "(" + x + ", " + y + ").";
                assertTrue(real[x][y] != result[x][2*y], "The real value should"
                    + " not be the same at " + coord);
                assertTrue(imag[x][y] != result[x][2*y+1], "The imaginary value"
                    + " should not be the same at " + coord);
            }
        }
    }
    @Test public void test_units()
    {
        double[][] real = new double[][] {
            {0.7491333299, 0.5542820629, 0.1879272540, 0.8584170661},
            {0.0305604090, 0.7808111477, 0.6247602260, 0.6811765293},
            {0.6611121864, 0.3942249921, 0.1238077507, 0.1966343374},
            {0.5457368629, 0.9026601034, 0.7550818323, 0.5276090343}
        };
        double[][] imag = new double[][] {
            {0.6906533149, 0.1062510323, 0.5731869642, 0.2101399789},
            {0.6916196061, 0.2327204311, 0.9912915487, 0.5350163478},
            {0.2655718145, 0.1526346228, 0.2690232265, 0.7611883011},
            {0.7277631769, 0.6861068860, 0.9135765966, 0.0137632145}
        };
        ReconstructionFieldImpl field = new ReconstructionFieldImpl(real, imag);
        AngularSpectrum test = new AngularSpectrum();
        test.process_beginning(M_even_hologram,
                               new DistanceUnitValue(500, DistanceUnits.Nano),
                               new DistanceUnitValue(300, DistanceUnits.Micro),
                               new DistanceUnitValue(310, DistanceUnits.Micro));
        test.propagate(null, field, M_z0,
                       new DistanceUnitValue(100, DistanceUnits.Micro));
        double[][] result1 = field.field().get_field();

        field = new ReconstructionFieldImpl(real, imag);
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
    @Test public void test_inverse_even()
    {

    }
    @Test public void test_inverse_odd()
    {

    }
    @Test public void test_combination_even()
    {

    }
    @Test public void test_combination_odd()
    {

    }
    @Test public void test_amplitude()
    {

    }
    @Test public void test_wavelength()
    {

    }
    @Test public void test_dimensions()
    {

    }
    @Test public void test_fx()
    {

    }
    @Test public void test_z()
    {

    }

    private ImagePlus M_even_hologram = new ImagePlus("",
                                        new FloatProcessor(new float[4][4]));
    private ImagePlus M_odd_hologram = new ImagePlus("",
                                       new FloatProcessor(new float[5][5]));
    private DistanceUnitValue M_wavelength
        = new DistanceUnitValue(500, DistanceUnits.Nano);
    private DistanceUnitValue M_width
        = new DistanceUnitValue(300, DistanceUnits.Micro);
    private DistanceUnitValue M_height
        = new DistanceUnitValue(300, DistanceUnits.Micro);
    private DistanceUnitValue M_z0
        = new DistanceUnitValue(0, DistanceUnits.Micro);
}
