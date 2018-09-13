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
import ij.gui.Roi;
import ij.process.ImageProcessor;
import ij.process.FloatProcessor;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

import unal.od.jdiffraction.cpu.utils.ArrayUtils;

import org.jtransforms.fft.FloatFFT_2D;

public class FilterImageComplex {
    public static float[][] get(float[][] real) {return get(real, null, null);}
    public static float[][] get(float[][] real, float[][] imaginary) {return get(real, imaginary, null);}
    public static float[][] get(float[][] real, float[][] imaginary, String s)
    {
        int width = real.length;
        int height = real[0].length;
        FloatFFT_2D fft = new FloatFFT_2D(width, height);
        float[][] field = ArrayUtils.complexAmplitude2(real, imaginary);
        fft.complexForward(field);
        ArrayUtils.complexShift(field);
        ImageProcessor proc = new FloatProcessor(ArrayUtils.modulus(field));
        proc.log();
        Roi roi = GetRoi.get(new ImagePlus("FFT", proc), s);
        // If the user didn't select an roi, act like it's the whole image
        if (roi == null) return ArrayUtils.complexAmplitude2(real, imaginary);
        float[][] result = new float[field.length][field[0].length];
        Rectangle rect = roi.getBounds();
        int center_x = (int)rect.getCenterX();
        int center_y = (int)rect.getCenterY();
        int xp = width / 2 - center_x;
        int yp = height / 2 - center_y;
        for (Point p : roi) {
            result[p.x + xp][(p.y + yp) * 2] = field[p.x][p.y * 2];
            result[p.x + xp][(p.y + yp) * 2 + 1] = field[p.x][p.y * 2 + 1];
        }
        ArrayUtils.complexShift(result);
        fft.complexInverse(result, true);
        return result;
    }
    public static Roi get_roi(float[][] real) {return get_roi(real, null, null);}
    public static Roi get_roi(float[][] real, float[][] imaginary) {return get_roi(real, imaginary, null);}
    public static Roi get_roi(float[][] real, float[][] imaginary, String s)
    {
        int width = real.length;
        int height = real[0].length;
        FloatFFT_2D fft = new FloatFFT_2D(width, height);
        float[][] field = ArrayUtils.complexAmplitude2(real, imaginary);
        fft.complexForward(field);
        ArrayUtils.complexShift(field);
        ImageProcessor proc = new FloatProcessor(ArrayUtils.modulus(field));
        proc.log();
        return GetRoi.get(new ImagePlus("FFT", proc), s);
    }
    public static float[][] get_with_roi(float[][] real, float[][] imaginary, Roi roi)
    {
        int width = real.length;
        int height = real[0].length;
        FloatFFT_2D fft = new FloatFFT_2D(width, height);
        float[][] field = ArrayUtils.complexAmplitude2(real, imaginary);
        fft.complexForward(field);
        ArrayUtils.complexShift(field);
        ImageProcessor proc = new FloatProcessor(ArrayUtils.modulus(field));
        proc.log();
        float[][] result = new float[field.length][field[0].length];
        Rectangle rect = roi.getBounds();
        int center_x = (int)rect.getCenterX();
        int center_y = (int)rect.getCenterY();
        int xp = width / 2 - center_x;
        int yp = height / 2 - center_y;
        for (Point p : roi) {
            result[p.x + xp][(p.y + yp) * 2] = field[p.x][p.y * 2];
            result[p.x + xp][(p.y + yp) * 2 + 1] = field[p.x][p.y * 2 + 1];
        }
        ArrayUtils.complexShift(result);
        fft.complexInverse(result, true);
        return result;
    }
}
