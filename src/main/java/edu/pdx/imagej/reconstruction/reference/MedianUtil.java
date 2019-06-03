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

import java.util.AbstractList;
import java.util.Arrays;

import ij.ImagePlus;

class MedianUtil {
    static double[][] calculate_median(ImagePlus imp,
                                       AbstractList<Integer> times)
    {
        final int width = imp.getWidth();
        final int height = imp.getHeight();
        final int size = times.size();
        float[][][] slices = new float[size][][];
        double[] values = new double[size];
        int middle = size / 2;
        boolean even = size % 2 == 0;
        for (int i = 0; i < size; ++i) {
            slices[i] = imp.getImageStack()
                           .getProcessor(times.get(i))
                           .getFloatArray();
        }
        double[][] result = new double[width][height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < height; ++x) {
                for (int i = 0; i < size; ++i) {
                    values[i] = slices[i][x][y];
                }
                Arrays.sort(values);
                if (even) result[x][y] = (values[middle - 1]
                                       +  values[middle]) / 2.0;
                else result[x][y] = values[middle];
            }
        }
        return result;
    }
}
