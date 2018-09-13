/* Copyright (C) 2018 Portland State University
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

import unal.od.jdiffraction.cpu.utils.ArrayUtils;

public class ReconRef extends Recon {
    float[][] M_reference_holo;
    public void set_reference_holo(float[][] real, float[][] imaginary)
    {
        M_reference_holo = ArrayUtils.complexAmplitude2(real, imaginary);
    }
    public void set_reference_holo(float[][] complex)
    {
        M_reference_holo = complex;
    }
    @Override
    protected void final_processing()
    {
        if (M_reference_holo == null) return;
        ArrayUtils.complexShift(M_filtered_field);
        M_fft.complexInverse(M_filtered_field, true);
        ArrayUtils.complexMultiplication2(M_filtered_field, M_reference_holo);
        M_fft.complexForward(M_filtered_field);
        ArrayUtils.complexShift(M_filtered_field);
    }
}
