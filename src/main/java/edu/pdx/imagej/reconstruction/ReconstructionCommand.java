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

import org.scijava.Initializable;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.ops.OpService;

import edu.pdx.imagej.dynamic_parameters.DoubleParameter;
import edu.pdx.imagej.dynamic_parameters.ImageParameter;
import edu.pdx.imagej.reconstruction.units.DistanceUnitValue;
import edu.pdx.imagej.reconstruction.units.UnitService;
import edu.pdx.imagej.reconstruction.plugin.AllPluginsParameter;

/** A command that performs reconstruction, propagation, and more.  This command
 * takes all enabled plugins, gets all of their parameters, and calls {@link
 * ReconstructionOp} with them.
 */
@Plugin(type = Command.class, menuPath = "Plugins > DHM > Reconstruction")
public class ReconstructionCommand implements Command, Initializable {
    @Parameter private ImageParameter      P_hologram;
    @Parameter private DoubleParameter     P_wavelength;
    @Parameter private DoubleParameter     P_width;
    @Parameter private DoubleParameter     P_height;
    @Parameter private TParameter          P_ts;
    @Parameter private ZParameter          P_zs;
    @Parameter private AllPluginsParameter P_plugins;

    @Parameter private OpService P_ops;
    @Parameter private UnitService P_units;

    /** Initializes the parameters */
    @Override
    public void initialize()
    {
        P_hologram = new ImageParameter("Hologram(s)");
        P_wavelength = new DoubleParameter(500.0, "Wavelength",
                                           P_units.wavelength().toString());
        P_width = new DoubleParameter(0.0, "Image_Width",
                                      P_units.image().toString());
        P_height = new DoubleParameter(0.0, "Image_Height",
                                       P_units.image().toString());
        P_ts = new TParameter(P_hologram, TParameter.PossibleTypes.All, "Main");
        P_zs = new ZParameter();
        P_plugins = new AllPluginsParameter(P_hologram);

        P_wavelength.setBounds(Double.MIN_VALUE, Double.MAX_VALUE);
        P_width.setBounds(Double.MIN_VALUE, Double.MAX_VALUE);
        P_height.setBounds(Double.MIN_VALUE, Double.MAX_VALUE);
    }
    /** Run the command, which just calls the op. */
    @Override
    public void run()
    {
        DistanceUnitValue wavelength
            = new DistanceUnitValue(P_wavelength.getValue(),
                                    P_units.wavelength());
        DistanceUnitValue width
            = new DistanceUnitValue(P_width.getValue(), P_units.image());
        DistanceUnitValue height
            = new DistanceUnitValue(P_height.getValue(), P_units.image());

        P_ops.run("Hologram Reconstruction",
                  P_hologram.getValue(),
                  wavelength,
                  width,
                  height,
                  P_ts.getValue(),
                  P_zs.getValue(),
                  P_plugins.getValue());
    }
}
