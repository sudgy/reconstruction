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
        M_choice = addParameter(new ChoiceParameter("Z_plane selection",
                                 S_choices, "Single"));
        M_paramSingle = addParameter(new SingleZ());
        M_paramList = addParameter(new ListZ());
        M_paramRange = addParameter(new RangeZ());
        setVisibilities();
    }
    @Override
    public void readFromDialog()
    {
        super.readFromDialog();
        setVisibilities();
    }
    @Override
    public void readFromPrefs(Class<?> c, String name)
    {
        super.readFromPrefs(c, name);
        setVisibilities();
    }
    @Override
    public List<DistanceUnitValue> getValue()
    {
        return currentParam().getValue();
    }

    private void setVisibilities()
    {
        M_paramSingle.setNewVisibility(false);
        M_paramList.setNewVisibility(false);
        M_paramRange.setNewVisibility(false);
        currentParam().setNewVisibility(true);
    }
    private DParameter<List<DistanceUnitValue>> currentParam()
    {
        switch (Choices.valueOf(M_choice.getValue())) {
            case Single: return M_paramSingle;
            case List: return M_paramList;
            case Range: return M_paramRange;
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

    private SingleZ M_paramSingle;
    private ListZ M_paramList;
    private RangeZ M_paramRange;
    @Parameter private UnitService P_units;



    /** Get a single z value. */
    public class SingleZ extends HoldingParameter<List<DistanceUnitValue>> {
        public SingleZ() {super("SingleZ");}
        @Override
        public void initialize()
        {
            M_z = addParameter(new DoubleParameter(0.0, "Z_value",
                                P_units.z().toString()));
        }
        @Override
        public List<DistanceUnitValue> getValue()
        {
            return new AbstractList<DistanceUnitValue>() {
                @Override
                public DistanceUnitValue get(int index)
                {return new DistanceUnitValue(M_z.getValue(), P_units.z());}
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
            setError("Z list is empty.");
        }
        @Override
        public void addToDialog(DPDialog dialog)
        {
            M_supplier = dialog.addTextBox(
                "Z values (in a comma separated list)", M_currentString);
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
        public List<DistanceUnitValue> getValue() {return M_zs;}

        private void processErrors()
        {
            List<String> zsAsString
                = Arrays.asList(M_currentString.split("\\s*,\\s*"));
            M_zs = new ArrayList<DistanceUnitValue>(zsAsString.size());
            for (String s : zsAsString) {
                try {
                    M_zs.add(new DistanceUnitValue(Double.parseDouble(s),
                             P_units.z()));
                }
                catch (NumberFormatException e) {
                    setError("Unable to parse Z list.  \"" + s
                              + "\" is not a number.");
                    return;
                }
            }
            setError(null);
        }
        private ArrayList<DistanceUnitValue> M_zs;
        private String M_currentString;
        private Supplier<String> M_supplier;
    }



    /** Get z values using an evenly-spaced range. */
    public class RangeZ extends HoldingParameter<List<DistanceUnitValue>> {
        public RangeZ() {super("RangeZ");}
        @Override
        public void initialize()
        {
            String units = P_units.z().toString();
            M_begin = addParameter(new DoubleParameter(0.0, "Z_value_begin",
                                    units));
            M_end = addParameter(new DoubleParameter(0.0, "Z_value_end",
                                  units));
            M_step = addParameter(new DoubleParameter(1.0, "Z_value_step",
                                   units));
        }
        @Override
        public void readFromDialog()
        {
            super.readFromDialog();
            processErrors();
        }
        @Override
        public void readFromPrefs(Class<?> c, String name)
        {
            super.readFromPrefs(c, name);
            processErrors();
        }
        @Override
        public List<DistanceUnitValue> getValue()
        {
            return new AbstractList<DistanceUnitValue>() {
                @Override
                public DistanceUnitValue get(int index)
                {return new DistanceUnitValue(M_begin.getValue()
                    + M_step.getValue() * index, P_units.z());}
                @Override
                public int size()
                {return Math.abs((int)((M_end.getValue() - M_begin.getValue())
                              / M_step.getValue())) + 1;}
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
                setError("Z value step cannot be zero.");
                return;
            }
            if (!(M_begin.getValue().equals(M_end.getValue())) &&
                (M_end.getValue() < M_begin.getValue())
                    == (M_step.getValue() > 0)) {
                setError("The sign of Z value step must be the same as the "
                    + "sign of Z value end minus Z value begin.");
                return;
            }
            setError(null);
        }
        private DoubleParameter M_begin;
        private DoubleParameter M_end;
        private DoubleParameter M_step;
    }
}
