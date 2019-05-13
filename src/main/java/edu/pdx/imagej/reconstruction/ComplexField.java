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

    double[][] get_field();
    double[][] get_real();
    double[][] get_imag();
    double[][] get_amp();
    double[][] get_amp2();
    double[][] get_arg();
    double get_real(int x, int y);
    double get_imag(int x, int y);
    int width();
    int height();

    void negate_in_place();
    void add_in_place(ComplexField other);
    void subtract_in_place(ComplexField other);
    void multiply_in_place(ComplexField other);
    void divide_in_place(ComplexField other);
    void add_in_place(double[][] other);
    void subtract_in_place(double[][] other);
    void multiply_in_place(double[][] other);
    void divide_in_place(double[][] other);
    void add_in_place(double real, double imag);
    void subtract_in_place(double real, double imag);
    void multiply_in_place(double real, double imag);
    void divide_in_place(double real, double imag);
    default void add_in_place(double real) {add_in_place(real, 0);}
    default void subtract_in_place(double real) {subtract_in_place(real, 0);}
    default void multiply_in_place(double real) {multiply_in_place(real, 0);}
    default void divide_in_place(double real) {divide_in_place(real, 0);}

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
    default ComplexField add(double[][] other)
    {
        ComplexField result = copy();
        result.add_in_place(other);
        return result;
    }
    default ComplexField subtract(double[][] other)
    {
        ComplexField result = copy();
        result.subtract_in_place(other);
        return result;
    }
    default ComplexField multiply(double[][] other)
    {
        ComplexField result = copy();
        result.multiply_in_place(other);
        return result;
    }
    default ComplexField divide(double[][] other)
    {
        ComplexField result = copy();
        result.divide_in_place(other);
        return result;
    }
    default ComplexField add(double real, double imag)
    {
        ComplexField result = copy();
        result.add_in_place(real, imag);
        return result;
    }
    default ComplexField subtract(double real, double imag)
    {
        ComplexField result = copy();
        result.subtract_in_place(real, imag);
        return result;
    }
    default ComplexField multiply(double real, double imag)
    {
        ComplexField result = copy();
        result.multiply_in_place(real, imag);
        return result;
    }
    default ComplexField divide(double real, double imag)
    {
        ComplexField result = copy();
        result.divide_in_place(real, imag);
        return result;
    }
}
