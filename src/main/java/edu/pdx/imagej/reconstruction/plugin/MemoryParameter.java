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
     * @param doCache Whether or not to start out with caching enabled.
     * @param initialPercent If you want to have the default setting be a
     *                        percentage of the maximum memory.
     * @param percentValue The value to be used for the initial percent (0 &le;
     *                      x &le; 100).
     * @param flatValue The value, in megabytes, to be used for the initial
     *                   flat value (x &gt; 0).
     */
    public MemoryParameter(String label, boolean doCache,
                           boolean initialPercent, double percentValue,
                           int flatValue)
    {
        super(label);
        M_doCache = doCache;
        M_initialPercent = initialPercent;
        M_percentValue = percentValue;
        M_flatValue = flatValue;
    }
    /** Initialize the parameters.
     */
    @Override
    public void initialize()
    {
        String[] choices = {"Percent", "Flat"};
        M_do = addParameter(new BoolParameter("Cache partial results?",
                             M_doCache));
        M_choice = new RadioParameter("Memory limit", choices,
                                      M_initialPercent ? "Percent" : "Flat",
                                      1, 2);
        addParameter(M_choice);
        M_percent = addParameter(new DoubleParameter(M_percentValue,
                                  "Percent of Maximum", "%"));
        M_flat = addParameter(new IntParameter(M_flatValue, "Flat Value","MB"));
        M_percent.setBounds(0.0, 100.0);
        M_flat.setBounds(0, Integer.MAX_VALUE);
        setVisibilities();
        checkForErrors();
    }
    @Override
    public void readFromDialog()
    {
        super.readFromDialog();
        setVisibilities();
        checkForErrors();
    }
    /** <strong>Does nothing</strong>.  Because options should be read from
     * prefs somewhere else, this method does nothing.  We expect you to pass
     * the correct, current value in the constructor.
     */
    @Override
    public void readFromPrefs(Class<?> cls, String name) {}
    /** Get the number of bytes the parameter says to use.  If caching has been
     * disabled, it will return <code>null</code>.
     *
     * @return The number of bytes the parameter says to use, or <code>null
     *         </code> if caching has been disabled.
     */
    @Override
    public Long getValue()
    {
        if (!M_do.getValue()) return null;
        if (M_choice.getValue().equals("Percent")) {
            return (long)(IJ.maxMemory() * M_percent.getValue() * 0.01);
        }
        else {
            return M_flat.getValue() * (1024L * 1024L);
        }
    }
    /** Get whether or not it was set to be a percent rather than a flat value.
     *
     * @return Whether or not it was set to be a percent.
     */
    public boolean percent() {return M_choice.getValue().equals("Percent");}
    /** Get the percent value selected.
     *
     * @return The percent value.  Make sure to multiply by 0.01.
     */
    public double percentValue() {return M_percent.getValue();}
    /** Get the flat value, in megabytes.
     *
     * @return The flat value.
     */
    public int flatValue() {return M_flat.getValue();}

    private void setVisibilities()
    {
        if (M_do.getValue()) {
            M_choice.setNewVisibility(true);
            boolean percent = M_choice.getValue().equals("Percent");
            M_percent.setNewVisibility(percent);
            M_flat.setNewVisibility(!percent);
        }
        else {
            M_choice.setNewVisibility(false);
            M_percent.setNewVisibility(false);
            M_flat.setNewVisibility(false);
        }
    }
    private void checkForErrors()
    {
        int flatMb = M_flat.getValue();
        int ijMb = (int)(IJ.maxMemory() / (1024*1024));
        if (!M_choice.getValue().equals("Percent") && flatMb > ijMb) {
            setWarning("Warning: Flat value " + flatMb + " is greater than "
                + "ImageJ's maximum allowed memory, " + ijMb + ".");
        }
        else setWarning(null);
    }

    private boolean M_doCache;
    private boolean M_initialPercent;
    private double M_percentValue;
    private int M_flatValue;
    private BoolParameter M_do;
    private RadioParameter M_choice;
    private DoubleParameter M_percent;
    private IntParameter M_flat;
}
