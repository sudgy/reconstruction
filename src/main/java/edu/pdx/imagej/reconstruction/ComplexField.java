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

/** ComplexField represents a two-dimensional array of complex numbers.  It has
 * very basic complex arithmetic defined, and also simple methods to get the
 * values in various forms.
 * <p>
 * When used in conjunction with {@link ReconstructionField}, modifying methods
 * are very different than non-modifying methods.  In general, modifying methods
 * might require the FFT or IFFT to be calculated again, and this is an
 * expensive operation.  As such, you should only call modifying methods if you
 * have to.  All methods below are identified as a modifying or non-modifying
 * operation.
 * <p>
 * In describing the following methods, each complex number is described as a +
 * bi, and if another complex number is mentioned it is c + di.
 */
public interface ComplexField {
    /** Create an identical copy of this ComplexField.  This is a non-modifying
     * operation.
     * <p>
     * Note that if this ComplexField was in a {@link ReconstructionField}, the
     * copy will not be associated with it.
     *
     * @return A copy of this ComplexField.
     */
    ComplexField copy();

    /** Get a reference to the data.  <strong>This is a modifying operation
     * </strong>, even if you don't change anything.  This class has no way of
     * knowing what you do with the result, so it has to assume you have changed
     * it.  You are do not want to modify it, you should probably use another
     * method.
     * <p>
     * The dimensions of the returned array are <code>[{@link width}][{@link
     * height} * 2]</code>, where <code>[x][2*y]</code> is the real value at (x,
     * y) and <code>[x][2*y+1]</code> is the imaginary value at (x, y).
     *
     * @return A reference to the data representing this ComplexField.
     */
    double[][] get_field();
    /** Set the data for this ComplexField.  This is a modifying operation.
     *
     * @param field The new data to represent this ComplexField.
     */
    void set_field(double[][] field);
    /** Get a copy of the real values of this ComplexField.  This is a
     * non-modifying operation.
     *
     * @return A copy of the real values of this ComplexField.
     */
    double[][] get_real();
    /** Get a copy of the imaginary values of this ComplexField.  This is a
     * non-modifying operation.
     *
     * @return A copy of the imaginary values of this ComplexField.
     */
    double[][] get_imag();
    /** Get a copy of the amplitude values of this ComplexField.  This is a
     * non-modifying operation.  The result is sqrt(a² + b²).
     *
     * @return A copy of the amplitude values of this ComplexField.
     */
    double[][] get_amp();
    /** Get a copy of the amplitude squared values of this ComplexField.  This
     * is a non-modifying operation.  The result is a² + b².
     *
     * @return A copy of the amplitude squared values of this ComplexField.
     */
    double[][] get_amp2();
    /** Get a copy of the phase values of this ComplexField.  This is a
     * non-modifying operation.  The result is atan2(b, a).
     *
     * @return A copy of the phase values of this ComplexField.
     */
    double[][] get_arg();
    /** Get a single real value from this ComplexField.  This is a non-modifying
     * operation.
     *
     * @param x The x coordinate to get the value from
     * @param y The y coordinate to get the value from
     * @return The real value corresponding to the x and y coordinates
     */
    double get_real(int x, int y);
    /** Get a single imaginary value from this ComplexField.  This is a
     * non-modifying operation.
     *
     * @param x The x coordinate to get the value from
     * @param y The y coordinate to get the value from
     * @return The imaginary value corresponding to the x and y coordinates
     */
    double get_imag(int x, int y);
    /** Get the width of this ComplexField.  This is a non-modifying operation.
     *
     * @return The width of this ComplexField.
     */
    int width();
    /** Get the height of this ComplexField.  This is a non-modifying operation.
     *
     * @return The height of this ComplexField.
     */
    int height();

