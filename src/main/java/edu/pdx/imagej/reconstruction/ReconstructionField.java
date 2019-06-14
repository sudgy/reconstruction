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

/** ReconstrucionField represents both a normal complex field and the FFT of it
 * at once.  Because the FFT is a fairly expensive operation, this class will
 * keep track of what it has already computed, and will only calculate the FFT
 * or the IFFT when absolutely needed.  The original field will henceforth be
 * called the "normal field".
 */
public interface ReconstructionField {
    /** Get the normal field associated with this ReconstructionField.
     *
     * @return The complex field associated with this ReconstructionField.
     */
    ComplexField field();
    /** Get the Fourier transform of the field associated with this
     * ReconstructionField.
     *
     * @return The Fourier transform of this field.
     */
    ComplexField fourier();
    /** See if the normal field has been computed.  If an algorithm could work
     * well with both the normal field and the fourier transform, this can be
     * used to check which is available to save on computation time to calculate
     * the FFT/IFFT if it is not needed.
     *
     * @return Whether or not the normal field has been computed.
     * @see has_fourier
     */
    boolean has_field();
    /** See if the Fourier transform has been computed.  If an algorithm could
     * work well with both the normal field and the fourier transform, this can
     * be used to check which is available to save on computation time to
     * calculate the FFT/IFFT if it is not needed.
     *
     * @return Whether or not the Fourier transform has been computed.
     * @see has_field
     */
    boolean has_fourier();
    /** Copy this ReconstructionField.  It will copy any fields (normal or
     * Fourier) that already exist.
     *
     * @return A copy of this ReconstructionField.
     */
    ReconstructionField copy();
}
