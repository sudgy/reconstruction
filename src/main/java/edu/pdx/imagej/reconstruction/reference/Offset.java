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

import java.util.Collection;

import ij.ImagePlus;

import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionFieldImpl;

/** A {@link ReferencePlugin} that uses an offset from the current time slice
 * as the reference hologram.
 */
@Plugin(type = ReferencePlugin.class,
        name = "Single Image With Offset",
        priority = Priority.VERY_HIGH * 0.999)
public class Offset extends AbstractReferencePlugin {
    /** Constructor intended for live use of the plugin.
     */
    public Offset()
    {
        M_param = new OffsetParameter();
    }
    // Constructor used for testing.
    Offset(Collection<ImagePlus> images)
    {
        M_param = new OffsetParameter(images);
    }
    /** Constructor intended for programmatic use of the plugin.
     *
     * @param imp The image stack to get from.
     * @param offset The offset to apply to the current time.
     */
    public Offset(ImagePlus imp, int offset)
    {
        M_imp = imp;
        M_offset = offset;
    }
    @Override
    public Offset duplicate()
    {
        return new Offset(M_imp, M_offset);
    }
    /** Get the reference hologram.
     */
    @Override
    public ReconstructionField getReferenceHolo(
        ConstReconstructionField field, int t)
    {
        // This should maybe be cached, but is it worth it?  That would use up a
        // lot of extra memory, and most of them should be a new calculation.

        if (M_param != null) {
            M_imp = M_param.getValue().imp;
            M_offset = M_param.getValue().offset;
        }
        int offset = OffsetUtil.getOffset(M_offset, t, 1,
                                           M_imp.getImageStackSize());
        int finalT = t + offset;
        float[][] floatArray = M_imp.getStack()
                                     .getProcessor(finalT)
                                     .getFloatArray();
        double[][] real = new double[floatArray.length][floatArray[0].length];
        double[][] imag = new double[real.length][real[0].length];
        for (int x = 0; x < real.length; ++x) {
            for (int y = 0; y < real[0].length; ++y) {
                real[x][y] = floatArray[x][y];
            }
        }
        return new ReconstructionFieldImpl(real, imag);
    }
    @Override
    public OffsetParameter param()
    {
        return M_param;
    }

    private OffsetParameter M_param;
    private ImagePlus M_imp;
    private int M_offset;

    private static class OffsetParams {
        public ImagePlus imp;
        public int offset;
    }

    static class OffsetParameter
                   extends HoldingParameter<OffsetParams> {
        public OffsetParameter()
        {
            super("OffsetParameter");
        }
        public OffsetParameter(Collection<ImagePlus> images)
        {
            super("OffsetParameter");
            M_images = images;
        }
        @Override
        public void initialize()
        {
            if (M_images == null) {
                M_img = addParameter(new ImageParameter(
                                      "Reference Hologram Image"));
            }
            else {
                M_img = new ImageParameter("Reference Hologram Image",
                                           M_images);
                addParameter(M_img);
            }
            M_offset = addParameter(new IntParameter(
                                     0, "Time offset", "frames"));
        }
        @Override
        public OffsetParams getValue()
        {
            OffsetParams result = new OffsetParams();
            result.imp = M_img.getValue();
            result.offset = M_offset.getValue();
            return result;
        }

        private ImageParameter M_img;
        private IntParameter M_offset;
        private Collection<ImagePlus> M_images;
    }
}
