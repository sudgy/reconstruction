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

package edu.pdx.imagej.reconstruction.status;

import java.util.List;

import ij.ImagePlus;

import org.scijava.app.StatusService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.plugin.AbstractReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;

/** A {@link edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin} that shows the progress of the command.
 */
@Plugin(type = ReconstructionPlugin.class, name = "Status")
public class Status extends AbstractReconstructionPlugin
                    implements MainReconstructionPlugin {
    /** Get the hologram, to get the name of the slices.
     */
    @Override public void processHologramParam(ImagePlus hologram)
        {M_hologram = hologram;}
    /** Get the number of time slices we are reconstructing.
     */
    @Override public void processTsParam(List<Integer> ts)
        {M_tSize = ts.size();}
    /** Get the number of z slices we are propagating to.
     */
    @Override public void processZsParam(List<DistanceUnitValue> zs)
        {M_zSize = zs.size();}
    /** Calculate the total number of image that will be created.
     */
    @Override public void processBeginning()
        {M_totalSize = M_tSize * M_zSize;}
    /** Show the progress of the plugin.
     */
    @Override public void processPropagatedField(ReconstructionField field,
                                                   int t, DistanceUnitValue z)
    {
        String label = M_hologram.getStack().getSliceLabel(t);
        if (label == null) label = M_hologram.getTitle();
        P_status.showStatus(M_current, M_totalSize, "Processing " + label
            + " at z = " + String.format("%.3f", z.value()));
        ++M_current;
    }
    @Override public Status duplicate() {return new Status();}

    private ImagePlus M_hologram;
    private int M_tSize;
    private int M_zSize;
    private int M_totalSize;
    private int M_current;
    @Parameter private StatusService P_status;
}
