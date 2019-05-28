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

import java.util.AbstractList;

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

@Plugin(type = ReconstructionPlugin.class)
public class Status extends AbstractReconstructionPlugin
                    implements MainReconstructionPlugin {
    @Override public void process_hologram_param(ImagePlus hologram)
        {M_hologram = hologram;}
    @Override public void process_ts_param(AbstractList<Integer> ts)
        {M_t_size = ts.size();}
    @Override public void process_zs_param(AbstractList<DistanceUnitValue> zs)
        {M_z_size = zs.size();}
    @Override public void process_beginning()
        {M_total_size = M_t_size * M_z_size;}
    @Override public void process_propagated_field(
        ConstReconstructionField original_field,
        ReconstructionField current_field,
        int t, DistanceUnitValue z_from, DistanceUnitValue z_to)
    {
        String label = M_hologram.getStack().getSliceLabel(t);
        if (label == null) label = M_hologram.getTitle();
        P_status.showStatus(M_current, M_total_size, "Processing " + label
            + " at z = " + String.format("%.3f", z_to.value()));
        ++M_current;
    }

    private ImagePlus M_hologram;
    private int M_t_size;
    private int M_z_size;
    private int M_total_size;
    private int M_current;
    @Parameter private StatusService P_status;
}
