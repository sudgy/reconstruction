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

package edu.pdx.imagej.reconstruction;

import java.util.AbstractList;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DPDialog;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.units.UnitService;

/** A DParameter that acquires a list of z values.  "z values" is pretty much
 * just {@link DistanceUnitValue}s.
 */
@Plugin(type = DParameter.class)
public class ZParameter extends HoldingParameter<List<DistanceUnitValue>> {
    public ZParameter() {super("Zs");}
    @Override
    public void initialize()
    {
        M_choice = add_parameter(ChoiceParameter.class, "Z_plane selection",
                                 S_choices, "Single");
        M_param_single = add_parameter(SingleZ.class, this);
        M_param_list = add_parameter(ListZ.class, this);
        M_param_range = add_parameter(RangeZ.class, this);
        set_visibilities();
    }
    @Override
    public void read_from_dialog()
    {
        super.read_from_dialog();
        set_visibilities();
    }
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        super.read_from_prefs(c, name);
        set_visibilities();
    }
    @Override
    public List<DistanceUnitValue> get_value()
    {
        return current_param().get_value();
    }

    private void set_visibilities()
    {
        M_param_single.set_new_visibility(false);
        M_param_list.set_new_visibility(false);
        M_param_range.set_new_visibility(false);
        current_param().set_new_visibility(true);
    }
    private DParameter<List<DistanceUnitValue>> current_param()
    {
        switch (Choices.valueOf(M_choice.get_value())) {
            case Single: return M_param_single;
            case List: return M_param_list;
            case Range: return M_param_range;
        }
        return null;
    }

    private enum Choices {
        Single, List, Range
    }
    private static String[] S_choices = {
        Choices.Single.name(),
        Choices.List.name(),
        Choices.Range.name()
    };

    private ChoiceParameter M_choice;

    private SingleZ M_param_single;
    private ListZ M_param_list;
    private RangeZ M_param_range;
    @Parameter private UnitService P_units;



    /** Get a single z value. */
    public class SingleZ extends HoldingParameter<List<DistanceUnitValue>> {
        public SingleZ() {super("SingleZ");}
        @Override
        public void initialize()
        {
            M_z = add_parameter(DoubleParameter.class, 0.0, "Z_value",
                                P_units.z().toString());
        }
        @Override
        public List<DistanceUnitValue> get_value()
        {
            return new AbstractList<DistanceUnitValue>() {
                @Override
                public DistanceUnitValue get(int index)
                {return new DistanceUnitValue(M_z.get_value(), P_units.z());}
                @Override
                public int size()
                {return 1;}
            };
        }
        private DoubleParameter M_z;
    }



    /** Get z values using a comma-separated list. */
    public class ListZ extends AbstractDParameter<List<DistanceUnitValue>> {
        public ListZ() {super("ListZ");}
        @Override
        public void initialize()
        {
            set_error("Z list is empty.");
        }
        @Override
        public void add_to_dialog(DPDialog dialog)
        {
            M_supplier = dialog.add_text_box(
                "Z values (in a comma separated list)", M_current_string);
        }
        @Override
        public void read_from_dialog()
        {
            M_current_string = M_supplier.get();
            process_errors();
        }
        @Override
        public void save_to_prefs(Class<?> c, String name)
        {
            prefs().put(c, name + ".value", M_current_string);
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            M_current_string = prefs().get(c, name + ".value", "");
            process_errors();
        }
        @Override
        public List<DistanceUnitValue> get_value() {return M_zs;}

        private void process_errors()
        {
            List<String> zs_as_string
                = Arrays.asList(M_current_string.split("\\s*,\\s*"));
            M_zs = new ArrayList<DistanceUnitValue>(zs_as_string.size());
            for (String s : zs_as_string) {
                try {
                    M_zs.add(new DistanceUnitValue(Double.parseDouble(s),
                             P_units.z()));
                }
                catch (NumberFormatException e) {
                    set_error("Unable to parse Z list.  \"" + s
                              + "\" is not a number.");
                    return;
                }
            }
            set_error(null);
        }
        private ArrayList<DistanceUnitValue> M_zs;
        private String M_current_string;
        private Supplier<String> M_supplier;
    }



    /** Get z values using an evenly-spaced range. */
    public class RangeZ extends HoldingParameter<List<DistanceUnitValue>> {
        public RangeZ() {super("RangeZ");}
        @Override
        public void initialize()
        {
            String units = P_units.z().toString();
            M_begin = add_parameter(DoubleParameter.class, 0.0, "Z_value_begin",
                                    units);
            M_end = add_parameter(DoubleParameter.class, 0.0, "Z_value_end",
                                  units);
            M_step = add_parameter(DoubleParameter.class, 1.0, "Z_value_step",
                                   units);
        }
        @Override
        public void read_from_dialog()
        {
            super.read_from_dialog();
            process_errors();
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            process_errors();
        }
        @Override
        public List<DistanceUnitValue> get_value()
        {
            return new AbstractList<DistanceUnitValue>() {
                @Override
                public DistanceUnitValue get(int index)
                {return new DistanceUnitValue(M_begin.get_value()
                    + M_step.get_value() * index, P_units.z());}
                @Override
                public int size()
                {return Math.abs((int)((M_end.get_value() - M_begin.get_value())
                              / M_step.get_value())) + 1;}
            };
        }

        private void process_errors()
        {
            set_error(M_begin.get_error());
            if (get_error() != null) return;
            set_error(M_end.get_error());
            if (get_error() != null) return;
            set_error(M_step.get_error());
            if (get_error() != null) return;
            if (M_step.get_value() == 0) {
                set_error("Z value step cannot be zero.");
                return;
            }
            if (!(M_begin.get_value().equals(M_end.get_value())) &&
                (M_end.get_value() < M_begin.get_value())
                    == (M_step.get_value() > 0)) {
                set_error("The sign of Z value step must be the same as the "
                    + "sign of Z value end minus Z value begin.");
                return;
            }
            set_error(null);
        }
        private DoubleParameter M_begin;
        private DoubleParameter M_end;
        private DoubleParameter M_step;
    }
}
