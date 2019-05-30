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

import java.util.LinkedHashMap;

import ij.gui.Roi;

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.HoldingSinglePlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.filter.Filter;

@Plugin(type = ReconstructionPlugin.class)
public class Reference extends HoldingSinglePlugin<ReferencePlugin>
                       implements MainReconstructionPlugin {
    public Reference()
    {
        super("Reference Hologram", ReferencePlugin.class);
    }
    public Reference(ReferencePlugin plugin)
    {
        super(plugin);
    }

    @Override
    public ReferenceParameter param()
    {
        if (M_param == null) {
            M_param = new ReferenceParameter(super.param());
        }
        return M_param;
    }
    @Override
    public void read_plugins(
        LinkedHashMap<Class<?>, ReconstructionPlugin> plugins)
    {
        for (ReconstructionPlugin plugin : plugins.values()) {
            if (plugin instanceof Filter) M_filter = (Filter)plugin;
        }
    }
    @Override
    public void process_filtered_field(ReconstructionField field, int t)
    {
        ReconstructionField reference_field
            = get_plugin().get_reference_holo(
                            new ConstReconstructionField(field), t);
        if (reference_field == null) return;
        if (M_param.use_same_roi()) {
            M_filter.filter_field(reference_field);
        }
        else {
            if (M_not_same_filter == null) {
                M_not_same_filter = new Filter();
                M_not_same_filter.get_filter(
                    new ConstReconstructionField(reference_field),
                    "Please select the ROI for the reference hologram and then "
                    + "press OK.");
            }
            M_not_same_filter.filter_field(reference_field);
        }

        GetReference.calculate(reference_field,
                               M_param.phase(), M_param.amplitude());
        field.field().multiply_in_place(reference_field.field());
    }
    public void set_not_same_filter(Filter filter)
    {
        M_not_same_filter = filter;
    }

    private Filter M_filter;
    private Filter M_not_same_filter;
    ReferenceParameter M_param; // Package private for testing
}
