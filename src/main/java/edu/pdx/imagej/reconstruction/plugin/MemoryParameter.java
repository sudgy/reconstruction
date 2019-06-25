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

import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.dynamic_parameters.RadioParameter;

/** A parameter to set memory usage.  It is meant to be used for options.  It
 * has two ways to select the memory usage: A flat value, or a percentage of
 * ImageJ's max memory.
 */
public class MemoryParameter extends HoldingParameter<Supplier<Integer>> {
    public MemoryParameter(String label, boolean initial_percent,
                           double percent_value, int flat_value)
    {
        super(label);
        M_initial_percent = initial_percent;
        M_percent_value = percent_value;
        M_flat_value = flat_value;
    }
    @Override
    public void initialize()
    {
        String[] choices = {"Percent", "Flat"};
        M_choice = new RadioParameter("Memory limit", choices,
                                      M_initial_percent ? "Percent" : "Flat",
                                      1, 2);
        add_premade_parameter(M_choice);
        M_percent = add_parameter(DoubleParameter.class, M_percent_value,
                                  "Percent Value", "%");
        M_flat = add_parameter(IntParameter.class, M_flat_value, "Flat Value",
                               "Bytes");
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
    @Override
    public void read_from_prefs(Class<?> cls, String name)
    {
        super.read_from_prefs(cls, name);
        set_visibilities();
        check_for_errors();
    }
    @Override
    public Supplier<Integer> get_value()
    {
        if (M_choice.get_value().equals("Percent")) {
            final double percent = M_percent.get_value();
            return new Supplier<Integer>() {
                @Override
                public Integer get()
                {
                    return (int)(IJ.maxMemory() * percent * 0.01);
                }
            };
        }
        else {
            final int flat = M_flat.get_value();
            return new Supplier<Integer>() {
                @Override
                public Integer get()
                {
                    return flat;
                }
            };
        }
    }

    private void set_visibilities()
    {
        boolean percent = M_choice.get_value().equals("Percent");
        M_percent.set_new_visibility(percent);
        M_flat.set_new_visibility(!percent);
    }
    private void check_for_errors()
    {
        if (!M_choice.get_value().equals("Percent")
                && M_flat.get_value() > IJ.maxMemory()) {
            set_warning("Flat value " + M_flat.get_value() + " is greater than "
                + "ImageJ's maximum allowed memory, " + IJ.maxMemory() +".");
        }
        else set_warning(null);
    }

    private boolean M_initial_percent;
    private double M_percent_value;
    private int M_flat_value;
    private RadioParameter M_choice;
    private DoubleParameter M_percent;
    private IntParameter M_flat;
}
