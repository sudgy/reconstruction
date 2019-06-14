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

/** This is a read-only wrapper around {@link ComplexField}.  All of these
 * methods directly call the ones in <code>ComplexField</code>, so look there
 * for descriptions.
 */
public final class ConstComplexField {
    /** Create a read-only view of <code>field</code>.
     *
     * @param field The ComplexField to wrap
     */
    public ConstComplexField(ComplexField field) {M_field = field;}

    public ComplexField copy() {return M_field.copy();}

    public double[][] get_real() {return M_field.get_real();}
    public double[][] get_imag() {return M_field.get_real();}
    public double[][] get_amp() {return M_field.get_amp();}
    public double[][] get_amp2() {return M_field.get_amp2();}
    public double[][] get_arg() {return M_field.get_arg();}
    public double get_real(int x, int y) {return M_field.get_real(x, y);}
    public double get_imag(int x, int y) {return M_field.get_real(x, y);}
    public int width() {return M_field.width();}
    public int height() {return M_field.height();}

    public ComplexField negate() {return M_field.negate();}
    public ComplexField add(ComplexField other) {return M_field.add(other);}
    public ComplexField subtract(ComplexField other)
        {return M_field.subtract(other);}
    public ComplexField multiply(ComplexField other)
        {return M_field.multiply(other);}
    public ComplexField divide(ComplexField other)
        {return M_field.divide(other);}
    public ComplexField add(double[][] other) {return M_field.add(other);}
    public ComplexField subtract(double[][] other)
        {return M_field.subtract(other);}
    public ComplexField multiply(double[][] other)
        {return M_field.multiply(other);}
    public ComplexField divide(double[][] other) {return M_field.divide(other);}
    public ComplexField add(double real, double imag)
        {return M_field.add(real, imag);}
    public ComplexField subtract(double real, double imag)
        {return M_field.subtract(real, imag);}
    public ComplexField multiply(double real, double imag)
        {return M_field.multiply(real, imag);}
    public ComplexField divide(double real, double imag)
        {return M_field.divide(real, imag);}

    private ComplexField M_field;
}
