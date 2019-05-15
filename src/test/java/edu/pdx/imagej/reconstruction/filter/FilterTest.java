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

package edu.pdx.imagej.reconstruction.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ij.gui.PointRoi;

import edu.pdx.imagej.reconstruction.ComplexField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

public class FilterTest {
    @Test public void test_no_roi()
    {
        Filter test = new Filter();
        double[][] real = {
            {1, 2},
            {3, 4}
        };
        double[][] imag = {
            {5, 6},
            {7, 8}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        ComplexField fourier = field.fourier().copy();
        test.filter_field(field);
        assertEquals(field.fourier().get_real(0, 0), fourier.get_real(0, 0));
        assertEquals(field.fourier().get_real(0, 1), fourier.get_real(0, 1));
        assertEquals(field.fourier().get_real(1, 0), fourier.get_real(1, 0));
        assertEquals(field.fourier().get_real(1, 1), fourier.get_real(1, 1));
        assertEquals(field.fourier().get_imag(0, 0), fourier.get_imag(0, 0));
        assertEquals(field.fourier().get_imag(0, 1), fourier.get_imag(0, 1));
        assertEquals(field.fourier().get_imag(1, 0), fourier.get_imag(1, 0));
        assertEquals(field.fourier().get_imag(1, 1), fourier.get_imag(1, 1));
    }
    @Test public void test_filter()
    {
        Filter test = new Filter();
        test.M_roi = new PointRoi(0, 0);
        double[][] real = {
            {1, 2, 3},
            {3, 4, 5},
            {5, 6, 7}
        };
        double[][] imag = {
            {11, 12, 13},
            {13, 14, 15},
            {15, 16, 17}
        };
        ReconstructionField field = new ReconstructionFieldImpl(real, imag);
        ComplexField fourier = field.fourier().copy();
        test.filter_field(field);
        assertEquals(field.fourier().get_real(1, 1), fourier.get_real(0, 0));
        assertEquals(field.fourier().get_imag(1, 1), fourier.get_imag(0, 0));
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                if (x == 1 && y == 1) continue;
                assertEquals(field.fourier().get_real(1, 1), 0);
                assertEquals(field.fourier().get_imag(1, 1), 0);
            }
        }
    }
}