    /** Negate this ComplexField.  This is a modifying operation.  The result is
     * -a - bi.
     */
    void negate_in_place();
    /** Add a ComplexField to this one.  This is a modifying operation.  The
     * result is (a + c) + (b + d)i.
     *
     * @param other The ComplexField to add to this one.
     */
    void add_in_place(ComplexField other);
    /** Subtract another ComplexField from this one.  This is a modifying
     * operation.  The result is (a - c) + (b - d)i.
     *
     * @param other The ComplexField to subtract with.
     */
    void subtract_in_place(ComplexField other);
    /** Multiply a ComplexField with this one.  This is a modifying operation.
     * The result is (ac - bd) + (ad + bc)i.
     *
     * @param other The ComplexField to multiply with this one.
     */
    void multiply_in_place(ComplexField other);
    /** Divide another ComplexField from this one.  This is a modifying
     * operation.  The result is (ac + bd)/(c² + d²) + ((bc - ad)/(c² + d²))i
     *
     * @param other The ComplexField to divide with.
     */
    void divide_in_place(ComplexField other);
    /** Add an array to this ComplexField.  This is a modifying operation.  The
     * result is (a + c) + (b + d)i.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     */
    void add_in_place(double[][] other);
    /** Subtract an array from this ComplexField.  This is a modifying
     * operation.  The result is (a - c) + (b - d)i.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     */
    void subtract_in_place(double[][] other);
    /** Multiply an array with this ComplexField.  This is a modifying
     * operation. The result is (ac - bd) + (ad + bc)i.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     */
    void multiply_in_place(double[][] other);
    /** Divide an array from this ComplexField.  This is a modifying operation.
     * The result is (ac + bd)/(c² + d²) + ((bc - ad)/(c² + d²))i
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     */
    void divide_in_place(double[][] other);
    /** Add a single value to this ComplexField.  This is a modifying operation.
     * The result is (a + c) + (b + d)i, with c = real and d = imag.
     *
     * @param real The real value for the complex number to add.
     * @param imag The imaginary value for the complex number to add.
     */
    void add_in_place(double real, double imag);
    /** Subtract a single value from this ComplexField.  This is a modifying
     * operation.  The result is (a - c) + (b - d)i, with c = real and d = imag.
     *
     * @param real The real value for the complex number to subtract.
     * @param imag The imaginary value for the complex number to subtract.
     */
    void subtract_in_place(double real, double imag);
    /** Multiply a single value to this ComplexField.  This is a modifying
     * operation.  The result is (ac - bd) + (ad + bc)i, with c = real and d =
     * imag.
     *
     * @param real The real value for the complex number to multiply.
     * @param imag The imaginary value for the complex number to multipy.
     */
    void multiply_in_place(double real, double imag);
    /** Divide a single value from this ComplexField.  This is a modifying
     * operation.  The result is (ac + bd)/(c² + d²) + ((bc - ad)/(c² + d²))i
     * with c = real and d = imag.
     *
     * @param real The real value for the complex number to divide.
     * @param imag The imaginary value for the complex number to divide.
     */
    void divide_in_place(double real, double imag);
    /** Add a single real number to this ComplexField.  This is a modifying
     * operation.  It is equivalent to {@link #add_in_place(double, double)
     * add_in_place(real, 0)}.
     *
     * @param real The real number to add.
     */
    default void add_in_place(double real) {add_in_place(real, 0);}
    /** Subtract a single real number from this ComplexField.  This is a
     * modifying operation.  It is equivalent to {@link #subtract_in_place(
     * double, double) subtract_in_place(real, 0)}.
     *
     * @param real The real number to subract.
     */
    default void subtract_in_place(double real) {subtract_in_place(real, 0);}
    /** Multiply a single real number to this ComplexField.  This is a modifying
     * operation.  It is equivalent to {@link #multiply_in_place(double, double)
     * multiply_in_place(real, 0)}.
     *
     * @param real The real number to multiply.
     */
    default void multiply_in_place(double real) {multiply_in_place(real, 0);}
    /** Divide a single real number from this ComplexField.  This is a modifying
     * operation.  It is equivalent to {@link #divide_in_place(double, double)
     * divide_in_place(real, 0)}.
     *
     * @param real The real number to divide.
     */
    default void divide_in_place(double real) {divide_in_place(real, 0);}

