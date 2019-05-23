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

package edu.pdx.imagej.reconstruction.reference;

import ij.gui.Roi;

import edu.pdx.imagej.reconstruction.ReconstructionField;

public class GetReference {
    public static ReconstructionField calculate(ReconstructionField hologram,
                                                boolean phase_enabled,
                                                boolean amplitude_enabled)
    {
        double[][] reference = hologram.field().get_field();
        for (int x = 0; x < hologram.field().width(); ++x) {
            for (int y = 0; y < hologram.field().height(); ++y) {
                double real = reference[x][y * 2];
                double imag = reference[x][y * 2 + 1];
                double abs;
                if (amplitude_enabled) {
                    // The edges get way too bright for some reason
                    // The 256 is to let the phase still be good
                    // I don't know if it actually does anything, though.
                    if (x == 0 || x == reference.length - 1 ||
                            y == 0 || y == reference[0].length - 1) {
                        abs = Double.MAX_VALUE / 256.0;
                    }
                    else abs = (real*real + imag*imag);
                }
                else abs = Math.sqrt(real*real + imag*imag);
                if (phase_enabled) {
                    reference[x][y * 2] = real / abs;
                    reference[x][y*2+1] = imag / abs * -1;
                }
                else {
                    if (amplitude_enabled) {
                        reference[x][y * 2] = 1 / Math.sqrt(abs);
                        reference[x][y*2+1] = 0;
                    }
                    else {
                        reference[x][y * 2] = 1;
                        reference[x][y*2+1] = 0;
                    }
                }
            }
        }
        return hologram;
    }
}
