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

public interface ComplexField {
    ComplexField copy();

    float[][] get_field();
    float[][] get_real();
    float[][] get_imag();
    float[][] get_amp();
    float[][] get_arg();
    float get(int x, int y);

    void negate_in_place();
    void add_in_place(ComplexField other);
    void subtract_in_place(ComplexField other);
    void multiply_in_place(ComplexField other);
    void divide_in_place(ComplexField other);
    void fourier_in_place();
    void inverse_fourier_in_place();

    default ComplexField negate()
    {
        ComplexField result = copy();
        result.negate_in_place();
        return result;
    }
    default ComplexField add(ComplexField other)
    {
        ComplexField result = copy();
        result.add_in_place(other);
        return result;
    }
    default ComplexField subtract(ComplexField other)
    {
        ComplexField result = copy();
        result.subtract_in_place(other);
        return result;
    }
    default ComplexField multiply(ComplexField other)
    {
        ComplexField result = copy();
        result.multiply_in_place(other);
        return result;
    }
    default ComplexField divide(ComplexField other)
    {
        ComplexField result = copy();
        result.divide_in_place(other);
        return result;
    }
    default ComplexField fourier()
    {
        ComplexField result = copy();
        result.fourier_in_place();
        return result;
    }
    default ComplexField inverse_fourier()
    {
        ComplexField result = copy();
        result.inverse_fourier_in_place();
        return result;
    }
}
