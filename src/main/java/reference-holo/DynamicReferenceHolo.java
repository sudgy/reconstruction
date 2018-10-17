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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashMap;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.ZProjector;

import edu.pdx.imagej.reconstruction.util.OffsetUtil;

public interface DynamicReferenceHolo {
    float[][] result(ImagePlus imp, int t, Roi roi);
    static class None implements DynamicReferenceHolo {
        @Override public float[][] result(ImagePlus imp, int t, Roi roi) {return null;}
    }
    static class Single implements DynamicReferenceHolo {
        public Single(float[][] img, boolean use_same_roi)
        {
            M_img = img;
            M_use_same_roi = use_same_roi;
        }
        @Override
        public float[][] result(ImagePlus imp, int t, Roi roi)
        {
            if (M_result == null) {
                float[][] img = M_img;
                if (!M_use_same_roi) {
                    roi = FilterImageComplex.get_roi(img, null, "Please select the ROI for the reference hologram.");
                }
                M_result = ReferenceHolo.get(img, roi);
            }
            return M_result;
        }

        private float[][] M_img;
        private boolean M_use_same_roi;
        private float[][] M_result;
    }
    static class Offset implements DynamicReferenceHolo {
        public Offset(ImagePlus imp, boolean use_same_roi, int offset)
        {
            M_imp = imp;
            M_use_same_roi = use_same_roi;
            M_offset = offset;
        }
        @Override
        public float[][] result(ImagePlus imp, int t, Roi roi)
        {
            int offset = OffsetUtil.get_offset(M_offset, t, 1, M_imp.getImageStackSize());
            int final_t = t + offset;
            float[][] result = M_cache.get(final_t);
            if (result == null) {
                float[][] img = M_imp.getStack().getProcessor(final_t).getFloatArray();
                if (!M_use_same_roi) {
                    if (M_roi == null) {
                        M_roi = FilterImageComplex.get_roi(img, null, "Please select the ROI for the reference hologram.");
                    }
                    roi = M_roi;
                }
                result = ReferenceHolo.get(img, roi);
                M_cache.put(final_t, result);
            }
            return result;
        }
        private ImagePlus M_imp;
        private boolean M_use_same_roi;
        private int M_offset;
        private Roi M_roi;
        private HashMap<Integer, float[][]> M_cache = new HashMap<>();
    }
    static class Median implements DynamicReferenceHolo {
        public Median(ImagePlus img, boolean use_same_roi, AbstractList<Integer> times)
        {
            M_img = img;
            M_use_same_roi = use_same_roi;
            M_times = times;
        }
        @Override
        public float[][] result(ImagePlus imp, int t, Roi roi)
        {
            if (M_result == null) {
                float[][] img = calculate_median(M_img, M_times);
                if (!M_use_same_roi) {
                    roi = FilterImageComplex.get_roi(img, null, "Please select the ROI for the reference hologram.");
                }
                M_result = ReferenceHolo.get(img, roi);
            }
            return M_result;
        }

        private ImagePlus M_img;
        private boolean M_use_same_roi;
        private float[][] M_result;
        private AbstractList<Integer> M_times;
    }
    static class MedianOffset implements DynamicReferenceHolo {
        public MedianOffset(ImagePlus imp, boolean use_same_roi, AbstractList<Integer> times, int offset)
        {
            M_imp = imp;
            M_use_same_roi = use_same_roi;
            M_times = times;
            M_offset = offset;
            int start_t = M_times.get(0);
            int end_t = M_times.get(M_times.size() - 1);
            M_min_t = Math.min(start_t, end_t);
            M_max_t = Math.max(start_t, end_t);
        }
        @Override
        public float[][] result(ImagePlus imp, int t, Roi roi)
        {
            int offset = OffsetUtil.get_multi_offset(M_offset, t, 1, M_imp.getImageStackSize(), M_min_t, M_max_t);
            float[][] result = M_cache.get(offset + M_min_t + t);
            if (result == null) {
                result = calculate_median(M_imp, new AbstractList<Integer>() {
                    @Override public Integer get(int index) {return M_times.get(index) + offset + t - 1;}
                    @Override public int size() {return M_times.size();}
                });
                if (!M_use_same_roi) {
                    if (M_roi == null) {
                        M_roi = FilterImageComplex.get_roi(result, null, "Please select the ROI for the reference hologram.");
                    }
                    roi = M_roi;
                }
                result = ReferenceHolo.get(result, roi);
                M_cache.put(offset + M_min_t + t, result);
            }
            return result;
        }
        private ImagePlus M_imp;
        private boolean M_use_same_roi;
        private AbstractList<Integer> M_times;
        private int M_min_t;
        private int M_max_t;
        private int M_offset;
        private Roi M_roi;
        private HashMap<Integer, float[][]> M_cache = new HashMap<>();
    }
    static class Self implements DynamicReferenceHolo {
        @Override
        public float[][] result(ImagePlus imp, int t, Roi roi)
        {
            float[][] current = imp.getStack().getProcessor(t).getFloatArray();
            if (M_roi == null) {
                M_roi = FilterImageComplex.get_roi(current, null, "Please select the Roi for the reference hologram.");
            }
            return ReferenceHolo.get(current, M_roi);
        }

        private Roi M_roi;
    }
    static float[][] calculate_median(ImagePlus imp, AbstractList<Integer> times)
    {
        final int width = imp.getWidth();
        final int height = imp.getHeight();
        final int size = times.size();
        float[][][] slices = new float[width][height][size];
        float[] values = new float[size];
        int middle = size / 2;
        boolean even = size % 2 == 0;
        for (int i = 0; i < size; ++i) {
            slices[i] = imp.getImageStack().getProcessor(times.get(i)).getFloatArray();
        }
        float[][] result = new float[width][height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < height; ++x) {
                for (int i = 0; i < size; ++i) {
                    values[i] = slices[i][x][y];
                }
                Arrays.sort(values);
                if (even) result[x][y] = (values[middle - 1] + values[middle])/2f;
                else result[x][y] = values[middle];
            }
        }
        return result;
    }
}