    /** Create a negation of this ComplexField.  This is a non-modifying
     * operation.
     *
     * @return A negation of this ComplexField.
     */
    default ComplexField negate()
    {
        ComplexField result = copy();
        result.negate_in_place();
        return result;
    }
    /** Create the sum of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * add_in_place(ComplexField) add_in_place(other)} on a copy of this.
     *
     * @param other The ComplexField to add.
     * @return The sum of this and other.
     */
    default ComplexField add(ComplexField other)
    {
        ComplexField result = copy();
        result.add_in_place(other);
        return result;
    }
    /** Create the difference of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * subtract_in_place(ComplexField) subtract_in_place(other)} on a copy of
     * this.
     *
     * @param other The ComplexField to subtract.
     * @return The difference of this and other.
     */
    default ComplexField subtract(ComplexField other)
    {
        ComplexField result = copy();
        result.subtract_in_place(other);
        return result;
    }
    /** Create the product of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * multiply_in_place(ComplexField) multiply_in_place(other)} on a copy of
     * this.
     *
     * @param other The ComplexField to multiply.
     * @return The product of this and other.
     */
    default ComplexField multiply(ComplexField other)
    {
        ComplexField result = copy();
        result.multiply_in_place(other);
        return result;
    }
    /** Create the quotient of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * divide_in_place(ComplexField) divide_in_place(other)} on a copy of this.
     *
     * @param other The ComplexField to divide.
     * @return The quotient of this and other.
     */
    default ComplexField divide(ComplexField other)
    {
        ComplexField result = copy();
        result.divide_in_place(other);
        return result;
    }
    /** Create the sum of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * add_in_place(double[][]) add_in_place(other)} on a copy of this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     * @return The sum of this and other.
     */
    default ComplexField add(double[][] other)
    {
        ComplexField result = copy();
        result.add_in_place(other);
        return result;
    }
    /** Create the difference of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * subtract_in_place(double[][]) subtract_in_place(other)} on a copy of
     * this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     * @return The difference of this and other.
     */
    default ComplexField subtract(double[][] other)
    {
        ComplexField result = copy();
        result.subtract_in_place(other);
        return result;
    }
    /** Create the product of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * multiply_in_place(double[][]) multiply_in_place(other)} on a copy of
     * this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     * @return The product of this and other.
     */
    default ComplexField multiply(double[][] other)
    {
        ComplexField result = copy();
        result.multiply_in_place(other);
        return result;
    }
    /** Create the quotient of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * divide_in_place(double[][]) divide_in_place(other)} on a copy of this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link get_field}.
     * @return The quotient of this and other.
     */
    default ComplexField divide(double[][] other)
    {
        ComplexField result = copy();
        result.divide_in_place(other);
        return result;
    }
    /** Create the sum of this ComplexField and a single value.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * add_in_place(double, double) add_in_place(real, imag)} on a copy of this.
     *
     * @param real The real value for the complex number to add.
     * @param imag The imaginary value for the complex number to add.
     * @return The sum of this and other.
     */
    default ComplexField add(double real, double imag)
    {
        ComplexField result = copy();
        result.add_in_place(real, imag);
        return result;
    }
    /** Create the difference of this ComplexField and a single value.  This is
     * a non-modifying operation.  It is equivalent to {@link
     * subtract_in_place(double, double) subtract_in_place(real, imag)} on a
     * copy of this.
     *
     * @param real The real value for the complex number to subtract.
     * @param imag The imaginary value for the complex number to subtract.
     * @return The difference of this and other.
     */
    default ComplexField subtract(double real, double imag)
    {
        ComplexField result = copy();
        result.subtract_in_place(real, imag);
        return result;
    }
    /** Create the product of this ComplexField and a single value.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * multiply_in_place(double, double) multiply_in_place(real, imag)} on a
     * copy of this.
     *
     * @param real The real value for the complex number to multiply.
     * @param imag The imaginary value for the complex number to multiply.
     * @return The product of this and other.
     */
    default ComplexField multiply(double real, double imag)
    {
        ComplexField result = copy();
        result.multiply_in_place(real, imag);
        return result;
    }
    /** Create the qoutient of this ComplexField and a single value.  This is
     * a non-modifying operation.  It is equivalent to {@link
     * divide_in_place(double, double) divide_in_place(real, imag)} on a copy of
     * this.
     *
     * @param real The real value for the complex number to divide.
     * @param imag The imaginary value for the complex number to divide.
     * @return The quotient of this and other.
     */
    default ComplexField divide(double real, double imag)
    {
        ComplexField result = copy();
        result.divide_in_place(real, imag);
        return result;
    }
}
