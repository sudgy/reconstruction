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

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.ZProjector;

import edu.pdx.imagej.dynamic_parameters.DParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.AbstractDParameter;
import edu.pdx.imagej.dynamic_parameters.BoolParameter;
import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;

import edu.pdx.imagej.reconstruction.DynamicReferenceHolo;

public class RefParameter extends HoldingParameter<DynamicReferenceHolo> {
    public RefParameter(ImageParameter holo)
    {
        M_holo = holo;
    }
    @Override
    public void initialize()
    {
        M_choice = add_parameter(ChoiceParameter.class, "Reference Hologram", S_choices, "None");
        M_param_none = new NoneRef();
        M_param_single = add_parameter(SingleRef.class, this);
        M_param_offset = add_parameter(OffsetRef.class);
        M_param_median = add_parameter(MedianRef.class);
        M_param_median_offset = add_parameter(MedianOffsetRef.class);
        M_param_self = new SelfRef();
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        super.read_from_dialog(gd);
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
        M_param_single.set_new_visibility(false);
        M_param_offset.set_new_visibility(false);
        M_param_median.set_new_visibility(false);
        M_param_median_offset.set_new_visibility(false);
        switch (Choices.value_of(M_choice.get_value())) {
            case Single: M_param_single.set_new_visibility(true); break;
            case Offset: M_param_offset.set_new_visibility(true); break;
            case Median: M_param_median.set_new_visibility(true); break;
            case MedianOffset: M_param_median_offset.set_new_visibility(true); break;
        }
    }
    public DynamicReferenceHolo get_value() {return current_param().get_value();}

    private DParameter<DynamicReferenceHolo> current_param()
    {
        switch (Choices.value_of(M_choice.get_value())) {
            case None: return M_param_none;
            case Single: return M_param_single;
            case Offset: return M_param_offset;
            case Median: return M_param_median;
            case MedianOffset: return M_param_median_offset;
            case Self: return M_param_self;
        }
        return null;
    }
    private enum Choices {
        None, Single, Offset, Median, MedianOffset, Self;
        @Override public String toString()
        {
            if (this == Single) return "Single Image";
            else if (this == Offset) return "Single Image with Offset";
            else if (this == MedianOffset) return "Median with Offset";
            else return name();
        }
        public static Choices value_of(String s)
        {
            if (s.equals(Single.toString())) return Single;
            else if (s.equals(Offset.toString())) return Offset;
            else if (s.equals(MedianOffset.toString())) return MedianOffset;
            else return Choices.valueOf(s);
        }
    }
    private static String[] S_choices = {Choices.None.toString(), Choices.Single.toString(), Choices.Offset.toString(), Choices.Median.toString(), Choices.MedianOffset.toString(), Choices.Self.toString()};

    private ChoiceParameter M_choice;

    private NoneRef M_param_none;
    private SingleRef M_param_single;
    private OffsetRef M_param_offset;
    private MedianRef M_param_median;
    private MedianOffsetRef M_param_median_offset;
    private SelfRef M_param_self;

    private ImageParameter M_holo;



    public static class NoneRef extends AbstractDParameter<DynamicReferenceHolo> {
        @Override public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.None();}
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
    }



    public class SingleRef extends HoldingParameter<DynamicReferenceHolo> {
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class, "Reference Hologram Image");
            M_use_same_roi = add_parameter(BoolParameter.class, "Use same ROI for reference hologram?", true);
            if (M_img.get_value() == M_holo.get_value()) set_warning("Warning: Reference Hologram is the same as the Hologram being reconstructed.");
            else set_warning(null);
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            super.read_from_dialog(gd);
            set_error(M_img.get_error());
            if (get_error() == null) set_error(M_use_same_roi.get_error());
            if (M_img.get_value() == M_holo.get_value()) set_warning("Warning: Reference Hologram is the same as the Hologram being reconstructed.");
            else set_warning(null);
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            set_error(M_img.get_error());
            if (get_error() == null) set_error(M_use_same_roi.get_error());
            if (M_img.get_value() == M_holo.get_value()) set_warning("Warning: Reference Hologram is the same as the Hologram being reconstructed.");
            else set_warning(null);
        }
        @Override
        public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.Single(M_img.get_value().getProcessor().getFloatArray(), M_use_same_roi.get_value());}

        private ImageParameter M_img;
        private BoolParameter M_use_same_roi;
    }



    public static class OffsetRef extends HoldingParameter<DynamicReferenceHolo> {
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class, "Reference Hologram Image");
            M_offset = add_parameter(IntParameter.class, 0, "Time offset", "frames");
            M_use_same_roi = add_parameter(BoolParameter.class, "Use same ROI for reference hologram?", true);
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            super.read_from_dialog(gd);
            check_for_errors();
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            check_for_errors();
        }
        @Override
        public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.Offset(M_img.get_value(), M_use_same_roi.get_value(), M_offset.get_value());}

        private void check_for_errors()
        {
            set_error(M_img.get_error());
            if (get_error() == null) set_error(M_offset.get_error());
            if (get_error() == null) set_error(M_use_same_roi.get_error());
        }
        private ImageParameter M_img;
        private IntParameter M_offset;
        private BoolParameter M_use_same_roi;
    }



    public static class MedianRef extends HoldingParameter<DynamicReferenceHolo> {
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class, "Reference Hologram Stack");
            M_ts = add_parameter(TParameter.class, M_img, TParameter.PossibleTypes.AllMulti);
            M_use_same_roi = add_parameter(BoolParameter.class, "Use same ROI for reference hologram?", true);
        }
        @Override
        public void read_from_dialog(GenericDialog gd)
        {
            super.read_from_dialog(gd);
            set_error(M_img.get_error());
            if (get_error() == null) set_error(M_use_same_roi.get_error());
        }
        @Override
        public void read_from_prefs(Class<?> c, String name)
        {
            super.read_from_prefs(c, name);
            set_error(M_img.get_error());
            if (get_error() == null) set_error(M_use_same_roi.get_error());
        }
        @Override
        public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.Median(M_img.get_value(), M_use_same_roi.get_value(), M_ts.get_value());}

        private TParameter M_ts;
        private ImageParameter M_img;
        private BoolParameter M_use_same_roi;
        private float[][] M_result;
    }



    public static class MedianOffsetRef extends HoldingParameter<DynamicReferenceHolo> {
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class, "Reference Hologram Stack");
            M_ts = add_parameter(TParameter.class, M_img, TParameter.PossibleTypes.SomeMulti);
            M_offset = add_parameter(IntParameter.class, 0, "Time offset", "frames");
            M_use_same_roi = add_parameter(BoolParameter.class, "Use same ROI for reference hologram?", true);
        }
        @Override
        public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.MedianOffset(
                                                                M_img.get_value(),
                                                                M_use_same_roi.get_value(),
                                                                M_ts.get_value(),
                                                                M_offset.get_value());}

        private ImageParameter M_img;
        private TParameter M_ts;
        private IntParameter M_offset;
        private BoolParameter M_use_same_roi;
    }



    public static class SelfRef extends AbstractDParameter<DynamicReferenceHolo> {
        @Override public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.Self();}
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
    }
}
