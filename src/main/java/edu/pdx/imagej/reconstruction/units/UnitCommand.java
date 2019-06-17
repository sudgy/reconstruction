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

package edu.pdx.imagej.reconstruction.units;

import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/** This is going to change soon so I'm not writing documentation. */
@Plugin(type = Command.class, menuPath = "Plugins>DHM>Reconstruction Options")
public class UnitCommand implements Command {
    @Override
    public void run()
    {
        P_units.set_wavelength(P_wavelength);
        P_units.set_image(P_image);
        P_units.set_z(P_z);
    }

    @Parameter private UnitService P_units;

    @Parameter(visibility = ItemVisibility.MESSAGE) private String P_ = "Units";
    @Parameter(label = "Wavelength",
               choices = {"Nanometers", "Micrometers", "Millimeters",
                          "Centimeters", "Meters"})
    private String P_wavelength = "Nanometers";
    @Parameter(label = "Image Dimensions",
               choices = {"Nanometers", "Micrometers", "Millimeters",
               "Centimeters", "Meters"})
    private String P_image = "Micrometers";
    @Parameter(label = "Z",
               choices = {"Nanometers", "Micrometers", "Millimeters",
               "Centimeters", "Meters"})
    private String P_z = "Micrometers";
}
