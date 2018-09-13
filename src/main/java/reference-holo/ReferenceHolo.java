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

import ij.gui.Roi;

/**
 * This class takes in a reference hologram and determines what complex value
 * needs to be multiplied by the reconstructed hologram to avoid aberration.
 *
 * @author David Cohoe
 */
public class ReferenceHolo {
    public static float[][] get(float[][] hologram, Roi roi)
    {
        float[][] result = FilterImageComplex.get_with_roi(hologram, null, roi);
        for (int x = 0; x < hologram.length; ++x) {
            for (int y = 0; y < hologram[0].length; ++y) {
                float real = result[x][y * 2];
                float imaginary = result[x][y * 2 + 1];
                double abs = Math.sqrt(real * real + imaginary * imaginary);
                result[x][y * 2] = (float)(real / abs);
                result[x][y * 2 + 1] = (float)(imaginary / abs * -1);
            }
        }
        return result;
    }
}
