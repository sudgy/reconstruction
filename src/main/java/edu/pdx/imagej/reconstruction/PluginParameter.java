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

import java.util.HashMap;
import java.util.Collection;

import org.scijava.plugin.Plugin;
import org.scijava.plugin.Parameter;

import edu.pdx.imagej.dynamic_parameters.HoldingParameter;
import edu.pdx.imagej.dynamic_parameters.DParameter;

@Plugin(type = DParameter.class)
public class PluginParameter extends HoldingParameter<
    HashMap<
        ReconstructionStep,
        Iterable<ReconstructionPlugin>
    >
> {
    @Parameter private ReconstructionPluginService P_plugin_service;

    public PluginParameter() {super("PluginParameters");}

    @Override
    public void initialize()
    {
        M_plugins = P_plugin_service.get_plugins();
        for (ReconstructionPlugin plugin : M_plugins) {
            DParameter param = plugin.param();
            if (param != null) add_premade_parameter(param);
        }
    }

    @Override
    public HashMap<ReconstructionStep, Iterable<ReconstructionPlugin>>
           get_value()
    {
        return P_plugin_service.get_plugins_map_from(M_plugins);
    }

    Iterable<ReconstructionPlugin> M_plugins;
}
