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
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.dynamic_parameters.IntParameter;

import edu.pdx.imagej.reconstruction.DynamicReferenceHolo;

public class RefParameter extends HoldingParameter<DynamicReferenceHolo> {
    @Override
    public void initialize()
    {
        M_param = add_parameter(NoneRef.class);
    }
    @Override
    public void add_to_dialog(GenericDialog gd)
    {
        gd.addChoice("Reference Hologram", S_choices, M_type.toString());
        super.add_to_dialog(gd);
    }
    @Override
    public void read_from_dialog(GenericDialog gd)
    {
        Choices old_type = M_type;
        M_type = Choices.value_of(gd.getNextChoice());
        if (old_type != M_type) M_reconstruction_needed = true;
        super.read_from_dialog(gd);
        set_error(M_param.get_error());
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
        M_type = Choices.value_of(prefs().get(c, name + ".type", Choices.None.toString()));
        clear_parameters();
        switch (M_type) {
            case None: M_param = add_parameter(NoneRef.class); break;
            case Single: M_param = add_parameter(SingleRef.class); break;
            case Offset: M_param = add_parameter(OffsetRef.class); break;
            case Median: M_param = add_parameter(MedianRef.class); break;
            case MedianOffset: M_param = add_parameter(MedianOffsetRef.class); break;
            case Self: M_param = add_parameter(SelfRef.class); break;
        }
        super.read_from_prefs(c, name);
        set_error(M_param.get_error());
    }
    @Override public boolean reconstruction_needed()
        {return M_reconstruction_needed || super.reconstruction_needed();}
    @Override
    public void recreate()
    {
        if (M_reconstruction_needed) {
            M_reconstruction_needed = false;
            clear_parameters();
            switch (M_type) {
                case None: M_param = add_parameter(NoneRef.class); break;
                case Single: M_param = add_parameter(SingleRef.class); break;
                case Offset: M_param = add_parameter(OffsetRef.class); break;
                case Median: M_param = add_parameter(MedianRef.class); break;
                case MedianOffset: M_param = add_parameter(MedianOffsetRef.class); break;
                case Self: M_param = add_parameter(SelfRef.class); break;
            }
            set_error(M_param.get_error());
        }
        else if (super.reconstruction_needed()) {
            super.recreate();
        }
        else throw new UnsupportedOperationException();
    }
    public DynamicReferenceHolo get_value() {return M_param.get_value();}

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
    private Choices M_type = Choices.None;
    private boolean M_reconstruction_needed = false;
    private DParameter<DynamicReferenceHolo> M_param;



    public static class NoneRef extends AbstractDParameter<DynamicReferenceHolo> {
        @Override public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.None();}
        @Override public void add_to_dialog(GenericDialog gd) {}
        @Override public void read_from_dialog(GenericDialog gd) {}
        @Override public void save_to_prefs(Class<?> c, String name) {}
        @Override public void read_from_prefs(Class<?> c, String name) {}
    }



    public static class SingleRef extends HoldingParameter<DynamicReferenceHolo> {
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class, "Reference Hologram Image");
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
        public DynamicReferenceHolo get_value() {return new DynamicReferenceHolo.Median(M_img.get_value(), M_use_same_roi.get_value());}

        private ImageParameter M_img;
        private BoolParameter M_use_same_roi;
        private float[][] M_result;
    }



    public static class MedianOffsetRef extends HoldingParameter<DynamicReferenceHolo> {
        @Override
        public void initialize()
        {
            M_img = add_parameter(ImageParameter.class, "Reference Hologram Stack");
            M_ts = add_parameter(TParameter.class, M_img, true);
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
