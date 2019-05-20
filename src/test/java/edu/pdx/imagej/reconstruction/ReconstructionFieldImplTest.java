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

package edu.pdx.imagej.reconstruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class ReconstructionFieldImplTest {
    @Test public void test_changes()
    {
        // They need to be at least 2x2 for the FFT's sake
        double[][] real = new double[][] {{0, 0}, {0, 0}};
        double[][] imag = new double[][] {{0, 0}, {0, 0}};
        ReconstructionFieldImpl test = new ReconstructionFieldImpl(real, imag);
        assertTrue(test.has_field(), "It should start out with a field.");
        assertTrue(!test.has_fourier(), "It should not start out with a "
            + "fourier.");

        ReconstructionComplexField field = test.field();
        assertTrue(test.has_field(), "It should still have a field when the "
            + "field is retrieved.");
        assertTrue(!test.has_fourier(), "It should still not have a fourier "
            + "when the field is retrieved.");

        field.get_field(); // This triggers field_changed
        assertTrue(test.has_field(), "It should still have a field when the "
            + "field has changed.");
        assertTrue(!test.has_fourier(), "It should still not have a fourier "
            + "when the field has changed.");

        ReconstructionComplexField fourier = test.fourier();
        assertTrue(test.has_field(), "It should still have a field when the "
            + "fourier is retrieved.");
        assertTrue(test.has_fourier(), "It should have a fourier when the "
            + "fourier is retrieved.");

        field.get_field();
        assertTrue(test.has_field(), "It should still have a field when the "
            + "field has changed, again.");
        assertTrue(!test.has_fourier(), "It should not have a fourier when the "
            + "field has changed.");

        assertTrue(test.fourier() != fourier, "When the fourier has to be "
            + "recreated, it should not be the same object as before.");
        fourier = test.fourier();
        fourier.get_field();
        assertTrue(!test.has_field(), "It should not have a field when the "
            + "fourier has changed.");
        assertTrue(test.has_fourier(), "It should have a fourier when the "
            + "fourier has changed.");

        assertTrue(test.field() != field, "When the field has to be recreated, "
            + "it should not be the same object as before.");
        assertTrue(test.has_field(), "It should have a field when the field is "
            + "retrieved after it was gone.");
        assertTrue(test.has_fourier(), "It should have a fourier when the field"
            + " is retrieved after it was gone.");
    }
    // The rest of these are testing the implementation of the fourier transform
    // IMPORTANT: each of these two-dimensional arrays actually are the
    // transpose of the image being transformed.
    //
    // These test ideas were taken from https://dsp.stackexchange.com/a/635

    // Test that the inverse gives back the original field
    @Test public void test_inverse_even()
    {
        // These values were taken from random.org
        double[][] real = {
            {0.815898329, 0.881980484, 0.182344800, 0.562266110},
            {0.984522410, 0.824962197, 0.338133363, 0.411648243},
            {0.397659590, 0.460967620, 0.462167683, 0.167189351},
            {0.287140619, 0.814748555, 0.725736929, 0.030753733},
        };
        double[][] imag = {
            {0.317941931, 0.847148538, 0.913971166, 0.484473145},
            {0.091406420, 0.357695183, 0.495526878, 0.929687979},
            {0.352819170, 0.963542928, 0.935250921, 0.974076885},
            {0.588840304, 0.342984407, 0.946417343, 0.426974965},
        };
        ReconstructionFieldImpl test = create_field(real, imag);
        test.field_changed(test.fourier());
        ComplexField field = test.field();
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                double fr = field.get_real(x, y);
                double fi = field.get_imag(x, y);
                double or = real[x][y];
                double oi = imag[x][y];
                String coords = "should be the same at (" + x + ", " + y + ").";
                assertEquals(fr, or, 1e-6, "The real part " + coords);
                assertEquals(fi, oi, 1e-6, "The imaginary part " + coords);
            }
        }
    }
    @Test public void test_inverse_odd()
    {
        double[][] real = {
            {0.815898329, 0.881980484, 0.182344800, 0.562266110, 0.456393194},
            {0.984522410, 0.824962197, 0.338133363, 0.411648243, 0.394666991},
            {0.397659590, 0.460967620, 0.462167683, 0.167189351, 0.592582210},
            {0.287140619, 0.814748555, 0.725736929, 0.030753733, 0.793807683},
            {0.955575465, 0.507567210, 0.289375561, 0.461053004, 0.407581372}
        };
        double[][] imag = {
            {0.317941931, 0.847148538, 0.913971166, 0.484473145, 0.411468675},
            {0.091406420, 0.357695183, 0.495526878, 0.929687979, 0.701568679},
            {0.352819170, 0.963542928, 0.935250921, 0.974076885, 0.678930283},
            {0.588840304, 0.342984407, 0.946417343, 0.426974965, 0.896290421},
            {0.125369785, 0.626356339, 0.410700467, 0.190303355, 0.697610398}
        };
        ReconstructionFieldImpl test = create_field(real, imag);
        test.field_changed(test.fourier());
        ComplexField field = test.field();
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                double fr = field.get_real(x, y);
                double fi = field.get_imag(x, y);
                double or = real[x][y];
                double oi = imag[x][y];
                String coords = "should be the same at (" + x + ", " + y + ").";
                assertEquals(fr, or, 1e-6, "The real part " + coords);
                assertEquals(fi, oi, 1e-6, "The imaginary part " + coords);
            }
        }
    }
    @Test public void test_inverse_rectangle()
    {
        double[][] real = {
            {0.815898329, 0.881980484, 0.182344800, 0.562266110, 0.456393194},
            {0.984522410, 0.824962197, 0.338133363, 0.411648243, 0.394666991}
        };
        double[][] imag = {
            {0.588840304, 0.342984407, 0.946417343, 0.426974965, 0.896290421},
            {0.125369785, 0.626356339, 0.410700467, 0.190303355, 0.697610398}
        };
        ReconstructionFieldImpl test = create_field(real, imag);
        test.field_changed(test.fourier());
        ComplexField field = test.field();
        for (int x = 0; x < 2; ++x) {
            for (int y = 0; y < 5; ++y) {
                double fr = field.get_real(x, y);
                double fi = field.get_imag(x, y);
                double or = real[x][y];
                double oi = imag[x][y];
                String coords = "should be the same at (" + x + ", " + y + ").";
                assertEquals(fr, or, 1e-6, "The real part " + coords);
                assertEquals(fi, oi, 1e-6, "The imaginary part " + coords);
            }
        }
    }
    // Test that the relationship F(ax + by) = aF(x) + bF(y)
    @Test public void test_linearity()
    {
        double[][] real1 = {
            {0.815898329, 0.881980484, 0.182344800, 0.562266110, 0.456393194},
            {0.984522410, 0.824962197, 0.338133363, 0.411648243, 0.394666991},
            {0.397659590, 0.460967620, 0.462167683, 0.167189351, 0.592582210},
            {0.287140619, 0.814748555, 0.725736929, 0.030753733, 0.793807683},
            {0.955575465, 0.507567210, 0.289375561, 0.461053004, 0.407581372}
        };
        double[][] imag1 = {
            {0.317941931, 0.847148538, 0.913971166, 0.484473145, 0.411468675},
            {0.091406420, 0.357695183, 0.495526878, 0.929687979, 0.701568679},
            {0.352819170, 0.963542928, 0.935250921, 0.974076885, 0.678930283},
            {0.588840304, 0.342984407, 0.946417343, 0.426974965, 0.896290421},
            {0.125369785, 0.626356339, 0.410700467, 0.190303355, 0.697610398}
        };
        double[][] real2 = {
            {0.879003350, 0.367729686, 0.553954449, 0.779187999, 0.630866790},
            {0.904226447, 0.766780572, 0.800144719, 0.690757529, 0.199009024},
            {0.870764798, 0.908054781, 0.167689113, 0.693923947, 0.409095372},
            {0.687949493, 0.233758166, 0.912003427, 0.458532148, 0.313389530},
            {0.459696140, 0.007289340, 0.864873732, 0.562815265, 0.644018383}
        };
        double[][] imag2 = {
            {0.428298891, 0.791462065, 0.823140224, 0.925204412, 0.035679981},
            {0.262897658, 0.546700537, 0.917782971, 0.996268427, 0.291322025},
            {0.833525629, 0.986018198, 0.568395271, 0.623113131, 0.434309336},
            {0.896312072, 0.788296391, 0.054577468, 0.961109212, 0.320915593},
            {0.892402590, 0.580905819, 0.144466841, 0.063301802, 0.928986986}
        };
        final double a1 = 0.623129568;
        final double a2 = 0.990190876;
        final double a3 = 0.376310383;
        final double a4 = 0.865926190;
        ReconstructionFieldImpl test1 = create_field(real1, imag1);
        ReconstructionFieldImpl test2 = create_field(real2, imag2);
        ComplexField field1 = test1.field();
        ComplexField field2 = test2.field();
        ComplexField fourier1 = test1.fourier();
        ComplexField fourier2 = test2.fourier();

        field1.multiply_in_place(a1, a2);
        field2.multiply_in_place(a3, a4);
        field1.add_in_place(field2);
        ComplexField final1 = test1.fourier();

        fourier1.multiply_in_place(a1, a2);
        fourier2.multiply_in_place(a3, a4);
        fourier1.add_in_place(fourier2);
        ComplexField final2 = fourier1;

        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                double r1 = final1.get_real(x, y);
                double i1 = final1.get_imag(x, y);
                double r2 = final2.get_real(x, y);
                double i2 = final2.get_imag(x, y);
                String coords = "should be the same at (" + x + ", " + y + ").";
                assertEquals(r1, r2, 1e-6, "The real part " + coords);
                assertEquals(i1, i2, 1e-6, "The imaginary part " + coords);
            }
        }
    }
    // Test that F(δ) = 1
    @Test public void test_unit_impulse()
    {
        double[][] real = {
            {1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        double[][] imag = {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        ReconstructionFieldImpl test = create_field(real, imag);
        ComplexField fourier = test.fourier();
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                String coords = "(" + x + ", " + y + ")";
                assertEquals(fourier.get_real(x, y), 1, "The real value at "
                    + coords + " should be one.");
                assertEquals(fourier.get_imag(x, y), 0, "The imaginary value at"
                    + " " + coords + " should be zero.");
            }
        }
    }
    // Test that an offset corresponds to a predictable change in phase, but no
    // change in amplitude
    @Test public void test_offset()
    {
        double[][] real1 = {
            {0.815898329, 0.881980484, 0.182344800, 0.562266110, 0.456393194},
            {0.984522410, 0.824962197, 0.338133363, 0.411648243, 0.394666991},
            {0.397659590, 0.460967620, 0.462167683, 0.167189351, 0.592582210},
            {0.287140619, 0.814748555, 0.725736929, 0.030753733, 0.793807683},
            {0.955575465, 0.507567210, 0.289375561, 0.461053004, 0.407581372}
        };
        double[][] imag1 = {
            {0.317941931, 0.847148538, 0.913971166, 0.484473145, 0.411468675},
            {0.091406420, 0.357695183, 0.495526878, 0.929687979, 0.701568679},
            {0.352819170, 0.963542928, 0.935250921, 0.974076885, 0.678930283},
            {0.588840304, 0.342984407, 0.946417343, 0.426974965, 0.896290421},
            {0.125369785, 0.626356339, 0.410700467, 0.190303355, 0.697610398}
        };
        double[][] real2 = {
            {0.411648243, 0.394666991, 0.984522410, 0.824962197, 0.338133363},
            {0.167189351, 0.592582210, 0.397659590, 0.460967620, 0.462167683},
            {0.030753733, 0.793807683, 0.287140619, 0.814748555, 0.725736929},
            {0.461053004, 0.407581372, 0.955575465, 0.507567210, 0.289375561},
            {0.562266110, 0.456393194, 0.815898329, 0.881980484, 0.182344800}
        };
        double[][] imag2 = {
            {0.929687979, 0.701568679, 0.091406420, 0.357695183, 0.495526878},
            {0.974076885, 0.678930283, 0.352819170, 0.963542928, 0.935250921},
            {0.426974965, 0.896290421, 0.588840304, 0.342984407, 0.946417343},
            {0.190303355, 0.697610398, 0.125369785, 0.626356339, 0.410700467},
            {0.484473145, 0.411468675, 0.317941931, 0.847148538, 0.913971166}
        };
        final int x_shift = -1;
        final int y_shift = 2;
        final int x_size = 5;
        final int y_size = 5;
        ReconstructionFieldImpl test1 = create_field(real1, imag1);
        ReconstructionFieldImpl test2 = create_field(real2, imag2);
        ComplexField fourier1 = test1.fourier();
        ComplexField fourier2 = test2.fourier();
        double[][] amp1 = fourier1.get_amp();
        double[][] amp2 = fourier2.get_amp();
        double[][] arg1 = fourier1.get_arg();
        double[][] arg2 = fourier2.get_arg();
        for (int x = 0; x < x_size; ++x) {
            for (int y = 0; y < y_size; ++y) {
                String coord = "(" + x + ", " + y + ")";
                assertEquals(amp1[x][y], amp2[x][y], 1e-6, "The amplitude of "
                    + "the Fourier transform should be identical at " + coord
                    + " when there is a shift.");
                final int x_distance = 3 - x;
                final int y_distance = 3 - y;
                final double x_change = x_shift * x_distance;
                final double y_change = y_shift * y_distance;
                final double phase_diff = 2*Math.PI * (x_change / x_size +
                                                       y_change / y_size);
                double arg1_mod = phase_diff + arg1[x][y];
                while (arg1_mod > Math.PI) arg1_mod -= 2 * Math.PI;
                while (arg1_mod < -Math.PI) arg1_mod += 2 * Math.PI;
                assertEquals(arg1_mod, arg2[x][y], 1e-6, "The phase of the "
                    + "Fourier transform should be shifted by a set amount at "
                    + coord + " when there is a shift.");
            }
        }
    }

    private ReconstructionFieldImpl create_field(double[][] real,
                                                 double[][] imag)
    {
        return new ReconstructionFieldImpl(real, imag);
    }
}
