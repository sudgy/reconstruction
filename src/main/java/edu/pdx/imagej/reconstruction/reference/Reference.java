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

import java.util.ArrayList;
import java.util.List;

import ij.gui.Roi;

import org.scijava.plugin.Plugin;
import org.scijava.Priority;

import edu.pdx.imagej.reconstruction.ConstReconstructionField;
import edu.pdx.imagej.reconstruction.ReconstructionField;
import edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin;
import edu.pdx.imagej.reconstruction.plugin.HoldingSinglePlugin;
import edu.pdx.imagej.reconstruction.plugin.MainReconstructionPlugin;
import edu.pdx.imagej.reconstruction.filter.Filter;

/** A {@link edu.pdx.imagej.reconstruction.plugin.ReconstructionPlugin
 * ReconstructionPlugin} that removes noise through the use of a reference
 * hologram.  Upon getting the reference hologram, it can remove phase noise
 * by conjugating the reference hologram, and it can remove amplitude noise by
 * dividing by the magnitude of the reference hologram, although there still
 * seems to be a few issues with that.  The method of getting the reference
 * hologram is customizable by making a {@link ReferencePlugin}.
 */
@Plugin(type = ReconstructionPlugin.class, name = "Reference Hologram",
        priority = Priority.VERY_HIGH * 0.5)
public class Reference extends HoldingSinglePlugin<ReferencePlugin>
                       implements MainReconstructionPlugin {
    /** Constructor intended for live use of the plugin.
     */
    public Reference()
    {
        super("Reference Hologram", ReferencePlugin.class);
        M_live = true;
    }
    /** Constructor intended for programmatic use of the plugin, with the same
     * filter as everything else.  If you want to supply your own filter, use
     * {@link Reference(ReferencePlugin, boolean, boolean,
     * edu.pdx.imagej.reconstruction.filter.Filter) Reference(ReferencePlugin,
     * boolean, boolean, Filter)}.
     *
     * @param plugin The method of getting the reference hologram to use.
     * @param phase Whether or not to try to cancel phase noise.
     * @param amplitude Whether or not to try to cancel amplitude noise.
     */
    public Reference(ReferencePlugin plugin, boolean phase, boolean amplitude)
    {
        super(plugin);
        M_phase = phase;
        M_amplitude = amplitude;
        M_use_same_roi = true;
    }
    /** Constructor intended for programmatic use of the plugin, with a custom
     * filter.  If you want to use the same filter as everyting else, use {@link
     * Reference(ReferencePlugin, boolean, boolean) Reference(ReferencePlugin,
     * boolean, boolean)}.
     *
     * @param plugin The method of getting the reference hologram to use.
     * @param phase Whether or not to try to cancel phase noise.
     * @param amplitude Whether or not to try to cancel amplitude noise.
     * @param filter The filter to apply on the reference hologram.
     */
    public Reference(ReferencePlugin plugin, boolean phase, boolean amplitude,
                     Filter filter)
    {
        super(plugin);
        M_phase = phase;
        M_amplitude = amplitude;
        M_use_same_roi = false;
        M_not_same_filter = filter;
    }

    /** Get the parameter for this plugin.
     */
    @Override
    public ReferenceParameter param()
    {
        if (M_param == null) {
            M_param = new ReferenceParameter(super.param());
        }
        return M_param;
    }
    /** Get the filter being used to be able to apply it to the reference
     * hologram.
     */
    @Override
    public void read_plugins(List<ReconstructionPlugin> plugins)
    {
        super.read_plugins(plugins);
        for (ReconstructionPlugin plugin : plugins) {
            if (plugin instanceof Filter) M_filter = (Filter)plugin;
        }
    }
    /** Apply the reference hologram.
     */
    @Override
    public void process_filtered_field(ReconstructionField field, int t)
    {
        if (M_live) {
            M_phase = M_param.phase();
            M_amplitude = M_param.amplitude();
            M_use_same_roi = M_param.use_same_roi();
        }
        ReconstructionField reference_field
            = get_plugin().get_reference_holo(
                            new ConstReconstructionField(field), t).copy();
        if (reference_field == null) return;
        if (M_use_same_roi && !get_plugin().dont_use_same_roi()) {
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

        get_reference(reference_field);
        field.field().multiply_in_place(reference_field.field());
    }
    /** Returns a singleton list of <code>{@link
     * ReferencePlugin}.class</code>.
     */
    @Override public List<Class<? extends ReconstructionPlugin>> sub_plugins()
    {
        ArrayList<Class<? extends ReconstructionPlugin>> result
            = new ArrayList<>();
        result.add(ReferencePlugin.class);
        return result;
    }

    void get_reference(ReconstructionField hologram)
    {
        double[][] reference = hologram.field().get_field();
        for (int x = 0; x < hologram.field().width(); ++x) {
            for (int y = 0; y < hologram.field().height(); ++y) {
                double real = reference[x][y * 2];
                double imag = reference[x][y * 2 + 1];
                double abs;
                if (M_amplitude) {
                    // The edges get way too bright for some reason
                    // The 256 is to let the phase still be good
                    // I don't know if it actually does anything, though.
                    if (x == 0 || x == reference.length - 1 ||
                            y == 0 || y == reference[0].length - 1) {
                        abs = Double.MAX_VALUE / 256.0;
                    }
                    else abs = (real*real + imag*imag);
                }
                else abs = Math.sqrt(real*real + imag*imag);
                if (M_phase) {
                    reference[x][y * 2] = real / abs;
                    reference[x][y*2+1] = imag / abs * -1;
                }
                else {
                    if (M_amplitude) {
                        reference[x][y * 2] = 1 / Math.sqrt(abs);
                        reference[x][y*2+1] = 0;
                    }
                    else {
                        reference[x][y * 2] = 1;
                        reference[x][y*2+1] = 0;
                    }
                }
            }
        }
    }

    private Filter M_filter;
    Filter M_not_same_filter; // Package private for testing
    private boolean M_use_same_roi = false;
    private boolean M_phase = false;
    private boolean M_amplitude = false;
    private boolean M_live = false;
    ReferenceParameter M_param; // Package private for testing
}
