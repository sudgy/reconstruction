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

import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import edu.pdx.imagej.dynamic_parameters.ChoiceParameter;
import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.DPDialog;

import edu.pdx.imagej.reconstruction.units.DistanceUnits;
import edu.pdx.imagej.reconstruction.units.UnitService;

/** A command that gets and sets the options for reconstruction.  It does all of
 * the options for all of the {@link
 * edu.pdx.imagej.reconstruction.ReconstructionPlugin ReconstructionPlugin}s,
 * along with a couple other things like units.
 */
@Plugin(type = Command.class, menuPath = "Plugins>DHM>Reconstruction Options")
public class OptionsCommand implements Command, Initializable {
    @Override
    public void initialize()
    {
        M_param = new OptionsParameter();
    }
    @Override
    public void run()
    {
        M_param.execute();
    }

    @Parameter private OptionsParameter M_param;
}

class OptionsParameter extends HoldingParameter<Void> {
    OptionsParameter()
    {
        super("Reconstruction Options");
    }
    @Override
    public void initialize()
    {
        M_wavelength = create_unit_param("Wavelength", P_units.wavelength());
        M_image = create_unit_param("Image Dimensions", P_units.image());
        M_z = create_unit_param("Z", P_units.z());
    }
    @Override
    public void add_to_dialog(DPDialog dialog)
    {
        dialog.add_message("Units");
        M_wavelength.add_to_dialog(dialog);
        M_image.add_to_dialog(dialog);
        M_z.add_to_dialog(dialog);
    }
    @Override public Void get_value() {return null;}
    public void execute()
    {
        P_units.set_wavelength(M_wavelength.get_value());
        P_units.set_image(M_image.get_value());
        P_units.set_z(M_z.get_value());
    }

    private ChoiceParameter create_unit_param(String name,
                                              DistanceUnits default_unit)
    {
        String default_string = null;
        switch (default_unit) {
            case Nano: default_string = "Nanometers"; break;
            case Micro: default_string = "Micrometers"; break;
            case Milli: default_string = "Millimeters"; break;
            case Centi: default_string = "Centimeters"; break;
            case Meter: default_string = "Meters"; break;
        }
        return add_parameter(ChoiceParameter.class,name, units, default_string);
    }

    private ChoiceParameter M_wavelength;
    private ChoiceParameter M_image;
    private ChoiceParameter M_z;
    private static String[] units = {"Nanometers", "Micrometers", "Millimeters",
                                     "Centimeters", "Meters"};

    @Parameter private UnitService P_units;
}
