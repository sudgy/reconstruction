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

import ij.ImagePlus;

import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.DPDialog;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;

import ij.IJ;

/** This is a DParameter that acquires a list of integers representing some time
 * slices in an image.  It is currently used for getting the time slices to
 * reconstruct and for choosing what slices to use for the median when finding
 * the reference hologram.
 * <p>
 * There are currently three ways to use this parameter, which are the three
 * possible values of the {@link PossibleTypes} enum.  See the description of
 * that enum for details.
 */
@Plugin(type = DParameter.class)
public class TParameter extends HoldingParameter<List<Integer>> {
    /** This is the possible ways to get time values.  It is passed to the
     * constructor of TParameter.
     */
    public enum PossibleTypes {
        /** All: Use all ways of getting time slices. */
        All,
        /** AllMulti: Use all ways of getting time slices other than ones that
         * require just one slice.
         */
        AllMulti,
        /** SomeMulti: Like AllMulti, but also remove the option to use all
         * times.
         */
        SomeMulti;
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
    /** Constructor.
     *
     * @param holo_p The image that you are choosing your time values from.
     *               Time values outside the number of time slices this image
     *               has will be rejected.
     * @param possible Which methods of choosing time values will be used.
     * @param label The label used on the dialog.
     */
    public TParameter(ImageParameter holo_p, PossibleTypes possible,
                      String label)
    {
        super(label + "Ts");
        M_holo_p = holo_p;
        M_possible = possible;
    }
    @Override
    public void initialize()
    {
        ImagePlus i = M_holo_p.get_value();
        if (i == null) return;
        M_max_t = i.getImageStackSize();
        M_choice_all = add_parameter(ChoiceParameter.class,
                                     "t_slice_selection",
                                     S_choices,
                                     Choices.Current.toString());
        M_choice_all_multi = add_parameter(ChoiceParameter.class,
                                           "t_slice_selection_",
                                           S_all_multi_choices,
                                           Choices.All.toString());
        M_choice_some_multi = add_parameter(ChoiceParameter.class,
                                            "t_slice_selection__",
                                            S_some_multi_choices,
                                            Choices.Range.toString());
        M_choice_all.set_new_visibility(false);
        M_choice_all_multi.set_new_visibility(false);
        M_choice_some_multi.set_new_visibility(false);
        if (M_max_t > 1) {
            switch (M_possible) {
                case All: M_choice_all.set_new_visibility(true); break;
                case AllMulti:
                    M_choice_all_multi.set_new_visibility(true); break;
                case SomeMulti:
                    M_choice_some_multi.set_new_visibility(true); break;
            }
        }

        M_param_single = add_parameter(SingleT.class, this);
        M_param_current = new CurrentT();
        M_param_all = new AllT();
        M_param_list = add_parameter(ListT.class, this);
        M_param_range = add_parameter(RangeT.class, this);
        M_param_continuous = add_parameter(ContinuousT.class, this);
    }
    @Override
    public void read_from_dialog()
    {
        update_max_t();
        super.read_from_dialog();
        set_visibilities();
    }
    @Override
    public void read_from_prefs(Class<?> c, String name)
    {
        super.read_from_prefs(c, name);
        set_visibilities();
    }
    private void set_visibilities()
    {
        update_max_t();
        if (M_holo_p.get_value() == null) return;
        M_param_single.set_new_visibility(false);
        M_param_list.set_new_visibility(false);
        M_param_range.set_new_visibility(false);
        M_param_continuous.set_new_visibility(false);
        current_choices().set_new_visibility(false);
        if (M_max_t > 1) {
            current_choices().set_new_visibility(true);
            switch (Choices.value_of(current_choices().get_value())) {
                case Single: M_param_single.set_new_visibility(true); break;
                case List: M_param_list.set_new_visibility(true); break;
                case Range: M_param_range.set_new_visibility(true); break;
                case Continuous:
                    M_param_continuous.set_new_visibility(true); break;
            }
        }
    }
    @Override
    public List<Integer> get_value() {return current_param().get_value();}
    void update_max_t() // Package private for testing
    {
        ImagePlus img = M_holo_p.get_value();
        if (img == null) return;
        M_max_t = img.getImageStackSize();
    }

