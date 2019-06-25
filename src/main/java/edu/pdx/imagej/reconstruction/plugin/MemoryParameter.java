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

package edu.pdx.imagej.reconstruction.plugin;

import java.util.function.Supplier;

import ij.IJ;

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.dynamic_parameters.RadioParameter;

/** A parameter to set memory usage.  It is meant to be used for options.  It
 * has two ways to select the memory usage: A flat value, or a percentage of
 * ImageJ's max memory.
 */
public class MemoryParameter extends HoldingParameter<Long> {
    /** Constructor.  Note that unlike most dynamic parameters, this one does
     * not read from prefs, so the values passed in to this constructor will be
     * the starting values seen by the user, no exceptions.
     *
     * @param label The internal label used to identify this parameter.
     * @param do_cache Whether or not to start out with caching enabled.
     * @param initial_percent If you want to have the default setting be a
     *                        percentage of the maximum memory.
     * @param percent_value The value to be used for the initial percent (0 &le;
     *                      x &le; 100).
     * @param flat_value The value, in megabytes, to be used for the initial
     *                   flat value (x &gt; 0).
     */
    public MemoryParameter(String label, boolean do_cache,
                           boolean initial_percent, double percent_value,
                           int flat_value)
    {
        super(label);
        M_do_cache = do_cache;
        M_initial_percent = initial_percent;
        M_percent_value = percent_value;
        M_flat_value = flat_value;
    }
    /** Initialize the parameters.
     */
    @Override
    public void initialize()
    {
        String[] choices = {"Percent", "Flat"};
        M_do = add_parameter(BoolParameter.class, "Cache partial results?",
                             M_do_cache);
        M_choice = new RadioParameter("Memory limit", choices,
                                      M_initial_percent ? "Percent" : "Flat",
                                      1, 2);
        add_premade_parameter(M_choice);
        M_percent = add_parameter(DoubleParameter.class, M_percent_value,
                                  "Percent of Maximum", "%");
        M_flat = add_parameter(IntParameter.class, M_flat_value, "Flat Value",
                               "MB");
        M_percent.set_bounds(0.0, 100.0);
        M_flat.set_bounds(0, Integer.MAX_VALUE);
        set_visibilities();
        check_for_errors();
    }
    @Override
    public void read_from_dialog()
    {
        super.read_from_dialog();
        set_visibilities();
        check_for_errors();
    }
    /** <strong>Does nothing</strong>.  Because options should be read from
     * prefs somewhere else, this method does nothing.  We expect you to pass
     * the correct, current value in the constructor.
     */
    @Override
    public void read_from_prefs(Class<?> cls, String name) {}
    /** Get the number of bytes the parameter says to use.  If caching has been
     * disabled, it will return <code>null</code>.
     *
     * @return The number of bytes the parameter says to use, or <code>null
     *         </code> if caching has been disabled.
     */
    @Override
    public Long get_value()
    {
        if (!M_do.get_value()) return null;
        if (M_choice.get_value().equals("Percent")) {
            return (long)(IJ.maxMemory() * M_percent.get_value() * 0.01);
        }
        else {
            return M_flat.get_value() * (1024L * 1024L);
        }
    }
    /** Get whether or not it was set to be a percent rather than a flat value.
     *
     * @return Whether or not it was set to be a percent.
     */
    public boolean percent() {return M_choice.get_value().equals("Percent");}
    /** Get the percent value selected.
     *
     * @return The percent value.  Make sure to multiply by 0.01.
     */
    public double percent_value() {return M_percent.get_value();}
    /** Get the flat value, in megabytes.
     *
     * @return The flat value.
     */
    public int flat_value() {return M_flat.get_value();}

    private void set_visibilities()
    {
        if (M_do.get_value()) {
            M_choice.set_new_visibility(true);
            boolean percent = M_choice.get_value().equals("Percent");
            M_percent.set_new_visibility(percent);
            M_flat.set_new_visibility(!percent);
        }
        else {
            M_choice.set_new_visibility(false);
            M_percent.set_new_visibility(false);
            M_flat.set_new_visibility(false);
        }
    }
    private void check_for_errors()
    {
        int flat_mb = M_flat.get_value();
        int ij_mb = (int)(IJ.maxMemory() / (1024*1024));
        if (!M_choice.get_value().equals("Percent") && flat_mb > ij_mb) {
            set_warning("Warning: Flat value " + flat_mb + " is greater than "
                + "ImageJ's maximum allowed memory, " + ij_mb + ".");
        }
        else set_warning(null);
    }

    private boolean M_do_cache;
    private boolean M_initial_percent;
    private double M_percent_value;
    private int M_flat_value;
    private BoolParameter M_do;
    private RadioParameter M_choice;
    private DoubleParameter M_percent;
    private IntParameter M_flat;
}
