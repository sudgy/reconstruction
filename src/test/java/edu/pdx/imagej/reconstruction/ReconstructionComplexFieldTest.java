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

public class ReconstructionComplexFieldTest {
    @Test public void test_get()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = create_field(values);
        assertEquals(field.get_real(0, 0), 1);
        assertEquals(field.get_imag(0, 0), 0);
        assertEquals(field.get_real(0, 1), 0);
        assertEquals(field.get_imag(0, 1), 1);
        assertEquals(field.get_real(1, 0), -1);
        assertEquals(field.get_imag(1, 0), 0);
        assertEquals(field.get_real(1, 1), 0);
        assertEquals(field.get_imag(1, 1), -1);
    }
    @Test public void test_shift_even()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = create_field(values);
        field.shift_forward();
        assertEquals(field.get_real(0, 0), 0);
        assertEquals(field.get_imag(0, 0), -1);
        assertEquals(field.get_real(0, 1), -1);
        assertEquals(field.get_imag(0, 1), 0);
        assertEquals(field.get_real(1, 0), 0);
        assertEquals(field.get_imag(1, 0), 1);
        assertEquals(field.get_real(1, 1), 1);
        assertEquals(field.get_imag(1, 1), 0);
        field.shift_backward();
        assertEquals(field.get_real(0, 0), 1);
        assertEquals(field.get_imag(0, 0), 0);
        assertEquals(field.get_real(0, 1), 0);
        assertEquals(field.get_imag(0, 1), 1);
        assertEquals(field.get_real(1, 0), -1);
        assertEquals(field.get_imag(1, 0), 0);
        assertEquals(field.get_real(1, 1), 0);
        assertEquals(field.get_imag(1, 1), -1);
    }
    @Test public void test_shift_odd()
    {
        double[][] values = {
            {1, 0,  2, 0,  3, 0},
            {4, 0,  5, 0,  6, 0},
            {7, 0,  8, 0,  9, 0}
        };
        ReconstructionComplexField field = create_field(values);
        field.shift_forward();
        assertEquals(field.get_real(0, 0), 5);
        assertEquals(field.get_real(0, 1), 6);
        assertEquals(field.get_real(0, 2), 4);
        assertEquals(field.get_real(1, 0), 8);
        assertEquals(field.get_real(1, 1), 9);
        assertEquals(field.get_real(1, 2), 7);
        assertEquals(field.get_real(2, 0), 2);
        assertEquals(field.get_real(2, 1), 3);
        assertEquals(field.get_real(2, 2), 1);
        field.shift_backward();
        assertEquals(field.get_real(0, 0), 1);
        assertEquals(field.get_real(0, 1), 2);
        assertEquals(field.get_real(0, 2), 3);
        assertEquals(field.get_real(1, 0), 4);
        assertEquals(field.get_real(1, 1), 5);
        assertEquals(field.get_real(1, 2), 6);
        assertEquals(field.get_real(2, 0), 7);
        assertEquals(field.get_real(2, 1), 8);
        assertEquals(field.get_real(2, 2), 9);
    }
    @Test public void test_shift_rectangle()
    {
        double[][] values = {
            {0, 0,  1, 0,  2, 0,  3, 0,  4, 0},
            {5, 0,  6, 0,  7, 0,  8, 0,  9, 0}
        };
        ReconstructionComplexField field = create_field(values);
        field.shift_forward();
        assertEquals(field.get_real(0, 0), 7);
        assertEquals(field.get_real(0, 1), 8);
        assertEquals(field.get_real(0, 2), 9);
        assertEquals(field.get_real(0, 3), 5);
        assertEquals(field.get_real(0, 4), 6);
        assertEquals(field.get_real(1, 0), 2);
        assertEquals(field.get_real(1, 1), 3);
        assertEquals(field.get_real(1, 2), 4);
        assertEquals(field.get_real(1, 3), 0);
        assertEquals(field.get_real(1, 4), 1);
        field.shift_backward();
        assertEquals(field.get_real(0, 0), 0);
        assertEquals(field.get_real(0, 1), 1);
        assertEquals(field.get_real(0, 2), 2);
        assertEquals(field.get_real(0, 3), 3);
        assertEquals(field.get_real(0, 4), 4);
        assertEquals(field.get_real(1, 0), 5);
        assertEquals(field.get_real(1, 1), 6);
        assertEquals(field.get_real(1, 2), 7);
        assertEquals(field.get_real(1, 3), 8);
        assertEquals(field.get_real(1, 4), 9);
    }
    @Test public void test_copy()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = create_field(values);
        ReconstructionComplexField copy = field.copy();
        for (int x = 0; x < field.width(); ++x) {
            for (int y = 0; y < field.height(); ++y) {
                String coords = "(" + x + ", " + y + ").";
                assertEquals(field.get_real(x, y), copy.get_real(x, y),
                    "Copying the real part should work at " + coords);
                assertEquals(field.get_imag(x, y), copy.get_imag(x, y),
                    "Copying the imaginary part should work at " + coords);
            }
        }
    }
    @Test public void test_get_amp()
    {
        double[][] values = {
            {0,  0,   1,  0},
            {3, -4,   0, -1}
        };
        ReconstructionComplexField field = create_field(values);
        double[][] amp = field.get_amp();
        assertEquals(amp[0][0], 0);
        assertEquals(amp[0][1], 1);
        assertEquals(amp[1][0], 5);
        assertEquals(amp[1][1], 1);
    }
    @Test public void test_get_amp2()
    {
        double[][] values = {
            {0,  0,   1,  0},
            {3, -4,   0, -1}
        };
        ReconstructionComplexField field = create_field(values);
        double[][] amp2 = field.get_amp2();
        assertEquals(amp2[0][0], 0);
        assertEquals(amp2[0][1], 1);
        assertEquals(amp2[1][0], 25);
        assertEquals(amp2[1][1], 1);
    }
    @Test public void test_get_arg()
    {
        double[][] values = {
            { 1, 0,    0,  1,   0, -1},
            {-1, 1,   -1, -1,   1, Math.sqrt(3)}
        };
        ReconstructionComplexField field = create_field(values);
        double[][] arg = field.get_arg();
        assertEquals(arg[0][0], 0);
        assertEquals(arg[0][1], Math.PI / 2);
        assertEquals(arg[0][2], -Math.PI / 2);
        assertEquals(arg[1][0], Math.PI * 3 / 4);
        assertEquals(arg[1][1], -Math.PI * 3 / 4);
        assertEquals(arg[1][2], Math.PI / 3);
    }
    @Test public void test_dimensions()
    {
        double[][] values = {
            { 1, 0,    0,  1,   0, -1},
            {-1, 1,   -1, -1,   1, Math.sqrt(3)}
        };
        ReconstructionComplexField field = create_field(values);
        assertEquals(field.width(), 2);
        assertEquals(field.height(), 3);
    }
    @Test public void test_negate()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = create_field(values);
        ComplexField neg = field.negate();
        for (int x = 0; x < field.width(); ++x) {
            for (int y = 0; y < field.height(); ++y) {
                String coords = "(" + x + ", " + y + ").";
                assertEquals(field.get_real(x, y), -neg.get_real(x, y),
                    "Negating the real part should work at " + coords);
                assertEquals(field.get_imag(x, y), -neg.get_imag(x, y),
                    "Negating the imaginary part should work at " + coords);
            }
        }
    }
    @Test public void test_add()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = create_field(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 0,   8, -3.5}
        };
        ReconstructionComplexField field2 = create_field(values2);
        ComplexField sum = field1.add(field2);
        assertEquals(field1.get_real(0, 0), 1, "Add should not affect the "
            + "original field.");

        assertEquals(sum.get_real(0, 0), 2);
        assertEquals(sum.get_imag(0, 0), 0);
        assertEquals(sum.get_real(0, 1), 1);
        assertEquals(sum.get_imag(0, 1), 3);
        assertEquals(sum.get_real(1, 0), -1);
        assertEquals(sum.get_imag(1, 0), 0);
        assertEquals(sum.get_real(1, 1), 8);
        assertEquals(sum.get_imag(1, 1), -4.5);
    }
    @Test public void test_subtract()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = create_field(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 0,   8, -3.5}
        };
        ReconstructionComplexField field2 = create_field(values2);
        ComplexField difference = field1.subtract(field2);
        assertEquals(field1.get_real(0, 0), 1, "Subtract should not affect the "
            + "original field.");

        assertEquals(difference.get_real(0, 0), 0);
        assertEquals(difference.get_imag(0, 0), 0);
        assertEquals(difference.get_real(0, 1), -1);
        assertEquals(difference.get_imag(0, 1), -1);
        assertEquals(difference.get_real(1, 0), -1);
        assertEquals(difference.get_imag(1, 0), 0);
        assertEquals(difference.get_real(1, 1), -8);
        assertEquals(difference.get_imag(1, 1), 2.5);
    }
    @Test public void test_multiply()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = create_field(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 0,   8, -3.5}
        };
        ReconstructionComplexField field2 = create_field(values2);
        ComplexField product = field1.multiply(field2);
        assertEquals(field1.get_real(0, 0), 1, "Multiply should not affect the "
            + "original field.");

        assertEquals(product.get_real(0, 0), 1);
        assertEquals(product.get_imag(0, 0), 0);
        assertEquals(product.get_real(0, 1), -2);
        assertEquals(product.get_imag(0, 1), 1);
        assertEquals(product.get_real(1, 0), -0.0);
        assertEquals(product.get_imag(1, 0), 0);
        assertEquals(product.get_real(1, 1), -3.5);
        assertEquals(product.get_imag(1, 1), -8);
    }
    @Test public void test_divide()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = create_field(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 1,   1, -0.5}
        };
        ReconstructionComplexField field2 = create_field(values2);
        ComplexField quotient = field1.divide(field2);
        assertEquals(field1.get_real(0, 0), 1, "Divide should not affect the "
            + "original field.");

        assertEquals(quotient.get_real(0, 0), 1);
        assertEquals(quotient.get_imag(0, 0), 0);
        assertEquals(quotient.get_real(0, 1), 0.4);
        assertEquals(quotient.get_imag(0, 1), 0.2);
        assertEquals(quotient.get_real(1, 0), 0);
        assertEquals(quotient.get_imag(1, 0), 1);
        assertEquals(quotient.get_real(1, 1), 0.4);
        assertEquals(quotient.get_imag(1, 1), -0.8);
    }
    @Test public void test_add_single()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = create_field(values);
        ComplexField sum = field.add(1, 2);
        assertEquals(field.get_real(0, 0), 1, "Add should not affect the "
            + "original field.");

        assertEquals(sum.get_real(0, 0), 2);
        assertEquals(sum.get_imag(0, 0), 2);
        assertEquals(sum.get_real(0, 1), 2);
        assertEquals(sum.get_imag(0, 1), 4);
        assertEquals(sum.get_real(1, 0), 1);
        assertEquals(sum.get_imag(1, 0), 2);
        assertEquals(sum.get_real(1, 1), 9);
        assertEquals(sum.get_imag(1, 1), -1.5);
    }
    @Test public void test_subtract_single()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = create_field(values);
        ComplexField difference = field.subtract(1, 2);
        assertEquals(field.get_real(0, 0), 1, "Subtract should not affect the "
            + "original field.");

        assertEquals(difference.get_real(0, 0), 0);
        assertEquals(difference.get_imag(0, 0), -2);
        assertEquals(difference.get_real(0, 1), 0);
        assertEquals(difference.get_imag(0, 1), 0);
        assertEquals(difference.get_real(1, 0), -1);
        assertEquals(difference.get_imag(1, 0), -2);
        assertEquals(difference.get_real(1, 1), 7);
        assertEquals(difference.get_imag(1, 1), -5.5);
    }
    @Test public void test_multiply_single()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = create_field(values);
        ComplexField product = field.multiply(1, 2);
        assertEquals(field.get_real(0, 0), 1, "Multiply should not affect the "
            + "original field.");

        assertEquals(product.get_real(0, 0), 1);
        assertEquals(product.get_imag(0, 0), 2);
        assertEquals(product.get_real(0, 1), -3);
        assertEquals(product.get_imag(0, 1), 4);
        assertEquals(product.get_real(1, 0), 0);
        assertEquals(product.get_imag(1, 0), 0);
        assertEquals(product.get_real(1, 1), 15);
        assertEquals(product.get_imag(1, 1), 12.5);
    }
    @Test public void test_divide_single()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = create_field(values);
        ComplexField quotient = field.divide(1, 2);
        assertEquals(field.get_real(0, 0), 1, "Divide should not affect the "
            + "original field.");

        assertEquals(quotient.get_real(0, 0), 0.2);
        assertEquals(quotient.get_imag(0, 0), -0.4);
        assertEquals(quotient.get_real(0, 1), 1);
        assertEquals(quotient.get_imag(0, 1), 0);
        assertEquals(quotient.get_real(1, 0), 0);
        assertEquals(quotient.get_imag(1, 0), 0);
        assertEquals(quotient.get_real(1, 1), 0.2);
        assertEquals(quotient.get_imag(1, 1), -3.9);
    }

    private ReconstructionComplexField create_field(double[][] values)
    {
        return new ReconstructionComplexField(values, null);
    }
}