    private ChoiceParameter current_choices()
    {
        switch (M_possible) {
            case All: return M_choice_all;
            case AllMulti: return M_choice_all_multi;
            case SomeMulti: return M_choice_some_multi;
        }
        return null;
    }
    private DParameter<List<Integer>> current_param()
    {
        if (M_max_t > 1) {
            switch (Choices.value_of(current_choices().get_value())) {
                case Single: return M_param_single;
                case Current: return M_param_current;
                case All: return M_param_all;
                case List: return M_param_list;
                case Range: return M_param_range;
                case Continuous: return M_param_continuous;
            }
            return null;
        }
        else return M_param_single;
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
    private static String[] S_choices = {
        Choices.Single.toString(),
        Choices.Current.toString(),
        Choices.All.toString(),
        Choices.List.toString(),
        Choices.Range.toString(),
        Choices.Continuous.toString()
    };
    private static String[] S_all_multi_choices = {
        Choices.All.toString(),
        Choices.List.toString(),
        Choices.Range.toString(),
        Choices.Continuous.toString()
    };
    private static String[] S_some_multi_choices = {
        Choices.List.toString(),
        Choices.Range.toString(),
        Choices.Continuous.toString()
    };

    private ChoiceParameter M_choice_all;
    private ChoiceParameter M_choice_all_multi;
    private ChoiceParameter M_choice_some_multi;

    private SingleT M_param_single;
    private CurrentT M_param_current;
    private AllT M_param_all;
    private ListT M_param_list;
    private RangeT M_param_range;
    private ContinuousT M_param_continuous;

    private ImageParameter M_holo_p;

    private int M_max_t;
    private PossibleTypes M_possible;



    /** Time parameter getting a single time value */
    public class SingleT extends HoldingParameter<List<Integer>> {
        public SingleT() {super("SingleT");}
        @Override
        public void initialize()
        {
            M_t = add_parameter(IntParameter.class, 1, "t_value");
            M_current_max_t = M_max_t;
            M_t.set_bounds(1, M_max_t);
        }
        @Override
        public void add_to_dialog(DPDialog dialog)
        {
            super.add_to_dialog(dialog);
            if (M_current_max_t != M_max_t) {
                M_current_max_t = M_max_t;
                M_t.set_bounds(1, M_max_t);
            }
        }
        @Override
        public void read_from_dialog()
        {
            super.read_from_dialog();
            if (M_current_max_t != M_max_t) {
                M_current_max_t = M_max_t;
                M_t.set_bounds(1, M_max_t);
            }
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            M_t.set_bounds(1, M_max_t);
        }
        @Override
        public List<Integer> get_value()
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

        private IntParameter M_t = new IntParameter(1, "t_value");
        private int M_current_max_t;
    }



    /** Time parameter returning the current time slice */
    public class CurrentT extends AbstractDParameter<List<Integer>> {
        public CurrentT() {super("CurrentT");}
        @Override public void add_to_dialog(DPDialog dialog) {}
        @Override public void read_from_dialog() {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
        @Override
        public List<Integer> get_value()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_holo_p.get_value().getCurrentSlice();}
                @Override
                public int size()
                {return 1;}
            };
        }
    }



