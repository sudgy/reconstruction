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

import java.awt.Point;
import java.awt.Rectangle;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.gui.Roi;

import org.jtransforms.fft.FloatFFT_2D;

import unal.od.jdiffraction.cpu.utils.ArrayUtils;

public class Recon {
    private int M_m;
    private int M_n;
    private float M_lambda, M_z, M_dx, M_dy;
    protected FloatFFT_2D M_fft;
    private float[][] M_field;
    protected float[][] M_filtered_field;
    private float[][] M_output_field;
    private float[][] M_spectrum;
    public void set_input_images(int m, int n, float[][] real_input, float[][] imaginary_input)
    {
        M_m = m;
        M_n = n;
        M_fft = new FloatFFT_2D(m, n);
        M_field = ArrayUtils.complexAmplitude2(real_input, imaginary_input);
    }
    public void calculateFFT()
    {
        M_fft.complexForward(M_field);
        ArrayUtils.complexShift(M_field);
    }
    public void filter(Roi roi)
    {
        M_filtered_field = new float[M_field.length][M_field[0].length];
        Rectangle rect = roi.getBounds();
        int center_x = (int)rect.getCenterX();
        int center_y = (int)rect.getCenterY();
        int xp = M_m / 2 - center_x;
        int yp = M_n / 2 - center_y;
        for (Point p : roi) {
            M_filtered_field[p.x + xp][(p.y + yp) * 2] = M_field[p.x][p.y * 2];
            M_filtered_field[p.x + xp][(p.y + yp) * 2 + 1] = M_field[p.x][p.y * 2 + 1];
        }
    }
    public void filter(Roi roi, int center_x, int center_y)
    {
        M_filtered_field = new float[M_field.length][M_field[0].length];
        int xp = M_m / 2 - center_x;
        int yp = M_n / 2 - center_y;
        for (Point p : roi) {
            M_filtered_field[p.x + xp][(p.y + yp) * 2] = M_field[p.x][p.y * 2];
            M_filtered_field[p.x + xp][(p.y + yp) * 2 + 1] = M_field[p.x][p.y * 2 + 1];
        }
    }
    public void filter()
    {
        M_filtered_field = M_field;
    }
    public void propagate() {propagate(true);}
    public void propagate(boolean process)
    {
        if (process) final_processing();
        FastFloatAngularSpectrum p = new FastFloatAngularSpectrum(M_m, M_n, M_lambda, M_z, M_dx, M_dy);
        M_output_field = new float[M_m][M_n * 2];
        for (int i = 0; i < M_m; ++i) {
            System.arraycopy(M_filtered_field[i], 0, M_output_field[i], 0, 2 * M_n);
        }
        p.diffract(M_output_field);
    }
    protected void final_processing() {} // To be overriden by subclasses possibly
    public void set_parameters(float lambda, float z, float width, float height)
    {
        M_lambda = (float)(lambda * 0.001);
        M_z = z;
        M_dx = width / M_m;
        M_dy = height / M_n;
    }
    public void set_distance(float z)
    {
        M_z = z;
    }
    public float[][] get_spectrum()
    {
        M_spectrum = ArrayUtils.modulus(M_field);
        return M_spectrum;
    }
    public float[][] get_result()
    {
        return M_output_field;
    }
}
