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

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;

@Plugin(type = DParameter.class)
public class TParameter extends HoldingParameter<AbstractList<Integer>> {
    public enum PossibleTypes {
        All, AllMulti, SomeMulti;
        Choices get_default_choice()
        {
            switch (this) {
                case All: return Choices.Current;
                case AllMulti: return Choices.All;
                case SomeMulti: return Choices.Range;
            }
            return null;
        }
        String[] get_choices()
        {
            switch (this) {
                case All: return S_choices;
                case AllMulti: return S_all_multi_choices;
                case SomeMulti: return S_some_multi_choices;
            }
            return null;
        }
    }
    public TParameter(ImageParameter holo_p, PossibleTypes possible)
    {
        M_holo_p = holo_p;
        M_possible = possible;
        M_type = M_possible.get_default_choice();
    }
    @Override
    public void initialize()
    {
        M_param = make_default_class();
        ImagePlus i = M_holo_p.get_value();
        if (i == null) return;
        M_max_t = i.getImageStackSize();
        M_show = M_max_t > 1;
    }
    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        M_show = M_max_t > 1;
        if (M_show) {
            gd.addChoice("t_slice selection", M_possible.get_choices(), M_type.toString());
            super.add_to_dialog(gd);
        }
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        M_max_t = M_holo_p.get_value().getImageStackSize();
        if (M_show) {
            Choices old_type = M_type;
            M_type = Choices.value_of(gd.getNextChoice());
            if (old_type != M_type) M_reconstruction_needed = true;
            // The user just selected an image with one layer when it was multi-layer
            if (M_max_t == 1) {
                M_type = M_possible.get_default_choice();
                M_reconstruction_needed = true;
            }
            super.read_from_dialog(gd);
            set_error(M_param.get_error());
        }
        // The user just selected an image with multiple layers when it was one layer
        if (!M_show && M_max_t > 1) {
            M_reconstruction_needed = true;
        }
    }
    @Override
    public void save_to_prefs(Class<?> c, String name)
    {
        prefs().put(c, name + ".type", M_type.toString());
        super.save_to_prefs(c, name);
    }
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        if (M_max_t == 1) {
            M_param = make_default_class();
            return;
        }
        M_type = Choices.value_of(prefs().get(c, name + ".type", M_possible.get_default_choice().toString()));
        clear_parameters();
        switch (M_type) {
            case Single: M_param = add_parameter(SingleT.class, this); break;
            case Current: M_param = add_parameter(CurrentT.class, this); break;
            case All: M_param = add_parameter(AllT.class, this); break;
            case List: M_param = add_parameter(ListT.class, this); break;
            case Range: M_param = add_parameter(RangeT.class, this); break;
            case Continuous: M_param = add_parameter(ContinuousT.class, this); break;
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
                case Single: M_param = add_parameter(SingleT.class, this); break;
                case Current: M_param = add_parameter(CurrentT.class, this); break;
                case All: M_param = add_parameter(AllT.class, this); break;
                case List: M_param = add_parameter(ListT.class, this); break;
                case Range: M_param = add_parameter(RangeT.class, this); break;
                case Continuous: M_param = add_parameter(ContinuousT.class, this); break;
            }
            set_error(M_param.get_error());
        }
        else {
            throw new UnsupportedOperationException();
        }
    }
    @Override
    public AbstractList<Integer> get_value() {return M_param.get_value();}

    private DParameter<AbstractList<Integer>> make_default_class()
    {
        switch (M_possible) {
            case All: return add_parameter(CurrentT.class, this);
            case AllMulti: return add_parameter(AllT.class, this);
            case SomeMulti: return add_parameter(RangeT.class, this);
        }
        return null;
    }
    private enum Choices {
        Single, Current, All, List, Range, Continuous;
        @Override public String toString()
        {
            if (this == Current) return "Current Frame";
            else if (this == Continuous) return "Continuous Range";
            else return name();
        }
        public static Choices value_of(String s)
        {
            if (s.equals("Current Frame")) return Current;
            else if (s.equals("Continuous Range")) return Continuous;
            else return Choices.valueOf(s);
        }
    }
    private static String[] S_choices = {Choices.Single.toString(), Choices.Current.toString(), Choices.All.toString(), Choices.List.toString(), Choices.Range.toString(), Choices.Continuous.toString()};
    private static String[] S_all_multi_choices = {Choices.All.toString(), Choices.List.toString(), Choices.Range.toString(), Choices.Continuous.toString()};
    private static String[] S_some_multi_choices = {Choices.List.toString(), Choices.Range.toString(), Choices.Continuous.toString()};
    private Choices M_type = Choices.Current;
    private boolean M_reconstruction_needed = false;
    private ImageParameter M_holo_p;
    private int M_max_t;
    private boolean M_show;
    private DParameter<AbstractList<Integer>> M_param;
    private PossibleTypes M_possible;



    public class SingleT extends HoldingParameter<AbstractList<Integer>> {
        @Override
        public void initialize()
        {
            M_t = add_parameter(IntParameter.class, 1, "t_value");
            M_t.set_bounds(1, M_max_t);
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
            M_t.set_bounds(1, M_max_t);
            process_errors();
        }
        @Override
        public AbstractList<Integer> get_value()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_t.get_value();}
                @Override
                public int size()
                {return 1;}
            };
        }

        private void process_errors()
        {
            set_error(M_t.get_error());
        }
        private IntParameter M_t = new IntParameter(1, "t_value");
    }



    public class CurrentT extends AbstractDParameter<AbstractList<Integer>> {
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
        @Override
        public AbstractList<Integer> get_value()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return WindowManager.getCurrentImage().getCurrentSlice();}
                @Override
                public int size()
                {return 1;}
            };
        }
    }



    public class AllT extends AbstractDParameter<AbstractList<Integer>> {
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
        @Override
        public AbstractList<Integer> get_value()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return index + 1;}
                @Override
                public int size()
                {return M_max_t;}
            };
        }
    }



    public class ListT extends AbstractDParameter<AbstractList<Integer>> {
        @Override
        public void initialize()
        {
            set_error("t list is empty.");
        }
        @Override
        public void add_to_dialog(GenericDialog gd)
        {
            gd.addStringField("t values (in a comma separated list)", M_current_string);
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
        public AbstractList<Integer> get_value() {return M_ts;}

        private void process_errors()
        {
            List<String> ts_as_string = Arrays.asList(M_current_string.split("\\s*,\\s*"));
            M_ts = new ArrayList<Integer>(ts_as_string.size());
            for (String s : ts_as_string) {
                try {M_ts.add(Integer.parseInt(s));}
                catch (NumberFormatException e) {
                    set_error("Unable to parse t list.  \"" + s + "\" is not an integer.");
                    return;
                }
            }
            for (int t : M_ts) {
                if (t < 1 || t > M_max_t) {
                    set_error("t value \"" + t + "\" is not in the range [1.." + M_max_t + "].");
                    return;
                }
            }
            set_error(null);
        }
        private ArrayList<Integer> M_ts;
        private String M_current_string;
    }



    public class RangeT extends HoldingParameter<AbstractList<Integer>> {
        @Override
        public void initialize()
        {
            M_begin = add_parameter(IntParameter.class, 1, "t_value_begin");
            M_end = add_parameter(IntParameter.class, 1, "t_value_end");
            M_step = add_parameter(IntParameter.class, 1, "t_value_step");
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            super.read_from_dialog(gd);
            M_begin.set_bounds(1, M_max_t);
            M_end.set_bounds(1, M_max_t);
            process_errors();
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            M_begin.set_bounds(1, M_max_t);
            M_end.set_bounds(1, M_max_t);
            process_errors();
        }
        @Override
        public AbstractList<Integer> get_value()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_begin.get_value() + M_step.get_value() * index;}
                @Override
                public int size()
                {return ((M_end.get_value() - M_begin.get_value()) / M_step.get_value()) + 1;}
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
                set_error("t value step cannot be zero.");
                return;
            }
            if ((M_end.get_value() < M_begin.get_value()) == (M_step.get_value() > 0)) {
                set_error("The sign of t value step must be the same as the sign of t value end minus t value begin.");
                return;
            }
            set_error(null);
        }
        private IntParameter M_begin;
        private IntParameter M_end;
        private IntParameter M_step;
    }



    public class ContinuousT extends AbstractDParameter<AbstractList<Integer>> {
        @Override
        public void initialize()
        {
            set_error("t range is empty.");
        }
        @Override
        public void add_to_dialog(GenericDialog gd)
        {
            gd.addStringField("t values (in the format \"begin-end\")", M_current_string);
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
        public AbstractList<Integer> get_value()
        {
            int multiplier = M_t1 < M_t2 ? 1 : -1;
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_t1 + index * multiplier;}
                @Override
                public int size()
                {return (M_t2 - M_t1) * multiplier + 1;}
            };
        }

        private void process_errors()
        {
            List<String> ts_as_string = Arrays.asList(M_current_string.split("\\s*-\\s*"));
            if (ts_as_string.size() != 2) {
                set_error("Unable to parse t range.");
                return;
            }
            try {M_t1 = Integer.parseInt(ts_as_string.get(0));}
            catch (NumberFormatException e) {
                set_error("Unable to parse t range.  \"" + ts_as_string.get(0) + "\" is not an integer.");
                return;
            }
            try {M_t2 = Integer.parseInt(ts_as_string.get(1));}
            catch (NumberFormatException e) {
                set_error("Unable to parse t range.  \"" + ts_as_string.get(1) + "\" is not an integer.");
                return;
            }
            if (M_t1 < 1 || M_t1 > M_max_t) {
                set_error("t value \"" + M_t1 + "\" is not in the range [1.." + M_max_t + "].");
                return;
            }
            if (M_t2 < 1 || M_t2 > M_max_t) {
                set_error("t value \"" + M_t2 + "\" is not in the range [1.." + M_max_t + "].");
                return;
            }
            set_error(null);
        }
        private int M_t1;
        private int M_t2;
        private String M_current_string;
    }
}
