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
        PolyFit pf = new PolyFit(image);
        double[] h_fit;
        if (options.h_line() == null) h_fit = pf.auto_h(10, options.degree());
        else h_fit = pf.fit(options.h_line(), options.degree());
        double[] v_fit;
        if (options.v_line() == null) v_fit = pf.auto_v(10, options.degree());
        else v_fit = pf.fit(options.v_line(), options.degree());
        final int M = image.length;
        final int N = image[0].length;
        float[][] result = new float[M][N * 2];
        for (int m = 0; m < M; ++m) {
            for (int n = 0; n < N; ++n) {
                double val = 0;
                for (int i = 0; i < h_fit.length; ++i) {
                    val += Math.pow(h_fit[i] * m, i + 1);
                    val += Math.pow(v_fit[i] * n, i + 1);
                }
                val *= -1;
                result[m][2*n  ] = (float)Math.cos(val);
                result[m][2*n+1] = (float)Math.sin(val);
            }
        }
        return result;
    }
}
