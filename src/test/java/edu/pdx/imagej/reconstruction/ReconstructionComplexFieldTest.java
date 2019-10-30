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
    @Test public void testGet()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = createField(values);
        assertEquals(field.getReal(0, 0), 1);
        assertEquals(field.getImag(0, 0), 0);
        assertEquals(field.getReal(0, 1), 0);
        assertEquals(field.getImag(0, 1), 1);
        assertEquals(field.getReal(1, 0), -1);
        assertEquals(field.getImag(1, 0), 0);
        assertEquals(field.getReal(1, 1), 0);
        assertEquals(field.getImag(1, 1), -1);
    }
    @Test public void testShiftEven()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = createField(values);
        field.shiftForward();
        assertEquals(field.getReal(0, 0), 0);
        assertEquals(field.getImag(0, 0), -1);
        assertEquals(field.getReal(0, 1), -1);
        assertEquals(field.getImag(0, 1), 0);
        assertEquals(field.getReal(1, 0), 0);
        assertEquals(field.getImag(1, 0), 1);
        assertEquals(field.getReal(1, 1), 1);
        assertEquals(field.getImag(1, 1), 0);
        field.shiftBackward();
        assertEquals(field.getReal(0, 0), 1);
        assertEquals(field.getImag(0, 0), 0);
        assertEquals(field.getReal(0, 1), 0);
        assertEquals(field.getImag(0, 1), 1);
        assertEquals(field.getReal(1, 0), -1);
        assertEquals(field.getImag(1, 0), 0);
        assertEquals(field.getReal(1, 1), 0);
        assertEquals(field.getImag(1, 1), -1);
    }
    @Test public void testShiftOdd()
    {
        double[][] values = {
            {1, 0,  2, 0,  3, 0},
            {4, 0,  5, 0,  6, 0},
            {7, 0,  8, 0,  9, 0}
        };
        ReconstructionComplexField field = createField(values);
        field.shiftForward();
        assertEquals(field.getReal(0, 0), 5);
        assertEquals(field.getReal(0, 1), 6);
        assertEquals(field.getReal(0, 2), 4);
        assertEquals(field.getReal(1, 0), 8);
        assertEquals(field.getReal(1, 1), 9);
        assertEquals(field.getReal(1, 2), 7);
        assertEquals(field.getReal(2, 0), 2);
        assertEquals(field.getReal(2, 1), 3);
        assertEquals(field.getReal(2, 2), 1);
        field.shiftBackward();
        assertEquals(field.getReal(0, 0), 1);
        assertEquals(field.getReal(0, 1), 2);
        assertEquals(field.getReal(0, 2), 3);
        assertEquals(field.getReal(1, 0), 4);
        assertEquals(field.getReal(1, 1), 5);
        assertEquals(field.getReal(1, 2), 6);
        assertEquals(field.getReal(2, 0), 7);
        assertEquals(field.getReal(2, 1), 8);
        assertEquals(field.getReal(2, 2), 9);
    }
    @Test public void testShiftRectangle()
    {
        double[][] values = {
            {0, 0,  1, 0,  2, 0,  3, 0,  4, 0},
            {5, 0,  6, 0,  7, 0,  8, 0,  9, 0}
        };
        ReconstructionComplexField field = createField(values);
        field.shiftForward();
        assertEquals(field.getReal(0, 0), 7);
        assertEquals(field.getReal(0, 1), 8);
        assertEquals(field.getReal(0, 2), 9);
        assertEquals(field.getReal(0, 3), 5);
        assertEquals(field.getReal(0, 4), 6);
        assertEquals(field.getReal(1, 0), 2);
        assertEquals(field.getReal(1, 1), 3);
        assertEquals(field.getReal(1, 2), 4);
        assertEquals(field.getReal(1, 3), 0);
        assertEquals(field.getReal(1, 4), 1);
        field.shiftBackward();
        assertEquals(field.getReal(0, 0), 0);
        assertEquals(field.getReal(0, 1), 1);
        assertEquals(field.getReal(0, 2), 2);
        assertEquals(field.getReal(0, 3), 3);
        assertEquals(field.getReal(0, 4), 4);
        assertEquals(field.getReal(1, 0), 5);
        assertEquals(field.getReal(1, 1), 6);
        assertEquals(field.getReal(1, 2), 7);
        assertEquals(field.getReal(1, 3), 8);
        assertEquals(field.getReal(1, 4), 9);
    }
    @Test public void testCopy()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = createField(values);
        ReconstructionComplexField copy = field.copy();
        for (int x = 0; x < field.width(); ++x) {
            for (int y = 0; y < field.height(); ++y) {
                String coords = "(" + x + ", " + y + ").";
                assertEquals(field.getReal(x, y), copy.getReal(x, y),
                    "Copying the real part should work at " + coords);
                assertEquals(field.getImag(x, y), copy.getImag(x, y),
                    "Copying the imaginary part should work at " + coords);
            }
        }
    }
    @Test public void testGetAmp()
    {
        double[][] values = {
            {0,  0,   1,  0},
            {3, -4,   0, -1}
        };
        ReconstructionComplexField field = createField(values);
        double[][] amp = field.getAmp();
        assertEquals(amp[0][0], 0);
        assertEquals(amp[0][1], 1);
        assertEquals(amp[1][0], 5);
        assertEquals(amp[1][1], 1);
    }
    @Test public void testGetAmp2()
    {
        double[][] values = {
            {0,  0,   1,  0},
            {3, -4,   0, -1}
        };
        ReconstructionComplexField field = createField(values);
        double[][] amp2 = field.getAmp2();
        assertEquals(amp2[0][0], 0);
        assertEquals(amp2[0][1], 1);
        assertEquals(amp2[1][0], 25);
        assertEquals(amp2[1][1], 1);
    }
    @Test public void testGetArg()
    {
        double[][] values = {
            { 1, 0,    0,  1,   0, -1},
            {-1, 1,   -1, -1,   1, Math.sqrt(3)}
        };
        ReconstructionComplexField field = createField(values);
        double[][] arg = field.getArg();
        assertEquals(arg[0][0], 0);
        assertEquals(arg[0][1], Math.PI / 2);
        assertEquals(arg[0][2], -Math.PI / 2);
        assertEquals(arg[1][0], Math.PI * 3 / 4);
        assertEquals(arg[1][1], -Math.PI * 3 / 4);
        assertEquals(arg[1][2], Math.PI / 3);
    }
    @Test public void testDimensions()
    {
        double[][] values = {
            { 1, 0,    0,  1,   0, -1},
            {-1, 1,   -1, -1,   1, Math.sqrt(3)}
        };
        ReconstructionComplexField field = createField(values);
        assertEquals(field.width(), 2);
        assertEquals(field.height(), 3);
    }
    @Test public void testNegate()
    {
        double[][] values = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field = createField(values);
        ComplexField neg = field.negate();
        for (int x = 0; x < field.width(); ++x) {
            for (int y = 0; y < field.height(); ++y) {
                String coords = "(" + x + ", " + y + ").";
                assertEquals(field.getReal(x, y), -neg.getReal(x, y),
                    "Negating the real part should work at " + coords);
                assertEquals(field.getImag(x, y), -neg.getImag(x, y),
                    "Negating the imaginary part should work at " + coords);
            }
        }
    }
    @Test public void testAdd()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = createField(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 0,   8, -3.5}
        };
        ReconstructionComplexField field2 = createField(values2);
        ComplexField sum = field1.add(field2);
        assertEquals(field1.getReal(0, 0), 1, "Add should not affect the "
            + "original field.");

        assertEquals(sum.getReal(0, 0), 2);
        assertEquals(sum.getImag(0, 0), 0);
        assertEquals(sum.getReal(0, 1), 1);
        assertEquals(sum.getImag(0, 1), 3);
        assertEquals(sum.getReal(1, 0), -1);
        assertEquals(sum.getImag(1, 0), 0);
        assertEquals(sum.getReal(1, 1), 8);
        assertEquals(sum.getImag(1, 1), -4.5);
    }
    @Test public void testSubtract()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = createField(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 0,   8, -3.5}
        };
        ReconstructionComplexField field2 = createField(values2);
        ComplexField difference = field1.subtract(field2);
        assertEquals(field1.getReal(0, 0), 1, "Subtract should not affect the "
            + "original field.");

        assertEquals(difference.getReal(0, 0), 0);
        assertEquals(difference.getImag(0, 0), 0);
        assertEquals(difference.getReal(0, 1), -1);
        assertEquals(difference.getImag(0, 1), -1);
        assertEquals(difference.getReal(1, 0), -1);
        assertEquals(difference.getImag(1, 0), 0);
        assertEquals(difference.getReal(1, 1), -8);
        assertEquals(difference.getImag(1, 1), 2.5);
    }
    @Test public void testMultiply()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = createField(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 0,   8, -3.5}
        };
        ReconstructionComplexField field2 = createField(values2);
        ComplexField product = field1.multiply(field2);
        assertEquals(field1.getReal(0, 0), 1, "Multiply should not affect the "
            + "original field.");

        assertEquals(product.getReal(0, 0), 1);
        assertEquals(product.getImag(0, 0), 0);
        assertEquals(product.getReal(0, 1), -2);
        assertEquals(product.getImag(0, 1), 1);
        assertEquals(product.getReal(1, 0), -0.0);
        assertEquals(product.getImag(1, 0), 0);
        assertEquals(product.getReal(1, 1), -3.5);
        assertEquals(product.getImag(1, 1), -8);
    }
    @Test public void testDivide()
    {
        double[][] values1 = {
            { 1, 0,   0,  1},
            {-1, 0,   0, -1}
        };
        ReconstructionComplexField field1 = createField(values1);
        double[][] values2 = {
            { 1, 0,   1,  2},
            { 0, 1,   1, -0.5}
        };
        ReconstructionComplexField field2 = createField(values2);
        ComplexField quotient = field1.divide(field2);
        assertEquals(field1.getReal(0, 0), 1, "Divide should not affect the "
            + "original field.");

        assertEquals(quotient.getReal(0, 0), 1);
        assertEquals(quotient.getImag(0, 0), 0);
        assertEquals(quotient.getReal(0, 1), 0.4);
        assertEquals(quotient.getImag(0, 1), 0.2);
        assertEquals(quotient.getReal(1, 0), 0);
        assertEquals(quotient.getImag(1, 0), 1);
        assertEquals(quotient.getReal(1, 1), 0.4);
        assertEquals(quotient.getImag(1, 1), -0.8);
    }
    @Test public void testAddSingle()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = createField(values);
        ComplexField sum = field.add(1, 2);
        assertEquals(field.getReal(0, 0), 1, "Add should not affect the "
            + "original field.");

        assertEquals(sum.getReal(0, 0), 2);
        assertEquals(sum.getImag(0, 0), 2);
        assertEquals(sum.getReal(0, 1), 2);
        assertEquals(sum.getImag(0, 1), 4);
        assertEquals(sum.getReal(1, 0), 1);
        assertEquals(sum.getImag(1, 0), 2);
        assertEquals(sum.getReal(1, 1), 9);
        assertEquals(sum.getImag(1, 1), -1.5);
    }
    @Test public void testSubtractSingle()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = createField(values);
        ComplexField difference = field.subtract(1, 2);
        assertEquals(field.getReal(0, 0), 1, "Subtract should not affect the "
            + "original field.");

        assertEquals(difference.getReal(0, 0), 0);
        assertEquals(difference.getImag(0, 0), -2);
        assertEquals(difference.getReal(0, 1), 0);
        assertEquals(difference.getImag(0, 1), 0);
        assertEquals(difference.getReal(1, 0), -1);
        assertEquals(difference.getImag(1, 0), -2);
        assertEquals(difference.getReal(1, 1), 7);
        assertEquals(difference.getImag(1, 1), -5.5);
    }
    @Test public void testMultiplySingle()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = createField(values);
        ComplexField product = field.multiply(1, 2);
        assertEquals(field.getReal(0, 0), 1, "Multiply should not affect the "
            + "original field.");

        assertEquals(product.getReal(0, 0), 1);
        assertEquals(product.getImag(0, 0), 2);
        assertEquals(product.getReal(0, 1), -3);
        assertEquals(product.getImag(0, 1), 4);
        assertEquals(product.getReal(1, 0), 0);
        assertEquals(product.getImag(1, 0), 0);
        assertEquals(product.getReal(1, 1), 15);
        assertEquals(product.getImag(1, 1), 12.5);
    }
    @Test public void testDivideSingle()
    {
        double[][] values = {
            {1, 0,   1,  2},
            {0, 0,   8, -3.5}
        };
        ReconstructionComplexField field = createField(values);
        ComplexField quotient = field.divide(1, 2);
        assertEquals(field.getReal(0, 0), 1, "Divide should not affect the "
            + "original field.");

        assertEquals(quotient.getReal(0, 0), 0.2);
        assertEquals(quotient.getImag(0, 0), -0.4);
        assertEquals(quotient.getReal(0, 1), 1);
        assertEquals(quotient.getImag(0, 1), 0);
        assertEquals(quotient.getReal(1, 0), 0);
        assertEquals(quotient.getImag(1, 0), 0);
        assertEquals(quotient.getReal(1, 1), 0.2);
        assertEquals(quotient.getImag(1, 1), -3.9);
    }

    private ReconstructionComplexField createField(double[][] values)
    {
        return new ReconstructionComplexField(values, null);
    }
}
