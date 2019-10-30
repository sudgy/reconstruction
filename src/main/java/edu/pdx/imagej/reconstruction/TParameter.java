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
        Choices getDefaultChoice()
        {
            switch (this) {
                case All: return Choices.Current;
                case AllMulti: return Choices.All;
                case SomeMulti: return Choices.Range;
            }
            return null;
        }
        String[] getChoices()
        {
            switch (this) {
                case All: return S_choices;
                case AllMulti: return S_allMultiChoices;
                case SomeMulti: return S_someMultiChoices;
            }
            return null;
        }
    }
    /** Constructor.
     *
     * @param holoP The image that you are choosing your time values from.
     *               Time values outside the number of time slices this image
     *               has will be rejected.
     * @param possible Which methods of choosing time values will be used.
     * @param label The label used on the dialog.
     */
    public TParameter(ImageParameter holoP, PossibleTypes possible,
                      String label)
    {
        super(label + "Ts");
        M_holoP = holoP;
        M_possible = possible;
    }
    @Override
    public void initialize()
    {
        ImagePlus i = M_holoP.getValue();
        if (i == null) return;
        M_maxT = i.getImageStackSize();
        M_choiceAll = addParameter(new ChoiceParameter(
                                     "t_slice_selection",
                                     S_choices,
                                     Choices.Current.toString()));
        M_choiceAllMulti = addParameter(new ChoiceParameter(
                                           "t_slice_selection_",
                                           S_allMultiChoices,
                                           Choices.All.toString()));
        M_choiceSomeMulti = addParameter(new ChoiceParameter(
                                            "t_slice_selection__",
                                            S_someMultiChoices,
                                            Choices.Range.toString()));
        M_choiceAll.setNewVisibility(false);
        M_choiceAllMulti.setNewVisibility(false);
        M_choiceSomeMulti.setNewVisibility(false);
        if (M_maxT > 1) {
            switch (M_possible) {
                case All: M_choiceAll.setNewVisibility(true); break;
                case AllMulti:
                    M_choiceAllMulti.setNewVisibility(true); break;
                case SomeMulti:
                    M_choiceSomeMulti.setNewVisibility(true); break;
            }
        }

        M_paramSingle = addParameter(new SingleT());
        M_paramCurrent = new CurrentT();
        M_paramAll = new AllT();
        M_paramList = addParameter(new ListT());
        M_paramRange = addParameter(new RangeT());
        M_paramContinuous = addParameter(new ContinuousT());
    }
    @Override
    public void readFromDialog()
    {
        updateMaxT();
        super.readFromDialog();
        setVisibilities();
    }
    @Override
    public void readFromPrefs(Class<?> c, String name)
    {
        super.readFromPrefs(c, name);
        setVisibilities();
    }
    private void setVisibilities()
    {
        updateMaxT();
        if (M_holoP.getValue() == null) return;
        M_paramSingle.setNewVisibility(false);
        M_paramList.setNewVisibility(false);
        M_paramRange.setNewVisibility(false);
        M_paramContinuous.setNewVisibility(false);
        currentChoices().setNewVisibility(false);
        if (M_maxT > 1) {
            currentChoices().setNewVisibility(true);
            switch (Choices.valueOf2(currentChoices().getValue())) {
                case Single: M_paramSingle.setNewVisibility(true); break;
                case List: M_paramList.setNewVisibility(true); break;
                case Range: M_paramRange.setNewVisibility(true); break;
                case Continuous:
                    M_paramContinuous.setNewVisibility(true); break;
            }
        }
    }
    @Override
    public List<Integer> getValue() {return currentParam().getValue();}
    void updateMaxT() // Package private for testing
    {
        ImagePlus img = M_holoP.getValue();
        if (img == null) return;
        M_maxT = img.getImageStackSize();
    }

    private ChoiceParameter currentChoices()
    {
        switch (M_possible) {
            case All: return M_choiceAll;
            case AllMulti: return M_choiceAllMulti;
            case SomeMulti: return M_choiceSomeMulti;
        }
        return null;
    }
    private DParameter<List<Integer>> currentParam()
    {
        if (M_maxT > 1) {
            switch (Choices.valueOf2(currentChoices().getValue())) {
                case Single: return M_paramSingle;
                case Current: return M_paramCurrent;
                case All: return M_paramAll;
                case List: return M_paramList;
                case Range: return M_paramRange;
                case Continuous: return M_paramContinuous;
            }
            return null;
        }
        else return M_paramSingle;
    }
    private enum Choices {
        Single, Current, All, List, Range, Continuous;
        @Override public String toString()
        {
            if (this == Current) return "Current Frame";
            else if (this == Continuous) return "Continuous Range";
            else return name();
        }
        public static Choices valueOf2(String s)
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
    private static String[] S_allMultiChoices = {
        Choices.All.toString(),
        Choices.List.toString(),
        Choices.Range.toString(),
        Choices.Continuous.toString()
    };
    private static String[] S_someMultiChoices = {
        Choices.List.toString(),
        Choices.Range.toString(),
        Choices.Continuous.toString()
    };

    private ChoiceParameter M_choiceAll;
    private ChoiceParameter M_choiceAllMulti;
    private ChoiceParameter M_choiceSomeMulti;

    private SingleT M_paramSingle;
    private CurrentT M_paramCurrent;
    private AllT M_paramAll;
    private ListT M_paramList;
    private RangeT M_paramRange;
    private ContinuousT M_paramContinuous;

    private ImageParameter M_holoP;

    private int M_maxT;
    private PossibleTypes M_possible;



    /** Time parameter getting a single time value */
    public class SingleT extends HoldingParameter<List<Integer>> {
        public SingleT() {super("SingleT");}
        @Override
        public void initialize()
        {
            M_t = addParameter(new IntParameter(1, "t_value"));
            M_currentMaxT = M_maxT;
            M_t.setBounds(1, M_maxT);
        }
        @Override
        public void addToDialog(DPDialog dialog)
        {
            super.addToDialog(dialog);
            if (M_currentMaxT != M_maxT) {
                M_currentMaxT = M_maxT;
                M_t.setBounds(1, M_maxT);
            }
        }
        @Override
        public void readFromDialog()
        {
            super.readFromDialog();
            if (M_currentMaxT != M_maxT) {
                M_currentMaxT = M_maxT;
                M_t.setBounds(1, M_maxT);
            }
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            super.readFromPrefs(c, name);
            M_t.setBounds(1, M_maxT);
        }
        @Override
        public List<Integer> getValue()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_t.getValue();}
                @Override
                public int size()
                {return 1;}
            };
        }

        private IntParameter M_t = new IntParameter(1, "tValue");
        private int M_currentMaxT;
    }



    /** Time parameter returning the current time slice */
    public class CurrentT extends AbstractDParameter<List<Integer>> {
        public CurrentT() {super("CurrentT");}
        @Override public void addToDialog(DPDialog dialog) {}
        @Override public void readFromDialog() {}
        @Override public void saveToPrefs(Class<?> c, String name) {}
        @Override public void readFromPrefs(Class<?> c, String name) {}
        @Override
        public List<Integer> getValue()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_holoP.getValue().getCurrentSlice();}
                @Override
                public int size()
                {return 1;}
            };
        }
    }



    /** Time parameter returning all time slices */
    public class AllT extends AbstractDParameter<List<Integer>> {
        public AllT() {super("AllT");}
        @Override public void addToDialog(DPDialog dialog) {}
        @Override public void readFromDialog() {}
        @Override public void saveToPrefs(Class<?> c, String name) {}
        @Override public void readFromPrefs(Class<?> c, String name) {}
        @Override
        public List<Integer> getValue()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return index + 1;}
                @Override
                public int size()
                {return M_maxT;}
            };
        }
    }



    /** Time parameter using a comma-separated list of values */
    public class ListT extends AbstractDParameter<List<Integer>> {
        public ListT() {super("ListT");}
        @Override
        public void initialize()
        {
            setError("t list is empty.");
        }
        @Override
        public void addToDialog(DPDialog dialog)
        {
            M_supplier = dialog.addTextBox(
                "t values (in a comma separated list)", M_currentString);
        }
        @Override
        public void readFromDialog()
        {
            M_currentString = M_supplier.get();
            processErrors();
        }
        @Override
        public void saveToPrefs(Class<?> c, String name)
        {
            prefs().put(c, name + ".value", M_currentString);
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            M_currentString = prefs().get(c, name + ".value", "");
            processErrors();
        }
        @Override
        public List<Integer> getValue() {return M_ts;}

        private void processErrors()
        {
            if (M_currentString == null || M_currentString == "") {
                setError("t list is empty.");
                return;
            }
            List<String> tsAsString
                = Arrays.asList(M_currentString.split("\\s*,\\s*"));
            M_ts = new ArrayList<Integer>(tsAsString.size());
            for (String s : tsAsString) {
                try {M_ts.add(Integer.parseInt(s));}
                catch (NumberFormatException e) {
                    setError("Unable to parse t list.  \"" + s
                              + "\" is not an integer.");
                    return;
                }
            }
            for (int t : M_ts) {
                if (t < 1 || t > M_maxT) {
                    setError("t value \"" + t + "\" is not in the range [1.."
                              + M_maxT + "].");
                    return;
                }
            }
            setError(null);
        }
        private ArrayList<Integer> M_ts;
        private String M_currentString;
        private Supplier<String> M_supplier;
    }



    /** Time parameter using an equally-spaced range */
    public class RangeT extends HoldingParameter<List<Integer>> {
        public RangeT() {super("RangeT");}
        @Override
        public void initialize()
        {
            M_begin = addParameter(new IntParameter(1, "t_value_begin"));
            M_end = addParameter(new IntParameter(1, "t_value_end"));
            M_step = addParameter(new IntParameter(1, "t_value_step"));
            M_begin.setBounds(1, M_maxT);
            M_end.setBounds(1, M_maxT);
        }
        @Override
        public void addToDialog(DPDialog dialog)
        {
            super.addToDialog(dialog);
            if (M_currentMaxT != M_maxT) {
                M_currentMaxT = M_maxT;
                M_begin.setBounds(1, M_maxT);
                M_end.setBounds(1, M_maxT);
            }
            processErrors();
        }
        @Override
        public void readFromDialog()
        {
            super.readFromDialog();
            if (M_currentMaxT != M_maxT) {
                M_currentMaxT = M_maxT;
                M_begin.setBounds(1, M_maxT);
                M_end.setBounds(1, M_maxT);
            }
            processErrors();
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            super.readFromPrefs(c, name);
            M_begin.setBounds(1, M_maxT);
            M_end.setBounds(1, M_maxT);
            processErrors();
        }
        @Override
        public List<Integer> getValue()
        {
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_begin.getValue() + M_step.getValue() * index;}
                @Override
                public int size()
                {return Math.abs((M_end.getValue() - M_begin.getValue())
                         / M_step.getValue()) + 1;}
            };
        }

        private void processErrors()
        {
            setError(M_begin.getError());
            if (getError() != null) return;
            setError(M_end.getError());
            if (getError() != null) return;
            setError(M_step.getError());
            if (getError() != null) return;
            if (M_step.getValue() == 0) {
                setError("t value step cannot be zero.");
                return;
            }
            if (!(M_begin.getValue().equals(M_end.getValue())) &&
                (M_end.getValue() < M_begin.getValue())
                    == (M_step.getValue() > 0)) {
                setError("The sign of t value step must be the same as the "
                          + "sign of t value end minus t value begin.");
                return;
            }
            setError(null);
        }
        private IntParameter M_begin;
        private IntParameter M_end;
        private IntParameter M_step;
        private int M_currentMaxT;
    }



    /** Time parameter using a continuous (separated by one) range */
    public class ContinuousT extends HoldingParameter<List<Integer>> {
        public ContinuousT() {super("ContinuousT");}
        @Override
        public void initialize()
        {
            M_begin = addParameter(new IntParameter(1, "t_value_begin"));
            M_end = addParameter(new IntParameter(1, "t_value_end"));
            M_begin.setBounds(1, M_maxT);
            M_end.setBounds(1, M_maxT);
        }
        @Override
        public void addToDialog(DPDialog dialog)
        {
            super.addToDialog(dialog);
            if (M_currentMaxT != M_maxT) {
                M_currentMaxT = M_maxT;
                M_begin.setBounds(1, M_maxT);
                M_end.setBounds(1, M_maxT);
            }
        }
        @Override
        public void readFromDialog()
        {
            super.readFromDialog();
            if (M_currentMaxT != M_maxT) {
                M_currentMaxT = M_maxT;
                M_begin.setBounds(1, M_maxT);
                M_end.setBounds(1, M_maxT);
            }
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            super.readFromPrefs(c, name);
            M_begin.setBounds(1, M_maxT);
            M_end.setBounds(1, M_maxT);
        }
        @Override
        public List<Integer> getValue()
        {
            int multiplier = M_begin.getValue() < M_end.getValue() ? 1 : -1;
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index)
                {return M_begin.getValue() + index * multiplier;}
                @Override
                public int size()
                {return Math.abs(M_end.getValue() - M_begin.getValue()) + 1;}
            };
        }

        private IntParameter M_begin;
        private IntParameter M_end;
        private int M_currentMaxT;
    }
}