    /** Time parameter returning all time slices */
    public class AllT extends AbstractDParameter<List<Integer>> {
        public AllT() {super("AllT");}
        @Override public void add_to_dialog(DPDialog dialog) {}
        @Override public void read_from_dialog() {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
        @Override
        public List<Integer> get_value()
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



    /** Time parameter using a comma-separated list of values */
    public class ListT extends AbstractDParameter<List<Integer>> {
        public ListT() {super("ListT");}
        @Override
        public void initialize()
        {
            set_error("t list is empty.");
        }
        @Override
        public void add_to_dialog(DPDialog dialog)
        {
            M_supplier = dialog.add_text_box(
                "t values (in a comma separated list)", M_current_string);
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
        public List<Integer> get_value() {return M_ts;}

        private void process_errors()
        {
            if (M_current_string == null || M_current_string == "") {
                set_error("t list is empty.");
                return;
            }
            List<String> ts_as_string
                = Arrays.asList(M_current_string.split("\\s*,\\s*"));
            M_ts = new ArrayList<Integer>(ts_as_string.size());
            for (String s : ts_as_string) {
                try {M_ts.add(Integer.parseInt(s));}
                catch (NumberFormatException e) {
                    set_error("Unable to parse t list.  \"" + s
                              + "\" is not an integer.");
                    return;
                }
            }
            for (int t : M_ts) {
                if (t < 1 || t > M_max_t) {
                    set_error("t value \"" + t + "\" is not in the range [1.."
                              + M_max_t + "].");
                    return;
                }
            }
            set_error(null);
        }
        private ArrayList<Integer> M_ts;
        private String M_current_string;
        private Supplier<String> M_supplier;
    }



    /** Time parameter using an equally-spaced range */
    public class RangeT extends HoldingParameter<List<Integer>> {
        public RangeT() {super("RangeT");}
        @Override
        public void initialize()
        {
            M_begin = add_parameter(IntParameter.class, 1, "t_value_begin");
            M_end = add_parameter(IntParameter.class, 1, "t_value_end");
            M_step = add_parameter(IntParameter.class, 1, "t_value_step");
            M_begin.set_bounds(1, M_max_t);
            M_end.set_bounds(1, M_max_t);
        }
        @Override
        public void add_to_dialog(DPDialog dialog)
        {
            super.add_to_dialog(dialog);
            if (M_current_max_t != M_max_t) {
                M_current_max_t = M_max_t;
                M_begin.set_bounds(1, M_max_t);
                M_end.set_bounds(1, M_max_t);
            }
            process_errors();
        }
        @Override
        public void read_from_dialog()
        {
            super.read_from_dialog();
            if (M_current_max_t != M_max_t) {
                M_current_max_t = M_max_t;
                M_begin.set_bounds(1, M_max_t);
                M_end.set_bounds(1, M_max_t);
            }
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
        public List<Integer> get_value()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_begin.get_value() + M_step.get_value() * index;}
                @Override
                public int size()
                {return Math.abs((M_end.get_value() - M_begin.get_value())
                         / M_step.get_value()) + 1;}
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
            if (!(M_begin.get_value().equals(M_end.get_value())) &&
                (M_end.get_value() < M_begin.get_value())
                    == (M_step.get_value() > 0)) {
                set_error("The sign of t value step must be the same as the "
                          + "sign of t value end minus t value begin.");
                return;
            }
            set_error(null);
        }
        private IntParameter M_begin;
        private IntParameter M_end;
        private IntParameter M_step;
        private int M_current_max_t;
    }



    /** Time parameter using a continuous (separated by one) range */
    public class ContinuousT extends HoldingParameter<List<Integer>> {
        public ContinuousT() {super("ContinuousT");}
        @Override
        public void initialize()
        {
            M_begin = add_parameter(IntParameter.class, 1, "t_value_begin");
            M_end = add_parameter(IntParameter.class, 1, "t_value_end");
            M_begin.set_bounds(1, M_max_t);
            M_end.set_bounds(1, M_max_t);
        }
        @Override
        public void add_to_dialog(DPDialog dialog)
        {
            super.add_to_dialog(dialog);
            if (M_current_max_t != M_max_t) {
                M_current_max_t = M_max_t;
                M_begin.set_bounds(1, M_max_t);
                M_end.set_bounds(1, M_max_t);
            }
        }
        @Override
        public void read_from_dialog()
        {
            super.read_from_dialog();
            if (M_current_max_t != M_max_t) {
                M_current_max_t = M_max_t;
                M_begin.set_bounds(1, M_max_t);
                M_end.set_bounds(1, M_max_t);
            }
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            M_begin.set_bounds(1, M_max_t);
            M_end.set_bounds(1, M_max_t);
        }
        @Override
        public List<Integer> get_value()
        {
            int multiplier = M_begin.get_value() < M_end.get_value() ? 1 : -1;
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_begin.get_value() + index * multiplier;}
                @Override
                public int size()
                {return Math.abs(M_end.get_value() - M_begin.get_value()) + 1;}
            };
        }

        private IntParameter M_begin;
        private IntParameter M_end;
        private int M_current_max_t;
    }
}
