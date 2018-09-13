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

package edu.pdx.imagej.reconstruction.parameter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ij.gui.GenericDialog;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.reconstruction.UnitService;

@Plugin(type = DParameter.class)
public class ZParameter extends HoldingParameter<AbstractList<Double>> {
    @Override
    public void initialize()
    {
        M_param = add_parameter(SingleZ.class);
    }
    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addChoice("Z_plane selection", S_choices, M_type.name());
        super.add_to_dialog(gd);
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        Choices old_type = M_type;
        M_type = Choices.valueOf(gd.getNextChoice());
        if (old_type != M_type) M_reconstruction_needed = true;
        super.read_from_dialog(gd);
        set_error(M_param.get_error());
    }
    @Override
    public void save_to_prefs(Class<?> c, String name)
    {
        prefs().put(c, name + ".type", M_type.name());
        super.save_to_prefs(c, name);
    }
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        M_type = Choices.valueOf(prefs().get(c, name + ".type", Choices.Single.name()));
        clear_parameters();
        switch (M_type) {
            case Single: M_param = add_parameter(SingleZ.class); break;
            case List: M_param = add_parameter(ListZ.class); break;
            case Range: M_param = add_parameter(RangeZ.class); break;
        }
        super.read_from_prefs(c, name);
        set_error(M_param.get_error());
    }
    @Override
    public boolean reconstruction_needed() {return M_reconstruction_needed;}
    @Override
    public void recreate()
    {
        if (M_reconstruction_needed) {
            M_reconstruction_needed = false;
            clear_parameters();
            switch (M_type) {
                case Single: M_param = add_parameter(SingleZ.class); break;
                case List: M_param = add_parameter(ListZ.class); break;
                case Range: M_param = add_parameter(RangeZ.class); break;
            }
            set_error(M_param.get_error());
        }
        else {
            throw new UnsupportedOperationException();
        }
    }
    @Override
    public AbstractList<Double> get_value() {return M_param.get_value();}

    private enum Choices {
        Single, List, Range
    }
    private static String[] S_choices = {Choices.Single.name(), Choices.List.name(), Choices.Range.name()};
    private Choices M_type = Choices.Single;
    private boolean M_reconstruction_needed = false;
    private DParameter<AbstractList<Double>> M_param;



    public static class SingleZ extends HoldingParameter<AbstractList<Double>> {
        @Override
        public void initialize()
        {
            M_z = add_parameter(DoubleParameter.class, 0.0, "Z_value", P_units.z().toString());
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            super.read_from_dialog(gd);
            process_errors();
        }
        @Override
        public AbstractList<Double> get_value()
        {
            return new AbstractList<Double>() {
                @Override
                public Double get(int index)
                {return M_z.get_value();}
                @Override
                public int size()
                {return 1;}
            };
        }

        private void process_errors()
        {
            set_error(M_z.get_error());
        }
        private DoubleParameter M_z;
        @Parameter private UnitService P_units;
    }



    public static class ListZ extends AbstractDParameter<AbstractList<Double>> {
        @Override
        public void initialize()
        {
            set_error("Z list is empty.");
        }
        @Override
        public void add_to_dialog(GenericDialog gd)
        {
            gd.addStringField("Z values (in a comma separated list)", M_current_string);
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            M_current_string = gd.getNextString();
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
        public AbstractList<Double> get_value() {return M_zs;}

        private void process_errors()
        {
            List<String> zs_as_string = Arrays.asList(M_current_string.split("\\s*,\\s*"));
            M_zs = new ArrayList<Double>(zs_as_string.size());
            for (String s : zs_as_string) {
                try {M_zs.add(Double.parseDouble(s));}
                catch (NumberFormatException e) {
                    set_error("Unable to parse Z list.  \"" + s + "\" is not a number.");
                    return;
                }
            }
            set_error(null);
        }
        private ArrayList<Double> M_zs;
        private String M_current_string;
    }



    public static class RangeZ extends HoldingParameter<AbstractList<Double>> {
        @Override
        public void initialize()
        {
            String units = P_units.z().toString();
            M_begin = add_parameter(DoubleParameter.class, 0.0, "Z_value_begin", units);
            M_end = add_parameter(DoubleParameter.class, 0.0, "Z_value_end", units);
            M_step = add_parameter(DoubleParameter.class, 1.0, "Z_value_step", units);
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            super.read_from_dialog(gd);
            process_errors();
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            process_errors();
        }
        @Override
        public AbstractList<Double> get_value()
        {
            return new AbstractList<Double>() {
                @Override
                public Double get(int index)
                {return M_begin.get_value() + M_step.get_value() * index;}
                @Override
                public int size()
                {return (int)((M_end.get_value() - M_begin.get_value()) / M_step.get_value()) + 1;}
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
            if ((M_end.get_value() < M_begin.get_value()) == (M_step.get_value() > 0)) {
                set_error("The sign of Z value step must be the same as the sign of Z value end minus Z value begin.");
                return;
            }
            set_error(null);
        }
        private DoubleParameter M_begin;
        private DoubleParameter M_end;
        private DoubleParameter M_step;
        @Parameter private UnitService P_units;
    }
}
