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

import java.awt.Point;

public class CenterField {
    public static float[][] get_field(float[][] image, CenterOptions options)
    {
        double[] h_fit = PolyFit.fit(image, options.h_line());
        double[] v_fit = PolyFit.fit(image, options.v_line());
        final int M = image.length;
        final int N = image[0].length;
        float[][] result = new float[M][N * 2];
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                final double val = -(h_fit[0] * m + v_fit[0] * n + h_fit[1] * h_fit[1] * m * m + v_fit[1] * v_fit[1] * n * n);
                result[m][2*n  ] = (float)Math.cos(val);
                result[m][2*n+1] = (float)Math.sin(val);
            }
        }
        return result;
    }
}
