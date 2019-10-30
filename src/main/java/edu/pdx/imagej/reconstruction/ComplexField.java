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
    double[][] getField();
    /** Set the data for this ComplexField.  This is a modifying operation.
     *
     * @param field The new data to represent this ComplexField.
     */
    void setField(double[][] field);
    /** Get a copy of the real values of this ComplexField.  This is a
     * non-modifying operation.
     *
     * @return A copy of the real values of this ComplexField.
     */
    double[][] getReal();
    /** Get a copy of the imaginary values of this ComplexField.  This is a
     * non-modifying operation.
     *
     * @return A copy of the imaginary values of this ComplexField.
     */
    double[][] getImag();
    /** Get a copy of the amplitude values of this ComplexField.  This is a
     * non-modifying operation.  The result is sqrt(a² + b²).
     *
     * @return A copy of the amplitude values of this ComplexField.
     */
    double[][] getAmp();
    /** Get a copy of the amplitude squared values of this ComplexField.  This
     * is a non-modifying operation.  The result is a² + b².
     *
     * @return A copy of the amplitude squared values of this ComplexField.
     */
    double[][] getAmp2();
    /** Get a copy of the phase values of this ComplexField.  This is a
     * non-modifying operation.  The result is atan2(b, a).
     *
     * @return A copy of the phase values of this ComplexField.
     */
    double[][] getArg();
    /** Get a single real value from this ComplexField.  This is a non-modifying
     * operation.
     *
     * @param x The x coordinate to get the value from
     * @param y The y coordinate to get the value from
     * @return The real value corresponding to the x and y coordinates
     */
    double getReal(int x, int y);
    /** Get a single imaginary value from this ComplexField.  This is a
     * non-modifying operation.
     *
     * @param x The x coordinate to get the value from
     * @param y The y coordinate to get the value from
     * @return The imaginary value corresponding to the x and y coordinates
     */
    double getImag(int x, int y);
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
    void negateInPlace();
    /** Add a ComplexField to this one.  This is a modifying operation.  The
     * result is (a + c) + (b + d)i.
     *
     * @param other The ComplexField to add to this one.
     */
    void addInPlace(ComplexField other);
    /** Subtract another ComplexField from this one.  This is a modifying
     * operation.  The result is (a - c) + (b - d)i.
     *
     * @param other The ComplexField to subtract with.
     */
    void subtractInPlace(ComplexField other);
    /** Multiply a ComplexField with this one.  This is a modifying operation.
     * The result is (ac - bd) + (ad + bc)i.
     *
     * @param other The ComplexField to multiply with this one.
     */
    void multiplyInPlace(ComplexField other);
    /** Divide another ComplexField from this one.  This is a modifying
     * operation.  The result is (ac + bd)/(c² + d²) + ((bc - ad)/(c² + d²))i
     *
     * @param other The ComplexField to divide with.
     */
    void divideInPlace(ComplexField other);
    /** Add an array to this ComplexField.  This is a modifying operation.  The
     * result is (a + c) + (b + d)i.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     */
    void addInPlace(double[][] other);
    /** Subtract an array from this ComplexField.  This is a modifying
     * operation.  The result is (a - c) + (b - d)i.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     */
    void subtractInPlace(double[][] other);
    /** Multiply an array with this ComplexField.  This is a modifying
     * operation. The result is (ac - bd) + (ad + bc)i.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     */
    void multiplyInPlace(double[][] other);
    /** Divide an array from this ComplexField.  This is a modifying operation.
     * The result is (ac + bd)/(c² + d²) + ((bc - ad)/(c² + d²))i
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     */
    void divideInPlace(double[][] other);
    /** Add a single value to this ComplexField.  This is a modifying operation.
     * The result is (a + c) + (b + d)i, with c = real and d = imag.
     *
     * @param real The real value for the complex number to add.
     * @param imag The imaginary value for the complex number to add.
     */
    void addInPlace(double real, double imag);
    /** Subtract a single value from this ComplexField.  This is a modifying
     * operation.  The result is (a - c) + (b - d)i, with c = real and d = imag.
     *
     * @param real The real value for the complex number to subtract.
     * @param imag The imaginary value for the complex number to subtract.
     */
    void subtractInPlace(double real, double imag);
    /** Multiply a single value to this ComplexField.  This is a modifying
     * operation.  The result is (ac - bd) + (ad + bc)i, with c = real and d =
     * imag.
     *
     * @param real The real value for the complex number to multiply.
     * @param imag The imaginary value for the complex number to multipy.
     */
    void multiplyInPlace(double real, double imag);
    /** Divide a single value from this ComplexField.  This is a modifying
     * operation.  The result is (ac + bd)/(c² + d²) + ((bc - ad)/(c² + d²))i
     * with c = real and d = imag.
     *
     * @param real The real value for the complex number to divide.
     * @param imag The imaginary value for the complex number to divide.
     */
    void divideInPlace(double real, double imag);
    /** Add a single real number to this ComplexField.  This is a modifying
     * operation.  It is equivalent to {@link #addInPlace(double, double)
     * addInPlace(real, 0)}.
     *
     * @param real The real number to add.
     */
    default void addInPlace(double real) {addInPlace(real, 0);}
    /** Subtract a single real number from this ComplexField.  This is a
     * modifying operation.  It is equivalent to {@link #subtractInPlace(
     * double, double) subtractInPlace(real, 0)}.
     *
     * @param real The real number to subract.
     */
    default void subtractInPlace(double real) {subtractInPlace(real, 0);}
    /** Multiply a single real number to this ComplexField.  This is a modifying
     * operation.  It is equivalent to {@link #multiplyInPlace(double, double)
     * multiplyInPlace(real, 0)}.
     *
     * @param real The real number to multiply.
     */
    default void multiplyInPlace(double real) {multiplyInPlace(real, 0);}
    /** Divide a single real number from this ComplexField.  This is a modifying
     * operation.  It is equivalent to {@link #divideInPlace(double, double)
     * divideInPlace(real, 0)}.
     *
     * @param real The real number to divide.
     */
    default void divideInPlace(double real) {divideInPlace(real, 0);}

    /** Create a negation of this ComplexField.  This is a non-modifying
     * operation.
     *
     * @return A negation of this ComplexField.
     */
    default ComplexField negate()
    {
        ComplexField result = copy();
        result.negateInPlace();
        return result;
    }
    /** Create the sum of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * addInPlace(ComplexField) addInPlace(other)} on a copy of this.
     *
     * @param other The ComplexField to add.
     * @return The sum of this and other.
     */
    default ComplexField add(ComplexField other)
    {
        ComplexField result = copy();
        result.addInPlace(other);
        return result;
    }
    /** Create the difference of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * subtractInPlace(ComplexField) subtractInPlace(other)} on a copy of
     * this.
     *
     * @param other The ComplexField to subtract.
     * @return The difference of this and other.
     */
    default ComplexField subtract(ComplexField other)
    {
        ComplexField result = copy();
        result.subtractInPlace(other);
        return result;
    }
    /** Create the product of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * multiplyInPlace(ComplexField) multiplyInPlace(other)} on a copy of
     * this.
     *
     * @param other The ComplexField to multiply.
     * @return The product of this and other.
     */
    default ComplexField multiply(ComplexField other)
    {
        ComplexField result = copy();
        result.multiplyInPlace(other);
        return result;
    }
    /** Create the quotient of this ComplexField and another.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * divideInPlace(ComplexField) divideInPlace(other)} on a copy of this.
     *
     * @param other The ComplexField to divide.
     * @return The quotient of this and other.
     */
    default ComplexField divide(ComplexField other)
    {
        ComplexField result = copy();
        result.divideInPlace(other);
        return result;
    }
    /** Create the sum of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * addInPlace(double[][]) addInPlace(other)} on a copy of this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     * @return The sum of this and other.
     */
    default ComplexField add(double[][] other)
    {
        ComplexField result = copy();
        result.addInPlace(other);
        return result;
    }
    /** Create the difference of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * subtractInPlace(double[][]) subtractInPlace(other)} on a copy of
     * this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     * @return The difference of this and other.
     */
    default ComplexField subtract(double[][] other)
    {
        ComplexField result = copy();
        result.subtractInPlace(other);
        return result;
    }
    /** Create the product of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * multiplyInPlace(double[][]) multiplyInPlace(other)} on a copy of
     * this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     * @return The product of this and other.
     */
    default ComplexField multiply(double[][] other)
    {
        ComplexField result = copy();
        result.multiplyInPlace(other);
        return result;
    }
    /** Create the quotient of this ComplexField and an array.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * divideInPlace(double[][]) divideInPlace(other)} on a copy of this.
     *
     * @param other A two-dimensional array that must be in the same format as
     *              the return value of {@link getField}.
     * @return The quotient of this and other.
     */
    default ComplexField divide(double[][] other)
    {
        ComplexField result = copy();
        result.divideInPlace(other);
        return result;
    }
    /** Create the sum of this ComplexField and a single value.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * addInPlace(double, double) addInPlace(real, imag)} on a copy of this.
     *
     * @param real The real value for the complex number to add.
     * @param imag The imaginary value for the complex number to add.
     * @return The sum of this and other.
     */
    default ComplexField add(double real, double imag)
    {
        ComplexField result = copy();
        result.addInPlace(real, imag);
        return result;
    }
    /** Create the difference of this ComplexField and a single value.  This is
     * a non-modifying operation.  It is equivalent to {@link
     * subtractInPlace(double, double) subtractInPlace(real, imag)} on a
     * copy of this.
     *
     * @param real The real value for the complex number to subtract.
     * @param imag The imaginary value for the complex number to subtract.
     * @return The difference of this and other.
     */
    default ComplexField subtract(double real, double imag)
    {
        ComplexField result = copy();
        result.subtractInPlace(real, imag);
        return result;
    }
    /** Create the product of this ComplexField and a single value.  This is a
     * non-modifying operation.  It is equivalent to {@link
     * multiplyInPlace(double, double) multiplyInPlace(real, imag)} on a
     * copy of this.
     *
     * @param real The real value for the complex number to multiply.
     * @param imag The imaginary value for the complex number to multiply.
     * @return The product of this and other.
     */
    default ComplexField multiply(double real, double imag)
    {
        ComplexField result = copy();
        result.multiplyInPlace(real, imag);
        return result;
    }
    /** Create the qoutient of this ComplexField and a single value.  This is
     * a non-modifying operation.  It is equivalent to {@link
     * divideInPlace(double, double) divideInPlace(real, imag)} on a copy of
     * this.
     *
     * @param real The real value for the complex number to divide.
     * @param imag The imaginary value for the complex number to divide.
     * @return The quotient of this and other.
     */
    default ComplexField divide(double real, double imag)
    {
        ComplexField result = copy();
        result.divideInPlace(real, imag);
        return result;
    }
}
