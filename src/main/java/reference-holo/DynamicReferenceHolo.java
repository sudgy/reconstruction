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

import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.ZProjector;

public interface DynamicReferenceHolo {
    float[][] result(ImagePlus imp, float[][] current, Roi roi);
    static class None implements DynamicReferenceHolo {
        @Override public float[][] result(ImagePlus imp, float[][] current, Roi roi) {return null;}
    }
    static class Single implements DynamicReferenceHolo {
        public Single(float[][] img, boolean use_same_roi)
        {
            M_img = img;
            M_use_same_roi = use_same_roi;
        }
        @Override
        public float[][] result(ImagePlus imp, float[][] current, Roi roi)
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
    static class Median implements DynamicReferenceHolo {
        public Median(ImagePlus img, boolean use_same_roi)
        {
            M_img = img;
            M_use_same_roi = use_same_roi;
        }
        @Override
        public float[][] result(ImagePlus imp, float[][] current, Roi roi)
        {
            if (M_result == null) {
                imp = ZProjector.run(M_img, "median");
                float[][] img = imp.getProcessor().getFloatArray();
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
    }
    static class Self implements DynamicReferenceHolo {
        @Override
        public float[][] result(ImagePlus imp, float[][] current, Roi roi)
        {
            if (M_roi == null) {
                M_roi = FilterImageComplex.get_roi(current, null, "Please select the Roi for the reference hologram.");
            }
            return ReferenceHolo.get(current, M_roi);
        }

        private Roi M_roi;
    }
}
